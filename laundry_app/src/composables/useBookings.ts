import { ref, computed } from 'vue'

const API_BASE = '/api'

export interface Booking {
  id: string
  userId: string
  machineId: string
  slotId: string
  state: 'active' | 'canceled' | 'completed'
  createdAt: string
}

// Локальное состояние для кэширования
const bookings = ref<Booking[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

export function useBookings() {
  // Создать бронирование
  const createBooking = async (
    userId: string,
    machineId: string,
    slotId: string
  ) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(`${API_BASE}/bookings/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId,
          machineId,
          slotId
        })
      })

      const data = await response.json()

      if (!response.ok || !data.result) {
        error.value = data.message || 'Failed to create booking'
        return { success: false, message: error.value }
      }

      return { success: true, message: data.message }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Create booking error:', err)
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  // Отменить бронирование
  const cancelBooking = async (bookingId: string, userId: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(`${API_BASE}/bookings/cancel`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          bookingId,
          userId
        })
      })

      const data = await response.json()

      if (!response.ok || !data.result) {
        error.value = data.message || 'Failed to cancel booking'
        return { success: false, message: error.value }
      }

      return { success: true, message: data.message }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Cancel booking error:', err)
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  // Перенести бронирование
  const rescheduleBooking = async (
    bookingId: string,
    newSlotId: string,
    userId: string
  ) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(`${API_BASE}/bookings/reschedule`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          bookingId,
          newSlotId,
          userId
        })
      })

      const data = await response.json()

      if (!response.ok || !data.result) {
        error.value = data.message || 'Failed to reschedule booking'
        return { success: false, message: error.value }
      }

      return { success: true, message: data.message }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Reschedule booking error:', err)
      return { success: false, message: error.value }
    } finally {
      loading.value = false
    }
  }

  // Фильтры для локального состояния
  const activeBookings = computed(() => {
    return bookings.value.filter(b => b.state === 'active')
  })

  const getUserBookings = (userId: string) => {
    return bookings.value.filter(b => b.userId === userId && b.state === 'active')
  }

  return {
    bookings,
    loading,
    error,
    activeBookings,
    createBooking,
    cancelBooking,
    rescheduleBooking,
    getUserBookings
  }
}
