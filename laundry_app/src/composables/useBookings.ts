import { ref, computed } from 'vue'

export interface Booking {
  id: string
  machine: number
  machineName?: string
  date: string
  dayName: string
  dayNumber: number
  time: string
  fullName: string
  room: string
  userEmail?: string
  createdAt: Date
}

const BOOKINGS_KEY = 'user_bookings'
const ALL_BOOKINGS_KEY = 'all_bookings'

// Глобальное реактивное состояние
const bookings = ref<Booking[]>([])

// Инициализация из localStorage
const initFromStorage = () => {
  try {
    const stored = localStorage.getItem(BOOKINGS_KEY)
    if (stored) {
      bookings.value = JSON.parse(stored)
    }
  } catch {
    // ignore parse errors
  }
}

// Сохранение в localStorage
const saveToStorage = () => {
  localStorage.setItem(BOOKINGS_KEY, JSON.stringify(bookings.value))
}

// Генерация ID
const generateId = () => {
  return 'booking_' + Math.random().toString(36).substring(2) + Date.now().toString(36)
}

export function useBookings() {
  // Инициализация при первом использовании
  if (bookings.value.length === 0) {
    initFromStorage()
  }

  const activeBookings = computed(() => {
    const now = new Date()
    return bookings.value.filter(booking => {
      // Простая фильтрация - показываем все записи
      // В реальном приложении здесь была бы проверка даты
      return true
    })
  })

  const addBooking = (bookingData: Omit<Booking, 'id' | 'createdAt'>) => {
    const newBooking: Booking = {
      ...bookingData,
      id: generateId(),
      createdAt: new Date()
    }
    bookings.value.push(newBooking)
    saveToStorage()
    
    // Также сохраняем в общий список для админа
    try {
      const allBookingsStr = localStorage.getItem(ALL_BOOKINGS_KEY)
      const allBookings = allBookingsStr ? JSON.parse(allBookingsStr) : []
      allBookings.push(newBooking)
      localStorage.setItem(ALL_BOOKINGS_KEY, JSON.stringify(allBookings))
    } catch {
      // ignore
    }
    
    return newBooking
  }

  const cancelBooking = (bookingId: string) => {
    const index = bookings.value.findIndex(b => b.id === bookingId)
    if (index !== -1) {
      bookings.value.splice(index, 1)
      saveToStorage()
      
      // Также удаляем из общего списка
      try {
        const allBookingsStr = localStorage.getItem(ALL_BOOKINGS_KEY)
        if (allBookingsStr) {
          const allBookings = JSON.parse(allBookingsStr)
          const allIndex = allBookings.findIndex((b: Booking) => b.id === bookingId)
          if (allIndex !== -1) {
            allBookings.splice(allIndex, 1)
            localStorage.setItem(ALL_BOOKINGS_KEY, JSON.stringify(allBookings))
          }
        }
      } catch {
        // ignore
      }
      
      return true
    }
    return false
  }

  const clearAllBookings = () => {
    bookings.value = []
    localStorage.removeItem(BOOKINGS_KEY)
  }

  const updateBooking = (bookingId: string, bookingData: Omit<Booking, 'id' | 'createdAt'>) => {
    const index = bookings.value.findIndex(b => b.id === bookingId)
    if (index !== -1) {
      const existing = bookings.value[index]
      const updatedBooking: Booking = {
        ...bookingData,
        id: bookingId,
        createdAt: existing ? existing.createdAt : new Date()
      }
      bookings.value[index] = updatedBooking
      saveToStorage()
      
      // Также обновляем в общем списке для админа
      try {
        const allBookingsStr = localStorage.getItem(ALL_BOOKINGS_KEY)
        if (allBookingsStr) {
          const allBookings = JSON.parse(allBookingsStr)
          const allIndex = allBookings.findIndex((b: Booking) => b.id === bookingId)
          if (allIndex !== -1) {
            allBookings[allIndex] = updatedBooking
            localStorage.setItem(ALL_BOOKINGS_KEY, JSON.stringify(allBookings))
          }
        }
      } catch {
        // ignore
      }
      
      return updatedBooking
    }
    return null
  }

  const getBookingById = (bookingId: string) => {
    return bookings.value.find(b => b.id === bookingId) || null
  }

  return {
    bookings,
    activeBookings,
    addBooking,
    cancelBooking,
    clearAllBookings,
    updateBooking,
    getBookingById
  }
}
