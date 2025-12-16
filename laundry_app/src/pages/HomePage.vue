<script setup lang="ts">
import TheHeader from '@/components/layout/TheHeader.vue'
import HeroSection from '@/components/sections/HeroSection.vue'
import RulesSection from '@/components/sections/RulesSection.vue'
import HowItWorksSection from '@/components/sections/HowItWorksSection.vue'
import BenefitsSection from '@/components/sections/BenefitsSection.vue'
</script>

<template>
  <div class="home-page">
    <TheHeader />
    <main>
      <HeroSection />
      <RulesSection />
      <HowItWorksSection />
      <BenefitsSection />
    </main>
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
