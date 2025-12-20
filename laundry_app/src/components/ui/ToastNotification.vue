<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const { toasts, removeToast, confirmDialogs } = useToast()

const handleConfirm = (dialog: any) => {
  dialog.onConfirm()
}

const handleCancel = (dialog: any) => {
  dialog.onCancel()
}
</script>

<template>
  <Teleport to="body">
    <!-- Toast Notifications -->
    <div class="toast-container">
      <TransitionGroup name="toast">
        <div 
          v-for="toast in toasts" 
          :key="toast.id"
          class="toast"
          :class="toast.type"
          @click="removeToast(toast.id)"
        >
          <span class="toast-icon">
            <svg v-if="toast.type === 'success'" width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M7 10L9 12L13 8M19 10C19 14.9706 14.9706 19 10 19C5.02944 19 1 14.9706 1 10C1 5.02944 5.02944 1 10 1C14.9706 1 19 5.02944 19 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <svg v-else-if="toast.type === 'error'" width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M10 6V10M10 14H10.01M19 10C19 14.9706 14.9706 19 10 19C5.02944 19 1 14.9706 1 10C1 5.02944 5.02944 1 10 1C14.9706 1 19 5.02944 19 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M10 11V6M10 14H10.01M19 10C19 14.9706 14.9706 19 10 19C5.02944 19 1 14.9706 1 10C1 5.02944 5.02944 1 10 1C14.9706 1 19 5.02944 19 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </span>
          <span class="toast-message">{{ toast.message }}</span>
        </div>
      </TransitionGroup>
    </div>
    
    <!-- Confirm Dialogs -->
    <TransitionGroup name="dialog">
      <div 
        v-for="dialog in confirmDialogs" 
        :key="dialog.id"
        class="dialog-overlay"
        @click.self="handleCancel(dialog)"
      >
        <div class="dialog-card">
          <div class="dialog-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
              <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke-linecap="round"/>
            </svg>
          </div>
          <h3 class="dialog-title">Подтверждение действия</h3>
          <p class="dialog-message">{{ dialog.message }}</p>
          <div class="dialog-actions">
            <button class="dialog-btn cancel-btn" @click="handleCancel(dialog)">
              Отмена
            </button>
            <button class="dialog-btn confirm-btn" @click="handleConfirm(dialog)">
              Подтвердить
            </button>
          </div>
        </div>
      </div>
    </TransitionGroup>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
}

.toast {
  pointer-events: auto;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  min-width: 300px;
  max-width: 500px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.toast:hover {
  transform: translateX(-5px);
}

.toast.success {
  background: #10B981;
  color: white;
}

.toast.error {
  background: #EF4444;
  color: white;
}

.toast.info {
  background: #3B82F6;
  color: white;
}

.toast-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toast-message {
  flex: 1;
}

/* Transition animations */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%) scale(0.8);
}

@media (max-width: 600px) {
  .toast-container {
    left: 20px;
    right: 20px;
  }
  
  .toast {
    min-width: auto;
  }
}

/* Confirm Dialog Styles */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 20px;
}

.dialog-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  max-width: 450px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.dialog-icon {
  margin-bottom: 20px;
}

.dialog-title {
  font-size: 20px;
  font-weight: 600;
  color: #3D4F61;
  margin-bottom: 12px;
}

.dialog-message {
  font-size: 15px;
  color: #6B7280;
  margin-bottom: 24px;
  line-height: 1.5;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  width: 100%;
}

.dialog-btn {
  flex: 1;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.cancel-btn {
  background: #E5E7EB;
  color: #6B7280;
}

.cancel-btn:hover {
  background: #D1D5DB;
}

.confirm-btn {
  background: #EF4444;
  color: white;
}

.confirm-btn:hover {
  background: #DC2626;
}

/* Dialog transitions */
.dialog-enter-active,
.dialog-leave-active {
  transition: all 0.3s ease;
}

.dialog-enter-from,
.dialog-leave-to {
  opacity: 0;
}

.dialog-enter-from .dialog-card,
.dialog-leave-to .dialog-card {
  transform: scale(0.9);
}
</style>
