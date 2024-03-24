<template>
  <div class="container">
    <!-- 我是乐单, {{ id }} -->
    <div class="info">
      <div class="picture">
        <img class="picture" :src="'http://localhost:8080/img/'+(colData.picture? colData.picture: 'noImg.png')">
      </div>
      <div class="words">
        <div class="head">
          <h2>{{ colData.name }}</h2>
          <div class="like-btn">
            <div class="add-col" v-show="!colData.isAdd && !isMe" @click="add(true)">+收藏</div>
            <div class="add-col is-add" v-show="colData.isAdd && !isMe" @click="add(false)"></div>
            <div class="add-col del" v-show="isMe" @click="delCol">删除</div>
          </div>
        </div>
        <div class="creator-colNum">
          <p class="creator">创建者：<span @click="toNewBlank(`/user/${colData.creatorId}`)" class="click-to-other">{{
            colData.creatorName }}</span></p>
          <p class="col-num">收藏量：{{ colData.addNum }}</p>
        </div>
        <div class="description">
          <div class="title">简介：</div>
          <p class="desc">
            {{ colData.description }}
          </p>
        </div>

      </div>
    </div>
    <div class="col-content" v-show="hasData">
      <div class="page">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :small="true"
          layout="prev, pager, next, jumper" :total="totalCount" :hide-on-single-page="true"
          @current-change="pageChange" />
      </div>
      <div class="items">
        <div class="col-item" v-for="item in colList" :key="item.id"
          @click="router.push(`/info/${item.area}/${item.id}`)">
          <div class="pic">
            <img :src="'http://localhost:8080/img/'+(item.picture? item.picture: 'noImg.png')" class="pic">
          </div>
          <div class="type">{{ areaStr(item.area) }}</div>
          <div class="name">
            {{ item.name }}
          </div>
        </div>
      </div>

    </div>
    <div class="empty" v-show="!hasData">
      <el-empty description="您还没有添加内容" style="margin-top: 4rem;"></el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue"
import { useRoute } from 'vue-router'
import router from "@/router"

import { getCollectionById, getCollectionContent, likeCollection, dislikeCollection, delCollection } from "@/api/collectionAPI"
import { ElMessage, ElMessageBox } from "element-plus"

import toNewBlank from "@/common/toNewBlank"
import getIdAndName from "@/common/getIdAndName"

const { params } = useRoute()

const id = ref(params.id as any)

const { id: userId, name } = getIdAndName()

const areaStr = computed(() => (area: string) => {
  let res = ""
  switch (area) {
    case "artists":
      res = "艺人"
      break
    case "releases":
      res = "发行"
      break
    case "labels":
      res = "厂牌"
      break
  }
  return res
})

const isMe = computed(() => {
  return userId == colData.value.creatorId
})

const hasData = ref(false)

const add = async (flag: boolean) => {
  if (flag) {
    const result = await likeCollection(id.value, userId)
    if (result.isSuccess) {
      colData.value.isAdd = true
      ElMessage.success("Dislike collection success!")
    } else {
      if (result.error) {
        ElMessage.error(result.error)
      } else {
        ElMessage.error(result.message)
      }
    }
  } else {
    const result = await dislikeCollection(id.value, userId)
    if (result.isSuccess) {
      colData.value.isAdd = true
      ElMessage.success("Like collection success!")
    } else {
      if (result.error) {
        ElMessage.error(result.error)
      } else {
        ElMessage.error(result.message)
      }
    }
  }
}

const colData = ref<any>({

})

const pageNum = ref(1)
const pageSize = ref(12)
const totalPage = ref(0)
const totalCount = ref(0)

const colList = ref<any[]>([])

const pageChange = async () => {
  const result = await getCollectionContent(id.value as number, pageNum.value, pageSize.value)
  if (result.isSuccess) {
    colList.value = result.data.data
    totalPage.value = result.data.totalPage
    totalCount.value = result.data.totalCount
    hasData.value = true
  } else {
    if (result.error) {
      ElMessage.error(result.error)
    } else {
      ElMessage.error(result.message)
    }
  }
}

const delCol = async () => {
  ElMessageBox.confirm("Are you sure to delete this collection?")
    .then(async () => {
      const result = await delCollection(id.value)
      if (result.isSuccess) {
        ElMessage.success("Delete collection success!")
        router.push(`/user/${userId}`)
      } else {
        if (result.error) {
          ElMessage.error(result.error)
        } else {
          ElMessage.error(result.message)
        }
      }
    })
    .catch((e)=> {
      console.log(e)
    })

}

onMounted(async () => {
  const result = await getCollectionById(id.value as number, userId)
  if (result.isSuccess) {
    colData.value = result.data
    pageChange()
  } else {
    if (result.error) {
      ElMessage.error(result.error)
    } else {
      ElMessage.error(result.message)
    }
  }
})
</script>

<style scoped lang="scss">
.container {
  @include container();
  padding: 2rem;

  .info {
    display: flex;
    flex-direction: row;
    margin: 1rem;

    @media screen and (max-width:640px) {
      .picture {
        display: none;
      }
    }

    .picture {
      width: 12rem;
      height: 12rem;
      flex: none;
    }

    .words {
      margin-left: 2rem;
      width: 100px;
      flex: 1;

      .head {
        display: flex;

        h2 {
          white-space: nowrap;
          width: 90%;
          @include hide-scroll-bar;
          font-size: 1.75rem;
        }

        .add-col {
          width: 4rem;
          font-size: 0.9rem;

          &:hover {
            color: blue;
            cursor: pointer;
          }
        }
      }

      .creator-colNum {
        display: flex;
        font-size: 0.9rem;

        .col-num {
          margin-left: 2rem;
        }
      }

      .description {
        margin-top: 1rem;

        .desc {
          margin-top: 0.5rem;
          max-height: 4.5rem;
          width: 100%;
          word-wrap: break-word;
          @include hide-scroll-bar();
        }
      }

    }
  }

  .col-content {
    padding: 1rem;
    margin-top: 1rem;
    background-color: #fff;

    .page {
      margin-bottom: 1rem;
    }

    .items {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr 1fr;
      grid-row-gap: 1rem;

      @media screen and (min-width:1200px) {
        grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
      }
      @media screen and (min-width: 640px) and (max-width: 820px) {
        grid-template-columns: 1fr 1fr 1fr;
      }
      @media screen and (max-width: 640px) {
        grid-template-columns: 1fr 1fr;
        
      }
      .col-item {
        place-self: center;
        width: 10rem;
        height: 13rem;
        padding: 1rem;
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
        box-shadow: var(--el-box-shadow-light);
        border-radius: 0.5rem;
        transition: 0.1s;

        &:hover {
          box-shadow: var(--el-box-shadow);
          cursor: pointer;
        }

        .pic {
          width: 8rem;
          height: 8rem;
        }

        .type {
          position: absolute;
          left: 0.5rem;
          top: 0.5rem;
          font-size: 0.9rem;
          background-color: rgb(142, 174, 205);
          color: white;
          border-radius: 0.9rem;
          padding: 0 0.5rem;

        }

        .name {
          @include line-ellipsis(2);
        }
      }
    }

  }
}

.click-to-other {
  @include click-to-other()
}

.is-add::before {
  content: "已收藏"
}

.is-add:hover::before {
  content: "取消收藏";
}
</style>