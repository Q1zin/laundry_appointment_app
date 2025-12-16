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
    <!-- Skull -->
    <ellipse cx="30" cy="25" rx="20" ry="18" stroke="#000000" stroke-width="2.5" fill="none"/>
    <!-- Eye sockets -->
    <ellipse cx="22" cy="24" rx="5" ry="6" fill="#000000"/>
    <ellipse cx="38" cy="24" rx="5" ry="6" fill="#000000"/>
    <!-- Nose -->
    <path d="M30 30 L28 36 L32 36 Z" fill="#000000"/>
    <!-- Jaw -->
    <path d="M18 38 L18 45 Q18 50 30 50 Q42 50 42 45 L42 38" stroke="#000000" stroke-width="2.5" fill="none"/>
    <!-- Teeth -->
    <line x1="22" y1="42" x2="22" y2="47" stroke="#000000" stroke-width="1.5"/>
    <line x1="26" y1="42" x2="26" y2="47" stroke="#000000" stroke-width="1.5"/>
    <line x1="30" y1="42" x2="30" y2="47" stroke="#000000" stroke-width="1.5"/>
    <line x1="34" y1="42" x2="34" y2="47" stroke="#000000" stroke-width="1.5"/>
    <line x1="38" y1="42" x2="38" y2="47" stroke="#000000" stroke-width="1.5"/>
    <!-- Crossbones -->
    <line x1="5" y1="52" x2="20" y2="38" stroke="#000000" stroke-width="2.5" stroke-linecap="round"/>
    <line x1="5" y1="38" x2="20" y2="52" stroke="#000000" stroke-width="2.5" stroke-linecap="round"/>
    <line x1="40" y1="52" x2="55" y2="38" stroke="#000000" stroke-width="2.5" stroke-linecap="round"/>
    <line x1="40" y1="38" x2="55" y2="52" stroke="#000000" stroke-width="2.5" stroke-linecap="round"/>
  </svg>
</template>
