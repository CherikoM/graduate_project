<template>
  <div>
    <div class="block" v-for="(item, index) in label" :key="item.id">
      <div class="left">
        <div class="info" v-if="type !== 'only'">
          <div class="name click-to-other" @click="toNewBlank(`/info/labels/${item.id}`)">
            {{ item.name }}</div>
          <div class="id-catno">
            <span class="id">{{ item.id }}</span>
            &nbsp;
            <span contenteditable="true" class="catno" @blur="getRole(item, index)" @keydown="warpDisable"
              @paste="styleDisable" ref="catnoBox">{{
                //@ts-ignore
                item.catno }}</span>
          </div>

        </div>
        <div class="info" v-if="type === 'only'">
          <span class="name click-to-other" @click="toNewBlank(`/info/artists/${item.id}`)">
              {{ item.name }}</span><span class="id">({{ item.id }})</span>
        </div>
      </div>
      <div class="delete" @click="listDelete(index)">
        <el-button :icon="Close" circle class="icon" />
      </div>
    </div>
    <div class="add">
      <p>添加厂牌</p>
      <el-autocomplete v-model="labelKeyword" :fetch-suggestions="querySearch" :trigger-on-focus="false" clearable
        class="deep-box" placeholder="输入关键词搜索厂牌" :debounce="1000" @select="handleSelect" ref="labelAutoComplete">
        <template #default="{ item }">
          <div class="search-result" style="display:flex;margin-top: 0.5rem;margin-left: -0.5rem;">
            <div class="picture">
              <img class="picture" :src="'http://localhost:8080/img/'+ (item.picture? item.picture: 'noImg.png')">
            </div>
            <div class="info" style="margin-left: 0.5rem;">
              <div class="name">{{ item.name }}</div>
              <span class="id">{{ item.id }}</span>
            </div>
          </div>
        </template>
      </el-autocomplete>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs } from 'vue'
import router from '@/router'

//@ts-ignore
import { Close } from '@element-plus/icons-vue'

import { getLabelById } from '@/api/labelAPI'
import { tagSearch } from '@/api/commonAPI'
import type { labelData } from '@/api/dataInterfaces'

import styleDisable from '@/common/styleDisable'

const props = defineProps<{
  label: labelData[] | any[],
  filt?: number | string,
  type?: string
}>()

const catnoBox = ref(null)

//源码中，fetch-suggestions的cb函数将数组赋值给了suggestions变量
//所以直接改变组件的suggestion也可以实现下拉列表的刷新
//这里直接取得组件dom元素
const labelAutoComplete = ref(null)

//自动补全选择框的输入内容
const labelKeyword = ref('')

//发送tagSearch查询请求时的参数
const query = {
  order: 1,
  currentPage: 1,
  pageSize: 5,
  area: 'labels'
}

const { label, type, filt } = toRefs(props)

const listDelete = async (index: number) => {
  //此处无法直接调用fetch-suggestions的函数，但下拉列表需要被刷新
  let complete
  label.value.splice(index, 1)
  complete = labelAutoComplete.value as any
  // complete.focus()
  if (labelKeyword.value && labelKeyword.value !== '') {
    complete.suggestions = await getAndFiltSuggestions(labelKeyword.value)
  }
}

const querySearch = async (queryString: string, cb: any) => {
  const result = await getAndFiltSuggestions(queryString)
  cb(result)
}

const handleSelect = (item: labelData) => {
  if(type?.value === 'only') {
    label.value.length = 0
    label.value.push(item)
  } else {
    label.value.push(item as labelData)
  }
}

//提取出远程获取建议选项的函数
const getAndFiltSuggestions = async (queryString: string) => {
  let idResult
  let keywordResult

  const id: number = queryString as unknown as number
  if (!isNaN(id)) {
    idResult = await getLabelById(id)
  }

  keywordResult = await tagSearch({ ...query, keyword: labelKeyword.value, area: 'labels' })

  keywordResult = keywordResult.data
  let result: labelData[] = []
  if (idResult && typeof (idResult) !== 'string') {
    if (Array.isArray(idResult)) {
      result = [...idResult]
    } else {
      result.push(idResult)
    }
  }
  if (keywordResult) {
    if (Array.isArray(keywordResult)) {
      result = [...result, ...keywordResult]
    } else {
      result.push(keywordResult)
    }
  }

  //@ts-ignore
  result = result.filter(r => {
    if(r.id == filt?.value) {
      return false
    }
    return label.value.every((l:labelData) => {
      return l.id !== r.id
    })
  })

  return result
}

/**
 * 获取配役
 * @param item 
 * @param index 
 */
const getRole = (item: any, index: number) => {
  const cbc = catnoBox.value as unknown as HTMLCollection
  const cb = cbc[index] as HTMLElement
  if (cb) {
    const catno = cb.innerText
    item.catno = catno
  }
}

/**
 * 禁止换行
 * @param e 
 */
const warpDisable = (e: any) => {
  if (e.keyCode === 13) {
    e.preventDefault()
    const cb = catnoBox.value as unknown as HTMLElement
    cb.blur()
  }
}

const toNewBlank = (path: string, query: any={})=> {
  const url = router.resolve({path, query})
  window.open(url.href, '_blank')
}
</script>

<style scoped lang="scss">
.block {
  height: 4rem;
  background-color: #fff;
  padding: 0.5rem;
  box-shadow: var(--el-box-shadow-light);
  border-radius: 0.25rem;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .left {
    display: flex;
    align-items: center;
    .info {
      margin-left: 1rem;

      .name {
        font-size: 1.1rem;
      }
    }
  }

  .delete {
    .icon {
      font-size: 1.25rem;
    }
  }
}

.catno {
  width: 7rem;
  @include one-line-hide();

  &:focus {
    content: none;
    outline: none;
    text-decoration: underline;
  }

  &:empty:before {
    content: "点击输入发行编号";
    color: #cccccc;
  }
}

.add {
  margin-top: 1rem;
  display: flex;
  align-items: center;

  p {
    flex: none;
    margin-right: 1rem;
  }
}

.picture {
  width: 4rem;
  height: 4rem;
}

.click-to-other {
  @include click-to-other();
}
</style>