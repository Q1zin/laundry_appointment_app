<script setup lang="ts">
import SkullIcon from '@/components/icons/SkullIcon.vue'
import BellIcon from '@/components/icons/BellIcon.vue'
import ClockBackIcon from '@/components/icons/ClockBackIcon.vue'

const props = defineProps<{
  isLoggedIn?: boolean
}>()

const emit = defineEmits<{
  authRequired: []
}>()

const benefits = [
  {
    icon: SkullIcon,
    text: 'ВАШ СЛОТ — ТОЛЬКО ВАШ. НИКТО НЕ ЗАЙМЕТ ВРЕМЯ ВАШЕЙ СТИРКИ.'
  },
  {
    icon: BellIcon,
    text: 'НЕ ЗАБУДЕТЕ О СТИРКЕ. ПРИШЛЁМ ПИСЬМО НА ПОЧТУ ЗА 1 ЧАС И ЕЩЁ РАЗ ЗА 15 МИНУТ ДО'
  },
  {
    icon: ClockBackIcon,
    text: 'НЕ ПРОПУСТИТЕ ОТКРЫТИЕ ЗАПИСИ. В МОМЕНТ СТАРТА ПРИДЁТ УВЕДОМЛЕНИЕ НА EMAIL.'
  }
]

const handleBookingClick = () => {
  if (!props.isLoggedIn) {
    emit('authRequired')
    return
  }
  // TODO: перейти к форме записи
}
</script>

<template>
  <section id="booking" class="benefits-section">
    <!-- Decorative waves -->
    <div class="wave-decoration"></div>

    <div class="section-content">
      <h2 class="section-title">БОЛЬШЕ НИКАКИХ ЗАБОТ!</h2>

      <div class="benefits-list">
        <div 
          v-for="(benefit, index) in benefits" 
          :key="index" 
          class="benefit-item"
        >
          <div class="benefit-icon">
            <component :is="benefit.icon" :size="60" />
          </div>
          <p class="benefit-text">{{ benefit.text }}</p>
        </div>
      </div>

      <button class="cta-button" @click="handleBookingClick">ЗАПИСАТЬСЯ</button>
    </div>
  </section>
</template>

<style scoped>
.benefits-section {
  background-color: #FFFFFF;
  padding: 80px 60px 100px;
  position: relative;
  overflow: hidden;
}

.wave-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 150px;
  background: 
    radial-gradient(ellipse 80% 50% at 20% 0%, rgba(232, 238, 242, 0.5) 0%, transparent 50%),
    radial-gradient(ellipse 80% 50% at 80% 0%, rgba(232, 238, 242, 0.5) 0%, transparent 50%);
  pointer-events: none;
}

.section-content {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.section-title {
  color: #2C3540;
  font-size: 50px;
  font-weight: 800;
  text-align: center;
  margin-bottom: 60px;
}

.benefits-list {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 40px;
}

.benefit-icon {
  flex-shrink: 0;
  width: 80px;
  display: flex;
  justify-content: center;
}

.benefit-text {
  color: #2C3540;
  font-size: 21px;
  line-height: 1.5;
  font-weight: 500;
}

.cta-button {
  display: block;
  margin: 60px auto 0;
  background-color: #3D4F61;
  color: #FFFFFF;
  border: none;
  border-radius: 35px;
  padding: 18px 50px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(61, 79, 97, 0.3);
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.cta-button:hover {
  background-color: #4a6275;
  transform: translateY(-2px);
}

.cta-button:active {
  transform: translateY(0);
}

@media (max-width: 768px) {
  .benefits-section {
    padding: 60px 30px 80px;
  }

  .section-title {
    font-size: 36px;
    margin-bottom: 50px;
  }

  .benefit-item {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .benefit-text {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .benefits-section {
    padding: 50px 20px 70px;
  }

  .section-title {
    font-size: 28px;
  }

  .benefit-text {
    font-size: 16px;
  }

  .cta-button {
    padding: 16px 40px;
    font-size: 16px;
  }
}
</style>
