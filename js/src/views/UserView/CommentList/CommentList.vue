<template>
  <div>
    <div v-show="commentList.length > 0">
      <div class="page">
        <el-pagination small layout="prev, pager, next" :total="totalCount" v-model:page-size="pageSize"
          v-model:current-page="currentPage" style="justify-content: center; margin-top: 1.5rem;"
          @current-change="getComment" />
      </div>

      <div class="comment-item" v-for="item in commentList" :key="item.id">
        <div class="info">
          {{ toNow(item.gmtCreated) }}，在{{ area(item.area) }}条目<span @click="toInfo(item)" class="click-to-other">{{
            item.areaName }}</span>中评论了：
        </div>
        <div class="content" v-html="item.content"></div>
      </div>

      <div class="page">
        <el-pagination small layout="prev, pager, next" :total="totalCount" v-model:page-size="pageSize"
          v-model:current-page="currentPage" style="justify-content: center; margin-bottom: 1.5rem;"
          @current-change="getComment" />
      </div>
    </div>
    <el-empty v-show="!commentList || commentList.length <= 0" description="no comments" style="margin-top: 8vh;" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, toRefs, computed } from 'vue'
import router from '@/router'

import { getTopCommentByUser } from "@/api/commentAPI"

const props = defineProps<{
  isMe: any,
  userId: number | string
}>()
const { isMe, userId } = toRefs(props)

const currentPage = ref(1)
const pageSize = ref(15)
const totalCount = ref(0)

const commentList = reactive<any>([])

const toNow = computed(() => (date: Date) => {
  //@ts-ignore
  const res = (Date.now() - new Date(date)) / 1000 / 60 / 60 / 24
  const floorRes = Math.floor(res)
  if (res < 0.1) {
    return "不久前"
  } else if (res < 1) {
    return "今天"
  } else if (floorRes < 7) {
    return `${floorRes}天前`
  } else if (floorRes < 28) {
    return `${Math.floor(floorRes / 7)}周前`
  } else if (floorRes < 365) {
    return `${Math.floor(floorRes / 30)}个月前`
  } else {
    return `${Math.floor(floorRes / 365)}年前`
  }
})

const area = computed(() => (a: string) => {
  let res = ""
  switch (a) {
    case "release":
      res = "发行"
      break
    case "artist":
      res = "艺人"
      break
    case "label":
      res = "厂牌"
      break
    case "style":
      res = "风格"
      break
  }
  return res
})

const toInfo = (item: any) => {
  if (item.area == "style") {
    router.push(`/info/styles/${item.areaName}`)
  } else {
    router.push(`/info/${item.area}s/${item.areaId}`)
  }
}

const getComment = async () => {
  const res = await getTopCommentByUser(userId.value as number, currentPage.value, pageSize.value)
  if (res.isSuccess) {
    totalCount.value = res.data.totalCount
    commentList.length = 0
    commentList.push(...res.data.data)
  }
}
onMounted(() => {
  getComment()
})
</script>

<style scoped lang="scss">
.comment-item {
  margin: 1rem auto;
  width: 30rem;
  padding: 1rem;
  background-color: #fff;

  .info {
    font-size: 0.8rem;
    margin-bottom: 0.5rem
  }
}

.click-to-other {
  @include click-to-other();
}
</style>