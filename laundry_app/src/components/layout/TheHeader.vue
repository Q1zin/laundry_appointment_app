<script setup lang="ts">
import { RouterLink } from 'vue-router'

defineProps<{
  variant?: 'light' | 'dark'
  isLoggedIn?: boolean
  userEmail?: string
}>()

const emit = defineEmits<{
  loginClick: []
  bookingClick: []
  rulesClick: []
}>()

const scrollToSection = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<template>
  <header class="header" :class="variant || 'light'">
    <div class="header-content">
      <RouterLink to="/" class="logo">СТИРКА</RouterLink>
      <nav class="nav">
        <button class="nav-link nav-btn" @click="emit('rulesClick')">ПРАВИЛА</button>
        <button class="nav-link nav-btn" @click="emit('bookingClick')">ЗАПИСЬ</button>
        <template v-if="isLoggedIn">
          <RouterLink to="/profile" class="profile-link">
            {{ userEmail }}
          </RouterLink>
        </template>
        <button 
          v-else 
          class="login-btn" 
          @click="emit('loginClick')"
        >
          ВОЙТИ
        </button>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.header {
  width: 100%;
  height: 80px;
  padding: 20px 60px;
}

.header.light {
  background-color: #E8EEF2;
}

.header.dark {
  background-color: #3D4F61;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo {
  font-size: 30px;
  font-weight: bold;
  color: #3D4F61;
  text-decoration: none;
}

.header.dark .logo {
  color: #FFFFFF;
}

.nav {
  display: flex;
  align-items: center;
  gap: 45px;
}

.nav-link {
  color: #3D4F61;
  text-decoration: none;
  font-size: 17px;
  font-weight: 500;
  transition: opacity 0.3s ease;
}

.header.dark .nav-link {
  color: #FFFFFF;
}

.nav-link:hover {
  opacity: 0.7;
}

.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
}

.profile-link {
  color: #3D4F61;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  padding: 10px 20px;
  border-radius: 20px;
  background: rgba(61, 79, 97, 0.1);
  transition: background-color 0.3s ease;
}

.profile-link:hover {
  background: rgba(61, 79, 97, 0.2);
}

.header.dark .profile-link {
  color: #FFFFFF;
  background: rgba(255, 255, 255, 0.15);
}

.header.dark .profile-link:hover {
  background: rgba(255, 255, 255, 0.25);
}

.user-email {
  color: #3D4F61;
  font-size: 16px;
  font-weight: 600;
}

.header.dark .user-email {
  color: #FFFFFF;
}

.login-btn {
  background-color: #3D4F61;
  color: #FFFFFF;
  border: none;
  border-radius: 25px;
  padding: 12px 35px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.header.dark .login-btn {
  background-color: #FFFFFF;
  color: #3D4F61;
}

.login-btn:hover {
  background-color: #2C3E50;
}

.header.dark .login-btn:hover {
  background-color: #f0f0f0;
}

@media (max-width: 768px) {
  .header {
    padding: 15px 20px;
  }

  .logo {
    font-size: 24px;
  }

  .nav {
    gap: 20px;
  }

  .nav-link {
    font-size: 14px;
  }

  .logout-btn,
  .login-btn {
    padding: 10px 25px;
    font-size: 14px;
  }
}
</style>
