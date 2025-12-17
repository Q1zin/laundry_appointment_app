<script setup lang="ts">
import { ref, computed, toRef, watch } from 'vue'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import { useAuth } from '@/composables/useAuth'
import { useBookings } from '@/composables/useBookings'
import { useSchedule, type Machine, type Timeslot, type Booking } from '@/composables/useSchedule'
import { useBodyScrollLock } from '@/composables/useBodyScrollLock'

const props = defineProps<{
  isOpen: boolean
}>()

useBodyScrollLock(toRef(props, 'isOpen'))

const emit = defineEmits<{
  close: []
}>()

const { user } = useAuth()
const { createBooking } = useBookings()
const { fetchSchedule } = useSchedule()

// Form state
const selectedMachineId = ref<string | null>(null)
const selectedSlotId = ref<string | null>(null)
const selectedDate = ref<string>('')
const isLoading = ref(false)
const bookingError = ref<string | null>(null)
const bookingSuccess = ref(false)

// Schedule data
const machines = ref<Machine[]>([])
const timeslots = ref<Timeslot[]>([])
const bookings = ref<Booking[]>([])

// Генерация дат на 7 дней вперед
interface DateOption {
  date: string // YYYY-MM-DD
  dayName: string // ПН, ВТ, СР...
  dayNum: number // 1, 2, 3...
}

const availableDates = computed<DateOption[]>(() => {
  const dates: DateOption[] = []
  const dayNames = ['ВС', 'ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ']
  
  for (let i = 0; i < 7; i++) {
    const date = new Date()
    date.setDate(date.getDate() + i)
    
    dates.push({
      date: date.toISOString().split('T')[0]!,
      dayName: dayNames[date.getDay()]!,
      dayNum: date.getDate()
    })
  }
  
  return dates
})

// Загрузка расписания при смене даты
watch(selectedDate, async (newDate) => {
  if (!newDate || !user.value?.id) return
  
  isLoading.value = true
  bookingError.value = null
  
  const result = await fetchSchedule(newDate, String(user.value.id))
  
  if (result.success && result.data) {
    machines.value = result.data.machines
    timeslots.value = result.data.timeslots
    bookings.value = result.data.bookings || []
  } else {
    bookingError.value = result.error || 'Не удалось загрузить расписание'
  }
  
  isLoading.value = false
})

// Инициализация при открытии
watch(() => props.isOpen, async (isOpen) => {
  if (isOpen) {
    // Устанавливаем сегодняшнюю дату
    const today = new Date()
    selectedDate.value = today.toISOString().split('T')[0]!
    
    // Сбрасываем выбор
    selectedMachineId.value = null
    selectedSlotId.value = null
    bookingSuccess.value = false
    bookingError.value = null
    
    // Загружаем начальные данные машинок
    if (user.value?.id) {
      isLoading.value = true
      const result = await fetchSchedule(selectedDate.value, String(user.value.id))
      
      if (result.success && result.data) {
        machines.value = result.data.machines
        timeslots.value = result.data.timeslots
        bookings.value = result.data.bookings || []
      }
      isLoading.value = false
    }
  }
}, { immediate: true })

// Доступные машинки (не заблокированные)
const availableMachines = computed(() => {
  return machines.value.filter(m => m.status === 'available' || !m.alreadyBlocked)
})

// Доступные слоты для выбранной машинки и даты
const availableSlots = computed(() => {
  if (!selectedMachineId.value || !selectedDate.value) return []
  
  // Получаем занятые слоты для выбранной машинки
  const occupiedSlotIds = bookings.value
    .filter(b => b.machineId === selectedMachineId.value && b.state === 'active')
    .map(b => b.slotId)
  
  // Фильтруем слоты: только для выбранной машинки и не занятые
  return timeslots.value.filter(
    slot => slot.machineId === selectedMachineId.value && 
            !occupiedSlotIds.includes(slot.slotId)
  )
})

// Форматирование времени из ISO в HH:MM
// Важно: сервер возвращает время без timezone (LocalDateTime),
// парсим его напрямую чтобы избежать сдвига из-за timezone
const formatTime = (isoString: string) => {
  // Если строка в формате "2025-12-17T10:00:00", парсим напрямую
  const timePart = isoString.split('T')[1]
  if (timePart) {
    const [hours, minutes] = timePart.split(':')
    return `${hours}:${minutes}`
  }
  // Fallback на Date если формат другой
  const date = new Date(isoString)
  const hours = date.getHours()
  const minutes = date.getMinutes()
  return `${hours}:${minutes.toString().padStart(2, '0')}`
}

