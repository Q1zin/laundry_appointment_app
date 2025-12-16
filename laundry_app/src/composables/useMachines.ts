import { ref } from 'vue'

export interface Machine {
  id: number
  name: string
  isActive: boolean
}

const MACHINES_KEY = 'laundry_machines'

// Глобальное состояние
const machines = ref<Machine[]>([])
let initialized = false

// Инициализация из localStorage
const initFromStorage = () => {
  if (initialized) return
  
  try {
    const stored = localStorage.getItem(MACHINES_KEY)
    if (stored) {
      machines.value = JSON.parse(stored)
    } else {
      // Начальные машинки по умолчанию
      machines.value = [
        { id: 1, name: 'Машинка №1', isActive: true },
        { id: 2, name: 'Машинка №2', isActive: true },
        { id: 3, name: 'Машинка №3', isActive: true },
        { id: 4, name: 'Машинка №4', isActive: true }
      ]
      saveToStorage()
    }
    initialized = true
  } catch {
    // ignore
  }
}

// Сохранение в localStorage
const saveToStorage = () => {
  localStorage.setItem(MACHINES_KEY, JSON.stringify(machines.value))
}

export function useMachines() {
  initFromStorage()

  const addMachine = (name: string) => {
    const maxId = machines.value.reduce((max, m) => Math.max(max, m.id), 0)
    const newMachine: Machine = {
      id: maxId + 1,
      name,
      isActive: true
    }
    machines.value.push(newMachine)
    saveToStorage()
    return newMachine
  }

  const removeMachine = (id: number) => {
    const index = machines.value.findIndex(m => m.id === id)
    if (index !== -1) {
      machines.value.splice(index, 1)
      saveToStorage()
      return true
    }
    return false
  }

  const toggleMachine = (id: number) => {
    const machine = machines.value.find(m => m.id === id)
    if (machine) {
      machine.isActive = !machine.isActive
      saveToStorage()
      return true
    }
    return false
  }

  const updateMachineName = (id: number, name: string) => {
    const machine = machines.value.find(m => m.id === id)
    if (machine) {
      machine.name = name
      saveToStorage()
      return true
    }
    return false
  }

  const getActiveMachines = () => {
    return machines.value.filter(m => m.isActive)
  }

  return {
    machines,
    addMachine,
    removeMachine,
    toggleMachine,
    updateMachineName,
    getActiveMachines
  }
}
