<template>
  <div class="words-info">
    <h2 class="title">
      {{ areaDatas.data.name }}
    </h2>
    <div class="infos">
      <table>
        <tr v-if="areaDatas.data.realName">
          <td>真名</td>
          <td>{{ areaDatas.data.realName }}</td>
        </tr>
        <tr v-if="areaDatas.data.nickname">
          <td>昵称</td>
          <td>{{ areaDatas.data.nickname }}</td>
        </tr>
        <tr v-if="areaDatas.data.profile">
          <td>简介</td>
          <td>{{ areaDatas.data.profile }}</td>
        </tr>
        <tr v-if="areaDatas.data.type === 0 && areaDatas.data.belong">
          <td>在组</td>
          <td>
            <span v-for="(item, index) in areaDatas.data.belong" :key="item.id" class="click-to-other"
              @click="router.push(`/info/artists/${item.id}`)"
              :style="{ 'text-decoration-line': (item.isActive ? 'none' : 'line-through') }">
              {{ index === areaDatas.data.belong.length - 1 ? item.name : (item.name + ', ') }}
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.official">
          <td>官网</td>
          <td>
            <a v-for="(item, index) in areaDatas.data.official" :key="index" :href="item">
              {{ index === areaDatas.data.official.length - 1 ? item : (item + ', ') }}
            </a>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue'
import router from '@/router'

const props = defineProps<{
  areaDatas: any
}>()

const { areaDatas } = toRefs(props)
</script>

<style scoped lang="scss">
.title {
  margin-bottom: 0.5rem;
}

.infos {
  font-size: 14px;

  tr {
    td {
      padding: 0.1rem 0;

      &:first-child {
        min-width: 4rem;
        vertical-align: top;
        white-space: nowrap;
      }
    }
  }
}

.click-to-other {
  @include click-to-other()
}
</style>