// Форматирование времени слота
const formatSlotTime = (slot: Timeslot) => {
  return `${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}`
}

const closeModal = () => {
  emit('close')
}

const handleOverlayClick = (e: MouseEvent) => {
  if (e.target === e.currentTarget) {
    closeModal()
  }
}

const canSubmit = computed(() => {
  return selectedMachineId.value !== null && 
         selectedSlotId.value !== null &&
         !isLoading.value
})

const handleSubmit = async () => {
  if (!canSubmit.value || !user.value?.id) return
  
  isLoading.value = true
  bookingError.value = null
  bookingSuccess.value = false
  
  const result = await createBooking(
    String(user.value.id),
    selectedMachineId.value!,
    selectedSlotId.value!
  )
  
  isLoading.value = false
  
  if (result.success) {
    bookingSuccess.value = true
    setTimeout(() => {
      closeModal()
    }, 1500)
  } else {
    bookingError.value = result.message || 'Не удалось создать запись'
  }
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click="handleOverlayClick">
        <div class="modal-card">
          <button class="close-btn" @click="closeModal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round"/>
            </svg>
          </button>

          <!-- Title -->
          <h2 class="modal-title">ЗАПИСЬ НА СТИРКУ</h2>
          <div class="title-divider"></div>

          <!-- Loading State -->
          <div v-if="isLoading" class="loading-state">
            <p>Загрузка...</p>
          </div>

          <!-- Error State -->
          <div v-else-if="bookingError" class="error-message">
            {{ bookingError }}
          </div>

          <!-- Success State -->
          <div v-else-if="bookingSuccess" class="success-message">
            ✓ Запись успешно создана!
          </div>

          <!-- Main Form -->
          <template v-else>
            <!-- Machine Selection -->
            <div class="section">
              <h3 class="section-title">ВЫБЕРИТЕ МАШИНКУ</h3>
              <div v-if="availableMachines.length === 0" class="no-data">
                Нет доступных машинок
              </div>
              <div v-else class="machines-grid">
                <button 
                  v-for="machine in availableMachines" 
                  :key="machine.id"
                  class="machine-card"
                  :class="{ active: selectedMachineId === machine.id }"
                  @click="selectedMachineId = machine.id; selectedSlotId = null"
                >
                  <WashingMachineOutlineIcon :size="80" color="#3D4F61" />
                  <span class="machine-name">{{ machine.name.toUpperCase() }}</span>
                </button>
              </div>
            </div>

            <!-- Date Selection -->
            <div v-if="selectedMachineId" class="section">
              <h3 class="section-title">ВЫБЕРИТЕ ДАТУ</h3>
              <div class="dates-grid">
                <button
                  v-for="dateOption in availableDates"
                  :key="dateOption.date"
                  class="date-btn"
                  :class="{ active: selectedDate === dateOption.date }"
                  @click="selectedDate = dateOption.date; selectedSlotId = null"
                >
                  <div class="date-day">{{ dateOption.dayName }}</div>
                  <div class="date-num">{{ dateOption.dayNum }}</div>
                </button>
              </div>
            </div>

            <!-- Time Slot Selection -->
            <div v-if="selectedMachineId && selectedDate" class="section">
              <h3 class="section-title">ВЫБЕРИТЕ ВРЕМЯ</h3>
              <div v-if="availableSlots.length === 0" class="no-data">
                Нет доступных слотов на эту дату
              </div>
              <div v-else class="times-grid">
                <button
                  v-for="slot in availableSlots"
                  :key="slot.slotId"
                  class="time-btn"
                  :class="{ active: selectedSlotId === slot.slotId }"
                  @click="selectedSlotId = slot.slotId"
                >
                  {{ formatSlotTime(slot) }}
                </button>
              </div>
            </div>

            <!-- Submit Button -->
            <button 
              class="submit-btn" 
              :class="{ disabled: !canSubmit }"
              :disabled="!canSubmit"
              @click="handleSubmit"
            >
              {{ isLoading ? 'СОЗДАНИЕ...' : 'ЗАПИСАТЬСЯ' }}
            </button>
          </template>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(232, 238, 242, 0.95);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
  overflow-y: auto;
}

