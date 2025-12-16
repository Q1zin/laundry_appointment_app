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
    <!-- Machine body -->
    <rect x="8" y="8" width="64" height="64" rx="6" stroke-width="2.5"/>
    <!-- Door -->
    <circle cx="40" cy="45" r="20" stroke-width="2.5"/>
    <!-- Inner drum -->
    <circle cx="40" cy="45" r="14" stroke-width="1.5" stroke-dasharray="4 2"/>
    <!-- Control panel -->
    <rect x="14" y="14" width="52" height="12" rx="2" stroke-width="2"/>
    <!-- Buttons -->
    <circle cx="24" cy="20" r="3" stroke-width="1.5"/>
    <circle cx="36" cy="20" r="3" stroke-width="1.5"/>
    <rect x="48" y="17" width="12" height="6" rx="1" stroke-width="1.5"/>
  </svg>
</template>
