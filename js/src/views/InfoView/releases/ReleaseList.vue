<template>
  <el-tabs v-model="areaDatas.releaseActiveName">
    <el-tab-pane label="曲目表" name="tracklist" v-if="areaDatas.data.tracklist">
      <div class="track-list-main">
        <div class="list-item" v-for="(item, index) in areaDatas.data.tracklist" :key="index">
          <span class="position">{{ item.position }}&nbsp;&nbsp;</span>
          <span class="title">{{ item.title }}</span>
          <span class="duration">{{ item.duration }}</span>
          <div v-if="item.extraartists">
            <div v-for="(it, idx) in item.extraartists" :key="idx" class="extra-artists">
              — {{ it.role }}&nbsp;
              <span @click="router.push(`/info/artists/${it.id}`)" class="click-to-other">{{ it.name
              }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-tab-pane>
    <el-tab-pane label="参与人员" name="credits" v-if="areaDatas.data.credits">
      <ul class="credit-list-main">
        <li class="list-item" v-for="item in areaDatas.data.credits" :key="item.id">
          {{ item.role }} -
          <span @click="router.push(`/info/artists/${item.id}`)" class="click-to-other">{{ item.name }}</span>
        </li>
      </ul>
    </el-tab-pane>
    <el-tab-pane label="补充" name="supplement" v-if="areaDatas.data.supplement">
      <div class="supplement-content">
        {{ areaDatas.data.supplement }}
      </div>
    </el-tab-pane>
    <el-tab-pane label="其他版本" name="other" v-if="areaDatas.data.otherVersion">
      <div class="list-item" v-for="item in areaDatas.data.otherVersion" :key="item.id">
        <span @click="router.push(`/info/releases/${item.id}`)" :class="{ 'click-to-other': Number(id) !== item.id }"
          :style="{ 'font-weight': Number(id) === item.id ? 'bold' : 'normal' }">
          {{ item.title }}
        </span>
        &nbsp; ({{ item.releaseDate }})
      </div>
    </el-tab-pane>
    <el-tab-pane label="评论区" name="comment">
      <CommentArea :area="1" :area-id="id" :area-name="areaDatas.data.title" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { toRefs } from 'vue'
import router from '@/router'

const props = defineProps<{
  areaDatas: any
  id: number
}>()

const { areaDatas } = toRefs(props)
</script>

<style scoped lang="scss">
.supplement-content {
  margin-top: 0.25rem;
  text-indent: 2rem;
}

.list-item {
  margin-top: 0.5rem;

  .duration {
    position: absolute;
    right: 0;
  }

  .extra-artists {
    text-indent: 6em;
    font-size: 0.75rem;
  }
}

.click-to-other {
  @include click-to-other()
}
</style>