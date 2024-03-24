<template>
  <div class="container">
    <div class="main">
      <div class="upload-img-container">
        <UploadImg :img="img" class="upload-img" @on-pic-update="(result: string)=> {collectionData.img = result}" />
      </div>
      <div class="words">
        <div class="title">
          <div class="title-flex">
            <h3>标题</h3>
            <el-input class="input" v-model="collectionData.title" placeholder=""></el-input>
          </div>
          <div class="rest-words character-limit" :class="{ 'character-limit-warning': tRestChar < 0 }">
            {{ tRestChar >= 0 ? `还可以输入${tRestChar}字` : `已超出${tRestChar}字` }}
          </div>
        </div>
        <div class="description">
          <h3>简介</h3>
          <el-input type="textarea" class="input" v-model="collectionData.description" placeholder="" rows="4"></el-input>
          <div class="rest-words character-limit" :class="{ 'character-limit-warning': dRestChar < 0 }">
            {{ dRestChar >= 0 ? `还可以输入${dRestChar}字` : `已超出${dRestChar}字` }}
          </div>
        </div>
      </div>
    </div>
    <div class="btn">
      <el-button class="create-btn" @click="create">创建</el-button>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, computed } from 'vue'
import { ElMessage } from 'element-plus'

import UploadImg from './UploadImg.vue'
import getIdAndName from '@/common/getIdAndName'
import { addCollection } from "@/api/collectionAPI"

const props = defineProps<{
  img?: string
}>()

const emits = defineEmits(["onAddSuccess", "onAddError"])

const { img } = toRefs(props)

const collectionData = ref({
  title: "",
  description: "",
  img: ""
})

const { id, name } = getIdAndName()

const tRestChar = computed(() => 100 - collectionData.value.title.length)

const dRestChar = computed(() => 500 - collectionData.value.description.length)

const create = async ()=> {
  if(collectionData.value.title.length <=0) {
    ElMessage.error("请输入乐单名！")
  } else if(tRestChar.value<0) {
    ElMessage.error("乐单名字数太多！")
  } else if(dRestChar.value<0) {
    ElMessage.error("简介名字数太多！")
  } else {
    const cc = collectionData.value
    const createCollection = {
      name: cc.title,
      creatorId: id,
      creatorName: name,
      picture: cc.img && cc.img?.length>0? cc.img: null,
      description: cc.description && cc.description.length>0? cc.description: null,
      content: null
    }
    const result = await addCollection(createCollection)
    if(result.isSuccess) {
      ElMessage.success("创建成功！")
      emits("onAddSuccess")
    } else {
      if(result.error) {
        ElMessage.error(result.error)
      } else {
        ElMessage.error(result.message)
      }
      emits("onAddError")
    }

  }
}
</script>

<style scoped lang="scss">
.container {

  .main {
    display: flex;

    .words {
      margin-left: 2rem;
      width: 100%;

      .title {
        .title-flex {
          display: flex;

          h3 {
            flex: none;
          }

          .input {
            margin-left: 1rem;
            flex: 1;
          }

        }


      }

      .description {
        margin-top: 0.5rem;

        .rest-words {
          text-align: right;
        }
      }
    }
  }

  .btn {
    margin-top: 1rem;
    text-align: right;
  }


}

.character-limit {
  font-size: 0.75rem;
  margin-top: 0.5rem;
  text-align: right;
}

.character-limit-warning {
  color: red;
}
</style>
<style>
.upload-img {
  width: fit-content;
}
</style>