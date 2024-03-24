<template>
  <div class="container">
    <el-empty v-if="!contributeData.list.length || contributeData.list.length <=0" description="No contribute now" style="margin-top: 4rem;" />
    <div class="contribute">

      <div class="page">
        <el-pagination layout="prev, pager, next" :total="contributeData.totalCount" :page-size="contributeData.pageSize"
          :current-page="contributeData.pageNum" :small="true" :background="false" :hide-on-single-page="true"
          @current-change="getContributeList" style="justify-content: center; margin-top: 1.5rem;" />
      </div>

      <ContributeItem  v-for="(item, index) in contributeData.list" :key="item.id + item.area" :item="item" :index="index" :isMe="isMe" :user-id="userId" />

      <div class="page">
        <el-pagination layout="prev, pager, next" :total="contributeData.totalCount" :page-size="contributeData.pageSize"
          :current-page="contributeData.pageNum" :small="true" :background="false" :hide-on-single-page="true"
          @current-change="getContributeList" style="justify-content: center; margin-top: 1.5rem;" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed, ref, onMounted } from "vue"
import { ElMessage } from "element-plus"

import { getUserContribute } from "@/api/userAPI"
import getIdAndName from "@/common/getIdAndName"

import ContributeItem from "./ContributeItem.vue"

const props = defineProps<{
  isMe: boolean | string
  userId: number | string
}>()

const emits = defineEmits(["onPageChange"])

const { isMe, userId } = toRefs(props)

const { id, name } = getIdAndName()


const contributeData = ref<any>({
  list: [],
  pageNum: 1,
  pageSize: 15,
  totalCount: 0,
})

const getContributeList = async (page: number) => {
  const cd = contributeData.value
  cd.pageNum = page
  const result = await getUserContribute(userId.value as number, id, cd.pageNum, cd.pageSize)
  cd.list = result.data
  cd.totalCount = result.totalCount
}


defineExpose({
  getContributeList
})

onMounted(()=> {
  getContributeList(1)
})

</script>

<style scoped lang="scss">
.contribute {
  
}
</style>