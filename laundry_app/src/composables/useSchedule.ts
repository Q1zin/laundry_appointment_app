import { ref, computed } from 'vue'

export interface ScheduleSlot {
  id: string
  date: string // формат YYYY-MM-DD
  timeSlot: string
  machineId: number
  isBlocked: boolean
  reason?: string
}

const SCHEDULE_KEY = 'laundry_schedule'

// Глобальное состояние
const blockedSlots = ref<ScheduleSlot[]>([])
let initialized = false

// Инициализация из localStorage
const initFromStorage = () => {
  if (initialized) return
  
  try {
    const stored = localStorage.getItem(SCHEDULE_KEY)
    if (stored) {
      blockedSlots.value = JSON.parse(stored)
    }
    initialized = true
  } catch {
    // ignore
  }
}

// Сохранение в localStorage
const saveToStorage = () => {
  localStorage.setItem(SCHEDULE_KEY, JSON.stringify(blockedSlots.value))
}

// Генерация ID
const generateId = () => {
  return 'slot_' + Math.random().toString(36).substring(2) + Date.now().toString(36)
}

export function useSchedule() {
  initFromStorage()

  // Заблокировать слот
  const blockSlot = (date: string, timeSlot: string, machineId: number, reason?: string) => {
    const existingIndex = blockedSlots.value.findIndex(
      s => s.date === date && s.timeSlot === timeSlot && s.machineId === machineId
    )
    
    if (existingIndex === -1) {
      const newSlot: ScheduleSlot = {
        id: generateId(),
        date,
        timeSlot,
        machineId,
        isBlocked: true,
        reason
      }
      blockedSlots.value.push(newSlot)
      saveToStorage()
      return newSlot
    }
    return null
  }

  // Разблокировать слот
  const unblockSlot = (id: string) => {
    const index = blockedSlots.value.findIndex(s => s.id === id)
    if (index !== -1) {
      blockedSlots.value.splice(index, 1)
      saveToStorage()
      return true
    }
    return false
  }

  // Проверить, заблокирован ли слот
  const isSlotBlocked = (date: string, timeSlot: string, machineId: number) => {
    return blockedSlots.value.some(
      s => s.date === date && s.timeSlot === timeSlot && s.machineId === machineId && s.isBlocked
    )
  }

  // Заблокировать всю дату для всех машинок
  const blockDate = (date: string, machineIds: number[], reason?: string) => {
    const timeSlots = ['9:45 - 11:45', '14:00 - 16:00', '18:00 - 20:00', '21:00 - 23:00']
    
    machineIds.forEach(machineId => {
      timeSlots.forEach(timeSlot => {
        blockSlot(date, timeSlot, machineId, reason)
      })
    })
    saveToStorage()
  }

  // Разблокировать всю дату
  const unblockDate = (date: string) => {
    blockedSlots.value = blockedSlots.value.filter(s => s.date !== date)
    saveToStorage()
  }

  // Получить все заблокированные слоты для даты
  const getBlockedSlotsForDate = (date: string) => {
    return blockedSlots.value.filter(s => s.date === date)
  }

  return {
    blockedSlots,
    blockSlot,
    unblockSlot,
    isSlotBlocked,
    blockDate,
    unblockDate,
    getBlockedSlotsForDate
  }
}
