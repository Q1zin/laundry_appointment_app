<script setup lang="ts">
import { ref, computed, toRef, watch } from 'vue'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'
import KeyIcon from '@/components/icons/KeyIcon.vue'
import { useAuth } from '@/composables/useAuth'
import { useBookings, type Booking } from '@/composables/useBookings'
import { useMachines } from '@/composables/useMachines'
import { useSchedule } from '@/composables/useSchedule'
import { useBodyScrollLock } from '@/composables/useBodyScrollLock'

const props = defineProps<{
  isOpen: boolean
  editBooking?: Booking | null
}>()

useBodyScrollLock(toRef(props, 'isOpen'))

const emit = defineEmits<{
  close: []
}>()

const { user } = useAuth()
const { addBooking, updateBooking } = useBookings()
const { machines, getActiveMachines } = useMachines()
const { isSlotBlocked } = useSchedule()

// Form state
const selectedMachine = ref<number | null>(null)
const selectedDateIndex = ref<number>(0)
const selectedTimeIndex = ref<number | null>(null)
const fullName = ref('')
const roomNumber = ref('')

// Режим редактирования
const isEditMode = computed(() => !!props.editBooking)

// Initialize from user data or edit booking
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    if (props.editBooking) {
      // Режим редактирования - заполняем из существующей записи
      selectedMachine.value = props.editBooking.machine
      fullName.value = props.editBooking.fullName || ''
      roomNumber.value = props.editBooking.room || ''
      
      // Находим индекс даты
      const dateIndex = dates.value.findIndex(d => d.dateStr === props.editBooking!.date)
      selectedDateIndex.value = dateIndex >= 0 ? dateIndex : 0
      
      // Находим индекс времени
      const timeIndex = timeSlots.findIndex(t => t === props.editBooking!.time)
      selectedTimeIndex.value = timeIndex >= 0 ? timeIndex : null
    } else if (user.value) {
      // Новая запись
      fullName.value = user.value.fullName || ''
      roomNumber.value = user.value.room || ''
      selectedMachine.value = null
      selectedTimeIndex.value = null
      selectedDateIndex.value = 0
    }
  }
}, { immediate: true })

// Активные машинки
const activeMachines = computed(() => getActiveMachines())

// Generate dates for next 7 days
const dates = computed(() => {
  const days = ['ВС', 'ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ']
  const result: { dayName: string; dayNumber: number; dateStr: string }[] = []
  const today = new Date()
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const dateStr = date.toISOString().split('T')[0] || ''
    result.push({
      dayName: days[date.getDay()] || 'ВС',
      dayNumber: date.getDate(),
      dateStr
    })
  }
  return result
})

const timeSlots = [
  '9:45 - 11:45',
  '14:00 - 16:00',
  '18:00 - 20:00',
  '21:00 - 23:00'
]

// Проверка доступности слота
const isTimeSlotAvailable = (timeIndex: number) => {
  if (selectedMachine.value === null) return true
  const selectedDate = dates.value[selectedDateIndex.value]
  if (!selectedDate) return true
  const time = timeSlots[timeIndex]
  if (!time) return true
  return !isSlotBlocked(selectedDate.dateStr, time, selectedMachine.value)
}

// Проверка доступности машинки
const isMachineAvailable = (machineId: number) => {
  if (selectedTimeIndex.value === null) return true
  const selectedDate = dates.value[selectedDateIndex.value]
  if (!selectedDate) return true
  const time = timeSlots[selectedTimeIndex.value]
  if (!time) return true
  return !isSlotBlocked(selectedDate.dateStr, time, machineId)
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
  return selectedMachine.value !== null && 
         selectedTimeIndex.value !== null && 
         fullName.value && 
         roomNumber.value
})

