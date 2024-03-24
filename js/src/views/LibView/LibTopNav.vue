<template>
  <div class="content">
    <nav class="top-bar">
      <div class="keyword-search">
        <el-input class="keyword-box" v-model="keyword" placeholder="请输入关键词" @keydown.enter="getInputData" />
        <el-button class="keyword-btn" plain @click="getInputData">搜索</el-button>
      </div>

      <div class="areas">
        <div 
          class="area" 
          @click="chooseArea('releases')"
          :class="{'active': area === 'releases'}">发行</div>
        <div 
          class="area" 
          @click="chooseArea('artists')"
          :class="{'active': area === 'artists'}">艺人</div>
        <div 
          class="area" 
          @click="chooseArea('labels')"
          :class="{'active': area === 'labels'}">厂牌</div>
      </div>
    </nav>
    <div class="page">
      <div class="contribute" @click="router.push(`/contribute/${area}`)">
        去贡献{{areaStr}}条目
      </div>
      <el-pagination class="pagination" v-model:current-page="currentPage" v-model:page-size="pageSize" :small="true"
        @current-change="currentPageChange" layout="prev, pager, next, jumper" :total="total" />
    </div>
    <RouterView />
  </div>
  <div class="main">
    <div class="show-item"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, computed } from 'vue'
import router from "@/router"

const emit = defineEmits(['onGetInputData', 'onGetSearchArea', 'onPageChange'])

const props = defineProps<{
  total: number
  area: string
}>()

//搜索关键词
const keyword = ref('')

//搜索关键词，但是是静态的
let staticKeyword = ''

//当前页码
const currentPage = ref(1)

let releasePage = 1

let artistPage = 1

let labelPage = 1

//每页数据量
const pageSize = ref(12)

//总数据量
const { total } = toRefs(props)

//搜索区域
const { area } = toRefs(props)

const areaStr = computed(()=> {
  let str = ''
  switch(area.value) {
    case "releases":
      str = "发行"
      break
    case "artists":
      str = "艺人"
      break
    case "labels":
      str = "厂牌"
      break
  }
  return str
})

/**
 * 点击搜索后的事件，收集点击时刻的keyword
 */
const getInputData = () => {
  if (staticKeyword !== keyword.value) {
    staticKeyword = keyword.value
    emit('onGetInputData', staticKeyword)
  }
}

/**
 * 搜索的内容，发行/艺人/厂牌
 * @param areaStr 
 */
const chooseArea = (areaStr: string) => {

  if (area.value !== areaStr) {
    // query.area = areaStr
    // doSearch()
    switch (areaStr) {
      case "releases":
        currentPage.value = releasePage
        break
      case "artists":
        currentPage.value = artistPage
        break
      case "labels":
        currentPage.value = labelPage
    }
    emit('onGetSearchArea', areaStr, currentPage.value)

  }
}

const currentPageChange = () => {
  switch (area.value) {
    case "releases":
      releasePage = currentPage.value
      break
    case "artists":
      artistPage = currentPage.value
      break
    case "labels":
      labelPage = currentPage.value
      break
  }
  emit('onPageChange', currentPage.value, pageSize.value)
}

</script>

<style lang="scss" scoped>
.content {
  background-color: gray;
  flex: 1;

  .top-bar {
    width: 100%;
    height: 3rem;
    background-color: #fff;
    display: flex;
    align-items: center;

    .keyword-search {
      .keyword-box {
        width: 20vw;
        margin-left: 1rem;
      }
    }

    .areas {
      display: flex;

      .area {
        font-size: 1.1rem;
        margin-left: 2rem;
        cursor: pointer;

        &:hover {
          color: $test-color;
        }
      }

    }

  }

  .page {
    background-color: #fff;
    padding: 0.25rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .contribute {
      margin-left: 1rem;
      font-size: 0.8rem;
      cursor: pointer;
      &:hover {
        color: $test-color;
      }
    }
  }
}

.active {
  color: $test-color;
}
</style>