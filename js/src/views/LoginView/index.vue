<template>
  <div class="container">
    <div class="inner">
      <Transition name="login">
        <div class="login" v-show="area === 'login'">
          <h3>登录</h3>
          <div class="inputs">
            <el-input class="input" v-model="login.mail" placeholder="请输入邮箱" />
            <el-input class="input" v-model="login.password" :show-password="true" placeholder="请输入密码" />
          </div>
          <el-button plain class="btn" @click="loginTest">登录</el-button>
          <div class="mention">
            还没有账号？<span class="click-to-other" @click="area = 'register'">去注册</span>
          </div>
        </div>
      </Transition>
      <Transition name="login">
        <div class="register" v-show="area === 'register'">
          <h3>注册</h3>
          <div class="inputs">
            <el-input class="input" v-model="register.name" placeholder="用户名" />
            <el-input class="input" v-model="register.mail" placeholder="邮箱" />
            <el-input class="input" v-model="register.password" :show-password="true"
              placeholder="密码，6-18位以字母开头的数字/字母/下划线" />
            <el-input class="input" v-model="register.confirmPassword" :show-password="true" placeholder="确认密码" />
          </div>
          <el-button plain class="btn" @click="registerTest">注册</el-button>
          <div class="mention">
            已有账号？<span class="click-to-other" @click="area = 'login'">去登录</span>
          </div>
        </div>
      </Transition>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue'

import { ElMessage } from 'element-plus'
import router from '@/router'

import { userRegister, userLogin } from '@/api/userAPI'
import isLogin from '@/common/isLogin'

const reload = inject("reload") as Function

const login = ref({
  mail: '',
  password: ''
})

const register = ref({
  name: '',
  mail: '',
  password: '',
  confirmPassword: ''
})

const loginTest = async () => {
  const frontResult = loginValidate()
  if (!frontResult.isSuccess) {
    ElMessage({
      message: frontResult.message,
      type: 'error'
    })
  } else {
    const result = await userLogin(JSON.stringify({
      mail: login.value.mail,
      password: login.value.password
    }))
    if (result.isSuccess) {
      window.localStorage.setItem("accessToken", result.message.accessToken)
      window.localStorage.setItem("refreshToken", result.message.refreshToken)
      if(result.message.auditorId) {
        window.localStorage.setItem("auditorId", result.message.auditorId)
      }
      router.push(`/lib`)
      location.reload()
    } else {
      ElMessage({
        message: result.message,
        type: 'error'
      })
    }
  }
}

const loginValidate = () => {
  if (login.value.mail === '') {
    return {
      isSuccess: false,
      message: '请输入邮箱！'
    }
  } else if (login.value.password === '') {
    return {
      isSuccess: false,
      message: '请输入密码！'
    }
  } else if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(login.value.mail)) {
    return {
      isSuccess: false,
      message: '邮箱格式不正确！'
    }
  } else if (!/^[a-zA-Z]\w{5,17}$/.test(login.value.password)) {
    return {
      isSuccess: false,
      message: '密码有误！'
    }
  } else {
    return {
      isSuccess: true
    }
  }
}

const registerTest = async () => {
  const frontResult = registerValidate()
  if (!frontResult.isSuccess) {
    ElMessage({
      message: frontResult.message,
      type: 'error'
    })
  } else {
    const result = await userRegister(JSON.stringify({
      name: register.value.name,
      mail: register.value.mail,
      password: register.value.password
    }))
    if (result.isSuccess) {
      window.localStorage.setItem("accessToken", result.message.accessToken)
      window.localStorage.setItem("refreshToken", result.message.refreshToken)
      router.push(`/lib`)
      location.reload()
    } else {
      ElMessage({
        message: result.message,
        type: 'error'
      })
    }
  }

}

const registerValidate = () => {
  if (register.value.name === '') {
    return {
      isSuccess: false,
      message: '请输入用户名！'
    }
  } else if (register.value.mail === '') {
    return {
      isSuccess: false,
      message: '请输入邮箱！'
    }
  } else if (register.value.password === '') {
    return {
      isSuccess: false,
      message: '请输入密码！'
    }
  } else if (register.value.confirmPassword === '') {
    return {
      isSuccess: false,
      message: '请确认密码！'
    }
  } else if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(register.value.mail)) {
    return {
      isSuccess: false,
      message: '邮箱格式不正确！'
    }
  } else if (!/^[a-zA-Z]\w{5,17}$/.test(register.value.password)) {
    return {
      isSuccess: false,
      message: '密码格式有误！'
    }
  } else if (register.value.password !== register.value.confirmPassword) {
    return {
      isSuccess: false,
      message: '确认密码与密码不一致！'
    }
  } else {
    return {
      isSuccess: true
    }
  }
}

const area = ref("login")
</script>

<style scoped lang="scss">
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(245, 245, 245);

  .inner {
    position: relative;
    top: -3rem;


    &>div {
      position: absolute;
      top: 0;
      left: 50%;
      transform: translate(-50%, -50%);
      width: fit-content;
      height: fit-content;
      background-color: #fff;
      border-radius: 0.25rem;
      box-shadow: var(--el-box-shadow-dark);
      padding: 1rem;
    }


    h3 {
      text-align: center;
    }

    .inputs {
      width: 30rem;

      .input {
        margin-top: 1rem;
      }
    }

    .btn {
      margin-top: 1rem;
      width: 100%;
    }

    .mention {
      margin-top: 1rem;
      font-size: 0.75rem;
      text-align: right;
    }
  }
}

.click-to-other {
  @include click-to-other()
}

.login-enter-active,
.login-leave-active {
  transition: opacity 0.5s ease;
  // transform: rotateY(180deg);
}

.login-enter-from,
.login-leave-to {
  opacity: 0;
  transform: rotateY(180deg);
}

.login-enter-to,
.login-leave-from {
  transform: rotateY(0deg);
}
</style>