<script setup lang="ts">
defineProps<{
  size?: number
  color?: string
}>()
</script>

<template>
  <svg 
    :width="size || 24" 
    :height="size || 24" 
    viewBox="0 0 24 24" 
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <rect 
      x="3" 
      y="4" 
      width="18" 
      height="18" 
      rx="2" 
      :stroke="color || '#3D4F61'" 
      stroke-width="2"
    />
    <path 
      d="M16 2V6" 
      :stroke="color || '#3D4F61'" 
      stroke-width="2" 
      stroke-linecap="round"
    />
    <path 
      d="M8 2V6" 
      :stroke="color || '#3D4F61'" 
      stroke-width="2" 
      stroke-linecap="round"
    />
    <path 
      d="M3 10H21" 
      :stroke="color || '#3D4F61'" 
      stroke-width="2"
    />
  </svg>
</template>
