<template>
  <div class="slot-content">
    <div class="role">
      <div contenteditable="true" class="role-main" @blur="getRole(item)"
        @keydown="warpDisable" @paste="styleDisable" ref="roleBox">{{
          //@ts-ignore
          item.role }}</div>
    </div>
    <div class="name-id">
      <slot>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs } from 'vue'
import router from '@/router'

import styleDisable from '@/common/styleDisable'

const props = defineProps<{
  item: any
}>()

const { item } = toRefs(props)

//roleBox dom
const roleBox = ref(null)
/**
 * 获取配役
 * @param item 
 * @param index 
 */
const getRole = (item: any) => {
  const rb = roleBox.value as unknown as HTMLElement
  if (rb) {
    const role = rb.innerText
    item.role = role
  }
}

/**
 * 禁止换行
 * @param e 
 */
const warpDisable = (e: any) => {
  if (e.keyCode === 13) {
    e.preventDefault()
    const rb = roleBox.value as unknown as HTMLElement
    rb.blur()
  }
}
</script>

<style scoped lang="scss">
.slot-content {
  width: 100%;
  display: flex;

  .role {
    width: 7rem;
    @include one-line-hide();

    .role-main {
      width: 100%;

      &:focus {
        content: none;
        outline: none;
        text-decoration: underline;
      }

      &:empty:before {
        content: "点击输入配役";
        color: #cccccc;
      }
    }
  }
}
</style>