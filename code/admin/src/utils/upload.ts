import request from './request'
import SparkMD5 from 'spark-md5'

type UploadStatus = 'calculating' | 'uploading' | 'merging' | 'paused' | 'completed' | 'error'

interface UploadOptions {
    chunkSize?: number
    concurrency?: number
    onProgress?: (percent: number) => void
    onStatusChange?: (status: UploadStatus) => void
    signal?: AbortSignal
}

interface UploadSessionSnapshot {
    identifier: string
    fileName: string
    fileSize: number
    chunkSize: number
    totalChunks: number
    lastModified: number
}

const SESSION_PREFIX = 'gradbox-upload-session:'

const createAbortError = () => {
    const error = new Error('AbortError')
    error.name = 'AbortError'
    return error
}

const ensureNotAborted = (signal?: AbortSignal) => {
    if (signal?.aborted) {
        throw createAbortError()
    }
}

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

const withRetry = async <T>(
    fn: () => Promise<T>,
    retries = 3,
    delay = 1000,
    signal?: AbortSignal
): Promise<T> => {
    try {
        return await fn()
    } catch (error: any) {
        if (retries <= 0 || signal?.aborted || error.name === 'CanceledError' || error.message === 'AbortError') {
            throw error
        }
        console.warn(`Retry upload attempt left: ${retries}, error:`, error.message)
        await sleep(delay)
        return withRetry(fn, retries - 1, delay * 2, signal)
    }
}

const getSessionStorageKey = (identifier: string) => `${SESSION_PREFIX}${identifier}`

const persistUploadSession = (snapshot: UploadSessionSnapshot) => {
    if (typeof window === 'undefined') return
    window.sessionStorage.setItem(getSessionStorageKey(snapshot.identifier), JSON.stringify(snapshot))
}

const clearUploadSession = (identifier: string) => {
    if (typeof window === 'undefined') return
    window.sessionStorage.removeItem(getSessionStorageKey(identifier))
}

export const calculateMd5 = (file: File, chunkSize: number, signal?: AbortSignal): Promise<string> => {
    return new Promise((resolve, reject) => {
        const spark = new SparkMD5.ArrayBuffer()
        const reader = new FileReader()
        const totalChunks = Math.ceil(file.size / chunkSize)
        let currentChunk = 0

        const cleanup = () => {
            signal?.removeEventListener('abort', abortHandler)
        }

        const abortHandler = () => {
            reader.abort()
            cleanup()
            reject(createAbortError())
        }

        reader.onload = async (event) => {
            try {
                ensureNotAborted(signal)
            } catch (error) {
                cleanup()
                reject(error)
                return
            }

            spark.append(event.target?.result as ArrayBuffer)
            currentChunk++

            if (currentChunk < totalChunks) {
                // 每计算 50 个分片稍微释放一下主线程，防止页面完全卡死
                if (currentChunk % 50 === 0) {
                    await sleep(1)
                }
                loadNext()
                return
            }

            cleanup()
            resolve(spark.end())
        }

        reader.onerror = () => {
            cleanup()
            reject(reader.error ?? new Error('Failed to read file'))
        }

        reader.onabort = () => {
            cleanup()
            reject(createAbortError())
        }

        const loadNext = async () => {
            try {
                ensureNotAborted(signal)
            } catch (error) {
                cleanup()
                reject(error)
                return
            }

            // 增加微小延迟以释放主线程
            await sleep(0)

            const start = currentChunk * chunkSize
            const end = Math.min(start + chunkSize, file.size)
            reader.readAsArrayBuffer(file.slice(start, end))
        }

        signal?.addEventListener('abort', abortHandler, { once: true })
        loadNext()
    })
}

export interface UploadResult {
    url: string
    size: number
    duration: number // 秒，保留一位小数
    fileName: string
}

