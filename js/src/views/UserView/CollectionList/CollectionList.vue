<template>
  <div class="container">
    <nav v-if="show">
      <div class="my" :class="{'choose': area=='my'}" @click="areaChange('my')">
        {{ isMe ? "我" : "ta" }}创建的
      </div>
      &nbsp;|&nbsp;
      <div class="other" :class="{'choose': area=='like'}" @click="areaChange('like')">
        {{ isMe ? "我" : "ta" }}收藏的
      </div>
    </nav>
    <el-dialog v-model="createShow" :title="'创建乐单'" width="80%">
      <AddCollect @on-add-success="addCol" />
    </el-dialog>

    <div class="my-col" v-show="area == 'my'">
      <div class="main">
        <div class="page">
          <el-pagination layout="prev, pager, next" :total="myCollectionData.totalCount"
            v-model:page-size="myCollectionData.pageSize" v-model:current-page="myCollectionData.pageNum" :small="true"
            :background="false" :hide-on-single-page="true" @current-change="getCol('my')"
            style="justify-content: center; margin-top: 1.5rem;" />
        </div>
        <div class="cl">
          <CollectionItem v-for="(col, index) in myCollectionData.list" :key="col.id" :item="col" :index="index"
            @on-click="(index, id) => { emits('onItemClick', index, id) }" />

        </div>
        <div class="page">
          <el-pagination layout="prev, pager, next" :total="myCollectionData.totalCount"
            v-model:page-size="myCollectionData.pageSize" v-model:current-page="myCollectionData.pageNum" :small="true"
            :background="false" :hide-on-single-page="true" @current-change="getCol('my')"
            style="justify-content: center; margin-top: 1.5rem;" />
        </div>
      </div>

      <el-empty v-if="!myCollectionData.list.length || myCollectionData.list.length <= 0" description="No collection now"
        style="margin-top: 1.5rem;">
        <!-- <el-button plain @click="createShow = true" v-show="isMe">创建乐单</el-button> -->
      </el-empty>

      <el-button plain @click="createShow = true" v-show="isMe" style="display: block; margin: 1rem auto;">创建乐单</el-button>
    </div>

    <div class="like-col" v-show="area == 'like'">
      <div class="main">
        <div class="page">
          <el-pagination layout="prev, pager, next" :total="likeCollectionData.totalCount"
            v-model:page-size="likeCollectionData.pageSize" v-model:current-page="likeCollectionData.pageNum"
            :small="true" :background="false" :hide-on-single-page="true" @current-change="getCol('like')"
            style="justify-content: center; margin-top: 1.5rem;" />
        </div>

        <div class="cl">
          <CollectionItem v-for="(col, index) in likeCollectionData.list" :key="col.id" :item="col" :index="index"
            @on-click="(index, id) => { emits('onItemClick', index, id) }" />

        </div>

        <div class="page">
          <el-pagination layout="prev, pager, next" :total="likeCollectionData.totalCount"
            v-model:page-size="likeCollectionData.pageSize" v-model:current-page="likeCollectionData.pageNum"
            :small="true" :background="false" :hide-on-single-page="true" @current-change="getCol('like')"
            style="justify-content: center; margin-top: 1.5rem;" />
        </div>
      </div>

      <el-empty v-if="!likeCollectionData.list.length || likeCollectionData.list.length <= 0" description="No collection now"
        style="margin-top: 1.5rem;"></el-empty>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, toRefs } from "vue"

import AddCollect from '@/components/AddCollect.vue'

import CollectionItem from '@/views/UserView/CollectionList/CollectionItem.vue'

import { getCollectionByUserId, getUserLikeList } from "@/api/collectionAPI"
import { ElMessage } from "element-plus"

const props = defineProps<{
  isMe: any
  userId: string | number
  pageNum?: string | number
  pageSize?: string | number
  show?: boolean
}>()

const emits = defineEmits(["onItemClick"])

const { isMe, userId, pageNum, pageSize } = toRefs(props)

const area = ref("my")

const myCollectionData = ref<any>({
  list: [],
  pageNum: pageNum?.value? pageNum?.value: 1,
  pageSize: pageSize?.value? pageSize?.value: 10,
  totalCount: 0,
})

const likeCollectionData = ref<any>({
  list: [],
  pageNum: pageNum?.value? pageNum?.value: 1,
  pageSize: pageSize?.value? pageSize?.value: 10,
  totalCount: 0,
})

const createShow = ref(false)

const areaChange = (a: string) => {
  area.value = a
  if (a == "like" && likeCollectionData.value.list.length == 0) {
    getCol('like')
  }
}

const getCol = async (a: string) => {
  let cd
  let result
  switch (a) {
    case 'my':
      cd = myCollectionData.value
      result = await getCollectionByUserId(userId.value as number, cd.pageNum, cd.pageSize)
      if(result.isSuccess) {
        cd.pageNum = result.data.pageNum
        cd.totalCount = result.data.totalCount
      } 
      break
    case 'like':
      cd = likeCollectionData.value
      result = await getUserLikeList(userId.value as number, cd.pageNum, cd.pageSize)
      if(result.isSuccess) {
        cd.pageNum = result.data.pageNum
        cd.totalCount = result.data.totalCount
      }
      break
  }
  if (result.isSuccess) {
    cd.list = [...result.data.data]
  } else {
    if (result.error) {
      ElMessage.error(result.error)
    } else {
      // ElMessage.error(result.message)
    }
  }
}

const addCol = async()=> {
  createShow.value = false
  getCol("my")
}

onMounted(async () => {
  getCol('my')
})
</script>
 
<style scoped lang="scss">
.container {
  nav {
    margin: 1rem 0;
    display: flex;
    // align-items: center;
    justify-content: center;

    &>div:hover {
      color: blue;
      cursor: pointer;
    }

    .choose {
      color: blue;
      text-decoration: underline;
    }
  }
}
</style>