<!-- eslint-disable vue/valid-v-for -->
<template>
  <div class="tracks">
    <TrackItem v-for="(track, index) in tracklist" :key="makeNum()" :track="track" :index="index"
      @on-track-delete="deleteTrack" />
  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue'

import TrackItem from './TrackItem.vue'

import makeNum from '@/common/makeNum'

const props = defineProps<{
  tracklist: any
}>()

const { tracklist } = toRefs(props)

const deleteTrack = (index: number) => {
  const conf = confirm("确定要删除吗？")
  if (conf) {
    tracklist.value.splice(index, 1)
  }
}
</script>

<style scoped>
.tracks {
  margin-bottom: 0.5rem;
}
</style>