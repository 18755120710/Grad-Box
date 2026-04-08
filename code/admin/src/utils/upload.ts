import request from './request'
import SparkMD5 from 'spark-md5'

interface UploadOptions {
    chunkSize?: number
    concurrency?: number
    onProgress?: (percent: number) => void
    onStatusChange?: (status: 'calculating' | 'uploading' | 'paused' | 'completed' | 'error') => void
    signal?: AbortSignal // 用于中止请求
}

/**
 * 计算文件 MD5 (分块计算，避免卡顿)
 */
export const calculateMd5 = (file: File, chunkSize: number): Promise<string> => {
    return new Promise((resolve, reject) => {
        const spark = new SparkMD5.ArrayBuffer()
        const reader = new FileReader()
        const totalChunks = Math.ceil(file.size / chunkSize)
        let currentChunk = 0

        reader.onload = (e) => {
            spark.append(e.target?.result as ArrayBuffer)
            currentChunk++
            if (currentChunk < totalChunks) {
                loadNext()
            } else {
                resolve(spark.end())
            }
        }

        reader.onerror = reject

        const loadNext = () => {
            const start = currentChunk * chunkSize
            const end = Math.min(start + chunkSize, file.size)
            reader.readAsArrayBuffer(file.slice(start, end))
        }

        loadNext()
    })
}

/**
 * 大文件分片上传工具 (支持断点续传与暂停)
 */
export const uploadLargeFile = async (file: File, options: UploadOptions = {}) => {
    const {
        chunkSize = 5 * 1024 * 1024,
        concurrency = 3,
        onProgress,
        onStatusChange,
        signal
    } = options

    try {
        // 1. 计算文件 MD5
        if (onStatusChange) onStatusChange('calculating')
        const identifier = await calculateMd5(file, chunkSize)

        // 2. 检查已上传的分片 (断点续传预检)
        const checkRes: any = await request.get('/api/file/upload/check', {
            params: { identifier },
            signal
        })

        const { uploadedChunks, uploadId } = checkRes.data
        const totalChunks = Math.ceil(file.size / chunkSize)

        // 如果已经全部上传，则直接完成合并
        if (uploadedChunks.length === totalChunks) {
            if (onStatusChange) onStatusChange('completed')
            const completeRes: any = await request.post('/api/file/upload/complete', null, {
                params: { uploadId, fileName: file.name, totalChunks }
            })
            return completeRes.data
        }

        // 3. 准备待上传的任务队列
        if (onStatusChange) onStatusChange('uploading')
        let uploadedCount = uploadedChunks.length
        const queue = []
        for (let i = 0; i < totalChunks; i++) {
            if (!uploadedChunks.includes(i)) {
                queue.push(i)
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

            await request.post('/api/file/upload/chunk', formData, { signal })

            uploadedCount++
            if (onProgress) {
                onProgress(Math.round((uploadedCount / totalChunks) * 100))
            }
        }

        // 并发控制器
        const workers = []
        const runningQueue = [...queue]
        for (let i = 0; i < Math.min(concurrency, runningQueue.length); i++) {
            workers.push((async () => {
                while (runningQueue.length > 0) {
                    if (signal?.aborted) throw new Error('AbortError')
                    const index = runningQueue.shift()
                    if (index !== undefined) {
                        await uploadChunk(index)
                    }
                }
            })())
        }

        await Promise.all(workers)

        // 4. 合并请求
        const completeRes: any = await request.post('/api/file/upload/complete', null, {
            params: { uploadId, fileName: file.name, totalChunks },
            signal
        })

        if (onStatusChange) onStatusChange('completed')
        return completeRes.data
    } catch (error: any) {
        if (error.name === 'CanceledError' || error.message === 'AbortError') {
            if (onStatusChange) onStatusChange('paused')
            throw new Error('Upload paused')
        }
        if (onStatusChange) onStatusChange('error')
        throw error
    }
}
