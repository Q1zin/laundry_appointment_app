<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMachines } from '@/composables/useMachines'
import { useSchedule } from '@/composables/useSchedule'
import type { Booking } from '@/composables/useBookings'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import CalendarIcon from '@/components/icons/CalendarIcon.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const ALL_BOOKINGS_KEY = 'all_bookings'

// Состояние
const activeTab = ref<'machines' | 'schedule' | 'bookings'>('machines')
const allBookings = ref<Booking[]>([])

// Машинки
const { machines, addMachine, removeMachine, toggleMachine, updateMachineName } = useMachines()
const newMachineName = ref('')

// Расписание
const { blockedSlots, blockSlot, unblockSlot, blockDate, unblockDate } = useSchedule()
const selectedDate = ref('')
const selectedTimeSlot = ref('')
const selectedMachineId = ref<number | null>(null)
const blockReason = ref('')

// Загрузка всех записей
const loadAllBookings = () => {
  try {
    const stored = localStorage.getItem(ALL_BOOKINGS_KEY)
    if (stored) {
      allBookings.value = JSON.parse(stored)
    }
  } catch {
    // ignore
  }
}

onMounted(() => {
  loadAllBookings()
})

// Форматирование даты
const formatDate = (offset: number) => {
  const date = new Date()
  date.setDate(date.getDate() + offset)
  return date.toISOString().split('T')[0]
}

// Генерация дат на неделю
const weekDates = computed(() => {
  const dates = []
  for (let i = 0; i < 14; i++) {
    const date = new Date()
    date.setDate(date.getDate() + i)
    dates.push({
      value: date.toISOString().split('T')[0],
      label: date.toLocaleDateString('ru-RU', { weekday: 'short', day: 'numeric', month: 'short' })
    })
  }
  return dates
})

const timeSlots = ['9:45 - 11:45', '14:00 - 16:00', '18:00 - 20:00', '21:00 - 23:00']

// Добавить машинку
const handleAddMachine = () => {
  if (newMachineName.value.trim()) {
    addMachine(newMachineName.value.trim())
    newMachineName.value = ''
  }
}

// Удалить машинку
const handleRemoveMachine = (id: number) => {
  if (confirm('Удалить эту машинку?')) {
    removeMachine(id)
  }
}

// Заблокировать слот
const handleBlockSlot = () => {
  if (selectedDate.value && selectedTimeSlot.value && selectedMachineId.value) {
    blockSlot(selectedDate.value, selectedTimeSlot.value, selectedMachineId.value, blockReason.value)
    blockReason.value = ''
  }
}

// Заблокировать всю дату для всех машинок
const handleBlockFullDate = () => {
  if (selectedDate.value) {
    const machineIds = machines.value.map(m => m.id)
    blockDate(selectedDate.value, machineIds, blockReason.value)
    blockReason.value = ''
  }
}

// Отменить запись (админ)
const handleCancelBooking = (bookingId: string) => {
  if (confirm('Отменить эту запись?')) {
    const index = allBookings.value.findIndex(b => b.id === bookingId)
    if (index !== -1) {
      allBookings.value.splice(index, 1)
      localStorage.setItem(ALL_BOOKINGS_KEY, JSON.stringify(allBookings.value))
      
      // Также удаляем из user_bookings
      try {
        const userBookingsStr = localStorage.getItem('user_bookings')
        if (userBookingsStr) {
          const userBookings = JSON.parse(userBookingsStr)
          const userIndex = userBookings.findIndex((b: Booking) => b.id === bookingId)
          if (userIndex !== -1) {
            userBookings.splice(userIndex, 1)
            localStorage.setItem('user_bookings', JSON.stringify(userBookings))
          }
        }
      } catch {
        // ignore
      }
    }
  }
}

// Проверить, заблокирован ли слот
const isSlotBlocked = (date: string, timeSlot: string, machineId: number) => {
  return blockedSlots.value.some(
    s => s.date === date && s.timeSlot === timeSlot && s.machineId === machineId
  )
}
</script>

