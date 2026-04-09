import { computed } from 'vue'
import { useDownloadStore, type DownloadProgress } from '~/stores/download'

export const useDownload = () => {
    const downloadStore = useDownloadStore()
    const downloadStates = computed(() => downloadStore.tasks)

    const performTurboDownload = async (fileUrl: string, fileName: string) => {
        const downloadKey = fileUrl

        // 如果已经存在该任务且在进行中，则不重复启动
        if (downloadStore.tasks[downloadKey] &&
            ['downloading', 'merging'].includes(downloadStore.tasks[downloadKey].status)) {
            return
        }

        downloadStore.addTask(downloadKey, {
            url: fileUrl,
            fileName,
            progress: 0,
            status: 'waiting'
        })

        const updateState = (updates: Partial<DownloadProgress>) => {
            downloadStore.updateTask(downloadKey, updates)
        }

        updateState({ status: 'downloading' })

        try {
            // 1. 预检：获取文件总量和类型
            const endpoint = `/api/file/download?url=${encodeURIComponent(fileUrl)}`
            const runtimeConfig = useRuntimeConfig()
            const baseUrl = runtimeConfig.public.apiBase || 'http://localhost:8080'
            const fullEndpoint = baseUrl + endpoint

            let headRes = await fetch(fullEndpoint, {
                method: 'HEAD'
            })

            if (!headRes.ok || headRes.status === 405) {
                headRes = await fetch(fullEndpoint, {
                    method: 'GET',
                    headers: { 'Range': 'bytes=0-0' }
                })
            }

            if (!headRes.ok) throw new Error('Failed to fetch file metadata')

            let totalSize = 0
            const contentRange = headRes.headers.get('content-range')
            const contentLength = headRes.headers.get('content-length')

            if (contentRange) {
                totalSize = parseInt(contentRange.split('/')[1])
            } else {
                totalSize = parseInt(contentLength || '0')
            }
            const contentType = headRes.headers.get('content-type') || 'application/octet-stream'

            updateState({ totalSize, status: 'downloading' })

            // 2. 分片参数设计
            const CHUNK_SIZE = 30 * 1024 * 1024
            const chunkCount = Math.ceil(totalSize / CHUNK_SIZE)
            const concurrency = 4
            const chunks: ArrayBuffer[] = new Array(chunkCount)
            let downloadedCount = 0
            let currentDownloadedSize = 0

            // 3. 并发下载执行器
            const downloadChunk = async (index: number) => {
                const start = index * CHUNK_SIZE
                const end = Math.min(start + CHUNK_SIZE - 1, totalSize - 1)

                const chunkRes = await fetch(fullEndpoint, {
                    headers: {
                        'Range': `bytes=${start}-${end}`
                    }
                })

                if (!chunkRes.ok) throw new Error(`Chunk ${index} download failed`)

                chunks[index] = await chunkRes.arrayBuffer()
                downloadedCount++
                currentDownloadedSize += (end - start + 1)

                updateState({
                    downloadedSize: currentDownloadedSize,
                    progress: Math.round((downloadedCount / chunkCount) * 100)
                })
            }

            const queue = Array.from({ length: chunkCount }, (_, i) => i)
            const workers = Array.from({ length: Math.min(concurrency, chunkCount) }, async () => {
                while (queue.length > 0) {
                    const index = queue.shift()!
                    await downloadChunk(index)
                }
            })

            await Promise.all(workers)

            // 4. 合并分片
            updateState({ status: 'merging' })
            const blob = new Blob(chunks, { type: contentType })

            // 5. 触发浏览器下载
            const url = URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = url
            a.download = fileName
            document.body.appendChild(a)
            a.click()
            document.body.removeChild(a)
            URL.revokeObjectURL(url)

            updateState({ status: 'completed', progress: 100 })

            // 3秒后清理状态
            setTimeout(() => {
                downloadStore.removeTask(downloadKey)
            }, 3000)

        } catch (error: any) {
            console.error('Turbo Download Error:', error)
            updateState({ status: 'error' })
            // @ts-ignore
            if (process.client) {
                // @ts-ignore
                ElMessage.error(`下载失败: ${fileName}`)
            }
        }
    }

    return {
        downloadStates,
        performTurboDownload
    }
}
