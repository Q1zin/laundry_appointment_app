<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg :width="size || 130" :height="size || 130" viewBox="0 0 120 120" class="washing-machine-icon">
    <!-- Machine body -->
    <rect x="10" y="10" width="100" height="100" rx="8" fill="#E8E8E8" stroke="#CCCCCC" stroke-width="2"/>
    <!-- Door frame -->
    <circle cx="60" cy="65" r="35" fill="#FFFFFF" stroke="#CCCCCC" stroke-width="2"/>
    <!-- Door glass -->
    <circle cx="60" cy="65" r="28" fill="#E3F2FD" stroke="#90CAF9" stroke-width="1"/>
    <!-- Orange clothing inside -->
    <ellipse cx="55" cy="70" rx="15" ry="12" fill="#FF9800" opacity="0.9"/>
    <ellipse cx="65" cy="62" rx="10" ry="8" fill="#FFB74D" opacity="0.8"/>
    <!-- Control panel -->
    <rect x="20" y="18" width="80" height="18" rx="3" fill="#D0D0D0"/>
    <!-- Buttons -->
    <circle cx="35" cy="27" r="5" fill="#4CAF50"/>
    <circle cx="55" cy="27" r="5" fill="#2196F3"/>
    <rect x="70" y="22" width="20" height="10" rx="2" fill="#333"/>
  </svg>
</template>

<style scoped>
.washing-machine-icon {
  display: block;
}
</style>
