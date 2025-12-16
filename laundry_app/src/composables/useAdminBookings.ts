import { ref, computed } from 'vue'
import type { Booking } from './useBookings'

const ALL_BOOKINGS_KEY = 'all_bookings'

// Глобальное состояние всех записей (для админа)
const allBookings = ref<Booking[]>([])
let initialized = false

// Инициализация из localStorage
const initFromStorage = () => {
  if (initialized) return
  
  try {
    const stored = localStorage.getItem(ALL_BOOKINGS_KEY)
    if (stored) {
      allBookings.value = JSON.parse(stored)
    }
    initialized = true
  } catch {
    // ignore
  }
}

// Сохранение в localStorage
const saveToStorage = () => {
  localStorage.setItem(ALL_BOOKINGS_KEY, JSON.stringify(allBookings.value))
}

export function useAdminBookings() {
  initFromStorage()

  // Добавить запись (вызывается из useBookings)
  const addBookingToAll = (booking: Booking) => {
    allBookings.value.push(booking)
    saveToStorage()
  }

  // Удалить запись (админ отменяет)
  const cancelBookingAdmin = (bookingId: string) => {
    const index = allBookings.value.findIndex(b => b.id === bookingId)
    if (index !== -1) {
      allBookings.value.splice(index, 1)
      saveToStorage()
      return true
    }
    return false
  }

  // Получить все записи
  const getAllBookings = () => {
    return allBookings.value
  }

  // Получить записи по дате
  const getBookingsByDate = (date: string) => {
    return allBookings.value.filter(b => b.date === date)
  }

  // Очистить все записи
  const clearAllBookings = () => {
    allBookings.value = []
    saveToStorage()
  }

  return {
    allBookings,
    addBookingToAll,
    cancelBookingAdmin,
    getAllBookings,
    getBookingsByDate,
    clearAllBookings
  }
}
