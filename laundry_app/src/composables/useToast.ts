import { ref } from 'vue'

interface Toast {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
}

interface ConfirmDialog {
  id: number
  message: string
  onConfirm: () => void
  onCancel: () => void
}

const toasts = ref<Toast[]>([])
const confirmDialogs = ref<ConfirmDialog[]>([])
let nextId = 0
let nextConfirmId = 0

export function useToast() {
  const showToast = (message: string, type: 'success' | 'error' | 'info' = 'info', duration = 3000) => {
    const id = nextId++
    toasts.value.push({ id, message, type })
    
    setTimeout(() => {
      removeToast(id)
    }, duration)
  }
  
  const removeToast = (id: number) => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }
  
  const success = (message: string, duration?: number) => {
    showToast(message, 'success', duration)
  }
  
  const error = (message: string, duration?: number) => {
    showToast(message, 'error', duration)
  }
  
  const info = (message: string, duration?: number) => {
    showToast(message, 'info', duration)
  }
  
  const confirm = (message: string): Promise<boolean> => {
    return new Promise((resolve) => {
      const id = nextConfirmId++
      confirmDialogs.value.push({
        id,
        message,
        onConfirm: () => {
          removeConfirmDialog(id)
          resolve(true)
        },
        onCancel: () => {
          removeConfirmDialog(id)
          resolve(false)
        }
      })
    })
  }
  
  const removeConfirmDialog = (id: number) => {
    const index = confirmDialogs.value.findIndex(d => d.id === id)
    if (index > -1) {
      confirmDialogs.value.splice(index, 1)
    }
  }
  
  return {
    toasts,
    confirmDialogs,
    showToast,
    removeToast,
    success,
    error,
    info,
    confirm,
    removeConfirmDialog
  }
}
