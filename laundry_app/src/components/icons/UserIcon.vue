<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg 
    :width="size || 24" 
    :height="size || 24" 
    viewBox="0 0 24 24" 
    fill="none" 
    stroke="currentColor" 
    stroke-width="2"
  >
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
    <circle cx="12" cy="7" r="4"/>
  </svg>
</template>