<template>
  <div class="admin-page">
    <header class="admin-header">
      <h1>АДМИН-ПАНЕЛЬ</h1>
      <RouterLink to="/" class="back-link">← На главную</RouterLink>
    </header>

    <!-- Табы -->
    <div class="tabs">
      <button 
        class="tab" 
        :class="{ active: activeTab === 'machines' }"
        @click="activeTab = 'machines'"
      >
        <WashingMachineOutlineIcon :size="20" />
        Машинки
      </button>
      <button 
        class="tab" 
        :class="{ active: activeTab === 'schedule' }"
        @click="activeTab = 'schedule'"
      >
        <CalendarIcon :size="20" />
        Расписание
      </button>
      <button 
        class="tab" 
        :class="{ active: activeTab === 'bookings' }"
        @click="activeTab = 'bookings'; loadAllBookings()"
      >
        📋 Записи
      </button>
    </div>

    <main class="admin-content">
      <!-- Управление машинками -->
      <section v-if="activeTab === 'machines'" class="section">
        <h2>Управление машинками</h2>
        
        <div class="add-machine-form">
          <input 
            v-model="newMachineName" 
            type="text" 
            placeholder="Название новой машинки"
            @keyup.enter="handleAddMachine"
          />
          <button class="btn primary" @click="handleAddMachine">Добавить</button>
        </div>

        <div class="machines-list">
          <div 
            v-for="machine in machines" 
            :key="machine.id" 
            class="machine-card"
            :class="{ inactive: !machine.isActive }"
          >
            <div class="machine-info">
              <WashingMachineOutlineIcon :size="32" :color="machine.isActive ? '#3D4F61' : '#9CA3AF'" />
              <span class="machine-name">{{ machine.name }}</span>
              <span v-if="!machine.isActive" class="inactive-badge">Неактивна</span>
            </div>
            <div class="machine-actions">
              <button 
                class="btn small" 
                :class="machine.isActive ? 'warning' : 'success'"
                @click="toggleMachine(machine.id)"
              >
                {{ machine.isActive ? 'Отключить' : 'Включить' }}
              </button>
              <button 
                class="btn small danger" 
                @click="handleRemoveMachine(machine.id)"
              >
                <TrashIcon :size="16" color="#fff" />
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Управление расписанием -->
      <section v-if="activeTab === 'schedule'" class="section">
        <h2>Управление расписанием</h2>
        
        <div class="schedule-form">
          <div class="form-row">
            <label>Дата:</label>
            <select v-model="selectedDate">
              <option value="">Выберите дату</option>
              <option v-for="date in weekDates" :key="date.value" :value="date.value">
                {{ date.label }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <label>Время:</label>
            <select v-model="selectedTimeSlot">
              <option value="">Все слоты</option>
              <option v-for="slot in timeSlots" :key="slot" :value="slot">
                {{ slot }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <label>Машинка:</label>
            <select v-model="selectedMachineId">
              <option :value="null">Все машинки</option>
              <option v-for="machine in machines" :key="machine.id" :value="machine.id">
                {{ machine.name }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <label>Причина:</label>
            <input v-model="blockReason" type="text" placeholder="Причина блокировки (опционально)" />
          </div>

          <div class="form-actions">
            <button 
              class="btn warning" 
              @click="handleBlockSlot"
              :disabled="!selectedDate || !selectedTimeSlot || !selectedMachineId"
            >
              Заблокировать слот
            </button>
            <button 
              class="btn danger" 
              @click="handleBlockFullDate"
              :disabled="!selectedDate"
            >
              Заблокировать весь день
            </button>
          </div>
        </div>

        <!-- Список заблокированных слотов -->
        <div class="blocked-slots">
          <h3>Заблокированные слоты</h3>
          <div v-if="blockedSlots.length === 0" class="empty-message">
            Нет заблокированных слотов
          </div>
          <div v-else class="slots-list">
            <div v-for="slot in blockedSlots" :key="slot.id" class="slot-card">
              <div class="slot-info">
                <span class="slot-date">{{ slot.date }}</span>
                <span class="slot-time">{{ slot.timeSlot }}</span>
                <span class="slot-machine">Машинка #{{ slot.machineId }}</span>
                <span v-if="slot.reason" class="slot-reason">{{ slot.reason }}</span>
              </div>
              <button class="btn small success" @click="unblockSlot(slot.id)">
                Разблокировать
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Записи пользователей -->
      <section v-if="activeTab === 'bookings'" class="section">
        <h2>Записи пользователей</h2>
        
        <div v-if="allBookings.length === 0" class="empty-message">
          Нет активных записей
        </div>

        <div v-else class="bookings-list">
          <div v-for="booking in allBookings" :key="booking.id" class="booking-card">
            <div class="booking-info">
              <div class="booking-header">
                <WashingMachineOutlineIcon :size="24" color="#3D4F61" />
                <span class="booking-machine">Машинка №{{ booking.machine }}</span>
              </div>
              <div class="booking-details">
                <div><strong>Дата:</strong> {{ booking.dayName }}, {{ booking.dayNumber }}</div>
                <div><strong>Время:</strong> {{ booking.time }}</div>
                <div><strong>ФИО:</strong> {{ booking.fullName }}</div>
                <div><strong>Комната:</strong> {{ booking.room }}</div>
                <div v-if="booking.userEmail"><strong>Email:</strong> {{ booking.userEmail }}</div>
              </div>
            </div>
            <button class="btn danger" @click="handleCancelBooking(booking.id)">
              Отменить
            </button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: #F5F7FA;
}

.admin-header {
  background: #3D4F61;
  color: white;
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h1 {
  font-size: 24px;
  margin: 0;
}

.back-link {
  color: white;
  text-decoration: none;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.back-link:hover {
  opacity: 1;
}

.tabs {
  display: flex;
  background: white;
  border-bottom: 1px solid #E5E7EB;
  padding: 0 40px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  color: #6B7280;
  transition: all 0.2s;
}

.tab:hover {
  color: #3D4F61;
  background: #F9FAFB;
}

.tab.active {
  color: #3D4F61;
  border-bottom-color: #3D4F61;
}

.admin-content {
  padding: 30px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.section h2 {
  font-size: 22px;
  color: #3D4F61;
  margin-bottom: 24px;
}

.section h3 {
  font-size: 18px;
  color: #3D4F61;
  margin: 30px 0 16px;
}

/* Форма добавления машинки */
.add-machine-form {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.add-machine-form input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #D1D5DB;
  border-radius: 8px;
  font-size: 15px;
}

/* Кнопки */
.btn {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn.small {
  padding: 8px 14px;
  font-size: 13px;
}

.btn.primary {
  background: #3D4F61;
  color: white;
}

.btn.primary:hover {
  background: #2C3E50;
}

.btn.success {
  background: #10B981;
  color: white;
}

.btn.success:hover {
  background: #059669;
}

.btn.warning {
  background: #F59E0B;
  color: white;
}

.btn.warning:hover {
  background: #D97706;
}

.btn.danger {
  background: #EF4444;
  color: white;
}

.btn.danger:hover {
  background: #DC2626;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Список машинок */
.machines-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.machine-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.machine-card.inactive {
  opacity: 0.6;
  background: #F9FAFB;
}

.machine-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.machine-name {
  font-size: 16px;
  font-weight: 600;
  color: #3D4F61;
}

.inactive-badge {
  font-size: 12px;
  padding: 4px 8px;
  background: #FEE2E2;
  color: #DC2626;
  border-radius: 4px;
}

.machine-actions {
  display: flex;
  gap: 8px;
}

/* Форма расписания */
.schedule-form {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.form-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row label {
  min-width: 80px;
  font-weight: 500;
  color: #6B7280;
}

.form-row select,
.form-row input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #D1D5DB;
  border-radius: 8px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

/* Заблокированные слоты */
.blocked-slots {
  margin-top: 30px;
}

.slots-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.slot-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 14px 18px;
  border-radius: 10px;
  border-left: 4px solid #EF4444;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.slot-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.slot-date {
  font-weight: 600;
  color: #3D4F61;
}

.slot-time {
  color: #6B7280;
}

.slot-machine {
  font-size: 13px;
  padding: 4px 8px;
  background: #E5E7EB;
  border-radius: 4px;
}

.slot-reason {
  font-size: 13px;
  color: #9CA3AF;
  font-style: italic;
}

/* Записи */
.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.booking-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.booking-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.booking-machine {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
}

.booking-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #6B7280;
}

.booking-details strong {
  color: #3D4F61;
}

.empty-message {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 12px;
  color: #9CA3AF;
  font-size: 16px;
}

@media (max-width: 768px) {
  .admin-header {
    padding: 16px 20px;
  }

  .tabs {
    padding: 0 20px;
    overflow-x: auto;
  }

  .tab {
    padding: 12px 16px;
    font-size: 14px;
  }

  .admin-content {
    padding: 20px;
  }

  .add-machine-form {
    flex-direction: column;
  }

  .machine-card {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .form-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .form-row label {
    min-width: auto;
  }

  .form-row select,
  .form-row input {
    width: 100%;
  }

  .form-actions {
    flex-direction: column;
  }

  .booking-card {
    flex-direction: column;
    gap: 16px;
  }

  .slot-card {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .slot-info {
    flex-wrap: wrap;
  }
}
</style>
