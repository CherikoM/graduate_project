<template>
  <div class="container" v-loading="!isLoadFinish">
    <div class="inner">
      <div class="top-user-info">
        <UserInfo :user-info="userInfo" :user-id="userId" />
      </div>
      <nav>
        <ul>
          <li>
            <el-button 
              text class="nav-btn" 
              @click="area='contribute'"
              :style="{'backgroundColor': area=='contribute'? 'rgb(245, 245, 245)': 'white'}">
              {{ isMe? "我": "ta" }}的贡献
            </el-button>
          </li>
          <li>
            <el-button 
              text class="nav-btn" 
              @click="area='collection'"
              :style="{'backgroundColor': area=='collection'? 'rgb(245, 245, 245)': 'white'}">
              {{ isMe? "我": "ta" }}的乐单
            </el-button>
          </li>
          <li>
            <el-button 
              text class="nav-btn" 
              @click="area='comment'"
              :style="{'backgroundColor': area=='comment'? 'rgb(245, 245, 245)': 'white'}">
              {{ isMe? "我": "ta" }}的评论
            </el-button>
          </li>
        </ul>
      </nav>
      <div class="all-info">
        <ContributeList :user-id="userId" :is-me="isMe" v-show="area=='contribute'" ref="contributeListDOM" />
        <CollectionList v-show="area=='collection'" :is-me="isMe" :user-id="userId" :show="true" @on-item-click="(index, id)=> router.push(`/collection/${id}`)" />
        <CommentList v-show="area=='comment'" :is-me="isMe" :user-id="userId" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue"
import { useRoute } from "vue-router"
import router from "@/router"

import ContributeList from "./ContributeList/ContributeList.vue"
import CollectionList from "./CollectionList/CollectionList.vue"
import CommentList from "./CommentList/CommentList.vue"

import UserInfo from '@/views/UserView/UserInfo.vue'

import { doThankContribute, undoThankContribute, getUserById } from "@/api/userAPI"

import getIdAndName from "@/common/getIdAndName"
import { ElMessage } from "element-plus"
import sleep from "@/common/sleep"

const { params } = useRoute()

const area = ref("contribute")

const userId = (params.id as unknown as number)

const { id, name } = getIdAndName()

const userInfo = ref<any>({})

const contributeListDOM = ref()

const isLoadFinish = ref(false)

const isMe = computed(() => {
  return userId == id
})

onMounted(async () => {
  userInfo.value = await getUserById(userId, id)
  await sleep(1000)
  isLoadFinish.value = true
})
</script>

<style scoped lang="scss">
.container {
  background-color: rgb(245, 245, 245);
  @include hide-scroll-bar();

  .top-user-info {
    height: 20rem;
    background-color: white;
    padding: 4rem;
    display: flex;
    align-items: center;

    

  }

  nav {
    display: flex;
    justify-content: center;
    border-bottom: 1px solid rgb(245, 245, 245);
    position: sticky;
    top: 0;
    z-index: 10;
    background-color: #fff;
    width: 100%;

    ul {
      display: flex;
      list-style: none;

      li {
        list-style: none;
        margin-left: 1rem;

        .nav-btn {
          height: 3rem;
        }

        &:first-child {
          margin-left: 0;
        }

      }
    }
  }

  .all-info {
    
  }
}

</style>