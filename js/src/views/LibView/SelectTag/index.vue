<template>
  <div class="tags" ref="tagContainer">
    <TagItem 
      class="tag" 
      v-for="(item, id) in tags" :key="id" :item="item"
      @on-delete-tag="deleteTag" />
  </div>
</template>

<script setup lang="ts">
import TagItem from './TagItem.vue'
import { ref } from 'vue'
import type { tag } from '@/api/dataInterfaces'

const props = defineProps<{
  tags: Array<any>
}>()

const emits = defineEmits(['onTagListChange'])

const tagContainer = ref<HTMLElement>()

defineExpose({
  tagContainer
})

const deleteTag = (item: tag)=> {
  const {tags} = props
  tags.some((tag, index)=> {
    if(tag.value === item.value && tag.data === item.data) {
      tags.splice(index, 1)
      return true
    }
    return false
  })
  emits('onTagListChange')
}
</script>

<style lang="scss" scoped>
.tags {
  width: 100%;
  max-height: 35%;
  @include hide-scroll-bar();

  .tag {
    margin-top: 0.25rem;

    &:first-child {
      margin-top: 0;
    }
  }
}
</style>