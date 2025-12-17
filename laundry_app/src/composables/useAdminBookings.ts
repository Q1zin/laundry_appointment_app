import { ref } from 'vue'

const API_BASE = '/api'

interface Booking {
  id: string
  userId: string
  machineId: string
  slotId: string
  state: string
  createdAt: string
}

const allBookings = ref<Booking[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

export function useAdminBookings() {
  // Удалить бронирование (Админ)
  const deleteBooking = async (bookingId: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(`${API_BASE}/admin/bookings/${bookingId}`, {
        method: 'DELETE'
      })

      const data = await response.json()

      if (!response.ok || !data.result) {
        error.value = data.message || 'Failed to delete booking'
        return { success: false, message: error.value }
      }

      return { success: true, message: data.message }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Delete booking error:', err)
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    allBookings,
    loading,
    error,
    deleteBooking
  }
}
