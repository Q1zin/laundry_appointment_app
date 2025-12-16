<script setup lang="ts">
import { ref } from 'vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import WashingMachineIcon from '@/components/icons/WashingMachineIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'
import LockIcon from '@/components/icons/LockIcon.vue'

const username = ref('')
const password = ref('')
</script>

<template>
  <AuthLayout>
    <BaseCard>
      <!-- Washing Machine Icon -->
      <div class="icon-container">
        <WashingMachineIcon :size="130" />
      </div>

      <!-- Welcome Title -->
      <h1 class="welcome-title">WELCOME!</h1>

      <!-- Username Input -->
      <div class="input-wrapper first">
        <BaseInput
          v-model="username"
          placeholder="Username"
        >
          <template #icon>
            <UserIcon />
          </template>
        </BaseInput>
      </div>

      <!-- Password Input -->
      <div class="input-wrapper">
        <BaseInput
          v-model="password"
          type="password"
          placeholder="Password"
        >
          <template #icon>
            <LockIcon />
          </template>
        </BaseInput>
      </div>

      <!-- Login Button -->
      <div class="button-wrapper">
        <BaseButton>ВОЙТИ</BaseButton>
      </div>
    </BaseCard>
  </AuthLayout>
</template>

<style scoped>
.icon-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.welcome-title {
  font-size: 40px;
  font-weight: bold;
  color: #2C3E50;
  margin-top: 20px;
  text-align: center;
}

.input-wrapper {
  width: 100%;
  margin-top: 40px;
}

.input-wrapper.first {
  margin-top: 60px;
}

.button-wrapper {
  margin-top: 60px;
}

@media (max-width: 680px) {
  .welcome-title {
    font-size: 32px;
  }

  .icon-container :deep(.washing-machine-icon) {
    width: 100px;
    height: 100px;
  }
}
</style>
