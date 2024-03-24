<template>
  <div class="f">
    <div v-if="isMe && logout" class="follow" @click="doLogout">退出登录</div>
    <div v-if="isMe && change" class="follow" @click="doChange">修改信息</div>
    <div v-if="!isMe" class="follow" v-show="!isFollow" @click="follow(true)">+关注</div>
    <div v-if="!isMe" class="follow is-follow" v-show="isFollow && !isFriend" @click="follow(false)"></div>
    <div v-if="!isMe" class="follow is-follow each-other" v-show="isFollow && isFriend" @click="follow(false)"></div>

    <el-dialog v-model="changeOpen" title="修改个人信息" width="80%">
      <div class="change-container">
        <div class="avatar">
          <UploadImg 
            :img="img" 
            :description="'修改头像'" 
            @on-pic-update="
              (result: string)=> {
                img = i+result
                changeForm.avatar = result
              }" />
        </div>
        <div class="words">
          <div class="name">
            <h3>修改昵称</h3>
            <el-input v-model="changeForm.name"></el-input>
          </div>
          <div class="description">
            <h3>修改个签</h3>
            <el-input type="textarea" :rows="5" v-model="changeForm.description"></el-input>
          </div>
        </div>
      </div>
      <div class="submit" style="text-align: right;">
        <el-button plain @click="changeSubmit">修改</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed, ref, onMounted, watch } from "vue"
import { ElMessage, ElMessageBox } from 'element-plus'

import { doFollow, undoFollow, logout as doLogoutAPI } from "@/api/userAPI"

import getIdAndName from "@/common/getIdAndName"
import router from "@/router"

import UploadImg from "@/components/UploadImg.vue"
import { update } from "@/api/userAPI"
import sleep from "@/common/sleep"

const props = withDefaults(
  defineProps<{
  isFollow: any
  userId: number | string
  isFriend: any
  logout: boolean | string
  change: boolean | string
  userInfo?: any
}>(), {
  logout: false,
  change: false,
})

const emits = defineEmits(["onFollowChange"])

const { isFollow, userId, logout, change, userInfo } = toRefs(props)

const { id, name } = getIdAndName()

const follow = async (operation: boolean) => {
  if (userId.value == id.value) {
    ElMessage.error("自己不能关注自己！")
  }
  if (operation) {
    const res = await doFollow(userId.value as unknown as number, id, name)
    if (res.isSuccess) {
      emits("onFollowChange", true)
      ElMessage.success("关注成功！")
    } else {
      ElMessage.error("关注失败！")
    }
  } else {
    const res = await undoFollow(userId.value as unknown as number, id, name)
    if (res.isSuccess) {
      emits("onFollowChange", false)
      ElMessage.success("取关成功！")
    } else {
      ElMessage.error("取关失败！")
    }
  }
}

const doLogout = async()=> {
  if(isMe.value) {
    ElMessageBox.confirm("Sure to log out?")
      .then(async ()=> {
        //下登
        const result = await doLogoutAPI(id) 
        if(result.isSuccess) {
          ElMessage.success("Logout success!")
          localStorage.removeItem("accessToken")
          localStorage.removeItem("refreshToken")
          localStorage.removeItem("auditorId")
          router.push(`/lib`)
          location.reload()
        } else {
          if(result.error) {
            throw result.error
          } else {
            throw result.message
          }
        }
      })
      .catch((e)=> {
        console.log(e)
      })
  }
}

const i = "http://localhost:8080/img/"
const img = ref("")
const changeOpen = ref(false)
const oldForm = ref({
  avatar: "",
  name: "",
  description: ""
})
const changeForm = ref({
  id: 0,
  avatar: "",
  name: "",
  description: ""
})
const doChange = async()=> {
  changeOpen.value = true
}
const isMe = computed(()=> {
  return userId.value == id
})

watch(userInfo, (newVal)=> {
  const cf = changeForm.value
  cf.id = newVal.id
  cf.avatar = newVal.avatar
  if(newVal.avatar) {
    img.value = i+newVal.avatar
  } else {
    img.value = i+'noImg.png'
  }
  cf.name = newVal.name
  cf.description = newVal.description
  const oform = oldForm.value
  oform.avatar = newVal.avatar
  oform.name = newVal.name
  oform.description = newVal.description
}, {deep: true})

const changeSubmit = async()=> {
  const cf = changeForm.value
  const ofrom = oldForm.value
  if(
    (cf.avatar === ofrom.avatar)
    && (cf.name === ofrom.name)
    && (cf.description === ofrom.description)
  ) {
    ElMessage.error("Nothing change!")
    return
  }
  const result = await update(cf)
  if(result.isSuccess) {
    ElMessage.success(result.data)
    changeOpen.value = false
    await sleep(100)
    location.reload()
  } else {
    if(result.error) {
      ElMessage.error(result.error)
    } else {
      ElMessage.error(result.message)
    }
  }
}
</script>

<style scoped lang="scss">
.follow {
  font-size: 0.75rem;
  border: 0.5px solid gray;
  border-radius: 1rem;
  padding: 0.1rem 0.25rem;
  margin-left: 1rem;
  position: relative;
  top: -0.25rem;
  width: 4rem;
  text-align: center;
  cursor: pointer;
  transition: 0.2s;

  &:hover {
    border-color: blue;
    color: blue;
  }
}

.change-container {
  display: flex;
  .words {
    width: 100%;
    margin-left: 2rem;
    &>div {
      display: flex;
      margin-top: 0.5rem;
      &:first-child {
        margin-top: 0;
      }
      h3 {
        flex: none;
        margin-right: 0.5rem;
      }
    }
    .description {
      display: block;
    }
  }
}

.is-follow::before {
  content: "已关注"
}

.each-other::before {
  content: "已互关";
}

.is-follow:hover::before {
  content: "取消关注";
}
</style>