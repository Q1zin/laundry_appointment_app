<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import TheHeader from '@/components/layout/TheHeader.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import BookingModal from '@/components/modals/BookingModal.vue'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import CalendarIcon from '@/components/icons/CalendarIcon.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import { useAuth } from '@/composables/useAuth'
import { useBookings } from '@/composables/useBookings'

const router = useRouter()
const { isLoggedIn, user, logout, deleteAccount } = useAuth()
const { activeBookings, cancelBooking, clearAllBookings } = useBookings()

const isBookingModalOpen = ref(false)

// Редирект если не авторизован
onMounted(() => {
  if (!isLoggedIn.value) {
    router.push('/')
  }
})

const userEmail = computed(() => user.value?.email || '')
const userFullName = computed(() => user.value?.fullName || '')
const userGroup = computed(() => user.value?.group || '')
const userRoom = computed(() => user.value?.room || '')
const userContract = computed(() => user.value?.contract || '')

const handleCancelBooking = (bookingId: string) => {
  if (confirm('Вы уверены, что хотите отменить эту запись?')) {
    cancelBooking(bookingId)
  }
}

const handleLogout = () => {
  logout()
  router.push('/')
}

const handleDeleteAccount = () => {
  if (confirm('Вы уверены, что хотите удалить аккаунт? Это действие необратимо.')) {
    clearAllBookings()
    deleteAccount()
    router.push('/')
  }
}

const openBookingModal = () => {
  isBookingModalOpen.value = true
}

const closeBookingModal = () => {
  isBookingModalOpen.value = false
}
</script>

<template>
  <div class="profile-page" v-if="isLoggedIn">
    <TheHeader 
      :is-logged-in="isLoggedIn"
      :user-email="userEmail"
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
                <span class="info-label">Email:</span>
                <span class="info-value">{{ userEmail }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">ФИО:</span>
                <span class="info-value">{{ userFullName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Группа:</span>
                <span class="info-value">{{ userGroup }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Комната:</span>
                <span class="info-value">{{ userRoom }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">Договор:</span>
                <span class="info-value">{{ userContract }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- Активные записи -->
        <section class="bookings-section">
          <h2 class="section-title">АКТИВНЫЕ ЗАПИСИ</h2>
          
          <div v-if="activeBookings.length === 0" class="no-bookings">
            <WashingMachineOutlineIcon :size="64" color="#9CA3AF" />
            <p>У вас пока нет активных записей на стирку</p>
            <BaseButton @click="openBookingModal">Записаться на стирку</BaseButton>
          </div>
          
          <div v-else class="bookings-list">
            <div 
              v-for="booking in activeBookings" 
              :key="booking.id" 
              class="booking-card"
            >
              <div class="booking-icon">
                <WashingMachineOutlineIcon :size="40" color="#3D4F61" />
              </div>
              <div class="booking-details">
                <div class="booking-machine">Машинка №{{ booking.machine }}</div>
                <div class="booking-datetime">
                  <CalendarIcon :size="16" color="#6B7280" />
                  <span>{{ booking.dayName }}, {{ booking.dayNumber }} — {{ booking.time }}</span>
                </div>
                <div class="booking-room">Комната: {{ booking.room }}</div>
              </div>
              <button 
                class="cancel-btn" 
                @click="handleCancelBooking(booking.id)"
                title="Отменить запись"
              >
                <TrashIcon :size="20" color="#EF4444" />
              </button>
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

        <!-- Удаление аккаунта -->
        <section class="danger-section">
          <p class="danger-warning">Удаление аккаунта приведёт к потере всех данных. Это действие необратимо.</p>
          <button class="action-btn delete-btn" @click="handleDeleteAccount">
            УДАЛИТЬ АККАУНТ
          </button>
        </section>
      </div>
    </main>

    <BookingModal 
      :is-open="isBookingModalOpen" 
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
  min-width: 100px;
}

.info-value {
  font-size: 16px;
  color: #3D4F61;
}

/* Bookings Section */
.bookings-section {
  margin-bottom: 50px;
}

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

.no-bookings p {
  font-size: 18px;
  color: #6B7280;
}

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
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
