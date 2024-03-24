<template>
  <div class="is-new-creat">
    <el-switch
      v-model="isNewCreate"
      size="large"
      active-text="新建发行样板"
      inactive-text="搜索现有发行样板"
      @change="mainReleaseSwitch"
    />
  </div>
  <div class="main-release" v-show="!isNewCreate">
    <div class="has-search" v-if="mainRelease.title && mainRelease.title !== ''">
      <p>
        已选择样板：{{ mainRelease.title }}({{ mainRelease.id }})
        <span class="click-to-other" @click="clear">清除</span>
      </p>
    </div>
    <div class="do-search">
      <div class="searchbox">
        <el-form-item>
          <el-autocomplete class="deep-box" v-model="mainReleaseKeyword" placeholder="输入关键词搜索主版本"
            :fetch-suggestions="getMainRelease" @select="MainReleaseSelected" :trigger-on-focus="false">
            <template #default="{ item }">
              <div class="search-result" style="display:flex;margin-top: 0.5rem;margin-left: -0.5rem;">
                <div class="info" style="margin-left: 0.5rem;">
                  <div class="name">{{ item.title }}<span class="id">({{ item.id }})</span></div>

                </div>
              </div>
            </template>
          </el-autocomplete>
        </el-form-item>
      </div>
      <!-- <div class="main-judge">
        <div>
          <el-checkbox v-model="isBase" label="标记本条目为基准版本" size="large" border @change="emits('onIsBaseChange', isBase)" />
        </div>
      </div> -->
    </div>
  </div>
  <div class="new-main-release" v-show="isNewCreate">
    <el-input
      v-model="newMainRelease.title"
      size="large"
      placeholder="输入新发行样板名"
      @blur="newMainReleaseInput"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, watch } from 'vue'

import { getMainReleaseById, getMainReleaseByKeyword } from '@/api/releaseAPI'


const emits = defineEmits(['onMainReleaseChange'])

const props = defineProps<{
  defaultValue: any,
}>()

const {defaultValue} = toRefs(props)

const mainReleaseKeyword = ref('')
const mainRelease = ref<any>({
  title: '',
  id: 0
})

const newMainRelease = ref<any>({})

const isNewCreate = ref(false)

//为了接收到异步props的值
watch(defaultValue, (newValue)=> {
  mainRelease.value.title = newValue.defaultTitle
  mainRelease.value.id = newValue.defaultId
}, {deep:true})

//主发行的自动补全框
//#region 
const getMainRelease = async (queryString: string, cb: Function) => {
  let idResult
  let keywordResult

  const id: number = queryString as unknown as number
  if (!isNaN(id)) {
    idResult = await getMainReleaseById(id)
  }
  keywordResult = await getMainReleaseByKeyword(queryString, 1, 5)

  keywordResult = keywordResult.data
  let result = []
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
    return r.id !== mainRelease.value.id
  })
  return cb(result)
}

const MainReleaseSelected = (item: any) => {
  mainRelease.value = item
  emits('onMainReleaseChange',{title:item.title, id:item.id, isNew:isNewCreate})
}

const clear = ()=> {
  mainRelease.value = {}
  emits('onMainReleaseChange', {})
}

const newMainReleaseInput = ()=> {
  let res = {...newMainRelease.value}
  if(res.title) res = {...res, isNew:isNewCreate}
  emits('onMainReleaseChange', res)
}

const mainReleaseSwitch = (val: boolean)=> {
  if(val) {
    let res = {...newMainRelease.value}
    if(res.title) res = {...res, isNew:isNewCreate}
    emits('onMainReleaseChange', res)
  } else {
    emits('onMainReleaseChange', {title:mainRelease.value.title, id:mainRelease.value.id , isNew:isNewCreate})
  }
}
</script>

<style scoped lang="scss">
.main-release {
  .has-search {
    table {
      td {
        vertical-align: top;
      }
    }
  }
  .do-search {
    display: flex;
    align-items: center;
    .searchbox {
      flex: 1;
    }
    .main-judge {
      flex: none;
      margin-left: 1rem;
    }
  }
}

.click-to-other {
  @include click-to-other()
}
</style>