<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg 
    :width="size || 60" 
    :height="size || 60" 
    viewBox="0 0 60 60" 
    fill="none"
  >
    <!-- Clock circle -->
    <circle cx="32" cy="32" r="24" stroke="#000000" stroke-width="2.5" fill="none"/>
    <!-- Clock hands -->
    <line x1="32" y1="32" x2="32" y2="16" stroke="#000000" stroke-width="2.5" stroke-linecap="round"/>
    <line x1="32" y1="32" x2="44" y2="32" stroke="#000000" stroke-width="2.5" stroke-linecap="round"/>
    <!-- Center dot -->
    <circle cx="32" cy="32" r="3" fill="#000000"/>
    <!-- Back arrow -->
    <path d="M8 20 L8 8 L20 8" stroke="#000000" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
    <path d="M8 8 Q2 18 8 28" stroke="#000000" stroke-width="2.5" fill="none" stroke-linecap="round"/>
  </svg>
</template>
