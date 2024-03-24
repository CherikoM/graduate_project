<template>
  <div>
    <div class="input-comment">
      <el-input v-model="textarea" :rows="3" type="textarea" placeholder="Please input" />
      <div class="input-extra" v-if="isLogin()">
        <div class="left">
          <el-pagination small layout="prev, pager, next" :total="totalCount" v-model:page-size="pageSize"
            v-model:current-page="currentPage" @click="commentChange" />
        </div>
        <div class="right">
          <div class="rest-words" :style="{ 'color': textarea.length > 250 ? 'red' : 'rgb(50, 50, 50)' }">
            {{ (textarea.length < 250 ? "还能输入" : "已超出") + commentRest + "字" }} <div class="submit-button">
              <el-button plain @click="submit(null)">提交</el-button>
          </div>
        </div>

      </div>
    </div>
  </div>


  <div class="comment-list">
    <el-empty description="no comment" v-if="commentData.length <= 0" />
    <div class="comment-list-main" v-else>
      <div class="comment-item" v-for="item in commentData" :key="item.id">
        <div class="avatar">
          <img class="avatar" :src="'http://localhost:8080/img/' + (item.user.avatar ? item.user.avatar : 'noImg.png')"
            @click="router.push(`/user/${item.user.id}`)" />
        </div>
        <div class="comment">
          <div class="info"> <span class="name" @click="router.push(`/user/${item.user.id}`)">{{ item.user.name }}</span>
            &nbsp;{{ toNow(item.gmtCreated as unknown as Date) }}
          </div>
          <div class="content">
            <span v-html="item.content"></span>
          </div>
          <div class="more">
            <div class="title" @click="openCommit(item)">
              <span class="close" v-show="!item.open" v-if="isLogin() || item.subCount > 0">
                {{ item.subCount > 0 ? `有${item.subCount}条回复，点击展开` : `还没有回复，点击回复` }}
              </span>
              <span class="open" v-show="item.open">
                点击收起
              </span>
            </div>
            <div class="reply">
              <div class="input-reply" v-show="item.open" v-if="isLogin()">
                <el-input v-model="item.replyText" :rows="2" type="textarea" placeholder="Please input" />
                <div class="input-extra">
                  <div class="left">
                    <el-pagination small layout="prev, pager, next" :total="item.subCount"
                      v-model:page-size="smallPageSize" v-model:current-page="item.currentPage"
                      style="margin-bottom: 0.5rem;" @current-change="replyChange(item)" />
                  </div>
                  <div class="right">
                    <div class="rest-words"
                      :style="{ 'color': (item.replyText && item.replyText.length > 250) ? 'red' : 'rgb(50, 50, 50)' }">
                      {{ ((!item.replyText || item.replyText?.length < 250) ? "还能输入" : "已超出") + replyRest(item.replyText)
                        + "字" }} <div class="submit-button">
                        <el-button plain @click="submit(item)">提交</el-button>
                    </div>
                  </div>
                </div>

              </div>
              <div class="reply-list" v-show="item.reply && item.reply.length > 0">
                <div class="reply-item" v-for="i in item.reply" :key="i.id">
                  <div class="avatar">
                    <img class="avatar"
                      :src="'http://localhost:8080/img/' + (i.user.avatar ? i.user.avatar : 'noImg.png')"
                      @click="router.push(`/user/${i.user.id}`)" />
                  </div>
                  <div class="content">
                    <div class="name">
                      {{ i.user.name }}
                    </div>
                    <div class="content">
                      <span v-html="i.content"></span>
                    </div>
                  </div>



                </div>
              </div>
            </div>
          </div>
        </div>
      </div>



    </div>
  </div>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, toRefs } from "vue"
import router from "@/router"

import { ElMessage } from "element-plus"

import { getTopCommentByArea, getSubComment, addCommit } from "@/api/commentAPI"

import isLogin from "@/common/isLogin"
import getIdAndName from "@/common/getIdAndName"

const props = defineProps<{
  area: number
  areaId: number
  areaName: string
}>()
const { area, areaId, areaName } = toRefs(props)

const { id } = getIdAndName()

const textarea = ref("")

const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const smallPageSize = ref(5)


