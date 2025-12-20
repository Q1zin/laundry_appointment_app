<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { useToast } from '@/composables/useToast'
import { useMachines } from '@/composables/useMachines'
import { useSchedule, type Machine } from '@/composables/useSchedule'
import { useAdminBookings } from '@/composables/useAdminBookings'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import CalendarIcon from '@/components/icons/CalendarIcon.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'
import LockIcon from '@/components/icons/LockIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import TheHeader from '@/components/layout/TheHeader.vue'
import BookingModal from '@/components/modals/BookingModal.vue'
import { toLocalISODate } from '@/utils/date'

interface AdminUser {
  id: string
  name: string
  email: string
  fullName: string
  room: string
  contract: string
  role: string
  isBlocked: boolean
  createdAt: string
}

interface AdminBooking {
  id: string
  userId: string
  userName: string
  userFullName: string
  userRoom: string
  machineId: string
  machineName: string
  slotId: string
  slotStartTime: string
  slotEndTime: string
  state: string
  createdAt: string
}

interface AdminMachine {
  id: string
  name: string
  status: string
  createdAt: string
}

interface AdminSchedule {
  id: string
  date: string
  isOpen: boolean
  machineIds: string[]
  createdAt: string
}

const router = useRouter()
const { isLoggedIn, isAdmin, user } = useAuth()
const { confirm } = useToast()

// Редирект если не админ
onMounted(() => {
  if (!isLoggedIn.value || !isAdmin.value) {
    router.push('/')
  }
})

// Состояние
const activeTab = ref<'machines' | 'schedules' | 'users' | 'bookings' | 'calendar'>('machines')
const isLoading = ref(false)
const actionError = ref<string | null>(null)
const actionSuccess = ref<string | null>(null)

// Календарь
interface CalendarSlot {
  slotId: string
  startTime: string
  endTime: string
  isBooked: boolean
  booking?: AdminBooking
}

interface CalendarDay {
  date: string
  dayName: string
  dayNum: number
  machines: Map<string, CalendarSlot[]>
}

const calendarDays = ref<CalendarDay[]>([])

// Модальные окна
const isBookingModalOpen = ref(false)

// Машинки
const adminMachines = ref<AdminMachine[]>([])
const newMachineName = ref('')
const showAddMachineForm = ref(false)

// Расписания
const schedules = ref<AdminSchedule[]>([])
const showScheduleForm = ref(false)
const scheduleForm = ref({
  date: '',
  isOpen: true,
  machineIds: [] as string[],
  timeSlots: [] as string[]
})
const editingScheduleId = ref<string | null>(null)

// Доступные временные слоты
const availableTimeSlots = [
  '08:00-10:00',
  '10:00-12:00',
  '12:00-14:00',
  '14:00-16:00',
  '16:00-18:00',
  '18:00-20:00',
  '20:00-22:00'
]

// Пользователи
const users = ref<AdminUser[]>([])

// Все записи
const allBookings = ref<AdminBooking[]>([])

// Временные данные для UI
const { fetchSchedule } = useSchedule()
const machines = ref<Machine[]>([])
const selectedDate = ref<string>('')

// Машинки (админ)
const { blockMachine, unblockMachine } = useMachines()

// Бронирования (админ)
const { deleteBooking } = useAdminBookings()

// Инициализация
onMounted(async () => {
  if (!user.value?.id) return
  
  selectedDate.value = toLocalISODate(new Date())
  
  await loadAllData()
})

const loadAllData = async () => {
  isLoading.value = true
  await Promise.all([
    loadAdminMachines(),
    loadSchedules(),
    loadUsers(),
    loadAllBookings()
  ])
  await loadCalendarData()
  isLoading.value = false
}

// Загрузка данных календаря на 7 дней вперед
const loadCalendarData = async () => {
  const days: CalendarDay[] = []
  const dayNames = ['ВС', 'ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ']
  
  for (let i = 0; i < 7; i++) {
    const date = new Date()
    date.setDate(date.getDate() + i)
    const dateStr = toLocalISODate(date)
    
    const result = await fetchSchedule(dateStr, 'admin')
    
    if (result.success && result.data) {
      const machines = new Map<string, CalendarSlot[]>()
      
      // Группируем слоты по машинкам
      result.data.machines.forEach(machine => {
        const machineSlots: CalendarSlot[] = []
        
        result.data!.timeslots
          .filter(slot => slot.machineId === machine.id)
          .forEach(slot => {
            const booking = result.data!.bookings?.find(
              b => b.slotId === slot.slotId && b.state === 'active'
            )
            
            machineSlots.push({
              slotId: slot.slotId,
              startTime: slot.startTime,
              endTime: slot.endTime,
              isBooked: !!booking,
              booking: booking as AdminBooking | undefined
            })
          })
        
        // Сортируем по времени начала
        machineSlots.sort((a, b) => a.startTime.localeCompare(b.startTime))
        
        if (machineSlots.length > 0) {
          machines.set(machine.id, machineSlots)
        }
      })
      
      days.push({
        date: dateStr,
        dayName: dayNames[date.getDay()],
        dayNum: date.getDate(),
        machines
      })
    }
  }
  
  calendarDays.value = days
}

