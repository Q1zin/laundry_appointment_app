import { watch } from 'vue'
import type { Ref } from 'vue'

export function useBodyScrollLock(isLocked: Ref<boolean>) {
  watch(isLocked, (locked) => {
    if (locked) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = ''
    }
  }, { immediate: true })
}
