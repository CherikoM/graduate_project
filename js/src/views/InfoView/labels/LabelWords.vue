<template>
  <div>
    <h2 class="title">
      {{ areaDatas.data.name }}
    </h2>
    <div class="infos">
      <table>
        <tr v-if="areaDatas.data.profile">
          <td>简介</td>
          <td>{{ areaDatas.data.profile }}</td>
        </tr>
        <tr v-if="areaDatas.data.parentName && areaDatas.data.parentId">
          <td>父厂牌</td>
          <td>
            <span @click="router.push(`/info/labels/${areaDatas.data.parentId}`)" class="click-to-other">
              {{ areaDatas.data.parentName }}
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.children">
          <td>子厂牌</td>
          <td>
            <span v-for="(item, index) in areaDatas.data.children" :key="index"
              @click="router.push(`/info/labels/${item.id}`)" class="click-to-other">
              {{ index === areaDatas.data.children?.length as number - 1 ? item.name : item.name + ', ' }}
            </span>
          </td>
        </tr>
        <tr v-if="areaDatas.data.contact">
          <td>联系</td>
          <td>
            <p v-for="(item, index) in areaDatas.data.contact" :key="index">
              {{ item }}
            </p>
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