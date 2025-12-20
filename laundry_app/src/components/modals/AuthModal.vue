<script setup lang="ts">
import { ref, toRef, watch } from 'vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import WashingMachineIcon from '@/components/icons/WashingMachineIcon.vue'
import LockIcon from '@/components/icons/LockIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'
import IdCardIcon from '@/components/icons/IdCardIcon.vue'
import HomeIcon from '@/components/icons/HomeIcon.vue'
import GroupIcon from '@/components/icons/GroupIcon.vue'
import DocumentIcon from '@/components/icons/DocumentIcon.vue'
import { useAuth } from '@/composables/useAuth'
import { useBodyScrollLock } from '@/composables/useBodyScrollLock'

const props = defineProps<{
  isOpen: boolean
}>()

useBodyScrollLock(toRef(props, 'isOpen'))

const emit = defineEmits<{
  close: []
  success: []
}>()

const { login, register } = useAuth()

type AuthMode = 'login' | 'register'
const mode = ref<AuthMode>('login')


const loginUsername = ref('')
const loginPassword = ref('')
const loginError = ref('')
const isLoginLoading = ref(false)


const regUsername = ref('')
const regEmail = ref('')
const regFullName = ref('')
const regRoom = ref('')
const regContract = ref('')
const regPassword = ref('')
const regPasswordConfirm = ref('')
const regError = ref('')
const isRegLoading = ref(false)


watch(mode, () => {
  loginError.value = ''
  regError.value = ''
})


watch([loginUsername, loginPassword], () => {
  loginError.value = ''
})

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

const handleLogin = async () => {
  if (!loginUsername.value || !loginPassword.value) {
    loginError.value = 'Заполните все поля'
    return
  }
  
  isLoginLoading.value = true
  loginError.value = ''
  
  try {
    const result = await login(loginUsername.value, loginPassword.value)
    
    if (result.success) {
      
      loginUsername.value = ''
      loginPassword.value = ''
      emit('success')
      closeModal()
    } else {
      
      loginError.value = result.message || 'Неверный логин или пароль'
    }
  } catch (error) {
    loginError.value = 'Ошибка соединения с сервером'
  } finally {
    isLoginLoading.value = false
  }
}

