import { ref } from 'vue'

const API_BASE = '/api'

export interface Machine {
  id: string
  name: string
  status: string
  alreadyBlocked: boolean
}

const machines = ref<Machine[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

export function useMachines() {
  // Блокировать машину (Админ)
  const blockMachine = async (machineId: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(`${API_BASE}/admin/machines/block`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ machineId })
      })

      const data = await response.json()

      if (!response.ok || !data.result) {
        error.value = data.message || 'Failed to block machine'
        return { success: false, message: error.value }
      }

      return { success: true, message: data.message }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Block machine error:', err)
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  // Разблокировать машину (Админ)
  const unblockMachine = async (machineId: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(`${API_BASE}/admin/machines/unblock`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ machineId })
      })

      const data = await response.json()

      if (!response.ok || !data.result) {
        error.value = data.message || 'Failed to unblock machine'
        return { success: false, message: error.value }
      }

      return { success: true, message: data.message }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Unblock machine error:', err)
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  const getActiveMachines = () => {
    return machines.value.filter(m => m.status === 'available')
  }

  return {
    machines,
    loading,
    error,
    blockMachine,
    unblockMachine,
    getActiveMachines
  }
}
