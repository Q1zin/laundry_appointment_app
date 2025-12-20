<script setup lang="ts">
</script>

<template>
  <RouterView />
</template>

<style>
/* Omnes Regular (place font files in /public/fonts) */
@font-face {
  font-family: 'Omnes';
  src:
    url('/fonts/Omnes-Regular.woff2') format('woff2'),
    url('/fonts/Omnes-Regular.woff') format('woff'),
    url('/fonts/Omnes-Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

/* Some distributions name the family as "Omnes Regular" */
@font-face {
  font-family: 'Omnes Regular';
  src:
    url('/fonts/Omnes-Regular.woff2') format('woff2'),
    url('/fonts/Omnes-Regular.woff') format('woff'),
    url('/fonts/Omnes-Regular.ttf') format('truetype');
  font-weight: 400;
  font-style: normal;
  font-display: swap;
}

:root {
  --app-font-sans: 'Omnes', 'Omnes Regular', 'Avenir Next', Avenir, Nunito, Futura,
    'Trebuchet MS', 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

html,
body {
  font-family: var(--app-font-sans);
  font-weight: 400;
}

button,
input,
textarea,
select {
  font: inherit;
}
</style>
