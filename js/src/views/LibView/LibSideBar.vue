<template>
  <div>
    <aside>
      <!-- 标签夹 -->
      <SelectTag 
        :tags="selectedTags" 
        ref="selectTag" 
        @on-tag-list-change="emit('onQueryCollect', selectedTags, orderBy)" 
        :style="{ 'visibility': area === 'releases'? 'visible': 'hidden' }"
        />

      <div class="tags">
        <!-- 标签选择夹 -->
        <div class="tag-box">
          <!-- 流派选择框 -->
          <el-autocomplete class="tag-selector" v-model="genre" placeholder="选择流派"
            :fetch-suggestions="(item: string, cb: any) => { cleanUpList(item, cb, styleDataType.genre) }"
            @select="((item: tag) => { handleSelect(item, styleDataType.genre) })" 
            :disabled="area !== 'releases'"
            />
          <!-- 风格选择框 -->
          <div class="tag-selector-with-btn">
            <el-autocomplete class="tag-selector" v-model="style" placeholder="选择风格"
              :fetch-suggestions="(item: string, cb: any) => { cleanUpList(item, cb, styleDataType.style) }"
              @select="((item: tag) => { handleSelect(item, styleDataType.style) })"
              :disabled="area !== 'releases'" />
          </div>
          <!-- 格式选择框 -->
          <el-autocomplete class="tag-selector" v-model="format" placeholder="选择格式"
            :fetch-suggestions="(item: string, cb: any) => { cleanUpList(item, cb, tagData.format) }"
            @select="((item: tag) => { handleSelect(item, tagData.format) })"
            :disabled="area !== 'releases'" />
          <!-- 形式选择框 -->
          <el-autocomplete class="tag-selector" v-model="form" placeholder="选择形式"
            :fetch-suggestions="(item: string, cb: any) => { cleanUpList(item, cb, tagData.form) }"
            @select="((item: tag) => { handleSelect(item, tagData.form) })"
            :disabled="area !== 'releases'" />
          <!-- 国家选择框 -->
          <el-autocomplete class="tag-selector" v-model="country" placeholder="选择国家/地区"
            :fetch-suggestions="(item: string, cb: any) => { cleanUpList(item, cb, tagData.country) }"
            @select="((item: tag) => { handleSelect(item, tagData.country) })"
            :disabled="area !== 'releases'" />
          <!-- 国家选择框 -->
          <el-autocomplete class="tag-selector" v-model="decade" placeholder="选择发行时间"
            :fetch-suggestions="(item: string, cb: any) => { cleanUpList(item, cb, tagData.decade) }"
            @select="((item: tag) => { handleSelect(item, tagData.decade) })"
            :disabled="area !== 'releases'" />
        </div>
        <el-select 
          class="order-by-selector" 
          v-model="orderBy" 
          placeholder="选择排序方式"
          @change="onSelectOptionChange">
          <el-option
            class="order-by-options"
            v-for="item in orderOption"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <p>
          缺少流派/风格？<a href="javascript:;">去编写</a>
        </p>

        <div class="select-mode">
          <el-switch v-model="isActive" class="mb-2" active-text="选了就搜" inactive-text="选完再搜" />
          <el-button plain :disabled="isActive" @click="emitSelectToParent()">选好了</el-button>
        </div>
      </div>
    </aside>
    <!-- <el-dialog v-model="dialogTableVisible" title="Shipping address" width="70%">
      <el-cascader-panel class="cascader-panel" :options="styleGroupDetail" :props="cascaderProps" @change="aaa" />

    </el-dialog> -->
  </div>

</template>

<script setup lang="ts">
import SelectTag from '@/views/LibView/SelectTag/index.vue'

import { ref, reactive, toRefs } from 'vue'
import type { tag } from '@/api/dataInterfaces'
import { styleDataType, tagData, format as formatEnum, form as formEnum } from '@/api/dataInterfaces'

import { useStyleStore } from '@/stores/style'
import { useCommonStore } from '@/stores/common'

const styleStore = useStyleStore()
const commonStore = useCommonStore()

const emit = defineEmits(['onQueryCollect'])

const props = defineProps<{
  area: string
}>()

//#region 数据
const genre = ref('')
const style = ref('')
const format = ref('')
const form = ref('')
const country = ref('')
const decade = ref('')

//被选的tag
const selectedTags: Array<tag> = reactive([])

//SelectTag组件
const selectTag = ref()

//排序方式
const orderBy = ref(1)

//开启“选了就搜”
const isActive = ref(true)

//选择的区域
let {area} = toRefs(props)

//排序方式选项
const orderOption = [
  {
    value: 1,
    label: '最近收录'
  },
  {
    value: 2,
    label: '最近更新'
  }
]
//#endregion

let searchParams = new URLSearchParams()

