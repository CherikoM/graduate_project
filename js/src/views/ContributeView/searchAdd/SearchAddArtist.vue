<template>
  <div>
    <div class="block" :class="{ 'big-block': size !== 'small' }" v-for="(item, index) in artist" :key="item.id">
      <div class="left" v-if="size !== 'small'">
        <div class="info">
          <slot name="info" :item="item" :index="index">
            <span class="name click-to-other" @click="toNewBlank(`/info/artists/${item.id}`)">
              {{ item.name }}</span><span class="id">({{ item.id }})</span>
          </slot>
        </div>
      </div>
      <div class="left-small" v-if="size === 'small'">
        <div class="info">
          <slot name="info-small" :item="item" :index="index">
            <span class="name click-to-other" @click="toNewBlank(`/info/artists/${item.id}`)">
              {{ item.name }}</span>
            <span class="id">({{ item.id }})</span>
          </slot>
        </div>
      </div>
      <div class="delete" @click="listDelete(index)" v-if="size !== 'small'">
        <el-button :icon="Close" circle class="icon" size="small" />
      </div>
      <div class="delete-small" v-if="size === 'small'">
        <Close style="width: 1rem;height: 1rem;" @click="listDelete(index)" />
      </div>
    </div>
    <div class="add" :class="{ 'small-add': size === 'small' }">
      <div class="add-main">
        <p :class="{ 'small-p': size === 'small' }">添加艺人</p>
        <el-autocomplete v-model="artistKeyword" :fetch-suggestions="querySearch" :trigger-on-focus="false" clearable
          class="deep-box" :class="{ 'small-autocomplete': size === 'small' }" placeholder="输入关键词搜索艺人" :debounce="1000"
          @select="handleSelect" ref="artistAutoComplete">
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
        <slot name="add"></slot>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs } from 'vue'
import router from '@/router'

//@ts-ignore
import { Close } from '@element-plus/icons-vue'

import { getArtistById } from '@/api/artistAPI'
import { tagSearch } from '@/api/commonAPI'
import type { artistData } from '@/api/dataInterfaces'


const props = defineProps<{
  artist: artistData[] | null,
  size?: string,
  filt?: number | string,
}>()

//源码中，fetch-suggestions的cb函数将数组赋值给了suggestions变量
//所以直接改变组件的suggestion也可以实现下拉列表的刷新
//这里直接取得组件dom元素
const artistAutoComplete = ref(null)

//自动补全选择框的输入内容
const artistKeyword = ref('')

//发送tagSearch查询请求时的参数
const query = {
  order: 1,
  currentPage: 1,
  pageSize: 5,
  area: 'artists',
}

const { artist, size, filt } = toRefs(props)

const listDelete = async (index: number) => {
  //此处无法直接调用fetch-suggestions的函数，但下拉列表需要被刷新
  if (artist.value) {
    artist.value.splice(index, 1)
    let complete = artistAutoComplete.value as any
    // complete.focus()
    if (artistKeyword.value && artistKeyword.value !== '') {
      complete.suggestions = await getAndFiltSuggestions(artistKeyword.value)
    }
  }

}

const querySearch = async (queryString: string, cb: any) => {
  const result = await getAndFiltSuggestions(queryString)
  console.log(result)
  cb(result)
}

const handleSelect = (item: artistData) => {
  if (artist.value) {
      artist.value.push(item as artistData)
  }
}

//提取出远程获取建议选项的函数
const getAndFiltSuggestions = async (queryString: string) => {
  let idResult
  let keywordResult

  const id: number = queryString as unknown as number
  if (!isNaN(id)) {
    idResult = await getArtistById(id)
  }
  keywordResult = await tagSearch({ ...query, keyword: artistKeyword.value, area: 'artists' })

  keywordResult = keywordResult.data
  let result: artistData[] = []
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
    return artist.value?.every(a => {
      return a.id !== r.id
    })
  })
  return result
}

const toNewBlank = (path: string, query: any = {}) => {
  const url = router.resolve({ path, query })
  window.open(url.href, '_blank')
}
</script>

<style scoped lang="scss">
.block {
  width: 100%;
  background-color: #fff;
  border-radius: 0.25rem;
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
}

.add {
  margin-top: 1rem;
  display: flex;
  align-items: center;

  .add-main {
    flex: 1;
    display: flex;
    align-items: center;

    p {
      flex: none;
      margin-right: 1rem;
    }
  }

}

.big-block {
  padding: 0.5rem;
  box-shadow: var(--el-box-shadow-light);
  height: 4rem;
  margin-bottom: 0.5rem;
}

.small-p {
  font-size: 0.75rem;
}

.small-add {
  margin-top: 0;
}

:deep(.small-autocomplete)>.el-input {
  height: 1.5rem;
}

.picture {
  width: 4rem;
  height: 4rem;
}

.click-to-other {
  @include click-to-other();
}
</style>