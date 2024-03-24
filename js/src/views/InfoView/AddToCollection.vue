<template>
  <div class="add-to-list">
    <span class="click-to-other" @click="dialogVisible=true">
      +添加到乐单
    </span>
    <el-dialog v-model="dialogVisible" title="添加到" width="80%">
      <CollectionList 
        class="collection-list" 
        :is-me="true" 
        :user-id="id" 
        :page-num="1" 
        :page-size="5"
        @on-item-click="addTo"/>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs } from "vue"

import CollectionList from '@/views/UserView/CollectionList/CollectionList.vue'

import { addItemToList } from "@/api/collectionAPI"
import getIdAndName from "@/common/getIdAndName"
import { ElMessage } from "element-plus"

const props = defineProps<{
  area: string,
  areaId: number | string,
}>()

const { id, name } = getIdAndName()

const { area, areaId } = toRefs(props)

const dialogVisible = ref(false)

const addTo = async (index: number, id: number)=> {
  const result = await addItemToList(id, area.value, areaId.value as number)
  if(result.isSuccess) {
    ElMessage.success("Add success!")
    dialogVisible.value = false
  } else if(result.error) {
    ElMessage.error(result.error)
  } else {
    ElMessage.error(result.message)
  }
}
</script>

<style scoped lang="scss">
.collection-list {
  
}

.click-to-other {
  @include click-to-other();
}
</style>