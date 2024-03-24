<template>
  <div class="tag">
    <div class="tag-name">{{ item.value }}</div>
    <div class="close" @click="emit('onDeleteTag', item)">×</div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  item: any
}>()

const emit = defineEmits(['onDeleteTag'])


</script>

<style lang="scss" scoped>
.tag {
  width: 100%;
  height: 2rem;
  background-color: white;
  margin: 0 auto;
  display: flex;

  .tag-name {
    width: calc(100% - 2rem);
    height: 2rem;
    line-height: 2rem;
    background-color: white;
    text-indent: 0.25rem;
    //单行文字省略号
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .close {
    width: 2rem;
    height: 2rem;
    text-align: center;
    line-height: 2rem;
    cursor: pointer;
    &:hover {
      color: rgb(9, 182, 141);
    }
  }
}
</style>