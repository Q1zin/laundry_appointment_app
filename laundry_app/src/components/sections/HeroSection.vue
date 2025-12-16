<script setup lang="ts">
import WashingMachine3DIcon from '@/components/icons/WashingMachine3DIcon.vue'

const emit = defineEmits<{
  bookingClick: []
}>()
</script>

<template>
  <section class="hero">
    <div class="hero-content">
      <div class="hero-text">
        <h1 class="hero-title">СТИРКА ПОД КОНТРОЛЕМ!</h1>
        <div class="hero-divider"></div>
        <p class="hero-description">
          ТЕПЕРЬ ВЫ НЕ ЗАБУДЕТЕ О СТИРКЕ: МЫ НАПОМНИМ ЗАРАНЕЕ, ПОДСКАЖЕМ, КОГДА ОТКРОЕТСЯ ЗАПИСЬ, И НИКТО НЕ ЗАЙМЕТ ВАШЕ ВРЕМЯ.
        </p>
        <button class="hero-button" @click="emit('bookingClick')">ЗАПИСАТЬСЯ</button>
      </div>
      <div class="hero-image">
        <WashingMachine3DIcon :size="380" />
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero {
  background-color: #3D4F61;
  border-radius: 20px;
  margin: 40px 60px;
  padding: 80px 100px;
  min-height: 500px;
}

.hero-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 60px;
}

.hero-text {
  flex: 1;
  max-width: 600px;
}

.hero-title {
  color: #FFFFFF;
  font-size: 52px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 20px;
}

.hero-divider {
  width: 100%;
  max-width: 700px;
  height: 2px;
  background-color: rgba(255, 255, 255, 0.3);
  margin: 30px 0;
}

.hero-description {
  color: #FFFFFF;
  font-size: 19px;
  line-height: 1.6;
  margin-bottom: 50px;
}

.hero-button {
  background-color: #FFFFFF;
  color: #3D4F61;
  border: none;
  border-radius: 30px;
  padding: 16px 45px;
  font-size: 17px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.hero-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.hero-button:active {
  transform: translateY(0);
}

.hero-image {
  flex-shrink: 0;
}

@media (max-width: 1024px) {
  .hero {
    margin: 20px 30px;
    padding: 50px 40px;
  }

  .hero-content {
    flex-direction: column;
    text-align: center;
  }

  .hero-text {
    max-width: 100%;
  }

  .hero-title {
    font-size: 38px;
  }

  .hero-description {
    font-size: 17px;
  }

  .hero-image :deep(svg) {
    width: 280px;
    height: 280px;
  }
}

@media (max-width: 600px) {
  .hero {
    margin: 15px;
    padding: 40px 25px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-description {
    font-size: 15px;
  }
}
</style>
