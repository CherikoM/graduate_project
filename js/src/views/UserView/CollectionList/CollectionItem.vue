<template>
  <div class="collection-item" @click="emits('onClick', index, item.id)">
    <!-- {{ col.id }}: {{ col.name }} -->
    <div class="pic">
      <img class="pic" :src="'http://localhost:8080/img/'+(item.picture? item.picture: 'noImg.png')">
    </div>
    <div class="words">
      <h3>{{ item.name }}</h3>
      <div class="num">
        <span>内容量：{{ item.size }}条</span>&nbsp;&nbsp;&nbsp;
        <span>收藏量：{{ item.addNum }}</span>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue'

const props = defineProps<{
  item: any
  index: number | string
}>()

const emits = defineEmits(["onClick"])

const { item } = toRefs(props)

</script>

<style scoped lang="scss">
.collection-item {
  margin: 1rem auto;
  width: 30rem;
  padding: 1rem;
  background-color: #fff;
  display: flex;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 0.5rem;
  transition: 0.1s;
  cursor: pointer;
  &:hover {
    box-shadow: var(--el-box-shadow);
  }

  .pic {
    width: 6rem;
    height: 6rem;
    background-color: pink;
    flex: none;
  }

  .words {
    margin-left: 1rem;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    h3 {
      height: 3.5rem;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .num {
      font-size: 0.8rem;
      color: gray;
    }
  }
}</style>