<script setup lang="ts">
defineProps<{
  size?: number
}>()
</script>

<template>
  <svg 
    :width="size || 400" 
    :height="size || 400" 
    viewBox="0 0 400 400" 
    fill="none"
  >
    <!-- Shadow -->
    <ellipse cx="200" cy="380" rx="140" ry="15" fill="#00000020"/>
    
    <!-- Machine body -->
    <rect x="60" y="40" width="280" height="330" rx="20" fill="#E0E0E0"/>
    <rect x="65" y="45" width="270" height="320" rx="18" fill="linear-gradient(180deg, #F5F5F5 0%, #BDBDBD 100%)"/>
    
    <!-- Top panel gradient -->
    <rect x="70" y="50" width="260" height="80" rx="10" fill="#EEEEEE"/>
    <rect x="70" y="50" width="260" height="80" rx="10" fill="url(#topPanelGradient)"/>
    
    <!-- Control knob -->
    <circle cx="120" cy="90" r="25" fill="#9E9E9E"/>
    <circle cx="120" cy="90" r="20" fill="#BDBDBD"/>
    <circle cx="120" cy="90" r="3" fill="#757575"/>
    <line x1="120" y1="75" x2="120" y2="82" stroke="#616161" stroke-width="2"/>
    
    <!-- Display panel -->
    <rect x="170" y="65" width="140" height="50" rx="5" fill="#263238"/>
    <text x="240" y="98" font-family="monospace" font-size="24" fill="#4FC3F7" text-anchor="middle">30°C</text>
    
    <!-- Door frame -->
    <circle cx="200" cy="250" r="110" fill="#9E9E9E"/>
    <circle cx="200" cy="250" r="100" fill="#BDBDBD"/>
    
    <!-- Door glass -->
    <circle cx="200" cy="250" r="85" fill="#E3F2FD"/>
    <circle cx="200" cy="250" r="85" fill="url(#glassGradient)"/>
    
    <!-- Inner drum -->
    <circle cx="200" cy="250" r="70" fill="#90CAF9" opacity="0.5"/>
    <circle cx="200" cy="250" r="70" stroke="#64B5F6" stroke-width="2" fill="none" stroke-dasharray="8 4"/>
    
    <!-- Clothes inside -->
    <ellipse cx="185" cy="260" rx="30" ry="25" fill="#FF7043" opacity="0.8"/>
    <ellipse cx="215" cy="245" rx="25" ry="20" fill="#42A5F5" opacity="0.7"/>
    <ellipse cx="200" cy="270" rx="20" ry="15" fill="#66BB6A" opacity="0.7"/>
    
    <!-- Door handle -->
    <rect x="285" y="240" width="15" height="30" rx="5" fill="#757575"/>
    
    <!-- Reflection on glass -->
    <ellipse cx="160" cy="210" rx="30" ry="15" fill="#FFFFFF" opacity="0.3" transform="rotate(-30 160 210)"/>
    
    <!-- Bottom panel -->
    <rect x="80" y="340" width="240" height="20" rx="5" fill="#9E9E9E"/>
    
    <!-- Legs -->
    <rect x="90" y="360" width="30" height="15" rx="3" fill="#757575"/>
    <rect x="280" y="360" width="30" height="15" rx="3" fill="#757575"/>
    
    <!-- Brand label -->
    <rect x="155" y="145" width="90" height="20" rx="3" fill="#CFD8DC"/>
    
    <!-- Gradients -->
    <defs>
      <linearGradient id="topPanelGradient" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stop-color="#FAFAFA"/>
        <stop offset="100%" stop-color="#E0E0E0"/>
      </linearGradient>
      <radialGradient id="glassGradient" cx="30%" cy="30%" r="70%">
        <stop offset="0%" stop-color="#FFFFFF" stop-opacity="0.4"/>
        <stop offset="100%" stop-color="#90CAF9" stop-opacity="0.1"/>
      </radialGradient>
    </defs>
  </svg>
</template>