// Загрузка машинок
const loadAdminMachines = async () => {
  try {
    const response = await fetch('/api/admin/machines')
    if (response.ok) {
      adminMachines.value = await response.json()
    }
  } catch (err) {
    console.error('Failed to load machines:', err)
  }
}

// Загрузка расписаний
const loadSchedules = async () => {
  try {
    const response = await fetch('/api/admin/schedules')
    if (response.ok) {
      schedules.value = await response.json()
    }
  } catch (err) {
    console.error('Failed to load schedules:', err)
  }
}

// Загрузка пользователей
const loadUsers = async () => {
  try {
    const response = await fetch('/api/admin/users')
    if (response.ok) {
      users.value = await response.json()
    }
  } catch (err) {
    console.error('Failed to load users:', err)
  }
}

// Загрузка всех записей
const loadAllBookings = async () => {
  try {
    const response = await fetch('/api/admin/bookings')
    if (response.ok) {
      allBookings.value = await response.json()
    }
  } catch (err) {
    console.error('Failed to load bookings:', err)
  }
}

// Обработчики машинок
const handleBlockMachine = async (machineId: string) => {
  const confirmed = await confirm('Заблокировать эту машинку?')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  const result = await blockMachine(machineId)
  
  isLoading.value = false
  
  if (result.success) {
    actionSuccess.value = 'Машинка заблокирована'
    await loadAdminMachines()
  } else {
    actionError.value = result.message || 'Ошибка блокировки'
  }
}

const handleUnblockMachine = async (machineId: string) => {
  const confirmed = await confirm('Разблокировать эту машинку?')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  const result = await unblockMachine(machineId)
  
  isLoading.value = false
  
  if (result.success) {
    actionSuccess.value = 'Машинка разблокирована'
    await loadAdminMachines()
  } else {
    actionError.value = result.message || 'Ошибка разблокировки'
  }
}

// Добавление машинки
const handleAddMachine = async () => {
  if (!newMachineName.value.trim()) {
    actionError.value = 'Введите название машинки'
    return
  }
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  try {
    const response = await fetch('/api/admin/machines', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: newMachineName.value })
    })
    
    if (response.ok) {
      actionSuccess.value = 'Машинка добавлена'
      newMachineName.value = ''
      showAddMachineForm.value = false
      await loadAdminMachines()
    } else {
      actionError.value = 'Ошибка добавления машинки'
    }
  } catch (err) {
    actionError.value = 'Ошибка соединения'
  } finally {
    isLoading.value = false
  }
}

