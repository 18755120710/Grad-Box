import request from './request'

interface UploadOptions {
    chunkSize?: number
    concurrency?: number
    onProgress?: (percent: number) => void
}

/**
 * 大文件分片上传工具
 * @param file 文件对象
 * @param options 配置项
 */
export const uploadLargeFile = async (file: File, options: UploadOptions = {}) => {
    const {
        chunkSize = 5 * 1024 * 1024, // 默认 5MB
        concurrency = 3,           // 默认并发数为 3
        onProgress
    } = options

    // 1. 初始化上传任务，获取 uploadId
    const initRes: any = await request.post('/api/file/upload/init', null, {
        params: { fileName: file.name }
    })

    if (initRes.code !== 200 && !initRes.data) {
        throw new Error('Failed to initialize upload')
    }

    const { uploadId } = initRes.data
    const totalChunks = Math.ceil(file.size / chunkSize)
    let uploadedCount = 0

    // 2. 定义分片上传逻辑
    const uploadChunk = async (index: number) => {
        const start = index * chunkSize
        const end = Math.min(start + chunkSize, file.size)
        const chunkBlob = file.slice(start, end)

        const formData = new FormData()
        formData.append('uploadId', uploadId)
        formData.append('index', index.toString())
        formData.append('file', chunkBlob)

        try {
            await request.post('/api/file/upload/chunk', formData)
            uploadedCount++
            if (onProgress) {
                onProgress(Math.round((uploadedCount / totalChunks) * 100))
            }
        } catch (error) {
            console.error(`Chunk ${index} upload failed:`, error)
            throw error // 抛出异常由上层处理（暂不实现复杂的自动重试）
        }
    }

    // 3. 并发执行上传
    const queue = Array.from({ length: totalChunks }, (_, i) => i)
    const workers = []

    for (let i = 0; i < Math.min(concurrency, totalChunks); i++) {
        workers.push((async () => {
            while (queue.length > 0) {
                const index = queue.shift()
                if (index !== undefined) {
                    await uploadChunk(index)
                }
            }
        })())
    }

    await Promise.all(workers)

    // 4. 完成合并请求
    const completeRes: any = await request.post('/api/file/upload/complete', null, {
        params: {
            uploadId,
            fileName: file.name,
            totalChunks
        }
    })

    if (completeRes.code !== 200 && !completeRes.data) {
        throw new Error('Failed to complete upload')
    }

    return completeRes.data // 返回最终的文件 URL
}
