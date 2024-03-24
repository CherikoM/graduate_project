<template>
  <header>
    <div class="logo" @click="toMain"></div>
      <nav>
        <RouterLink class="guide" to="/">首页</RouterLink>
        <RouterLink class="guide" to="/lib">百科</RouterLink>
        <RouterLink v-if="!isLogin()" class="guide" to="/login">注册/登录</RouterLink>
        <RouterLink v-if="isLogin()" class="guide" :to="`/user/${id}`">我的</RouterLink>
      </nav>
  </header>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import isLogin from '@/common/isLogin'

import getIdAndName from '@/common/getIdAndName'

//点击logo时能回到首页
const router = useRouter()
const toMain = ()=> {
  router.push('/')
}

const { id, name } = getIdAndName()

// const isLogin = ref(false)

// onMounted(()=> {
//   const token = window.localStorage.getItem("token")
//   if(token && token !== null) {
//     return true
//   } else {
//     return false
//   }
// })
</script>

<style lang="scss" scoped>
header {
  height: 4rem;
  background-color: rgb(229, 231, 234);
  display: flex;
  align-items: center;
  font-size: 1.2rem;
  .logo {
    width: 8rem;
    height: 3rem;
    background-color: gray;
    margin-left: 1rem;
  }
  nav {
    
    .guide {
      position: relative;
      top: 0.5rem;
      margin-left: 1rem;
      &:hover {
        color: $test-color;
      }
    }
  }
}
</style>