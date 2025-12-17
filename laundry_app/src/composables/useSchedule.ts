import { ref } from 'vue'

const API_BASE = '/api'

export interface Machine {
  id: string
  name: string
  status: string
  createdAt: string
  alreadyBlocked: boolean
}

export interface Timeslot {
  id: string
  startTime: string
  endTime: string
  isAvailable: boolean
  machineId: string
  createdAt: string
  slotId: string
}

export interface Booking {
  id: string
  userId: string
  machineId: string
  slotId: string
  state: string
  createdAt: string
}

export interface ScheduleData {
  machines: Machine[]
  timeslots: Timeslot[]
  bookings: Booking[]
}

// Глобальное состояние
const scheduleData = ref<ScheduleData | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

export function useSchedule() {
  // Получить расписание на определенную дату
  const fetchSchedule = async (date: string, userId: string) => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(
        `${API_BASE}/schedule?date=${date}&userId=${userId}`
      )

      if (!response.ok) {
        throw new Error('Failed to fetch schedule')
      }

      const data = await response.json()
      scheduleData.value = data

      return { success: true, data }
    } catch (err) {
      error.value = 'Network error. Please try again.'
      console.error('Fetch schedule error:', err)
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // Получить доступные слоты для машины на дату
  const getAvailableSlots = (machineId: string) => {
    if (!scheduleData.value) return []
    
    return scheduleData.value.timeslots.filter(
      slot => slot.machineId === machineId && slot.isAvailable
    )
  }

  // Получить все бронирования пользователя
  const getUserBookings = (userId: string) => {
    if (!scheduleData.value) return []
    
    return scheduleData.value.bookings.filter(
      booking => booking.userId === userId && booking.state === 'active'
    )
  }

  // Получить информацию о машине
  const getMachine = (machineId: string) => {
    if (!scheduleData.value) return null
    
    return scheduleData.value.machines.find(m => m.id === machineId) || null
  }

  // Проверить доступность слота
  const isSlotAvailable = (slotId: string) => {
    if (!scheduleData.value) return false
    
    const slot = scheduleData.value.timeslots.find(s => s.id === slotId)
    return slot?.isAvailable || false
  }

  return {
    scheduleData,
    loading,
    error,
    fetchSchedule,
    getAvailableSlots,
    getUserBookings,
    getMachine,
    isSlotAvailable
  }
}
