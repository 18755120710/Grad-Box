import { defineStore } from 'pinia'

export interface DownloadProgress {
    url: string
    fileName: string
    progress: number
    status: 'waiting' | 'downloading' | 'merging' | 'completed' | 'error'
    totalSize?: number
    downloadedSize?: number
}

export const useDownloadStore = defineStore('download', {
    state: () => ({
        tasks: {} as Record<string, DownloadProgress>
    }),
    actions: {
        addTask(key: string, task: DownloadProgress) {
            this.tasks[key] = task
        },
        updateTask(key: string, updates: Partial<DownloadProgress>) {
            if (this.tasks[key]) {
                this.tasks[key] = { ...this.tasks[key], ...updates }
            }
        },
        removeTask(key: string) {
            delete this.tasks[key]
        }
    }
})
