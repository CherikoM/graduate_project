<template>
  <div class="data-item">
    <div class="avatar" @click="router.push(`/user/${item.id}`)">
      <img class="avatar" :src="'http://localhost:8080/img/'+(item.avatar? item.avatar: 'noImg.png')">
    </div>
    <div class="words-info">
      <div class="head">
        <h3 class="name click-to-other" @click="router.push(`/user/${item.id}`)">{{ item.name }}</h3>
        <FollowBtn class="f-btn" :is-follow="item.isFollow" :user-id="item.id" :is-friend="item.isFriend" @on-follow-change="follow" />
      </div>
      <div class="point">{{ item.contributePoint? item.contributePoint: 0 }}贡献分</div>
      <p class="description">{{ item.description }}</p>
    </div>
    
  </div>
</template>

<script setup lang="ts">
import { toRefs } from "vue"
import router from "@/router"

import FollowBtn from "../FollowBtn.vue"

const props = defineProps<{
  item: any
}>()

const { item } = toRefs(props)

const follow = (value: boolean)=> {
  item.value.isFollow = value
  if(value) {
    item.value.isFriend = true
  } else {
    item.value.isFriend = false
  }
}
</script>

<style lang="scss" scoped>
.data-item {
  display: flex;
  .avatar {
    width: 6rem;
    height: 6rem;
    background-color: #fff;
    border-radius: 50%;
    box-shadow: var(--el-box-shadow);
    flex: none;
  }

  .words-info {
    margin-left: 1rem;
    width: 100%;
    font-size: 0.75rem;
    .head {
      display: grid;
      grid-template-columns: 1fr min-content;
      align-items: end;
      h3 {
        font-size: 1.25rem;
        @include one-line-hide();
      }
      .f-btn {
        position: relative;
        top: 0.1rem;
        justify-self: right;
      }
    }
    

    .description {
      overflow: hidden;
      word-wrap: break-word;
      word-break:break-all;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

  }

}

.click-to-other {
  @include click-to-other();
  cursor: pointer;
}

</style>