// Удаление машинки
const handleDeleteMachine = async (machineId: string) => {
  const confirmed = await confirm('Удалить эту машинку? Все связанные записи будут также удалены.')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  try {
    const response = await fetch(`/api/admin/machines/${machineId}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.result) {
      actionSuccess.value = 'Машинка удалена'
      await loadAdminMachines()
    } else {
      actionError.value = data.message || 'Ошибка удаления'
    }
  } catch (err) {
    actionError.value = 'Ошибка соединения'
  } finally {
    isLoading.value = false
  }
}

// Сегодняшняя дата для фильтрации
const today = computed(() => toLocalISODate(new Date()))

// Прошедшие расписания (до сегодня)
const pastSchedules = computed(() => {
  return [...schedules.value]
    .filter(s => s.date < today.value)
    .sort((a, b) => b.date.localeCompare(a.date)) // новые сверху
})

// Актуальные расписания (сегодня и будущее)
const upcomingSchedules = computed(() => {
  return [...schedules.value]
    .filter(s => s.date >= today.value)
    .sort((a, b) => a.date.localeCompare(b.date))
})

// Флаг для показа прошедших расписаний
const showPastSchedules = ref(false)

// Обработчики расписаний
const openScheduleForm = (schedule?: AdminSchedule) => {
  if (schedule) {
    editingScheduleId.value = schedule.id
    scheduleForm.value = {
      date: schedule.date,
      isOpen: schedule.isOpen,
      machineIds: [...schedule.machineIds],
      timeSlots: [] // TODO: загрузить сохраненные слоты
    }
  } else {
    editingScheduleId.value = null
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    scheduleForm.value = {
      date: toLocalISODate(tomorrow),
      isOpen: true,
      machineIds: [],
      timeSlots: [...availableTimeSlots] // По умолчанию все слоты
    }
  }
  showScheduleForm.value = true
  
  // Прокручиваем страницу вверх к форме
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const toggleMachineInSchedule = (machineId: string) => {
  const idx = scheduleForm.value.machineIds.indexOf(machineId)
  if (idx >= 0) {
    scheduleForm.value.machineIds.splice(idx, 1)
  } else {
    scheduleForm.value.machineIds.push(machineId)
  }
}

const toggleTimeSlotInSchedule = (timeSlot: string) => {
  const idx = scheduleForm.value.timeSlots.indexOf(timeSlot)
  if (idx >= 0) {
    scheduleForm.value.timeSlots.splice(idx, 1)
  } else {
    scheduleForm.value.timeSlots.push(timeSlot)
  }
}

const handleSaveSchedule = async () => {
  if (!scheduleForm.value.date) {
    actionError.value = 'Выберите дату'
    return
  }
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  try {
    const response = await fetch('/api/admin/schedules', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(scheduleForm.value)
    })
    
    if (response.ok) {
      actionSuccess.value = editingScheduleId.value ? 'Расписание обновлено' : 'Расписание создано'
      showScheduleForm.value = false
      await loadSchedules()
    } else {
      actionError.value = 'Ошибка сохранения расписания'
    }
  } catch (err) {
    actionError.value = 'Ошибка соединения'
  } finally {
    isLoading.value = false
  }
}

const handleDeleteSchedule = async (scheduleId: string) => {
  const confirmed = await confirm('Удалить это расписание?')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  try {
    const response = await fetch(`/api/admin/schedules/${scheduleId}`, {
      method: 'DELETE'
    })
    
    const data = await response.json()
    
    if (data.result) {
      actionSuccess.value = 'Расписание удалено'
      await loadSchedules()
    } else {
      actionError.value = data.message || 'Ошибка удаления'
    }
  } catch (err) {
    actionError.value = 'Ошибка соединения'
  } finally {
    isLoading.value = false
  }
}

// Получить название машинки по ID
const getMachineName = (machineId: string) => {
  const machine = adminMachines.value.find(m => m.id === machineId)
  return machine?.name || machineId
}

const handleDeleteBooking = async (bookingId: string) => {
  const confirmed = await confirm('Отменить эту запись?')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  const result = await deleteBooking(bookingId)
  
  isLoading.value = false
  
  if (result.success) {
    actionSuccess.value = 'Запись отменена'
    await loadSchedules()
    await loadAllBookings()
  } else {
    actionError.value = result.message || 'Ошибка отмены'
  }
}

// Форматирование времени
// Парсим напрямую чтобы избежать сдвига timezone
const formatTime = (isoString: string) => {
  if (!isoString) return ''
  // Если строка в формате "2025-12-17T10:00:00", парсим напрямую
  const timePart = isoString.split('T')[1]
  if (timePart) {
    const [hours, minutes] = timePart.split(':')
    return `${hours}:${minutes}`
  }
  // Fallback на Date если формат другой
  const date = new Date(isoString)
  return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

// Форматирование даты
// Парсим напрямую чтобы избежать сдвига timezone
const formatDate = (isoString: string) => {
  if (!isoString) return ''
  // Если строка в формате "2025-12-17T10:00:00", парсим напрямую
  const datePart = isoString.split('T')[0]
  if (datePart) {
    const [year, month, day] = datePart.split('-')
    return `${day}.${month}.${year}`
  }
  // Fallback на Date если формат другой
  const date = new Date(isoString)
  return date.toLocaleDateString('ru-RU')
}

// Получение статуса записи
const getBookingStatus = (state: string) => {
  switch (state) {
    case 'active': return 'Активна'
    case 'canceled': return 'Отменена'
    case 'deleted': return 'Удалена'
    default: return state
  }
}

// Блокировка пользователя
const handleBlockUser = async (userId: string) => {
  const confirmed = await confirm('Заблокировать этого пользователя?')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  try {
    const response = await fetch('/api/admin/users/block', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId })
    })
    
    const data = await response.json()
    
    if (data.result) {
      actionSuccess.value = 'Пользователь заблокирован'
      await loadUsers()
    } else {
      actionError.value = data.message || 'Ошибка блокировки'
    }
  } catch (err) {
    actionError.value = 'Ошибка соединения'
  } finally {
    isLoading.value = false
  }
}

// Разблокировка пользователя
const handleUnblockUser = async (userId: string) => {
  const confirmed = await confirm('Разблокировать этого пользователя?')
  if (!confirmed) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  try {
    const response = await fetch('/api/admin/users/unblock', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId })
    })
    
    const data = await response.json()
    
    if (data.result) {
      actionSuccess.value = 'Пользователь разблокирован'
      await loadUsers()
    } else {
      actionError.value = data.message || 'Ошибка разблокировки'
    }
  } catch (err) {
    actionError.value = 'Ошибка соединения'
  } finally {
    isLoading.value = false
  }
}

// Обработчики модальных окон
const openBookingModal = () => {
  isBookingModalOpen.value = true
}

const closeBookingModal = () => {
  isBookingModalOpen.value = false
}

const scrollToRules = () => {
  // На странице админки нет раздела правил, просто игнорируем
}
</script>

<template>
  <div class="admin-page" v-if="isLoggedIn && isAdmin">
    <TheHeader 
      variant="dark"
      @booking-click="openBookingModal"
      @rules-click="scrollToRules"
    />

    <main class="admin-content">
      <div class="container">
        <h1 class="page-title">АДМИН-ПАНЕЛЬ</h1>

        <!-- Success/Error Messages -->
        <div v-if="actionSuccess" class="success-message">
          {{ actionSuccess }}
        </div>
        <div v-if="actionError" class="error-message">
          {{ actionError }}
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state">
          <p>Загрузка...</p>
        </div>

        <!-- Tabs -->
        <div class="tabs" v-if="!isLoading">
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'machines' }"
            @click="activeTab = 'machines'"
          >
            <WashingMachineOutlineIcon :size="20" :color="activeTab === 'machines' ? '#fff' : '#3D4F61'" />
            Машинки
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'schedules' }"
            @click="activeTab = 'schedules'"
          >
            <CalendarIcon :size="20" :color="activeTab === 'schedules' ? '#fff' : '#3D4F61'" />
            Расписание
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'users' }"
            @click="activeTab = 'users'"
          >
            <UserIcon :size="20" :color="activeTab === 'users' ? '#fff' : '#3D4F61'" />
            Пользователи
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'bookings' }"
            @click="activeTab = 'bookings'"
          >
            <EditIcon :size="20" :color="activeTab === 'bookings' ? '#fff' : '#3D4F61'" />
            Записи
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'calendar' }"
            @click="activeTab = 'calendar'"
          >
            <CalendarIcon :size="20" :color="activeTab === 'calendar' ? '#fff' : '#3D4F61'" />
            Календарь
          </button>
        </div>

        <!-- Machines Management -->
        <section v-if="!isLoading && activeTab === 'machines'" class="section">
          <div class="section-header">
            <h2 class="section-title">УПРАВЛЕНИЕ МАШИНКАМИ</h2>
            <button class="add-btn" @click="showAddMachineForm = true">
              + Добавить машинку
            </button>
          </div>

          <!-- Add Machine Form -->
          <div v-if="showAddMachineForm" class="form-card">
            <h3 class="form-title">Новая машинка</h3>
            <div class="form-row">
              <input 
                v-model="newMachineName"
                type="text"
                placeholder="Название машинки"
                class="form-input"
              />
            </div>
            <div class="form-actions">
              <button class="btn-secondary" @click="showAddMachineForm = false">Отмена</button>
              <button class="btn-primary" @click="handleAddMachine">Добавить</button>
            </div>
          </div>
          
          <div v-if="adminMachines.length === 0" class="no-data">
            Нет машинок
          </div>

          <div v-else class="machines-list">
            <div v-for="machine in adminMachines" :key="machine.id" class="machine-card">
              <div class="machine-icon">
                <WashingMachineOutlineIcon :size="50" color="#3D4F61" />
              </div>
              <div class="machine-info">
                <h3 class="machine-name">{{ machine.name }}</h3>
                <span 
                  class="machine-status"
                  :class="machine.status"
                >
                  {{ machine.status === 'available' ? 'Доступна' : 'Заблокирована' }}
                </span>
              </div>
              <div class="machine-actions">
                <button 
                  v-if="machine.status === 'available'"
                  class="action-btn block-btn"
                  @click="handleBlockMachine(machine.id)"
                >
                  Заблокировать
                </button>
                <button 
                  v-else
                  class="action-btn unblock-btn"
                  @click="handleUnblockMachine(machine.id)"
                >
                  Разблокировать
                </button>
                <button 
                  class="action-btn delete-btn"
                  @click="handleDeleteMachine(machine.id)"
                >
                  <TrashIcon :size="16" color="white" />
                  Удалить
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Schedules Management -->
        <section v-if="!isLoading && activeTab === 'schedules'" class="section">
          <div class="section-header">
            <h2 class="section-title">УПРАВЛЕНИЕ РАСПИСАНИЕМ</h2>
            <button class="add-btn" @click="openScheduleForm()">
              + Добавить расписание
            </button>
          </div>

          <!-- Schedule Form Modal -->
          <div v-if="showScheduleForm" class="form-card">
            <h3 class="form-title">{{ editingScheduleId ? 'Редактировать расписание' : 'Новое расписание' }}</h3>
            <div class="form-row">
              <label class="form-label">Дата:</label>
              <input 
                v-model="scheduleForm.date"
                type="date"
                class="form-input"
              />
            </div>
            <div class="form-row checkbox-row">
              <label class="checkbox-label">
                <input 
                  v-model="scheduleForm.isOpen"
                  type="checkbox"
                />
                Записи разрешены
              </label>
            </div>
            <div class="form-row">
              <label class="form-label">Машинки для этой даты:</label>
              <div class="machines-checkboxes">
                <label 
                  v-for="machine in adminMachines" 
                  :key="machine.id"
                  class="machine-checkbox"
                >
                  <input 
                    type="checkbox"
                    :checked="scheduleForm.machineIds.includes(machine.id)"
                    @change="toggleMachineInSchedule(machine.id)"
                  />
                  {{ machine.name }}
                </label>
              </div>
            </div>
            <div class="form-row">
              <label class="form-label">Доступное время для записи:</label>
              <div class="timeslots-checkboxes">
                <label 
                  v-for="timeSlot in availableTimeSlots" 
                  :key="timeSlot"
                  class="timeslot-checkbox"
                >
                  <input 
                    type="checkbox"
                    :checked="scheduleForm.timeSlots.includes(timeSlot)"
                    @change="toggleTimeSlotInSchedule(timeSlot)"
                  />
                  {{ timeSlot }}
                </label>
              </div>
            </div>
            <div class="form-actions">
              <button class="btn-secondary" @click="showScheduleForm = false">Отмена</button>
              <button class="btn-primary" @click="handleSaveSchedule">Сохранить</button>
            </div>
          </div>

          <div v-if="schedules.length === 0" class="no-data">
            Нет настроенных расписаний. По умолчанию все дни открыты для записи.
          </div>

          <template v-else>
            <!-- Прошедшие расписания (свёрнутые) -->
            <div v-if="pastSchedules.length > 0" class="past-schedules-section">
              <button 
                class="past-schedules-toggle"
                @click="showPastSchedules = !showPastSchedules"
              >
                <span class="toggle-icon">{{ showPastSchedules ? '▼' : '▶' }}</span>
                Прошедшие даты ({{ pastSchedules.length }})
              </button>
              
              <div v-if="showPastSchedules" class="schedules-list past-list">
                <div v-for="schedule in pastSchedules" :key="schedule.id" class="schedule-card past">
                  <div class="schedule-icon">
                    <CalendarIcon :size="40" color="#9CA3AF" />
                  </div>
                  <div class="schedule-info">
                    <h3 class="schedule-date">{{ formatDate(schedule.date) }}</h3>
                    <span 
                      class="schedule-status"
                      :class="{ open: schedule.isOpen, closed: !schedule.isOpen }"
                    >
                      {{ schedule.isOpen ? 'Записи открыты' : 'Записи закрыты' }}
                    </span>
                    <div class="schedule-machines" v-if="schedule.machineIds.length > 0">
                      <span class="schedule-machines-label">Машинки:</span>
                      <span 
                        v-for="machineId in schedule.machineIds" 
                        :key="machineId"
                        class="schedule-machine-tag"
                      >
                        {{ getMachineName(machineId) }}
                      </span>
                    </div>
                  </div>
                  <div class="schedule-actions">
                    <button 
                      class="action-btn delete-btn"
                      @click="handleDeleteSchedule(schedule.id)"
                    >
                      <TrashIcon :size="16" color="white" />
                      Удалить
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Актуальные расписания (сегодня и будущее) -->
            <div v-if="upcomingSchedules.length === 0" class="no-data">
              Нет актуальных расписаний на ближайшие дни.
            </div>
            
            <div v-else class="schedules-list">
              <div v-for="schedule in upcomingSchedules" :key="schedule.id" class="schedule-card">
                <div class="schedule-icon">
                  <CalendarIcon :size="40" color="#3D4F61" />
                </div>
                <div class="schedule-info">
                  <h3 class="schedule-date">{{ formatDate(schedule.date) }}</h3>
                  <span 
                    class="schedule-status"
                    :class="{ open: schedule.isOpen, closed: !schedule.isOpen }"
                  >
                    {{ schedule.isOpen ? 'Записи открыты' : 'Записи закрыты' }}
                  </span>
                  <div class="schedule-machines" v-if="schedule.machineIds.length > 0">
                    <span class="schedule-machines-label">Машинки:</span>
                    <span 
                      v-for="machineId in schedule.machineIds" 
                      :key="machineId"
                      class="schedule-machine-tag"
                    >
                      {{ getMachineName(machineId) }}
                    </span>
                  </div>
                  <div v-else class="schedule-machines">
                    <span class="schedule-machines-label">Все машинки недоступны</span>
                  </div>
                </div>
                <div class="schedule-actions">
                  <button 
                    class="action-btn edit-btn"
                    @click="openScheduleForm(schedule)"
                  >
                    <EditIcon :size="16" color="white" />
                    Изменить
                  </button>
                  <button 
                    class="action-btn delete-btn"
                    @click="handleDeleteSchedule(schedule.id)"
                  >
                    <TrashIcon :size="16" color="white" />
                    Удалить
                  </button>
                </div>
              </div>
            </div>
          </template>
        </section>

        <!-- Users Management -->
        <section v-if="!isLoading && activeTab === 'users'" class="section">
          <h2 class="section-title">УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ</h2>
          
          <div v-if="users.length === 0" class="no-data">
            Нет пользователей
          </div>

          <div v-else class="users-list">
            <div v-for="u in users" :key="u.id" class="user-card">
              <div class="user-icon">
                <UserIcon :size="40" color="#3D4F61" />
              </div>
              <div class="user-info">
                <h3 class="user-name">{{ u.fullName || u.name }}</h3>
                <div class="user-details">
                  <span class="user-detail">Логин: {{ u.name }}</span>
                  <span class="user-detail" v-if="u.email">Email: {{ u.email }}</span>
                  <span class="user-detail" v-if="u.room">Комната: {{ u.room }}</span>
                  <span class="user-detail" v-if="u.contract">Договор: {{ u.contract }}</span>
                </div>
                <div class="user-meta">
                  <span 
                    class="user-role"
                    :class="u.role"
                  >
                    {{ u.role === 'admin' ? 'Админ' : 'Пользователь' }}
                  </span>
                  <span 
                    class="user-status"
                    :class="{ blocked: u.isBlocked }"
                  >
                    {{ u.isBlocked ? 'Заблокирован' : 'Активен' }}
                  </span>
                </div>
              </div>
              <div class="user-actions" v-if="u.role !== 'admin'">
                <button 
                  v-if="!u.isBlocked"
                  class="action-btn block-btn"
                  @click="handleBlockUser(u.id)"
                >
                  <LockIcon :size="16" color="white" />
                  Заблокировать
                </button>
                <button 
                  v-else
                  class="action-btn unblock-btn"
                  @click="handleUnblockUser(u.id)"
                >
                  Разблокировать
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Calendar View -->
        <section v-if="!isLoading && activeTab === 'calendar'" class="section calendar-section">
          <h2 class="section-title">КАЛЕНДАРЬ ЗАПИСЕЙ</h2>
          
          <div v-if="calendarDays.length === 0" class="no-data">
            Нет данных расписания
          </div>
          
          <div v-else class="calendar-grid">
            <div 
              v-for="day in calendarDays" 
              :key="day.date"
              class="calendar-day"
            >
              <div class="day-header">
                <span class="day-name">{{ day.dayName }}</span>
                <span class="day-number">{{ day.dayNum }}</span>
              </div>
              
              <div class="day-machines">
                <div 
                  v-for="[machineId, slots] of day.machines"
                  :key="machineId"
                  class="machine-slots"
                >
                  <div class="machine-label">
                    {{ adminMachines.find(m => m.id === machineId)?.name || 'Машинка' }}
                  </div>
                  
                  <div class="slots-list">
                    <div 
                      v-for="slot in slots"
                      :key="slot.slotId"
                      class="slot-item"
                      :class="{ booked: slot.isBooked, free: !slot.isBooked }"
                      :title="slot.isBooked ? `Занято: ${slot.booking?.userFullName || slot.booking?.userName}` : 'Свободно'"
                    >
                      <span class="slot-time">{{ formatTime(slot.startTime) }}</span>
                      <span v-if="slot.isBooked" class="slot-user">{{ slot.booking?.userFullName || slot.booking?.userName }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Bookings Management -->
        <section v-if="!isLoading && activeTab === 'bookings'" class="section">
          <h2 class="section-title">ВСЕ ЗАПИСИ</h2>
          
          <div v-if="allBookings.length === 0" class="no-data">
            Нет записей
          </div>

          <div v-else class="bookings-list">
            <div 
              v-for="booking in allBookings" 
              :key="booking.id" 
              class="booking-card"
              :class="{ 'booking-inactive': booking.state !== 'active' }"
            >
              <div class="booking-icon">
                <WashingMachineOutlineIcon :size="40" color="#3D4F61" />
              </div>
              <div class="booking-info">
                <h3 class="booking-machine">{{ booking.machineName || `Машинка #${booking.machineId}` }}</h3>
                <div class="booking-details">
                  <span class="booking-detail">
                    <CalendarIcon :size="14" color="#6B7280" />
                    {{ formatDate(booking.slotStartTime) }} {{ formatTime(booking.slotStartTime) }} - {{ formatTime(booking.slotEndTime) }}
                  </span>
                </div>
                <div class="booking-user">
                  <UserIcon :size="14" color="#6B7280" />
                  <span>{{ booking.userFullName || booking.userName }}</span>
                  <span v-if="booking.userRoom" class="booking-room">Комната: {{ booking.userRoom }}</span>
                </div>
                <div class="booking-meta">
                  <span 
                    class="booking-status"
                    :class="booking.state"
                  >
                    {{ getBookingStatus(booking.state) }}
                  </span>
                  <span class="booking-created">Создано: {{ new Date(booking.createdAt).toLocaleString('ru-RU') }}</span>
                </div>
              </div>
              <div class="booking-actions" v-if="booking.state === 'active'">
                <button 
                  class="action-btn block-btn"
                  @click="handleDeleteBooking(booking.id)"
                >
                  <TrashIcon :size="16" color="white" />
                  Отменить
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Back Link -->
        <div class="back-section">
          <RouterLink to="/" class="back-link">← Вернуться на главную</RouterLink>
        </div>
      </div>
    </main>

    <BookingModal 
      :is-open="isBookingModalOpen" 
      @close="closeBookingModal" 
    />
  </div>
</template>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #E8EEF2;
}

.admin-content {
  padding: 40px 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: #3D4F61;
  margin-bottom: 30px;
  text-align: center;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #3D4F61;
  margin-bottom: 20px;
}

.success-message,
.error-message {
  padding: 15px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  text-align: center;
}

.success-message {
  background: rgba(34, 197, 94, 0.1);
  border: 2px solid rgba(34, 197, 94, 0.3);
  color: #22C55E;
}

.error-message {
  background: rgba(239, 68, 68, 0.1);
  border: 2px solid rgba(239, 68, 68, 0.3);
  color: #EF4444;
}

.loading-state {
  padding: 60px;
  text-align: center;
  color: #6B8DB8;
  font-size: 18px;
}

.no-data {
  padding: 40px;
  text-align: center;
  color: #9CA3AF;
  font-size: 16px;
}

.machines-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.machine-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.machine-icon {
  flex-shrink: 0;
}

.machine-info {
  flex: 1;
}

.machine-name {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
  margin: 0 0 8px 0;
}

.machine-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.machine-status.available {
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.machine-status.blocked {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.machine-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.block-btn {
  background: #EF4444;
  color: white;
}

.block-btn:hover {
  background: #DC2626;
}

.unblock-btn {
  background: #22C55E;
  color: white;
}

.unblock-btn:hover {
  background: #16A34A;
}

.back-section {
  margin-top: 40px;
  text-align: center;
}

.back-link {
  color: #6B8DB8;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.back-link:hover {
  color: #3D4F61;
}

/* Tabs */
.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #FFFFFF;
  color: #9CA3AF;
}

.tab-btn.active {
  background: #3D4F61;
  color: white;
}

.tab-btn:hover:not(.active) {
  background: #F3F4F6;
}

/* Users */
.users-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.user-icon {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #E8EEF2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
  margin: 0 0 8px 0;
}

.user-details {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 8px;
}

.user-detail {
  font-size: 14px;
  color: #6B7280;
}

.user-meta {
  display: flex;
  gap: 10px;
}

.user-role {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.user-role.admin {
  background: rgba(139, 92, 246, 0.1);
  color: #8B5CF6;
}

.user-role.user {
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
}

.user-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.user-status.blocked {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.user-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Bookings */
.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.booking-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.booking-card.booking-inactive {
  opacity: 0.6;
}

.booking-icon {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #E8EEF2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.booking-info {
  flex: 1;
}

.booking-machine {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
  margin: 0 0 8px 0;
}

.booking-details {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 6px;
}

.booking-detail {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #6B7280;
}

.booking-user {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6B7280;
  margin-bottom: 6px;
}

.booking-room {
  color: #9CA3AF;
  margin-left: 10px;
}

.booking-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.booking-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.booking-status.active {
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.booking-status.canceled,
.booking-status.deleted {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.booking-created {
  font-size: 12px;
  color: #9CA3AF;
}

.booking-actions {
  display: flex;
  gap: 10px;
}

/* Section Header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.add-btn {
  padding: 10px 20px;
  background: #3B82F6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-btn:hover {
  background: #2563EB;
}

/* Form Card */
.form-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.form-title {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
  margin: 0 0 20px 0;
}

.form-row {
  margin-bottom: 15px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #6B7280;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #E5E7EB;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #3B82F6;
}

.checkbox-row {
  margin-top: 10px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #3D4F61;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.machines-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.machine-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  background: #F3F4F6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.machine-checkbox:hover {
  background: #E5E7EB;
}

.machine-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.timeslots-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.timeslot-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #EFF6FF;
  border: 1px solid #BFDBFE;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #1E40AF;
}

.timeslot-checkbox:hover {
  background: #DBEAFE;
  border-color: #93C5FD;
}

.timeslot-checkbox input[type="checkbox"] {
  width: 14px;
  height: 14px;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn-primary {
  padding: 12px 24px;
  background: #3B82F6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background: #2563EB;
}

.btn-secondary {
  padding: 12px 24px;
  background: #E5E7EB;
  color: #6B7280;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: #D1D5DB;
}

/* Delete Button */
.delete-btn {
  background: #DC2626;
  color: white;
}

.delete-btn:hover {
  background: #B91C1C;
}

/* Edit Button */
.edit-btn {
  background: #3B82F6;
  color: white;
}

.edit-btn:hover {
  background: #2563EB;
}

/* Past Schedules Toggle */
.past-schedules-section {
  margin-bottom: 20px;
}

.past-schedules-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 15px 20px;
  background: #F3F4F6;
  border: 2px solid #E5E7EB;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #6B7280;
  cursor: pointer;
  transition: all 0.3s ease;
}

.past-schedules-toggle:hover {
  background: #E5E7EB;
  border-color: #D1D5DB;
}

.toggle-icon {
  font-size: 12px;
  transition: transform 0.2s ease;
}

.past-list {
  margin-top: 15px;
  padding-left: 20px;
  border-left: 3px solid #E5E7EB;
}

.schedule-card.past {
  opacity: 0.7;
  background: #F9FAFB;
}

.schedule-card.past .schedule-date {
  color: #6B7280;
}

/* Schedules */
.schedules-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.schedule-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.schedule-icon {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #E8EEF2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.schedule-info {
  flex: 1;
}

.schedule-date {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
  margin: 0 0 8px 0;
}

.schedule-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 10px;
}

.schedule-status.open {
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.schedule-status.closed {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.schedule-machines {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.schedule-machines-label {
  font-size: 14px;
  color: #6B7280;
}

.schedule-machine-tag {
  display: inline-block;
  padding: 4px 10px;
  background: #E8EEF2;
  border-radius: 6px;
  font-size: 13px;
  color: #3D4F61;
}

.schedule-actions {
  display: flex;
  gap: 10px;
}

/* Machine actions flex direction */
.machine-actions {
  flex-direction: column;
}

/* Calendar Section */
.calendar-section {
  padding: 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.calendar-day {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.calendar-day:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.day-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 15px;
  margin-bottom: 15px;
  border-bottom: 2px solid #E8EEF2;
}

.day-name {
  font-size: 14px;
  font-weight: 600;
  color: #6B7280;
  text-transform: uppercase;
}

.day-number {
  font-size: 28px;
  font-weight: bold;
  color: #3D4F61;
  margin-top: 5px;
}

.day-machines {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.machine-slots {
  background: #F9FAFB;
  border-radius: 10px;
  padding: 10px;
}

.machine-label {
  font-size: 13px;
  font-weight: 600;
  color: #3D4F61;
  margin-bottom: 8px;
  padding: 0 5px;
}

.slots-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.slot-item {
  display: flex;
  flex-direction: column;
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 12px;
  transition: all 0.2s;
  cursor: pointer;
}

.slot-item.free {
  background: #D1FAE5;
  border: 1px solid #A7F3D0;
  color: #065F46;
}

.slot-item.free:hover {
  background: #A7F3D0;
  border-color: #6EE7B7;
}

.slot-item.booked {
  background: #FEE2E2;
  border: 1px solid #FECACA;
  color: #991B1B;
}

.slot-item.booked:hover {
  background: #FECACA;
  border-color: #FCA5A5;
}

.slot-time {
  font-weight: 600;
  font-size: 11px;
}

.slot-user {
  font-size: 10px;
  margin-top: 3px;
  opacity: 0.8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 1200px) {
  .calendar-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .calendar-grid {
    grid-template-columns: 1fr;
  }
}
</style>
