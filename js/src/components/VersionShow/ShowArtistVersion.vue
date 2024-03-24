<template>
  <div class="version-info-main">
    <h4>
      <slot></slot>
    </h4>
    <el-divider class="divider" style="margin-top: 0.5rem;" />
    <div class="picture" v-if="info.picture">
      <h4 :class="{ 'change': hasChange('picture') }">照片</h4>
      <div class="pic">
        <img class="pic" :src="'http://localhost:8080/img/' +(info.picture? info.picture: 'noImg.png')">
      </div>
    </div>
    <div class="type" v-if="info.type!==null">
      <h4 :class="{ 'change': hasChange('type') }">类型</h4>
      <div class="type">
        {{ info.type }}
      </div>
    </div>
    <div class="real-name" v-if="info.realName">
      <h4 :class="{ 'change': hasChange('realName') }">真名</h4>
      <div class="rn">
        {{ info.realName }}
      </div>
    </div>
    <div class="nickname" v-if="info.nickname">
      <h4 :class="{ 'change': hasChange('nickname') }">昵称</h4>
      <div class="nn">
        {{ info.nickname }}
      </div>
    </div>
    <div class="belong" v-if="info.belong">
      <h4 :class="{ 'change': hasChange('belong') }">所属</h4>
      <div class="bel">
        <span class="b-item" v-for="(b, index) in info.belong" :key="b.id">
          <span>{{ b.name }}</span><span>{{ index === info.belong.length - 1 ? "" : ", " }}</span><span>({{ b.isActive? "活跃中": "已退出" }})</span>
        </span>
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
    <div class="profile" v-if="info.profile">
      <h4 :class="{ 'change': hasChange('profile') }">简介</h4>
      <p>{{ info.profile }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed } from 'vue'

const props = defineProps<{
  info: any
  change?: string[]
}>()

const { info, change } = toRefs(props)

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
</style>