const submit = async (item: any) => {
  if (item) {
    if (item.replyText.length <= 0) {
      ElMessage.error("please enter comment")
    } else if (item.replyText.length > 250) {
      ElMessage.error("too much characters")
    } else {
      const form = {
        area: area.value,
        areaId: areaId.value,
        areaName: areaName.value,
        content: item.replyText,
        parentId: item.id,
        userId: id
      }
      console.log(form)
      const res1 = await addCommit(form)
      if(!res1.isSuccess) {
        ElMessage.error(res1.message)
      } else {
        item.replyText = ""
        ElMessage.success(res1.message)
        currentPage.value=1
        item.currentPage = 1
        replyChange(item)
      }
    }
  } else {
    if (textarea.value.length <= 0) {
      ElMessage.error("please enter comment")
    } else if (textarea.value.length > 250) {
      ElMessage.error("too much characters")
    } else {
      const form = {
        area: area.value,
        areaId: areaId.value,
        areaName: areaName.value,
        content: textarea.value,
        userId: id
      }
      const res1 = await addCommit(form)
      if(!res1.isSuccess) {
        ElMessage.error(res1.message)
      } else {
        textarea.value = ""
        ElMessage.success(res1.message)
        currentPage.value=1
        const res2 = await getComment()
        commentData.length=0
        commentData.push(...res2.data)
      }
      
    }
  }
}

const commentRest = computed(() => {
  let length = textarea.value.length
  return length < 250 ? (250 - length) : (length - 250)
})
const replyRest = computed(() => (text: string) => {
  if (!text?.length) {
    return 250
  }
  let length = text.length
  return length < 250 ? (250 - length) : (length - 250)
})

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

const commentData = reactive<any>([])


const openCommit = async (item: any) => {
  if (!item.currentPage) item.currentPage = 1
  if (!item.reply) item.reply = []
  if (!item.replyText) item.replyText = ""
  if (item.subCount > 0 && item.reply.length <= 0) {
    const result = await getReply(item.id, 1)
    if (!result) {
      ElMessage.error("unknown error!")
    } else {
      console.log(result)
      item.reply = [...result.data]
    }
  }
  item.open = !item.open
}
const replyChange = async (item: any) => {
  const result = await getReply(item.id, item.currentPage)
  if (!result) {
    ElMessage.error("unknown error!")
  } else {
    console.log(result)
    item.reply = [...result.data]
  }
}
const getReply = async (parentId: number, currentPage: number) => {
  const result = await getSubComment(parentId, currentPage, smallPageSize.value)
  if (!result.isSuccess) {
    ElMessage.error(result.message)
  } else {
    return result.data
  }
}

const commentChange = async () => {
  const result = await getComment()
  if (!result) {
    ElMessage.error("unknown error!")
  } else {
    totalCount.value = result.totalCount
    commentData.length = 0
    commentData.push(...result.data)
  }
}
const getComment = async () => {
  const result = await getTopCommentByArea(area.value, areaId.value, currentPage.value, pageSize.value)
  if (result.isSuccess) {
    return result.data
  }
}
onMounted(async () => {
  const result = await getComment()
  if (!result) {
    // ElMessage.error("unknown error!")
  } else {
    // const data = result.data
    totalCount.value = result.totalCount
    commentData.push(...result.data)
  }
})
</script>

<style scoped lang="scss">
.input-comment {}

.comment-list {
  .comment-list-main {
    .comment-item {
      display: flex;
      padding: 1rem;

      .avatar {
        width: 4rem;
        height: 4rem;
        border-radius: 50%;
        transition: 0.2s;

        &:hover {
          box-shadow: var(--el-box-shadow);
          cursor: pointer;
        }
      }

      .comment {
        margin-left: 0.5rem;
        width: 100%;

        .info {
          font-size: 0.8rem;

          .name {
            &:hover {
              color: $test-color;
              cursor: pointer;
            }
          }
        }


      }

      .content {
        padding: 0.5rem;
      }

      .more {
        .title {
          font-size: 0.75rem;
          text-align: right;

          &:hover {
            color: $test-color;
            cursor: pointer;
          }
        }

        .reply {


          .reply-list {
            .reply-item {
              display: flex;

              .avatar {
                width: 3rem;
                height: 3rem;
              }

              .content {
                padding: 0.5rem;
                margin-left: 0.5rem;

                .name {
                  font-size: 0.75rem;
                }

                .content {
                  padding: 0.25rem;
                  margin-top: 0.25rem;
                }
              }

            }
          }

        }
      }
    }
  }
}

.input-extra {
  margin-top: 0.25rem;
  display: flex;
  justify-content: space-between;

  .right {
    .rest-words {
      font-size: 0.8rem;
      text-align: right;
      color: rgb(50, 50, 50);
    }

    .submit-button {
      // margin-top: 0.5rem;
      text-align: right;
    }
  }


}
</style>