<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import TheHeader from '@/components/layout/TheHeader.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import BookingModal from '@/components/modals/BookingModal.vue'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import CalendarIcon from '@/components/icons/CalendarIcon.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import { useAuth } from '@/composables/useAuth'

// Тип для записи пользователя (от нового эндпоинта)
interface UserBooking {
  id: string
  machineId: string
  machineName: string
  slotId: string
  slotStartTime: string
  slotEndTime: string
  state: string
  createdAt: string
}

const router = useRouter()
const { isLoggedIn, user, logout } = useAuth()

const isBookingModalOpen = ref(false)
const isLoading = ref(false)
const error = ref<string | null>(null)
const userBookings = ref<UserBooking[]>([])

// Загрузка записей пользователя
const loadUserBookings = async () => {
  if (!user.value?.id) return
  
  isLoading.value = true
  error.value = null
  
  try {
    const response = await fetch(`/api/bookings/user/${user.value.id}`)
    
    if (response.ok) {
      userBookings.value = await response.json()
    } else {
      error.value = 'Не удалось загрузить записи'
    }
  } catch (err) {
    error.value = 'Ошибка соединения с сервером'
  }
  
  isLoading.value = false
}

// Форматирование времени из ISO в HH:MM
const formatTime = (isoString: string) => {
  if (!isoString) return ''
  const timePart = isoString.split('T')[1]
  if (timePart) {
    const [hours, minutes] = timePart.split(':')
    return `${hours}:${minutes}`
  }
  const date = new Date(isoString)
  return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

// Форматирование даты
const formatDate = (isoString: string) => {
  if (!isoString) return ''
  const datePart = isoString.split('T')[0]
  if (datePart) {
    const [year, month, day] = datePart.split('-')
    return `${day}.${month}.${year}`
  }
  return new Date(isoString).toLocaleDateString('ru-RU')
}

// Форматирование времени слота
const formatSlotTime = (booking: UserBooking) => {
  return `${formatTime(booking.slotStartTime)} - ${formatTime(booking.slotEndTime)}`
}

// Редирект если не авторизован + загрузка записей
onMounted(async () => {
  if (!isLoggedIn.value) {
    router.push('/')
    return
  }
  await loadUserBookings()
})

const handleCancelBooking = async (bookingId: string) => {
  if (!user.value?.id) return
  
  if (!confirm('Вы уверены, что хотите отменить эту запись?')) return
  
  isLoading.value = true
  error.value = null
  
  try {
    const response = await fetch('/api/bookings/cancel', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ 
        bookingId: bookingId,
        userId: user.value.id 
      })
    })
    
    const data = await response.json()
    
    if (data.result) {
      alert('Запись успешно отменена')
      await loadUserBookings() // Перезагружаем список
    } else {
      error.value = data.message || 'Ошибка при отмене записи'
      alert(error.value)
    }
  } catch (err) {
    error.value = 'Ошибка соединения с сервером'
    alert(error.value)
  } finally {
    isLoading.value = false
  }
}

const handleLogout = () => {
  logout()
  router.push('/')
}

const openBookingModal = () => {
  isBookingModalOpen.value = true
}

const closeBookingModal = async () => {
  isBookingModalOpen.value = false
  await loadUserBookings() // Перезагружаем после создания записи
}
</script>

