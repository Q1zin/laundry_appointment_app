<script setup lang="ts">
import PersonQuestionIcon from '@/components/icons/PersonQuestionIcon.vue'
import WashingMachineOutlineIcon from '@/components/icons/WashingMachineOutlineIcon.vue'
import TimerIcon from '@/components/icons/TimerIcon.vue'
import ProfileIcon from '@/components/icons/ProfileIcon.vue'

const steps = [
  {
    number: 1,
    title: 'РЕГИСТРАЦИЯ',
    icon: ProfileIcon,
    color: '#C8A090'
  },
  {
    number: 2,
    title: 'ВЫБЕРИ МАШИНКУ',
    icon: WashingMachineOutlineIcon,
    color: '#6B7B8F'
  },
  {
    number: 3,
    title: 'ВЫБЕРИ ДАТУ И ВРЕМЯ',
    icon: TimerIcon,
    color: '#6BA5A0'
  }
]
</script>

<template>
  <section class="how-it-works">
    <div class="section-content">
      <div class="section-header">
        <PersonQuestionIcon :size="120" />
        <h2 class="section-title">КАК ЭТО</h2>
      </div>

      <div class="steps-container">
        <div 
          v-for="step in steps" 
          :key="step.number" 
          class="step-card"
          :style="{ backgroundColor: step.color }"
        >
          <span class="step-label">ШАГ {{ step.number }}</span>
          <h3 class="step-title">{{ step.title }}</h3>
          <component :is="step.icon" :size="80" class="step-icon" />
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.how-it-works {
  background-color: #F5F8FA;
  padding: 80px 60px;
}

.section-content {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60px;
}

.section-title {
  color: #2C3540;
  font-size: 46px;
  font-weight: bold;
  margin-top: 30px;
  text-align: center;
}

.steps-container {
  display: flex;
  justify-content: center;
  gap: 40px;
  flex-wrap: wrap;
}

.step-card {
  width: 300px;
  height: 320px;
  border-radius: 20px;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  color: #FFFFFF;
}

.step-label {
  font-size: 14px;
  font-weight: 500;
  opacity: 0.9;
  margin-bottom: 15px;
}

.step-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
}

.step-icon {
  margin-top: auto;
  color: #FFFFFF;
}

@media (max-width: 1024px) {
  .how-it-works {
    padding: 60px 30px;
  }

  .steps-container {
    gap: 30px;
  }

  .step-card {
    width: 280px;
    height: 300px;
    padding: 40px 30px;
  }
}

@media (max-width: 600px) {
  .how-it-works {
    padding: 50px 20px;
  }

  .section-title {
    font-size: 36px;
  }

  .steps-container {
    flex-direction: column;
    align-items: center;
    gap: 25px;
  }

  .step-card {
    width: 100%;
    max-width: 320px;
    height: auto;
    padding: 35px 30px;
  }

  .step-icon {
    margin-top: 20px;
  }
}
</style>
