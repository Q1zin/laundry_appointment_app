<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg 
    :width="size || 130" 
    :height="size || 130" 
    viewBox="0 0 130 130" 
    fill="none"
  >
    <!-- Book -->
    <path d="M20 25 L20 105 Q20 110 25 110 L105 110 Q110 110 110 105 L110 25 Q110 20 105 20 L25 20 Q20 20 20 25 Z" 
          stroke="#3D4F61" stroke-width="3" fill="none"/>
    <!-- Book spine -->
    <line x1="45" y1="20" x2="45" y2="110" stroke="#3D4F61" stroke-width="3"/>
    <!-- Pages lines -->
    <line x1="55" y1="40" x2="100" y2="40" stroke="#3D4F61" stroke-width="2"/>
    <line x1="55" y1="55" x2="100" y2="55" stroke="#3D4F61" stroke-width="2"/>
    <line x1="55" y1="70" x2="100" y2="70" stroke="#3D4F61" stroke-width="2"/>
    <!-- Checkmark circle -->
    <circle cx="35" cy="50" r="12" stroke="#4CAF50" stroke-width="2" fill="none"/>
    <path d="M29 50 L33 54 L41 46" stroke="#4CAF50" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
    <!-- X circle -->
    <circle cx="35" cy="85" r="12" stroke="#E53935" stroke-width="2" fill="none"/>
    <path d="M30 80 L40 90 M40 80 L30 90" stroke="#E53935" stroke-width="2" stroke-linecap="round"/>
  </svg>
</template>
