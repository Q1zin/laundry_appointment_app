<script setup lang="ts">
import { ref } from 'vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import WashingMachineIcon from '@/components/icons/WashingMachineIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'
import LockIcon from '@/components/icons/LockIcon.vue'
import EmailIcon from '@/components/icons/EmailIcon.vue'
import IdCardIcon from '@/components/icons/IdCardIcon.vue'
import HomeIcon from '@/components/icons/HomeIcon.vue'
import GroupIcon from '@/components/icons/GroupIcon.vue'
import DocumentIcon from '@/components/icons/DocumentIcon.vue'
import { useAuth } from '@/composables/useAuth'

const props = defineProps<{
  isOpen: boolean
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const { login, register } = useAuth()

type AuthMode = 'login' | 'register'
const mode = ref<AuthMode>('login')

// Login form
const loginUsername = ref('')
const loginPassword = ref('')

// Register form
const regUsername = ref('')
const regEmail = ref('')
const regFullName = ref('')
const regGroup = ref('')
const regRoom = ref('')
const regContract = ref('')
const regPassword = ref('')
const regPasswordConfirm = ref('')

const switchMode = (newMode: AuthMode) => {
  mode.value = newMode
}

const closeModal = () => {
  emit('close')
}

const handleOverlayClick = (e: MouseEvent) => {
  if (e.target === e.currentTarget) {
    closeModal()
  }
}

const handleLogin = () => {
  if (!loginUsername.value || !loginPassword.value) return
  
  login(loginUsername.value, loginPassword.value)
  loginUsername.value = ''
  loginPassword.value = ''
  emit('success')
  closeModal()
}

const handleRegister = () => {
  if (!regUsername.value || !regEmail.value || !regFullName.value || 
      !regGroup.value || !regRoom.value || !regContract.value || 
      !regPassword.value || regPassword.value !== regPasswordConfirm.value) return
  
  register({
    username: regUsername.value,
    email: regEmail.value,
    fullName: regFullName.value,
    group: regGroup.value,
    room: regRoom.value,
    contract: regContract.value,
    password: regPassword.value
  })
  
  // Reset form
  regUsername.value = ''
  regEmail.value = ''
  regFullName.value = ''
  regGroup.value = ''
  regRoom.value = ''
  regContract.value = ''
  regPassword.value = ''
  regPasswordConfirm.value = ''
  
  emit('success')
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

          <!-- Icon -->
          <div class="icon-container">
            <WashingMachineIcon :size="100" />
          </div>

          <!-- Title -->
          <h2 class="modal-title">{{ mode === 'login' ? 'ВХОД' : 'РЕГИСТРАЦИЯ' }}</h2>

          <!-- Mode switcher -->
          <div class="mode-switcher">
            <button 
              class="mode-btn" 
              :class="{ active: mode === 'login' }"
              @click="switchMode('login')"
            >
              Вход
            </button>
            <button 
              class="mode-btn" 
              :class="{ active: mode === 'register' }"
              @click="switchMode('register')"
            >
              Регистрация
            </button>
          </div>

          <!-- Login Form -->
          <form v-if="mode === 'login'" class="auth-form" @submit.prevent>
            <div class="form-fields">
              <BaseInput v-model="loginUsername" placeholder="Username">
                <template #icon><UserIcon /></template>
              </BaseInput>

              <BaseInput v-model="loginPassword" type="password" placeholder="Пароль">
                <template #icon><LockIcon /></template>
              </BaseInput>
            </div>

            <BaseButton class="submit-btn" @click="handleLogin">ВОЙТИ</BaseButton>
          </form>

          <!-- Register Form -->
          <form v-else class="auth-form" @submit.prevent>
            <div class="form-fields">
              <BaseInput v-model="regUsername" placeholder="Username">
                <template #icon><UserIcon /></template>
              </BaseInput>

              <BaseInput v-model="regEmail" type="email" placeholder="Email">
                <template #icon><EmailIcon /></template>
              </BaseInput>

              <BaseInput v-model="regFullName" placeholder="ФИО">
                <template #icon><IdCardIcon /></template>
              </BaseInput>

              <BaseInput v-model="regGroup" placeholder="Группа (например, 23212)">
                <template #icon><GroupIcon /></template>
              </BaseInput>

              <BaseInput v-model="regRoom" placeholder="Номер комнаты (например, 107М)">
                <template #icon><HomeIcon /></template>
              </BaseInput>

              <BaseInput v-model="regContract" placeholder="Номер договора найма">
                <template #icon><DocumentIcon /></template>
              </BaseInput>

              <BaseInput v-model="regPassword" type="password" placeholder="Пароль">
                <template #icon><LockIcon /></template>
              </BaseInput>

              <BaseInput v-model="regPasswordConfirm" type="password" placeholder="Подтвердите пароль">
                <template #icon><LockIcon /></template>
              </BaseInput>
            </div>

            <BaseButton class="submit-btn" @click="handleRegister">ЗАРЕГИСТРИРОВАТЬСЯ</BaseButton>
          </form>
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
  background-color: rgba(74, 95, 127, 0.85);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-card {
  position: relative;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  background-color: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: none;
  border: none;
  cursor: pointer;
  color: #2C3E50;
  padding: 5px;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 0.6;
}

.icon-container {
  margin-bottom: 15px;
}

.modal-title {
  font-size: 32px;
  font-weight: bold;
  color: #2C3E50;
  margin-bottom: 25px;
}

.mode-switcher {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.mode-btn {
  padding: 10px 25px;
  border: 2px solid #2C3E50;
  background: transparent;
  color: #2C3E50;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.mode-btn.active {
  background-color: #2C3E50;
  color: #FFFFFF;
}

.mode-btn:not(.active):hover {
  background-color: rgba(44, 62, 80, 0.1);
}

.auth-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-fields {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 25px;
  margin-bottom: 40px;
}

.submit-btn {
  min-width: 200px;
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

@media (max-width: 540px) {
  .modal-card {
    padding: 40px 25px;
  }

  .modal-title {
    font-size: 26px;
  }

  .mode-btn {
    padding: 8px 18px;
    font-size: 13px;
  }
}
</style>