//#region el-autocomplete api接口操作
/**
 * 当选择一个选项后，执行的操作
 * @param item 选择对应的对象
 * @param type 输入框种类
 */
const handleSelect = (item: any, type: string) => {
  //清空输入框
  switch (type) {
    case styleDataType.genre:
      genre.value = ''
      break
    case styleDataType.style:
      style.value = ''
      break
    case tagData.format:
      format.value = ''
      break
    case tagData.form:
      form.value = ''
      break
    case tagData.country:
      country.value = ''
      break
    case tagData.decade:
      decade.value = ''
      break
    default:
      return
  }
  //失去焦点，将被选项加入已选
  // @ts-ignore
  document.activeElement?.blur()
  selectedTags.push(item)
  const tagContainer = selectTag.value.tagContainer
  setTimeout(() => {
    tagContainer.scrollTop = tagContainer.scrollHeight
  })
  if (isActive.value) {
    emitSelectToParent()
  }
}

/**
 * 呈现提示
 * @param queryString 输入的字符串
 * @param cb 用于实现展示的函数
 * @param type 输入框种类
 */
const cleanUpList = async (queryString: string, cb: any, type: string) => {
  //如果没有数据，发送请求
  switch (type) {
    case styleDataType.genre:
      if (styleStore.genreList.length <= 0) {
        await styleStore.getGenreList()
      }
      break
    case styleDataType.style:
      if (styleStore.styleList.length <= 0) {
        await styleStore.getStyleList()
      }
      break
  }

  //根据输入和已选标签过滤提示
  const results = queryString
    ? filtNo(type).filter(listFilter(queryString, type))
    : filtNo(type)
  cb(results)
}

/**
 * 关键词匹配的规则
 * @param queryString 输入的字符串
 * @param type 输入框种类
 */
const listFilter = (queryString: string, type: string) => {
  if (type === styleDataType.genre || type === styleDataType.style) {
    return (listItem: tag) => {
      const name = listItem.name
      //@ts-ignore
      const judgeName: boolean = name && name.toLowerCase().indexOf(queryString.toLowerCase()) >= 0

      const enName = listItem.enName
      //@ts-ignore
      const judgeEnName: boolean = enName.toLowerCase().indexOf(queryString.toLowerCase()) >= 0

      const otherName = listItem.otherName
      let JudgeOtherName = false
      if (Array.isArray(otherName)) {
        JudgeOtherName = otherName.some(name =>
          name.toLowerCase().indexOf(queryString.toLowerCase()) >= 0
        )
      }

      return judgeName || judgeEnName || JudgeOtherName
    }
  } else {
    return (listItem: tag) => listItem.value.toLowerCase().indexOf(queryString.toLowerCase()) >= 0
  }
}

/**
 * 在每次展开筛选列表前，根据已选的标签进行过滤
 * @param type 输入框种类
 */
const filtNo = (type: string) => {
  const doFilt = (list: Array<tag>) => {
    if (selectedTags.length <= 0) {
      return list
    }
    return list.filter((item1: tag) => {
      //@ts-ignore
      return selectedTags.every((item2) => item1.data === item2.data ? item1.value !== item2.value : true)
    })
  }
  //根据type确定操作哪个列表

  switch (type) {
    case styleDataType.genre:
      return doFilt(styleStore.genreTagList)
    case styleDataType.style:
      return doFilt(styleStore.styleTagList)
    case tagData.format:
      return doFilt(commonStore.formatTagList)
    case tagData.form:
      return doFilt(commonStore.formTagList)
    case tagData.country:
      return doFilt(commonStore.countryTagList)
    case tagData.decade:
      return doFilt(commonStore.decadeTagList)
    default:
      return []
  }
}
//#endregion

const emitSelectToParent = ()=> {
  emit('onQueryCollect', selectedTags, orderBy.value)
}

const onSelectOptionChange = ()=> {
  if (isActive.value) {
    emitSelectToParent()
  }
}
</script>

<style lang="scss" scoped>
aside {
  min-width: 16rem;
  max-width: 16rem;
  height: calc(100vh - 4rem);
  background-color: rgb(229, 231, 234);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;


  .tags {
    .tag-box {
      text-align: center;
      width: 14rem;
      margin-top: 0.5rem;

      :deep(.tag-selector),
      .tag-selector-with-btn {
        width: 100%;

        &:nth-child(2) {
          display: flex;
          // flex-direction: row;
        }
      }
    }
  }

  :deep(.order-by-selector) {
    width: 14rem;
    :deep(.order-by-options) {
      text-indent: 1rem;
    }
  }

  p {
    font-size: 0.75rem;
    text-align: right;

    a:hover {
      color: $test-color;
    }
  }

  .select-mode {
    margin-top: 0.5rem;
    text-align: right;
  }
}

:deep(.cascader-panel) {
  @include hide-scroll-bar
}
</style>