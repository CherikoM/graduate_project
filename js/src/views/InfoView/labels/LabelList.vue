<template>
  <div class="label-release">
    <div class="big-title">
      发行列表
    </div>
    <el-divider />
    <el-pagination v-model:current-page="areaDatas.pageNum" v-model:page-size="areaDatas.pageSize"
      :page-sizes="[10, 20, 30]" :small="true" layout="total, sizes, prev, pager, next, jumper"
      :total="areaDatas.totalCount" @size-change="handleSizeChange" @current-change="handleCurrentChange"
      class="label-release-list" />

    <div class="list-item" v-for="item in areaDatas.labelRelease" :key="item.id">
      <div class="cover-title-label">
        <div class="cover">
          <img class="cover" :src="'http://localhost:8080/img/' + (item.picture ? item.picture : 'noImg.png')">
        </div>
        <div class="title-label">
          <div class="title">
            <span v-for="(a, index) in item.artist" :key="a.id">
              <span class="click-to-other" @click="router.push(`/info/artists/${a.id}`)">
                {{ a.name }}
              </span>
              {{ index === item.artist.length - 1 ? '' : ' & ' }}
            </span>
            -
            <span class="click-to-other" @click="router.push(`/info/releases/${item.id}`)">
              {{ item.title }}
            </span> ({{ item.format }}{{ item.form && ' - ' + item.form }})
          </div>
          <div class="label">
            {{ item.releaseDate }} - {{ item.catno }}
          </div>
        </div>
      </div>
    </div>

  </div>

  <div class="comment">
    <div class="big-title">
      评论区
    </div>
    <CommentArea :area="3" :area-id="id" :area-name="areaDatas.data.name" />
  </div>

</template>

<script setup lang="ts">
import { toRefs } from 'vue'
import router from '@/router'

import { getLabelRelease } from '@/api/releaseAPI'
import type { releaseData } from '@/api/dataInterfaces'

const props = defineProps<{
  areaDatas: any
  id: number
  getCatno: Function
}>()

const { areaDatas, id, getCatno } = toRefs(props)


const handleCurrentChange = async (a: number) => {
  areaDatas.value.pageNum = a
  const middle = await getLabelRelease(id.value, areaDatas.value.pageNum, areaDatas.value.pageSize)
  getCatno.value(middle.data)
  areaDatas.value.labelRelease = middle.data
}
const handleSizeChange = async (a: number) => {
  areaDatas.value.pageSize = a
  const middle = await getLabelRelease(id.value, areaDatas.value.pageNum, areaDatas.value.pageSize)
  getCatno.value(middle.data)
  areaDatas.value.labelRelease = middle.data
}

</script>

<style scoped lang="scss">
.list-item {
  margin-top: 0.5rem;

  .cover-title-label {
    display: flex;
    align-items: flex-end;
    min-width: 0;
    flex: 1;

    .cover {
      width: 4rem;
      height: 4rem;
      flex: none;
    }

    .title-label {
      margin-left: 1rem;
      flex: 1;
      @include one-line-hide();

      .title {
        @include one-line-hide();
      }

      .label {
        @include one-line-hide();
      }
    }
  }
}

.label-release {
  &>

  .label-release-list {
    margin: 0.5rem auto;
  }
}

.big-title {
    margin: 0.5rem 0;
    font-size: 1.5rem;
  }
.click-to-other {
  @include click-to-other()
}
</style>