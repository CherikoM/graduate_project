<template>
  <div class="container" v-if="isPageAlive">
    <HeadGuide class="head-guide"></HeadGuide>
    <RouterView class="router-view" :key="route.fullPath+Math.random()" />
  </div>
</template>

<script setup lang="ts">
import { nextTick, ref, provide } from 'vue'
import { useRouter, RouterLink, RouterView, useRoute, onBeforeRouteUpdate } from 'vue-router'
// import { Router } from '@/router'

import HeadGuide from './components/HeadGuide.vue'

const route = useRoute()
const router = useRouter()

const isPageAlive = ref(true)

onBeforeRouteUpdate((to, from, next)=> {
  next()
})

</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;

  .head-guide {
    flex: none;
  }

  .router-view {
    height: calc(100vh - 4rem);
    flex: 1;
  }
}
</style>
