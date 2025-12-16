<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg 
    :width="size || 80" 
    :height="size || 80" 
    viewBox="0 0 80 80" 
    fill="none"
    stroke="currentColor"
  >
    <!-- Head -->
    <circle cx="40" cy="28" r="14" stroke-width="2.5"/>
    <!-- Body -->
    <path d="M16 72 L16 58 Q16 42 40 42 Q64 42 64 58 L64 72" 
          stroke-width="2.5" fill="none"/>
  </svg>
</template>