const handleSubmit = () => {
  if (!canSubmit.value) return
  
  const selectedDate = dates.value[selectedDateIndex.value]
  if (!selectedDate) return
  
  const time = timeSlots[selectedTimeIndex.value!]
  if (!time) return

  const machine = activeMachines.value.find(m => m.id === selectedMachine.value)
  
  const bookingData = {
    machine: selectedMachine.value!,
    machineName: machine?.name,
    date: selectedDate.dateStr,
    dayName: selectedDate.dayName,
    dayNumber: selectedDate.dayNumber,
    time: time,
    fullName: fullName.value,
    room: roomNumber.value,
    userEmail: user.value?.email
  }

  if (isEditMode.value && props.editBooking) {
    updateBooking(props.editBooking.id, bookingData)
  } else {
    addBooking(bookingData)
  }
  
  closeModal()
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
          <h2 class="modal-title">{{ isEditMode ? 'ПЕРЕНОС ЗАПИСИ' : 'ЗАПИСЬ НА СТИРКУ' }}</h2>
          <div class="title-divider"></div>

          <!-- Date Selection -->
          <div class="section">
            <h3 class="section-title">ВЫБЕРИТЕ ДАТУ</h3>
            <div class="dates-grid">
              <button
                v-for="(date, index) in dates"
                :key="index"
                class="date-btn"
                :class="{ active: selectedDateIndex === index }"
                @click="selectedDateIndex = index"
              >
                <span class="day-name">{{ date.dayName }}</span>
                <span class="day-number">{{ date.dayNumber }}</span>
              </button>
            </div>
          </div>

          <!-- Time Selection -->
          <div class="section">
            <h3 class="section-title">ВЫБЕРИТЕ ВРЕМЯ</h3>
            <div class="times-grid">
              <button
                v-for="(time, index) in timeSlots"
                :key="index"
                class="time-btn"
                :class="{ active: selectedTimeIndex === index, disabled: !isTimeSlotAvailable(index) }"
                :disabled="!isTimeSlotAvailable(index)"
                @click="selectedTimeIndex = index"
              >
                {{ time }}
              </button>
            </div>
          </div>

          <!-- Machine Selection -->
          <div class="section">
            <h3 class="section-title">ВЫБЕРИТЕ МАШИНКУ</h3>
            <div class="machines-grid">
              <button 
                v-for="machine in activeMachines" 
                :key="machine.id"
                class="machine-card"
                :class="{ active: selectedMachine === machine.id, disabled: !isMachineAvailable(machine.id) }"
                :disabled="!isMachineAvailable(machine.id)"
                @click="selectedMachine = machine.id"
              >
                <WashingMachineOutlineIcon :size="50" />
                <span class="machine-name">{{ machine.name }}</span>
              </button>
            </div>
          </div>

          <!-- Input Fields (readonly) -->
          <div class="section inputs-section">
            <div class="input-group">
              <UserIcon :size="24" class="input-icon" />
              <input 
                v-model="fullName"
                type="text" 
                placeholder="ФАМИЛИЯ И ИМЯ" 
                class="input-field"
                readonly
              />
            </div>
            <div class="input-group">
              <KeyIcon :size="24" class="input-icon" />
              <input 
                v-model="roomNumber"
                type="text" 
                placeholder="НОМЕР КОМНАТЫ" 
                class="input-field"
                readonly
              />
            </div>
          </div>

          <!-- Submit Button -->
          <button 
            class="submit-btn" 
            :class="{ disabled: !canSubmit }"
            :disabled="!canSubmit"
            @click="handleSubmit"
          >
            {{ isEditMode ? 'ПЕРЕНЕСТИ' : 'ЗАПИСАТЬСЯ' }}
          </button>
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
  font-size: 16px;
  font-weight: bold;
  color: #3D4F61;
  margin-bottom: 15px;
}

/* Machines */
.machines-grid {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.machine-card {
  width: 130px;
  height: 130px;
  border-radius: 15px;
  border: 2px solid #E8EEF2;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #3D4F61;
}

.machine-card:hover {
  border-color: #6B8DB8;
}

.machine-card.active {
  border: 3px solid #6B8DB8;
}

.machine-card.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #F0F0F0;
}

.machine-card.disabled:hover {
  border-color: #E8EEF2;
}

.machine-name {
  font-size: 14px;
  font-weight: 600;
  margin-top: 10px;
  color: #3D4F61;
}

/* Dates */
.dates-grid {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.date-btn {
  width: 65px;
  height: 65px;
  border-radius: 12px;
  border: 2px solid #E8EEF2;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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

.day-name {
  font-size: 12px;
  color: #3D4F61;
}

.day-number {
  font-size: 16px;
  font-weight: bold;
  color: #3D4F61;
}

.date-btn.active .day-name,
.date-btn.active .day-number {
  color: #FFFFFF;
}

/* Times */
.times-grid {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.time-btn {
  padding: 10px 20px;
  border-radius: 20px;
  border: 2px solid #E8EEF2;
  background: #FFFFFF;
  font-size: 14px;
  color: #3D4F61;
  cursor: pointer;
  transition: all 0.3s ease;
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

.time-btn.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #F0F0F0;
}

.time-btn.disabled:hover {
  border-color: #E8EEF2;
  background: #F0F0F0;
}

/* Input Fields */
.inputs-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.input-group {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #B0B8C0;
}

.input-field {
  width: 100%;
  height: 50px;
  border: 2px solid #E8EEF2;
  border-radius: 12px;
  padding: 12px 12px 12px 50px;
  font-size: 14px;
  color: #3D4F61;
  background: #F8F9FA;
  outline: none;
  transition: border-color 0.3s ease;
}

.input-field:read-only {
  cursor: default;
  background: #F0F2F4;
}

.input-field::placeholder {
  color: #C8D0D8;
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
