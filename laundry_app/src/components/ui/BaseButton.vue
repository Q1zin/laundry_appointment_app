<script setup lang="ts">
defineProps<{
  variant?: 'primary' | 'secondary'
  fullWidth?: boolean
}>()
</script>

<template>
  <button 
    class="base-button" 
    :class="[
      variant || 'primary',
      { 'full-width': fullWidth }
    ]"
  >
    <slot />
  </button>
</template>

<style scoped>
.base-button {
  padding: 15px 60px;
  border: none;
  border-radius: 30px;
  font-size: 17px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.base-button.primary {
  background-color: #2C3E50;
  color: #FFFFFF;
  box-shadow: 0 4px 15px rgba(44, 62, 80, 0.3);
}

.base-button.primary:hover {
  background-color: #1a252f;
}

.base-button.secondary {
  background-color: #FFFFFF;
  color: #2C3E50;
  border: 2px solid #2C3E50;
}

.base-button.secondary:hover {
  background-color: #f5f5f5;
}

.base-button:active {
  transform: scale(0.98);
}

.base-button.full-width {
  width: 100%;
}
</style>
