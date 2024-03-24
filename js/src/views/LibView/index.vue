<template>
  <div class="lib-main">
    <LibSideBar class="lib-side-bar" :area="area" @on-query-collect="collectQuery"
      :class="sideBarOpen ? 'open' : 'close'" />
    <div class="show-btn" @click="sideBarOpen = !sideBarOpen">
      {{ sideBarOpen ? "收起" : "展开" }}
    </div>

    <div class="right">
      <LibTopNav @on-get-input-data="getInputData" @on-get-search-area="getSearchArea" @on-page-change="pageChange"
        :total="total" :area="area" />

      <div class="loading" v-loading="isLoading"></div>
      <LibDataList ref="libDataList" v-show="!isLoading" :listData="resultList" :area="area" :connect="connect" />
    </div>

  </div>
</template>

<script lang="ts" setup>
import LibSideBar from '@/views/LibView/LibSideBar.vue'
import LibTopNav from './LibTopNav.vue'
import LibDataList from './LibDataList.vue'

import { ref, onMounted } from 'vue'
import { RouterView } from 'vue-router'

import type { tag, tagSearchQuery } from '@/api/dataInterfaces'
import { styleDataType, tagData, format, form } from '@/api/dataInterfaces'
import { tagSearch } from '@/api/commonAPI'

import merge from 'webpack-merge'

import sleep from '@/common/sleep'

const emit = defineEmits(['onQueryChange'])

const connect = ref(true)

//#region 数据

//搜索排序
let order = 1

//当前页码
let currentPage = 1

//每页数据量
let pageSize = 12

//总数据量
let total = ref(0)

//搜索范围
let area = ref('releases')

//搜索结果
const resultList = ref([])

//错误信息
const errMsg = ref('')

//是否正在加载
const isLoading = ref(true)

//数据列表组件
const libDataList = ref<any>()

const sideBarOpen = ref(false)

//发送请求时使用的参数
const query: tagSearchQuery = {
  keyword: null,
  genre: null,
  style: null,
  format: null,
  form: null,
  country: null,
  decade: null,
  area: 'releases',
  order: order,
  currentPage: 1,
  pageSize: 12
}
//#endregion

//#region 方法
/**
 * @on-query-collect的响应方法，用于整理发送请求时使用的参数（query）
 * @param selectedTags 
 */
const collectQuery = (selectedTags: Array<tag>, o: number) => {

  const makeQuery = {}

  for (const key of Object.keys(query)) {
    Object.defineProperty(makeQuery, key, { value: [] })
  }

  selectedTags.forEach(tag => {
    let qdata: string = tag.data

    let qvalue: string = tag.name as string
    if (qdata === styleDataType.genre || qdata === styleDataType.style) {
      qvalue = tag.enName as string
    }
    else if (qdata === tagData.format) {
      //@ts-ignore
      qvalue = format[qvalue] + 1
    } else if (qdata === tagData.form) {
      //@ts-ignore
      qvalue = form[qvalue] + 1
    } else if (qdata === tagData.decade) {
      qvalue = tag.name as string
    } else if (qdata === tagData.country) {
      qvalue = tag.value
    }
    //@ts-ignore
    makeQuery[qdata].push(qvalue)
  })

  for (const key of Object.keys(query)) {
    //@ts-ignore
    const arr: Array<string> = makeQuery[key]
    const length: number = arr.length

    if (key === 'area' || key === 'keyword') continue

    if (length > 0) {
      //@ts-ignore
      query[key] = arr
    } else {
      //@ts-ignore
      query[key] = null
    }
  }

  order = o
  query.order = order
  doSearch()
}

/**
 * @on-get-input-data的响应方法，用于获取输入框内的关键词
 * @param keyword
 */
const getInputData = (keyword: string) => {
  query.keyword = keyword === "" ? null : keyword
  doSearch()
}

/**
 * @on-get-search-area的响应方法，用于获取涉及的区域
 * @param area 
 */
const getSearchArea = (a: string, page: number) => {
  query.area = a
  query.currentPage = page
  area.value = a
  currentPage = page
  doSearch()
  libDataList.value.scrollToTop()
}

const pageChange = (cp: number, ps: number) => {
  currentPage = cp
  pageSize = ps
  doSearch()
  libDataList.value.scrollToTop()
}

/**
 * 正式进行搜索
 */
const doSearch = async () => {
  isLoading.value = true
  query.pageSize = pageSize
  query.currentPage = currentPage
  const result = await tagSearch(query)
  if (typeof (result) !== 'string') {
    total.value = result.totalCount
    resultList.value = result.data
    errMsg.value = ''
  } else if (result === 'Net error!') {
    connect.value = false
  } else {
    resultList.value = []
    errMsg.value = result
  }
  await sleep(500)
  isLoading.value = false
}

const abc123 = () => {

}

/**
 * 在页面挂载完成时进行一次搜索
 */
onMounted(() => {
  doSearch()
})

//#endregion
</script>

<style lang="scss" scoped>
.lib-main {
  display: flex;

  .lib-side-bar {
    transition: 0.2s;

    @media screen and (max-width: 900px) {
      position: absolute;
      z-index: 99;
    }
  }

  @media screen and (max-width: 900px) {
    .open {
      transform: translateX(0rem);
    }

    .close {
      transform: translateX(-16rem);
    }
  }



  .show-btn {
    visibility: hidden;
    position: fixed;
    bottom: 0.5rem;
    left: 0.5rem;
    background-color: #fff;
    padding: 0.5rem 1rem;
    border-radius: 50%;
    z-index: 100;
    transition: 0.2s;
    box-shadow: var(--el-box-shadow-light);
    cursor: pointer;

    &:hover {
      box-shadow: var(--el-box-shadow);
    }

    @media screen and (max-width: 900px) {
      visibility: visible;
    }
  }

  .right {
    flex: 1;

    .loading {
      position: relative;
      top: 40%;
    }

    .lib-data-list {
      width: 100%;
    }
  }
}
</style>
