<script setup lang="ts">
import BookRulesIcon from '@/components/icons/BookRulesIcon.vue'

const rules = [
  'МАКСИМУМ ДВЕ АКТИВНЫЕ ЗАПИСИ НА ОДНОГО СТУДЕНТА',
  'СТИРАЙТЕ СТРОГО В СВОЁ ВРЕМЯ, УКАЗАННОЕ В ЗАПИСИ',
  'ДОПУСКАЕТСЯ ТОЛЬКО ОДИН ЗАПУСК МАШИНКИ',
  'ЗАГРУЖАЙТЕ ОТ 1 ДО 3 КГ БЕЛЬЯ',
  'ПЕРЕД СТИРКОЙ ОБЯЗАТЕЛЬНО ПРОВЕРЬТЕ КАРМАНЫ — НИКАКИХ ПОСТОРОННИХ ПРЕДМЕТОВ!',
  'ПОСЛЕ СТИРКИ УБЕДИТЕСЬ, ЧТО: БАРАБАН ОТКРЫТ; ВНУТРИ НЕТ ЗАБЫТЫХ ВЕЩЕЙ',
  'ЕСЛИ МАШИНКА РАБОТАЕТ НЕКОРРЕКТНО — СООБЩИТЕ СТУДСОВЕТУ',
  'РУЧНАЯ СТИРКА — НЕ ДЛЯ МАШИНКИ',
  'НЕ ПРОПУСКАЙТЕ СТИРКУ, ЕСЛИ ЗАПИСАЛИСЬ'
]
</script>

<template>
  <section id="rules" class="rules-section">
    <!-- Decorative waves -->
    <div class="wave-decoration wave-top"></div>
    
    <div class="rules-content">
      <div class="section-header">
        <BookRulesIcon :size="130" />
        <h2 class="section-title">ПРАВИЛА ПОЛЬЗОВАНИЯ ПРАЧЕЧНОЙ</h2>
      </div>

      <div class="rules-grid">
        <div 
          v-for="(rule, index) in rules" 
          :key="index" 
          class="rule-card"
        >
          <div class="rule-number">{{ index + 1 }}</div>
          <p class="rule-text">{{ rule }}</p>
        </div>
      </div>
    </div>

    <div class="wave-decoration wave-bottom"></div>
  </section>
</template>

<style scoped>
.rules-section {
  background-color: #E8EEF2;
  padding: 80px 60px;
  position: relative;
  overflow: hidden;
}

.wave-decoration {
  position: absolute;
  left: 0;
  right: 0;
  height: 100px;
  background: 
    radial-gradient(circle at 10% 50%, transparent 20px, #E8EEF2 21px, #E8EEF2 30px, transparent 31px),
    radial-gradient(circle at 30% 50%, transparent 20px, #E8EEF2 21px, #E8EEF2 30px, transparent 31px),
    radial-gradient(circle at 50% 50%, transparent 20px, #E8EEF2 21px, #E8EEF2 30px, transparent 31px),
    radial-gradient(circle at 70% 50%, transparent 20px, #E8EEF2 21px, #E8EEF2 30px, transparent 31px),
    radial-gradient(circle at 90% 50%, transparent 20px, #E8EEF2 21px, #E8EEF2 30px, transparent 31px);
  pointer-events: none;
  opacity: 0.5;
}

.wave-top {
  top: 0;
}

.wave-bottom {
  bottom: 0;
  transform: rotate(180deg);
}

.rules-content {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.section-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60px;
}

.section-title {
  color: #2C3540;
  font-size: 40px;
  font-weight: bold;
  margin-top: 30px;
  text-align: center;
}

.rules-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.rule-card {
  background-color: #3D4F61;
  border-radius: 15px;
  padding: 40px 30px;
  min-height: 220px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.rule-number {
  width: 50px;
  height: 50px;
  border: 2px solid #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 25px;
  flex-shrink: 0;
}

.rule-text {
  color: #FFFFFF;
  font-size: 17px;
  line-height: 1.5;
  font-weight: 500;
}

@media (max-width: 1024px) {
  .rules-section {
    padding: 60px 30px;
  }

  .rules-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .section-title {
    font-size: 32px;
  }
}

@media (max-width: 600px) {
  .rules-section {
    padding: 50px 20px;
  }

  .rules-grid {
    grid-template-columns: 1fr;
  }

  .section-title {
    font-size: 26px;
  }

  .rule-card {
    padding: 30px 25px;
    min-height: auto;
  }
}
</style>
