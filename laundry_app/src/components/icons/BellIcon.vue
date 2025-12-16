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
    <!-- Bell body -->
    <path d="M30 8 C30 8 20 8 15 18 C10 28 10 38 10 42 L50 42 C50 38 50 28 45 18 C40 8 30 8 30 8 Z" 
          stroke="#000000" stroke-width="2.5" fill="none"/>
    <!-- Bell bottom -->
    <ellipse cx="30" cy="42" rx="20" ry="4" stroke="#000000" stroke-width="2.5" fill="none"/>
    <!-- Clapper -->
    <circle cx="30" cy="52" r="5" stroke="#000000" stroke-width="2.5" fill="none"/>
    <!-- Top loop -->
    <circle cx="30" cy="6" r="3" stroke="#000000" stroke-width="2" fill="none"/>
    <!-- Sound waves -->
    <path d="M54 30 Q58 30 58 35" stroke="#000000" stroke-width="2" fill="none" stroke-linecap="round"/>
    <path d="M6 30 Q2 30 2 35" stroke="#000000" stroke-width="2" fill="none" stroke-linecap="round"/>
  </svg>
</template>
