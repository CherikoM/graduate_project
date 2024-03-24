<template>
  <div class="user-info">
    <div class="avatar">
      <img class="avatar" :src="'http://localhost:8080/img/'+(userInfo.avatar? userInfo.avatar: 'noImg.png')">
    </div>
    <div class="content">
      <div class="title-content">
        <h1>{{ userInfo.name }}</h1>
        <div class="audit" v-if="auditorId>0 && userId == id" @click="router.push('/audit')">前往审核平台</div>
        <FollowBtn :is-follow="userInfo.isFollow" :user-id="userId" :is-friend="userInfo.isFriend" :logout="true" />
        <FollowBtn 
          v-if="userId == id"
          :is-follow="userInfo.isFollow" 
          :user-id="userId" 
          :is-friend="userInfo.isFriend" 
          :change="true" 
          :user-info="{
            id: userInfo.id,
            avatar: userInfo.avatar,
            name: userInfo.name,
            description: userInfo.description,
          }" />
      </div>
      <div class="communication">
        <div class="look click-to-other" @click="showFollow">关注：{{ userInfo.follow }}</div>
        <div class="fan click-to-other" @click="showFan">粉丝：{{ userInfo.fan }}</div>
      </div>
      <div class="achievement">
        <div class="age">站龄：{{ toNow(userInfo.gmtCreated) }}</div>
        <div class="contribute">贡献值：{{ userInfo.contributePoint? userInfo.contributePoint: 0 }}分！</div>
      </div>
      <div class="introduction">
        <div class="title">个人简介</div>
        <p>{{ userInfo.description }}</p>
      </div>
    </div>
    <el-dialog v-model="followData.followVisiable" :title="userInfo.name + '的关注'" width="80%">
      <div class="empty" v-if="!followData.mainData || followData.mainData.length <= 0" style="text-align: center;">
        No follows now
      </div>
      <div class="have" v-if="followData.mainData && followData.mainData.length > 0">
        <FriendList :friend-data="followData.mainData" />
      </div>
      <el-pagination layout="->, prev, pager, next" :total="followData.totalCount" :page-size="followData.pageSize"
        :current-page="followData.pageNum" :small="true" :hide-on-single-page="true" @current-change="followCurrentChange"
        style="margin-top: 1.5rem;" />
    </el-dialog>
    <el-dialog v-model="fanData.fanVisiable" :title="userInfo.name + '的粉丝'" width="80%">
      <div class="empty" v-if="!fanData.mainData || fanData.mainData.length <= 0" style="text-align: center;">
        No follows now
      </div>
      <div class="have" v-if="fanData.mainData && fanData.mainData.length > 0">
        <FriendList :friend-data="fanData.mainData" />
      </div>
      <el-pagination layout="->, prev, pager, next" :total="fanData.totalCount" :page-size="fanData.pageSize"
        :current-page="fanData.pageNum" :small="true" :hide-on-single-page="true" @current-change="fanCurrentChange"
        style="margin-top: 1.5rem;" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { toRefs, ref, computed } from "vue"
import router from "@/router"

import FriendList from "./FriendList/FriendList.vue"
import FollowBtn from "./FollowBtn.vue"

import { myFollows, myFollowers } from "@/api/userAPI"
import getIdAndName from "@/common/getIdAndName"
import getAuditorId from '@/common/getAuditorId'

const { id, name } = getIdAndName()

const props = defineProps<{
  userInfo: any
  userId: number | string
}>()

const { userInfo, userId } = toRefs(props)
const auditorId = getAuditorId()

const toNow = computed(() => (date: Date) => {
  //@ts-ignore
  const res = (Date.now() - new Date(date)) / 1000 / 60 / 60 / 24
  const floorRes = Math.floor(res)
  if (res < 1) {
    return "萌新"
  } else if (floorRes < 7) {
    return `${floorRes}天`
  } else if (floorRes < 28) {
    return `${Math.floor(floorRes / 7)}周`
  } else if (floorRes < 365) {
    return `${Math.floor(floorRes / 30)}个月`
  } else {
    return `${Math.floor(floorRes / 365)}年`
  }
})

const followData = ref<any>({
  followVisiable: false,
  pageNum: 1,
  pageSize: 12,
  totalCount: 0,
  mainData: [],
})

const followCurrentChange = async (pageNum: number) => {
  const fd = followData.value
  fd.pageNum = pageNum
  const result = await myFollows(userId.value as number, fd.pageNum, fd.pageSize)
  if (result.data && result.data.length > 0) {
    fd.mainData = [...result.data]
  }
}


const fanData = ref<any>({
  fanVisiable: false,
  pageNum: 1,
  pageSize: 12,
  totalCount: 0,
  mainData: [],
  async currentChange() {
    const result = await myFollows(userId.value as number, this.pageNum, this.pageSize)
    if (result.data && result.data.length > 0) {
      this.mainData = [...result.data]
    }
  }
})

const follow = (value: boolean) => {

  const ui = userInfo.value
  ui.isFollow = value
  if (value) ui.fan++
  else ui.fan--
  if (value && ui.isFollower) {
    ui.isFriend = true
  } else {
    userInfo.value.isFriend = false
  }


}

const fanCurrentChange = async (pageNum: number) => {
  const fd = fanData.value
  fd.pageNum = pageNum
  const result = await myFollows(userId.value as number, fd.pageNum, fd.pageSize)
  if (result.data && result.data.length > 0) {
    fd.mainData = [...result.data]
  }
}

const showFollow = async () => {
  const fd = followData.value
  fd.followVisiable = !fd.followVisiable
  if (fd.mainData.length <= 0) {
    const result = await myFollows(userId.value as number, fd.pageNum, fd.pageSize)
    if (result.data && result.data.length > 0) {
      fd.mainData = [...result.data]
      fd.totalCount = result.totalPage
    }
  }
}

const showFan = async () => {
  const fd = fanData.value
  fd.fanVisiable = !fd.fanVisiable
  if (fd.mainData.length <= 0) {
    const result = await myFollowers(userId.value as number, fd.pageNum, fd.pageSize)
    if (result.data && result.data.length > 0) {
      fd.mainData = [...result.data]
      fd.totalCount = result.totalPage
    }
  }
}
</script>

<style scoped lang="scss">
.user-info {
  display: flex;

  .avatar {
    width: 15rem;
    height: 15rem;
    background-color: #fff;
    border-radius: 50%;
    box-shadow: var(--el-box-shadow);
    // border: none;
    flex: none;
  }

  .content {
    margin-left: 2rem;

    .title-content {
      display: flex;
      align-items: baseline;

      .audit {
        font-size: 0.75rem;
        border: 0.5px solid gray;
        border-radius: 1rem;
        padding: 0.1rem 0.25rem;
        margin-left: 1rem;
        position: relative;
        top: -0.25rem;
        width: 6rem;
        text-align: center;
        cursor: pointer;
        transition: 0.2s;

        &:hover {
          border-color: blue;
          color: blue;
        }
      }
    }

    .achievement,
    .communication {
      display: flex;
      font-size: 0.9rem;

      &>div {
        margin-right: 1rem;
      }
    }

    .introduction {
      margin-top: 1rem;

      p {
        margin-top: 0.5rem;
        max-height: 6rem;
        @include hide-scroll-bar();
      }
    }
  }
}

.click-to-other {
  @include click-to-other();
}
</style>