const handleRegister = async () => {
  
  if (!regUsername.value || !regEmail.value || !regFullName.value || !regRoom.value || !regContract.value) {
    regError.value = 'Заполните все обязательные поля'
    return
  }
  
  
  const roomPattern = /^\d{3}[МБмб]$/
  if (!roomPattern.test(regRoom.value)) {
    regError.value = 'Комната должна быть в формате: 3 цифры + М или Б (например, 107М)'
    return
  }
  
  
  const contractPattern = /^ОБ5-\d{4}$/i
  if (!contractPattern.test(regContract.value)) {
    regError.value = 'Договор должен быть в формате: ОБ5-**** (например, ОБ5-1234)'
    return
  }
  
  if (regPassword.value !== regPasswordConfirm.value) {
    regError.value = 'Пароли не совпадают'
    return
  }
  
  if (regPassword.value.length < 6) {
    regError.value = 'Пароль должен быть не менее 6 символов'
    return
  }
  
  isRegLoading.value = true
  regError.value = ''
  
  try {
    const result = await register({
      username: regUsername.value,
      email: regEmail.value,
      fullName: regFullName.value,
      room: regRoom.value.toUpperCase(),
      contract: regContract.value.toUpperCase(),
      password: regPassword.value
    })
    
    if (result.success) {
      
      regUsername.value = ''
      regEmail.value = ''
      regFullName.value = ''
      regRoom.value = ''
      regContract.value = ''
      regPassword.value = ''
      regPasswordConfirm.value = ''
      
      emit('success')
      closeModal()
    } else {
      regError.value = result.message || 'Ошибка регистрации'
    }
  } catch (error) {
    regError.value = 'Ошибка соединения с сервером'
  } finally {
    isRegLoading.value = false
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

          <!-- Icon -->
          <div class="icon-container">
            <WashingMachineIcon :size="80" />
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
          <form v-if="mode === 'login'" class="auth-form" @submit.prevent="handleLogin">
            <div class="form-fields">
              <BaseInput 
                v-model="loginUsername" 
                type="text" 
                placeholder="Логин (например: admin)"
                :disabled="isLoginLoading"
              >
                <template #icon><UserIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="loginPassword" 
                type="password" 
                placeholder="Пароль"
                :disabled="isLoginLoading"
              >
                <template #icon><LockIcon /></template>
              </BaseInput>
            </div>

            <!-- Контейнер для ошибки с фиксированной высотой -->
            <div class="error-container">
              <div v-if="loginError" class="error-message">
                {{ loginError }}
              </div>
            </div>

            <BaseButton 
              class="submit-btn" 
              :disabled="isLoginLoading"
              @click="handleLogin"
            >
              {{ isLoginLoading ? 'ВХОД...' : 'ВОЙТИ' }}
            </BaseButton>

            <!-- Подсказка для тестирования
            <p class="hint-text">
              Тестовый логин: <strong>admin</strong> или <strong>john_doe</strong><br>
              Пароль: <strong>password123</strong>
            </p> -->
          </form>

          <!-- Register Form -->
          <form v-else class="auth-form" @submit.prevent="handleRegister">
            <div class="form-fields">
              <BaseInput 
                v-model="regUsername" 
                placeholder="Логин"
                :disabled="isRegLoading"
              >
                <template #icon><UserIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="regFullName" 
                placeholder="ФИО"
                :disabled="isRegLoading"
              >
                <template #icon><IdCardIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="regEmail" 
                type="email" 
                placeholder="Email"
                :disabled="isRegLoading"
              >
                <template #icon><UserIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="regRoom" 
                placeholder="Комната (например, 107М)"
                :disabled="isRegLoading"
              >
                <template #icon><HomeIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="regContract" 
                placeholder="Договор найма (например, ОБ5-1234)"
                :disabled="isRegLoading"
              >
                <template #icon><DocumentIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="regPassword" 
                type="password" 
                placeholder="Пароль (мин. 6 символов)"
                :disabled="isRegLoading"
              >
                <template #icon><LockIcon /></template>
              </BaseInput>

              <BaseInput 
                v-model="regPasswordConfirm" 
                type="password" 
                placeholder="Подтвердите пароль"
                :disabled="isRegLoading"
              >
                <template #icon><LockIcon /></template>
              </BaseInput>
            </div>

            <!-- Контейнер для ошибки с фиксированной высотой -->
            <div class="error-container">
              <div v-if="regError" class="error-message">
                {{ regError }}
              </div>
            </div>

            <BaseButton 
              class="submit-btn" 
              :disabled="isRegLoading"
              @click="handleRegister"
            >
              {{ isRegLoading ? 'РЕГИСТРАЦИЯ...' : 'ЗАРЕГИСТРИРОВАТЬСЯ' }}
            </BaseButton>
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
  max-width: 450px;
  max-height: 90vh;
  overflow-y: auto;
  background-color: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 30px 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
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
  margin-bottom: 10px;
}

.modal-title {
  font-size: 24px;
  font-weight: bold;
  color: #2C3E50;
  margin-bottom: 20px;
}

.mode-switcher {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.mode-btn {
  padding: 8px 20px;
  border: 2px solid #2C3E50;
  background: transparent;
  color: #2C3E50;
  border-radius: 20px;
  font-size: 13px;
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
  gap: 15px;
  margin-bottom: 15px;
}

.error-container {
  width: 100%;
  min-height: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.submit-btn {
  min-width: 180px;
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

  .submit-btn {
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

.error-message {
  width: 100%;
  padding: 12px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  color: #ef4444;
  font-size: 14px;
  text-align: center;
}

.hint-text {
  margin-top: 16px;
  padding: 12px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 8px;
  color: #60a5fa;
  font-size: 13px;
  line-height: 1.5;
  text-align: center;

  strong {
    color: #3b82f6;
    font-weight: 600;
  }
}

.info-text {
  margin-top: 8px;
  padding: 10px;
  background: rgba(234, 179, 8, 0.1);
  border: 1px solid rgba(234, 179, 8, 0.3);
  border-radius: 8px;
  color: #eab308;
  font-size: 13px;
  line-height: 1.4;
  text-align: center;
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