.modal-card {
  position: relative;
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  background-color: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.close-btn {
  position: absolute;
  top: 25px;
  right: 25px;
  background: none;
  border: none;
  cursor: pointer;
  color: #3D4F61;
  padding: 5px;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 0.6;
}

.modal-title {
  font-size: 28px;
  font-weight: bold;
  color: #3D4F61;
  margin-bottom: 10px;
  text-align: center;
}

.title-divider {
  width: 100%;
  height: 1px;
  background-color: #E0E0E0;
  margin-bottom: 30px;
}

.section {
  width: 100%;
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #3D4F61;
  margin-bottom: 25px;
  margin-top: 20px;
}

/* Machines */
.machines-grid {
  display: flex;
  gap: 25px;
  justify-content: center;
  flex-wrap: wrap;
}

.machine-card {
  width: 180px;
  height: 180px;
  border-radius: 20px;
  border: 2px solid #E8EEF2;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 30px 40px;
}

.machine-card:hover {
  border-color: #6B8DB8;
}

.machine-card.active {
  border: 3px solid #6B8DB8;
}

.machine-name {
  font-size: 16px;
  font-weight: 500;
  color: #3D4F61;
  text-align: center;
}

/* Dates */
.dates-grid {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.date-btn {
  width: 80px;
  height: 80px;
  border-radius: 15px;
  border: 2px solid #E8EEF2;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.date-btn:hover {
  background: #F5F8FA;
  border-color: #6B8DB8;
}

.date-btn.active {
  background: #6B8DB8;
  border-color: #6B8DB8;
}

.date-day {
  font-size: 16px;
  color: #3D4F61;
}

.date-num {
  font-size: 20px;
  font-weight: bold;
  color: #3D4F61;
}

.date-btn.active .date-day,
.date-btn.active .date-num {
  color: #FFFFFF;
}

/* Times */
.times-grid {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.time-btn {
  padding: 15px 35px;
  border-radius: 25px;
  border: 2px solid #E8EEF2;
  background: #FFFFFF;
  font-size: 16px;
  color: #3D4F61;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.time-btn:hover {
  border-color: #6B8DB8;
  background: #F5F8FA;
}

.time-btn.active {
  background: #6B8DB8;
  border-color: #6B8DB8;
  color: #FFFFFF;
}

/* Input Fields */
.inputs-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 15px;
  background: #F5F8FA;
  border-radius: 12px;
  border: 2px solid #E8EEF2;
}

.input-icon {
  color: #6B8DB8;
}

.info-text {
  font-size: 14px;
  color: #3D4F61;
  font-weight: 500;
}

/* States */
.loading-state {
  padding: 60px 40px;
  text-align: center;
}

.loading-state p {
  font-size: 18px;
  color: #6B8DB8;
}

.error-message {
  padding: 15px 20px;
  background: rgba(239, 68, 68, 0.1);
  border: 2px solid rgba(239, 68, 68, 0.3);
  border-radius: 12px;
  color: #EF4444;
  font-size: 15px;
  text-align: center;
  margin: 20px 0;
}

.success-message {
  padding: 15px 20px;
  background: rgba(34, 197, 94, 0.1);
  border: 2px solid rgba(34, 197, 94, 0.3);
  border-radius: 12px;
  color: #22C55E;
  font-size: 15px;
  font-weight: 600;
  text-align: center;
  margin: 20px 0;
}

.no-data {
  padding: 30px;
  text-align: center;
  color: #9CA3AF;
  font-size: 14px;
}

.input-field:focus {
  border-color: #6B8DB8;
}

/* Submit Button */
.submit-btn {
  margin-top: 30px;
  padding: 15px 60px;
  background: #6B8DB8;
  color: #FFFFFF;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(107, 141, 184, 0.35);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: #5A7CA5;
  box-shadow: 0 10px 30px rgba(107, 141, 184, 0.45);
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(0);
}

.submit-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.submit-btn.disabled:hover {
  transform: none;
  background: #6B8DB8;
  box-shadow: none;
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-active .modal-card,
.modal-leave-active .modal-card {
  transition: transform 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-card,
.modal-leave-to .modal-card {
  transform: scale(0.9) translateY(-20px);
}

/* Responsive */
@media (max-width: 768px) {
  .modal-card {
    padding: 40px 30px;
  }

  .modal-title {
    font-size: 28px;
  }

  .machines-grid {
    flex-direction: column;
    align-items: center;
  }

  .machine-card {
    width: 160px;
    height: 160px;
  }

  .dates-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
  }

  .date-btn {
    width: 100%;
    height: 70px;
  }

  .times-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .time-btn {
    padding: 12px 20px;
    width: 100%;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .modal-card {
    padding: 30px 20px;
  }

  .modal-title {
    font-size: 24px;
  }

  .section-title {
    font-size: 16px;
  }

  .dates-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .times-grid {
    grid-template-columns: 1fr;
  }

  .input-field {
    height: 60px;
    font-size: 14px;
  }

  .submit-btn {
    width: 100%;
    padding: 18px 40px;
    font-size: 17px;
  }
}
</style>
