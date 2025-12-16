<script setup lang="ts">
defineProps<{
  maxWidth?: string
}>()
</script>

<template>
  <div 
    class="base-card"
    :style="{ maxWidth: maxWidth || '620px' }"
  >
    <slot />
  </div>
</template>

<style scoped>
.base-card {
  width: 100%;
  background-color: #FFFFFF;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

@media (max-width: 680px) {
  .base-card {
    margin: 20px;
    padding: 40px 30px;
  }
}
</style>
