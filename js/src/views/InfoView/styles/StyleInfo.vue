<template>
  <div class="style-container">
    <div class="top">
      <h1>
        音乐{{ areaDatas.data.type === "genre" ? "流派" : "风格" }}简介：
        {{ areaDatas.data.value }}
      </h1>
      <div class="modify" v-if="isLogin()">
        <span class="click-to-other" @click="router.push(`/history/styles/${areaDatas.data.enName}`)">历史版本</span> /
        <span class="click-to-other" @click="router.push(`/contribute/styles/${areaDatas.data.enName}`)">信息纠错</span>
      </div>
    </div>

    <el-divider class="divider" />


    <div class="tabel">
      <table>
        <tr v-if="areaDatas.data.name">
          <td>英文名：</td>
          <td>{{ areaDatas.data.enName }}</td>
        </tr>
        <tr v-if="areaDatas.data.otherName && areaDatas.data.otherName.length > 0">
          <td>别名：</td>
          <td>
            <span v-for="(name, index) in areaDatas.data.otherName" :key="index">
              {{ name }}<span>{{ index === areaDatas.data.otherName.length - 1 ? "" : ", " }}</span>
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.type !== 'genre' && areaDatas.data.belong && areaDatas.data.belong.length > 0">
          <td>父流派/风格：</td>
          <td>
            <span v-for="(name, index) in areaDatas.data.belong" :key="index">
              <span @click="router.push(`/info/styles/${name}`)" class="click-to-other">{{ father(name) }}</span>
              <span>{{ index === areaDatas.data.belong.length - 1 ? "" : ", " }}</span>
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.type !== 'subdivide' && areaDatas.data.branch && areaDatas.data.branch.length > 0">
          <td>子风格：</td>
          <td>
            <span v-for="(child, index) in areaDatas.data.branch" :key="child.id">
              <span @click="router.push(`/info/styles/${child.enName}`)" class="click-to-other">{{ child.value }}</span>
              <span>{{ index === areaDatas.data.branch.length - 1 ? "" : ", " }}</span>
            </span>
          </td>
        </tr>
      </table>
    </div>

    <div class="content">
      <div class="inner" v-html="areaDatas.data.description"></div>
    </div>
  </div>
  <div class="comment">
    <h1 class="big-title">
      评论区
    </h1>
    <el-divider class="divider" />
    <CommentArea :area="4" :area-id="areaDatas.data.id" :area-name="areaDatas.data.enName" />
  </div>
</template>

<script setup lang="ts">
import router from '@/router';
import { toRefs, onMounted, computed } from 'vue'

import { useStyleStore } from '@/stores/style'
import isLogin from '@/common/isLogin'

const styleStore = useStyleStore()

const props = defineProps<{
  areaDatas: any
}>()

const { areaDatas } = toRefs(props)


const father = computed(() => (enName: string) => {
  let res = null
  styleStore.genreAndPrimaryList.some((g: any) => {
    if (g.enName === enName && g.name) {
      res = g.name
      return true
    }
  })
  return res ? res : enName
})

onMounted(async () => {
  await styleStore.getGenreAndPrimary()
})
</script>

<style scoped lang="scss">
.style-container {
  padding: 2rem;

  .top {
    display: flex;
    align-items: baseline;

    .modify {
      margin-left: 1rem;
    }
  }

  .tabel {
    margin-top: 0.5rem;
    width: 100%;
    background-color: #fff;
    border-radius: 0.25rem;
    padding: 0.5rem 1rem;
    box-shadow: var(--el-box-shadow);
  }

  .content {
    background-color: #fff;
    border-radius: 0.5rem;
    padding: 1.5rem;
    margin-top: 1rem;
    box-shadow: var(--el-box-shadow);
  }
}

.comment {
    margin: 2rem;
  }
.click-to-other {
  @include click-to-other()
}

.divider {
  margin-bottom: 1rem;
}
</style>
<style lang="scss">
.inner {
  @include rich-text()
}
</style>