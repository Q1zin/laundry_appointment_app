<script setup lang="ts">
import { ref } from 'vue'
import TheHeader from '@/components/layout/TheHeader.vue'
import HeroSection from '@/components/sections/HeroSection.vue'
import RulesSection from '@/components/sections/RulesSection.vue'
import HowItWorksSection from '@/components/sections/HowItWorksSection.vue'
import BenefitsSection from '@/components/sections/BenefitsSection.vue'
import AuthModal from '@/components/modals/AuthModal.vue'
import BookingModal from '@/components/modals/BookingModal.vue'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn } = useAuth()

const isAuthModalOpen = ref(false)
const isBookingModalOpen = ref(false)

const openAuthModal = () => {
  isAuthModalOpen.value = true
}

const closeAuthModal = () => {
  isAuthModalOpen.value = false
}

const openBookingModal = () => {
  isBookingModalOpen.value = true
}

const closeBookingModal = () => {
  isBookingModalOpen.value = false
}

const handleBookingClick = () => {
  if (isLoggedIn.value) {
    openBookingModal()
  } else {
    openAuthModal()
  }
}

const scrollToRules = () => {
  const element = document.getElementById('rules')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<template>
  <div class="home-page">
    <TheHeader 
      @login-click="openAuthModal"
      @booking-click="handleBookingClick"
      @rules-click="scrollToRules"
    />
    <main>
      <HeroSection @booking-click="handleBookingClick" />
      <RulesSection />
      <HowItWorksSection />
      <BenefitsSection @booking-click="handleBookingClick" />
    </main>

    <AuthModal 
      :is-open="isAuthModalOpen" 
      @close="closeAuthModal" 
    />

    <BookingModal 
      :is-open="isBookingModalOpen" 
      @close="closeBookingModal" 
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