<template>
  <div class="profile-page" v-if="isLoggedIn">
    <TheHeader 
      @booking-click="openBookingModal"
      @rules-click="router.push('/#rules')"
    />
    
    <main class="main-content">
      <div class="container">
        <!-- Профиль пользователя -->
        <section class="profile-section">
          <h1 class="page-title">ЛИЧНЫЙ КАБИНЕТ</h1>
          
          <div class="profile-card">
            <div class="profile-info">
              <div class="info-row">
                <span class="info-label">ФИО:</span>
                <span class="info-value">{{ user?.fullName || 'Не указано' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Email:</span>
                <span class="info-value">{{ user?.email || 'Не указан' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Комната:</span>
                <span class="info-value">{{ user?.room || 'Не указана' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Договор найма:</span>
                <span class="info-value">{{ user?.contract || 'Не указан' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Логин:</span>
                <span class="info-value">{{ user?.name || 'Не указан' }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Роль:</span>
                <span class="info-value">{{ user?.role === 'admin' ? 'Администратор' : 'Пользователь' }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- Активные записи -->
        <section class="bookings-section">
          <div class="section-header-with-count">
            <h2 class="section-title">АКТИВНЫЕ ЗАПИСИ</h2>
            <div class="bookings-counter">
              <span class="counter-text">{{ userBookings.length }} / 2</span>
              <span class="counter-label">записей</span>
            </div>
          </div>
          
          <div v-if="isLoading" class="loading-state">
            <p>Загрузка записей...</p>
          </div>

          <div v-else-if="error" class="error-state">
            <p class="error-message">{{ error }}</p>
            <BaseButton @click="loadUserBookings">Повторить попытку</BaseButton>
          </div>
          
          <div v-else-if="userBookings.length === 0" class="no-bookings">
            <WashingMachineOutlineIcon :size="64" color="#9CA3AF" />
            <p>У вас пока нет активных записей на стирку</p>
            <BaseButton @click="openBookingModal">Записаться на стирку</BaseButton>
          </div>
          
          <div v-else>
            <div class="bookings-list">
              <div 
                v-for="booking in userBookings" 
                :key="booking.id" 
                class="booking-card"
              >
              <div class="booking-icon">
                <WashingMachineOutlineIcon :size="40" color="#3D4F61" />
              </div>
              <div class="booking-details">
                <div class="booking-machine">{{ booking.machineName }}</div>
                <div class="booking-datetime">
                  <CalendarIcon :size="16" color="#6B7280" />
                  <span>{{ formatDate(booking.slotStartTime) }}, {{ formatSlotTime(booking) }}</span>
                </div>
                <div class="booking-date">Создано: {{ new Date(booking.createdAt).toLocaleString('ru-RU') }}</div>
              </div>
              <div class="booking-actions">
                <button 
                  class="cancel-btn" 
                  @click="handleCancelBooking(booking.id)"
                  title="Отменить запись"
                  :disabled="isLoading"
                >
                  <TrashIcon :size="20" color="#EF4444" />
                </button>
              </div>
            </div>
          </div>
          
          <!-- Кнопка добавления записи, если меньше 2 -->
          <div v-if="userBookings.length < 2" class="add-booking-section">
            <BaseButton @click="openBookingModal">+ Добавить еще одну запись</BaseButton>
          </div>
        </div>
        </section>

        <!-- Действия аккаунта -->
        <section class="account-section">
          <div class="logout-section">
            <button class="action-btn logout-btn" @click="handleLogout">
              ВЫЙТИ ИЗ АККАУНТА
            </button>
          </div>
        </section>
      </div>
    </main>

    <BookingModal 
      :is-open="isBookingModalOpen" 
      :edit-booking="editingBooking"
      @close="closeBookingModal" 
    />
  </div>
</template>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #E8EEF2;
}

.main-content {
  padding: 40px 60px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: #3D4F61;
  margin-bottom: 30px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #3D4F61;
  margin-bottom: 20px;
}

/* Profile Section */
.profile-section {
  margin-bottom: 50px;
}

.profile-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 15px;
}

.info-label {
  font-size: 16px;
  font-weight: 600;
  color: #6B7280;
  min-width: 130px;
}

.info-value {
  font-size: 16px;
  color: #3D4F61;
}

/* Bookings Section */
.bookings-section {
  margin-bottom: 50px;
}

.section-header-with-count {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 25px;
}

.bookings-counter {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  background: #E8EEF2;
  padding: 10px 20px;
  border-radius: 12px;
}

.counter-text {
  font-size: 24px;
  font-weight: bold;
  color: #3D4F61;
}

.counter-label {
  font-size: 12px;
  color: #6B7280;
  text-transform: uppercase;
}

.loading-state,
.error-state,
.no-bookings {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.loading-state p {
  font-size: 18px;
  color: #3B82F6;
}

.error-state .error-message {
  font-size: 16px;
  color: #EF4444;
  padding: 12px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
}

.no-bookings p {
  font-size: 18px;
  color: #6B7280;
}

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.add-booking-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.booking-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 20px 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.booking-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.booking-icon {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  background: #E8EEF2;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.booking-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.booking-machine {
  font-size: 18px;
  font-weight: 600;
  color: #3D4F61;
}

.booking-datetime {
  font-size: 14px;
  color: #6B7280;
  display: flex;
  align-items: center;
  gap: 8px;
}

.booking-room {
  font-size: 14px;
  color: #9CA3AF;
}

.booking-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.edit-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #DBEAFE;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.edit-btn:hover {
  background: #BFDBFE;
}

.cancel-btn {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border: none;
  background: #FEE2E2;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.cancel-btn:hover {
  background: #FECACA;
}

/* Account Section */
.account-section {
  margin-bottom: 30px;
  padding: 0 25px;
  /* background: #d0ccff; */
  border-radius: 16px;
  display: flex;
  /* align-items: center; */
  justify-content: center;
  /* border: 1px solid #d0ccff; */
}

.logout-section {
  display: flex;
}

.action-btn {
  padding: 15px 30px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn {
  background: #3D4F61;
  color: #FFFFFF;
  border: none;
}

.logout-btn:hover {
  background: #2C3E50;
}

/* Danger Section */
.danger-section {
  margin-bottom: 50px;
  padding: 25px;
  background: #FEF2F2;
  border-radius: 16px;
  border: 1px solid #FECACA;
}

.danger-title {
  color: #DC2626;
}

.danger-warning {
  font-size: 14px;
  color: #991B1B;
  margin-bottom: 20px;
}

.delete-btn {
  background: transparent;
  color: #EF4444;
  border: 2px solid #EF4444;
}

.delete-btn:hover {
  background: #FEE2E2;
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px;
  }

  .page-title {
    font-size: 28px;
  }

  .section-title {
    font-size: 20px;
  }

  .booking-card {
    flex-wrap: wrap;
  }

  .account-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    text-align: center;
  }
}
</style>
