<template>
  <div class="data-list-main">
    <el-empty v-show="!haveListData && connect" class="empty" description="未找到资料">
      <el-button plain @click="router.push(`/contribute/${area}`)">去贡献资料</el-button>
    </el-empty>
    <el-empty v-show="!haveListData && !connect" class="empty" description="网络连接失败" />
    <div class="data-list" ref="dataList" v-show="haveListData">
      <LibDataItem 
        class="data-list-item"
        v-show="haveListData" 
        v-for="(item, id) in listData" 
        :key="id"
        :img="item.picture"
        @click="toNewBlank(`/info/${area}/${item.id}`)">
        <template v-slot:title>
          {{ item.title || item.name }}
        </template>
        <template v-slot:sub>
          {{ item.id }}
        </template>
      </LibDataItem>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, toRefs, computed, ref } from 'vue'
import LibDataItem from '@/views/LibView/LibDataItem.vue'

import toNewBlank from '@/common/toNewBlank'


import router from '@/router'


const props = defineProps<{
  listData: Array<any>
  area: string
  connect: boolean
}>()

//展示数据的列表
const dataList = ref<HTMLElement>()

const { listData, area, connect } = toRefs(props)

const haveListData = computed(() => listData.value.length !== 0)


const scrollToTop = ()=> {
  //@ts-ignore
  dataList.value.scrollTop = 0
}

onMounted(async () => {
  setTimeout(() => {
    
  }, 1000);
})

defineExpose({
  scrollToTop
})
</script>

<style lang="scss" scoped>

:deep(.empty) {
  position: relative;
  top: 20vh;
}

.data-list {
  height: fit-content;
  max-height: calc(100vh - 9rem);
  @include hide-scroll-bar();
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: min-content;
  grid-row-gap: 1rem;
  padding: 0.5rem 0;
  transition: 0.2s;
  @media screen and (min-width:1200px) {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }

  @media screen and (max-width: 600px) {
    grid-template-columns: 1fr 1fr;
  }
  .data-list-item {
    // height: 4rem;
    cursor: pointer;
  }
}
</style>