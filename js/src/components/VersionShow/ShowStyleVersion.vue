<template>
  <div class="version-info-main">
    <h4>
      <slot></slot>
    </h4>
    <el-divider class="divider" style="margin-top: 0.5rem;" />
    <div class="name" v-if="info.name!==null">
      <h4 :class="{ 'change': hasChange('name') }">中文译名</h4>
      <div class="name">
        {{ info.name }}
      </div>
    </div>
    <div class="other-name" v-if="info.otherName">
      <h4 :class="{ 'change': hasChange('other name') }">别名</h4>
      <div class="other">
        <span class="o-item" v-for="(o, index) in info.otherName" :key="index">
          <span>{{ o }}</span><span>{{ index === info.otherName.length - 1 ? "" : ", " }}</span>
        </span>
      </div>
    </div>
    <div class="belong" v-if="info.belong">
      <h4 :class="{ 'change': hasChange('belong') }">父风格</h4>
      <div class="bel">
        <span class="b-item" v-for="(b, index) in info.belong" :key="index">
          <span @click="toNewBlank(`/info/styles/${b}`)">{{ b }}</span><span>{{ index === info.belong.length - 1 ? "" : ", " }}</span>
        </span>
      </div>
    </div>
    <div class="type" v-if="info.type!==null">
      <h4 :class="{ 'change': hasChange('type') }">类型</h4>
      <div class="type">
        {{ typeStr }}
      </div>
    </div>
    <div class="description" v-if="info.description!==null">
      <h4 :class="{ 'change': hasChange('description') }">正文</h4>
      <div class="description-main" v-html="info.description">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed } from 'vue'
import router from '@/router'
import '@wangeditor/editor/dist/css/style.css' // 引入 css

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

const toNewBlank = (path: string, query: any = {}) => {
  const url = router.resolve({ path, query })
  window.open(url.href, '_blank')
}

const typeStr = computed(()=> {
  if(isNaN(info.value.type*1)) {
    return info.value.type
  }
  let res = null
  switch(info.value.type) {
    case "0":
      res = "genre"
      break
    case "1":
      res = "primary"
      break
    case "2":
      res = "subdivide"
      break
  }
  return res
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

</style>

<style lang="scss">
.description-main {
  @include rich-text()
}
</style>