<script setup lang="ts">
import { ref, computed } from 'vue'
import TheHeader from '@/components/layout/TheHeader.vue'
import HeroSection from '@/components/sections/HeroSection.vue'
import RulesSection from '@/components/sections/RulesSection.vue'
import HowItWorksSection from '@/components/sections/HowItWorksSection.vue'
import BenefitsSection from '@/components/sections/BenefitsSection.vue'
import AuthModal from '@/components/modals/AuthModal.vue'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, user, logout } = useAuth()

const isAuthModalOpen = ref(false)

const userName = computed(() => user.value?.fullName || user.value?.username || '')

const openAuthModal = () => {
  isAuthModalOpen.value = true
}

const closeAuthModal = () => {
  isAuthModalOpen.value = false
}

const handleLogout = () => {
  logout()
}
</script>

<template>
  <div class="home-page">
    <TheHeader 
      :is-logged-in="isLoggedIn"
      :user-name="userName"
      @login-click="openAuthModal"
      @logout-click="handleLogout"
    />
    <main>
      <HeroSection 
        :is-logged-in="isLoggedIn" 
        @auth-required="openAuthModal" 
      />
      <RulesSection />
      <HowItWorksSection />
      <BenefitsSection 
        :is-logged-in="isLoggedIn" 
        @auth-required="openAuthModal" 
      />
    </main>

    <AuthModal 
      :is-open="isAuthModalOpen" 
      @close="closeAuthModal" 
    />
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #E8EEF2;
}

main {
  width: 100%;
}
</style>
