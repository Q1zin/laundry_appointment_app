import { ref, computed } from 'vue'

export interface Booking {
  id: string
  machine: number
  date: string
  dayName: string
  dayNumber: number
  time: string
  fullName: string
  room: string
  createdAt: Date
}

const BOOKINGS_KEY = 'user_bookings'

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
    return newBooking
  }

  const cancelBooking = (bookingId: string) => {
    const index = bookings.value.findIndex(b => b.id === bookingId)
    if (index !== -1) {
      bookings.value.splice(index, 1)
      saveToStorage()
      return true
    }
    return false
  }

  const clearAllBookings = () => {
    bookings.value = []
    localStorage.removeItem(BOOKINGS_KEY)
  }

  return {
    bookings,
    activeBookings,
    addBooking,
    cancelBooking,
    clearAllBookings
  }
}
