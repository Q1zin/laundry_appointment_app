<script setup lang="ts">
defineProps<{
  modelValue: string
  type?: string
  placeholder?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>

<template>
  <div class="input-group">
    <div class="input-icon">
      <slot name="icon" />
    </div>
    <input
      :value="modelValue"
      :type="type || 'text'"
      :placeholder="placeholder"
      class="input-field"
      @input="handleInput"
    />
  </div>
</template>

<style scoped>
.input-group {
  width: 100%;
  position: relative;
}

.input-icon {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  color: #2C3E50;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-field {
  width: 100%;
  padding: 15px 0 15px 40px;
  border: none;
  border-bottom: 1px solid #E0E0E0;
  font-size: 16px;
  color: #2C3E50;
  background: transparent;
  outline: none;
  transition: border-color 0.3s ease;
}

.input-field::placeholder {
  color: #B0B0B0;
}

.input-field:focus {
  border-bottom-color: #2C3E50;
}
</style>
