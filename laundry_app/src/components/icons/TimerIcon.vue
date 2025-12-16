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
    <!-- Main circle -->
    <circle cx="40" cy="44" r="28" stroke-width="2.5"/>
    <!-- Top button -->
    <rect x="35" y="8" width="10" height="8" rx="2" stroke-width="2"/>
    <!-- Side button -->
    <path d="M62 24 L68 18" stroke-width="2.5" stroke-linecap="round"/>
    <!-- Clock hands -->
    <line x1="40" y1="44" x2="40" y2="28" stroke-width="2.5" stroke-linecap="round"/>
    <line x1="40" y1="44" x2="52" y2="44" stroke-width="2.5" stroke-linecap="round"/>
    <!-- Center dot -->
    <circle cx="40" cy="44" r="3" fill="currentColor"/>
    <!-- Hour marks -->
    <line x1="40" y1="20" x2="40" y2="24" stroke-width="2" stroke-linecap="round"/>
    <line x1="40" y1="64" x2="40" y2="68" stroke-width="2" stroke-linecap="round"/>
    <line x1="16" y1="44" x2="20" y2="44" stroke-width="2" stroke-linecap="round"/>
    <line x1="60" y1="44" x2="64" y2="44" stroke-width="2" stroke-linecap="round"/>
  </svg>
</template>
