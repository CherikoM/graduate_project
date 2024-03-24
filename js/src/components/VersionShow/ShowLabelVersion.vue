<template>
  <div class="version-info-main">
    <h4>
      <slot></slot>
    </h4>
    <el-divider class="divider" style="margin-top: 0.5rem;" />
    <div class="picture" v-if="info.picture">
      <h4 :class="{ 'change': hasChange('picture') }">logo</h4>
      <div class="pic">
        <img class="pic" :src="'http://localhost:8080/img/' + (info.picture? info.picture: 'noImg.png')">
      </div>
    </div>
    <div class="parent" v-if="info.parent || info.parentId || info.parentName">
      <h4 :class="{ 'change': hasChange('parent') }">父厂牌</h4>
      <div class="parent">
        <span 
          @click="toNewBlank(`/info/labels/${info.parentId}`)"
          class="click-to-other">{{ info.parentName }}</span>({{ info.parentId }})
      </div>
    </div>
    <div class="official" v-if="info.official">
      <h4 :class="{ 'change': hasChange('official') }">官网</h4>
      <div class="off">
        <p class="o-item" v-for="(o, index) in info.official" :key="index">
          <a :href="o" target="_blank">{{ index+1 }}&nbsp;&nbsp;{{ o.slice(o.indexOf("/")+2) }}</a>
        </p>
      </div>
    </div>
    <div class="contact" v-if="info.contact">
      <h4 :class="{ 'change': hasChange('contact') }">联系方式</h4>
      <div class="con">
        <p class="c-item" v-for="(c, index) in info.contact" :key="index">
          {{ index }}&nbsp;&nbsp;{{ c }}
        </p>
      </div>
    </div>
    <div class="profile" v-if="info.profile">
      <h4 :class="{ 'change': hasChange('profile') }">简介</h4>
      <p>{{ info.profile }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed } from 'vue'
import router from "@/router"

const props = defineProps<{
  info: any
  change?: string[]
}>()

const { info, change } = toRefs(props)

const toNewBlank = (path: string, query: any = {}) => {
  const url = router.resolve({ path, query })
  window.open(url.href, '_blank')
}

const hasChange = computed(() => (words: string) => {
  if(!change?.value) return false
  if (!change.value) return false
  return change.value.indexOf(words) >= 0
})

</script>

<style scoped lang="scss">
.version-info-main {
  padding: 1rem;
  border-radius: 0.25rem;
  box-shadow: var(--el-box-shadow);
  width: 97%;
  height: fit-content;
  margin: 0 auto;
  background-color: #fff;

  &>div {
    margin-bottom: 1rem;
  }

  h4 {
    font-size: 1.1rem;
    margin-bottom: 0.5rem;
  }

  .picture {
    .pic {
      width: 12rem;
      height: 12rem;
      background-color: pink;
      margin: 0 auto;
    }
  }

  .profile {
    p {
      text-indent: 2rem;
    }
  }
}

.change {
  color: red;
}

.click-to-other {
  @include click-to-other();
}
</style>