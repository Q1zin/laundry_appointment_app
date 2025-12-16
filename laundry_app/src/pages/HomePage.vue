<script setup lang="ts">
import { ref } from 'vue'
import TheHeader from '@/components/layout/TheHeader.vue'
import HeroSection from '@/components/sections/HeroSection.vue'
import RulesSection from '@/components/sections/RulesSection.vue'
import HowItWorksSection from '@/components/sections/HowItWorksSection.vue'
import BenefitsSection from '@/components/sections/BenefitsSection.vue'
import AuthModal from '@/components/modals/AuthModal.vue'

const isAuthModalOpen = ref(false)
const isLoggedIn = ref(false)

const openAuthModal = () => {
  isAuthModalOpen.value = true
}

const closeAuthModal = () => {
  isAuthModalOpen.value = false
}

const handleLogout = () => {
  isLoggedIn.value = false
}
</script>

<template>
  <div class="home-page">
    <TheHeader 
      :is-logged-in="isLoggedIn"
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
