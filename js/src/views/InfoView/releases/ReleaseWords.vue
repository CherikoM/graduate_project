<template>
  <div class="words-info">
    <h2>
      <div class="title">
        <span v-for="(item, index) in areaDatas.data.artist" :key="item.id" class="click-to-other"
          @click="router.push(`/info/artists/${item.id}`)">
          {{ index === areaDatas.data.artist.length - 1 ? item.name : (item.name + ' & ') }}
        </span>
        - {{ areaDatas.data.title }}
      </div>
    </h2>

    <div class="infos">
      <table>
        <tr v-if="areaDatas.data.releaseLabel">
          <td>厂牌</td>
          <td>
            <span v-for="(item, index) of areaDatas.data.releaseLabel" :key="item.id">
              <span class="click-to-other" @click="router.push(`/info/labels/${item.id}`)">
                {{ item.name }}
              </span>
              <span v-if="item.catno">
                - {{ item.catno }}
              </span>
              {{ index === areaDatas.data.releaseLabel.length - 1 ? "" : ", " }}
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.format">
          <td>格式</td>
          <td>{{ areaDatas.data.format }}</td>
        </tr>
        <tr v-if="areaDatas.data.form">
          <td>形式</td>
          <td>{{ areaDatas.data.form
          }}</td>
        </tr>
        <tr v-if="areaDatas.data.country">
          <td>国家</td>
          <td>{{ areaDatas.data.country }}</td>
        </tr>
        <tr v-if="areaDatas.data.releaseDate">
          <td>日期</td>
          <td>{{ areaDatas.data.releaseDate }}</td>
        </tr>
        <tr v-if="areaDatas.data.genre">
          <td>流派</td>
          <td>
            <span v-for="(str, index) in areaDatas.data.genre" :key="index" @click="router.push(`/info/styles/${str}`)"
              class="click-to-other">
              {{ index === areaDatas.data.genre.length - 1 ? str : (str + ', ') }}
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.style">
          <td>风格</td>
          <td>
            <span v-for="(str, index) in areaDatas.data.style" :key="index" @click="router.push(`/info/styles/${str}`)"
              class="click-to-other">
              {{ index === areaDatas.data.style.length - 1 ? str : (str + ', ') }}
            </span>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { toRefs } from 'vue'
import router from '@/router'

const props = defineProps<{
  areaDatas: any
}>()

const { areaDatas } = toRefs(props)
</script>

<style lang="scss" scoped>
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