export const uploadLargeFile = async (file: File, options: UploadOptions = {}): Promise<UploadResult> => {
    const startTime = performance.now()
    const {
        chunkSize = 5 * 1024 * 1024,
        concurrency = 3,
        onProgress,
        onStatusChange,
        signal
    } = options

    try {
        ensureNotAborted(signal)

        if (onStatusChange) onStatusChange('calculating')
        const identifier = await calculateMd5(file, chunkSize, signal)
        const totalChunks = Math.ceil(file.size / chunkSize)

        const sessionSnapshot: UploadSessionSnapshot = {
            identifier,
            fileName: file.name,
            fileSize: file.size,
            chunkSize,
            totalChunks,
            lastModified: file.lastModified
        }
        persistUploadSession(sessionSnapshot)

        const checkRes: any = await request.get('/api/file/upload/check', {
            params: { identifier, fileName: file.name, fileSize: file.size },
            signal,
            timeout: 30000
        })

        const buildResult = (url: string): UploadResult => {
            const endTime = performance.now()
            return {
                url,
                size: file.size,
                duration: Number(((endTime - startTime) / 1000).toFixed(1)),
                fileName: file.name
            }
        }

        if (checkRes.data.completed) {
            if (onProgress) onProgress(100)
            if (onStatusChange) onStatusChange('completed')
            return buildResult(checkRes.data.url)
        }

        const uploadId = checkRes.data.uploadId
        let uploadedChunks: number[] = Array.isArray(checkRes.data.uploadedChunks)
            ? checkRes.data.uploadedChunks
            : []

        if (onProgress && uploadedChunks.length > 0) {
            onProgress(Math.round((uploadedChunks.length / totalChunks) * 100))
        }

        if (uploadedChunks.length === totalChunks) {
            if (onStatusChange) onStatusChange('merging')
            const completeRes: any = await request.post('/api/file/upload/complete', null, {
                params: { uploadId, fileName: file.name, totalChunks },
                signal,
                timeout: 120000 // 延长合并超时时间
            })
            clearUploadSession(identifier)
            if (onStatusChange) onStatusChange('completed')
            return buildResult(completeRes.data)
        }

        if (onStatusChange) onStatusChange('uploading')
        let uploadedCount = uploadedChunks.length
        const queue: number[] = []
        for (let index = 0; index < totalChunks; index++) {
            if (!uploadedChunks.includes(index)) {
                queue.push(index)
            }
        }

        const uploadChunk = async (index: number) => {
            const start = index * chunkSize
            const end = Math.min(start + chunkSize, file.size)
            const chunkBlob = file.slice(start, end)

            const formData = new FormData()
            formData.append('uploadId', uploadId)
            formData.append('index', index.toString())
            formData.append('file', chunkBlob)

            await withRetry(
                () => request.post('/api/file/upload/chunk', formData, {
                    signal,
                    timeout: 60000
                }),
                3,
                1000,
                signal
            )

            uploadedCount++
            if (onProgress) {
                onProgress(Math.round((uploadedCount / totalChunks) * 100))
            }
        }

        const workers = []
        const runningQueue = [...queue]
        for (let index = 0; index < Math.min(concurrency, runningQueue.length); index++) {
            workers.push((async () => {
                while (runningQueue.length > 0) {
                    ensureNotAborted(signal)
                    const chunkIndex = runningQueue.shift()
                    if (chunkIndex !== undefined) {
                        await uploadChunk(chunkIndex)
                    }
                }
            })())
        }

        await Promise.all(workers)

        if (onStatusChange) onStatusChange('merging')
        const completeRes: any = await request.post('/api/file/upload/complete', null, {
            params: { uploadId, fileName: file.name, totalChunks },
            signal,
            timeout: 120000 // 延长合并超时时间
        })

        clearUploadSession(identifier)
        if (onStatusChange) onStatusChange('completed')
        return buildResult(completeRes.data)
    } catch (error: any) {
        if (error.name === 'CanceledError' || error.message === 'AbortError') {
            if (onStatusChange) onStatusChange('paused')
            throw new Error('Upload paused')
        }

        if (onStatusChange) onStatusChange('error')
        throw error
    }
}
