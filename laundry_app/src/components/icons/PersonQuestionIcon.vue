<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg 
    :width="size || 120" 
    :height="size || 120" 
    viewBox="0 0 120 120" 
    fill="none"
  >
    <!-- Person head -->
    <circle cx="60" cy="35" r="18" stroke="#000000" stroke-width="3" fill="none"/>
    <!-- Person body -->
    <path d="M30 100 L30 75 Q30 55 60 55 Q90 55 90 75 L90 100" 
          stroke="#000000" stroke-width="3" fill="none"/>
    <!-- Question marks -->
    <text x="15" y="45" font-size="24" fill="#3D4F61" font-weight="bold">?</text>
    <text x="95" y="30" font-size="20" fill="#3D4F61" font-weight="bold">?</text>
    <text x="100" y="55" font-size="18" fill="#3D4F61" font-weight="bold">?</text>
  </svg>
</template>
