<script setup lang="ts">
defineProps<{
  variant?: 'light' | 'dark'
  isLoggedIn?: boolean
  userName?: string
}>()

const emit = defineEmits<{
  loginClick: []
  logoutClick: []
  bookingClick: []
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
      <div class="logo">СТИРКА-</div>
      <nav class="nav">
        <button class="nav-link nav-btn" @click="scrollToSection('rules')">ПРАВИЛА</button>
        <button class="nav-link nav-btn" @click="emit('bookingClick')">ЗАПИСЬ</button>
        <template v-if="isLoggedIn">
          <span class="user-name">{{ userName }}</span>
          <button 
            class="logout-btn" 
            @click="emit('logoutClick')"
          >
            ВЫХОД
          </button>
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

.user-name {
  color: #3D4F61;
  font-size: 16px;
  font-weight: 600;
}

.header.dark .user-name {
  color: #FFFFFF;
}

.logout-btn,
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

.header.dark .logout-btn,
.header.dark .login-btn {
  background-color: #FFFFFF;
  color: #3D4F61;
}

.logout-btn:hover,
.login-btn:hover {
  background-color: #2C3E50;
}

.header.dark .logout-btn:hover,
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
