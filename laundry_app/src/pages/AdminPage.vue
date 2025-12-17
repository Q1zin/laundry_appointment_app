<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { useMachines } from '@/composables/useMachines'
import { useSchedule, type Machine } from '@/composables/useSchedule'
import { useAdminBookings } from '@/composables/useAdminBookings'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import CalendarIcon from '@/components/icons/CalendarIcon.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import TheHeader from '@/components/layout/TheHeader.vue'

const router = useRouter()
const { isLoggedIn, isAdmin, user } = useAuth()

// Редирект если не админ
onMounted(() => {
  if (!isLoggedIn.value || !isAdmin.value) {
    router.push('/')
  }
})

// Состояние
const activeTab = ref<'schedule' | 'machines'>('schedule')
const isLoading = ref(false)
const actionError = ref<string | null>(null)
const actionSuccess = ref<string | null>(null)

// Расписание
const { fetchSchedule } = useSchedule()
const machines = ref<Machine[]>([])
const selectedDate = ref<string>('')
const selectedMachineId = ref<string | null>(null)

// Машинки (админ)
const { blockMachine, unblockMachine } = useMachines()

// Бронирования (админ)
const { deleteBooking } = useAdminBookings()

// Инициализация - загружаем текущее расписание
onMounted(async () => {
  if (!user.value?.id) return
  
  const today = new Date().toISOString().split('T')[0]
  selectedDate.value = today || ''
  
  await loadSchedule()
})

// Загрузка расписания
const loadSchedule = async () => {
  if (!user.value?.id || !selectedDate.value) return
  
  isLoading.value = true
  actionError.value = null
  
  const result = await fetchSchedule(selectedDate.value, String(user.value.id))
  
  if (result.success && result.data) {
    machines.value = result.data.machines
  } else {
    actionError.value = result.error || 'Не удалось загрузить расписание'
  }
  
  isLoading.value = false
}

// Обработчики
const handleBlockMachine = async (machineId: string) => {
  if (!confirm('Заблокировать эту машинку?')) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  const result = await blockMachine(machineId)
  
  isLoading.value = false
  
  if (result.success) {
    actionSuccess.value = 'Машинка заблокирована'
    await loadSchedule()
  } else {
    actionError.value = result.message || 'Ошибка блокировки'
  }
}

const handleUnblockMachine = async (machineId: string) => {
  if (!confirm('Разблокировать эту машинку?')) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  const result = await unblockMachine(machineId)
  
  isLoading.value = false
  
  if (result.success) {
    actionSuccess.value = 'Машинка разблокирована'
    await loadSchedule()
  } else {
    actionError.value = result.message || 'Ошибка разблокировки'
  }
}

const handleDeleteBooking = async (bookingId: string) => {
  if (!confirm('Удалить эту запись?')) return
  
  isLoading.value = true
  actionError.value = null
  actionSuccess.value = null
  
  const result = await deleteBooking(bookingId)
  
  isLoading.value = false
  
  if (result.success) {
    actionSuccess.value = 'Запись удалена'
    await loadSchedule()
  } else {
    actionError.value = result.message || 'Ошибка удаления'
  }
}
</script>

<template>
  <div class="admin-page" v-if="isLoggedIn && isAdmin">
    <TheHeader variant="dark" />

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

        <!-- Machines Management -->
        <section v-else class="section">
          <h2 class="section-title">УПРАВЛЕНИЕ МАШИНКАМИ</h2>
          
          <div v-if="machines.length === 0" class="no-data">
            Нет доступных машинок
          </div>

          <div v-else class="machines-grid">
            <div v-for="machine in machines" :key="machine.id" class="machine-card">
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

.machines-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
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
</style>
