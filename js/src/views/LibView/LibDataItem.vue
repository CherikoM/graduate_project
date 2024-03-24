<template>
  <div class="container">
    <el-card class="card" shadow="hover"> 
      <div class="picture">
        <img class="picture" :src="'http://localhost:8080/img/'+(img? img: 'noImg.png')">
      </div>
      <div class="words">
        <h4>
          <slot name="title"></slot>
        </h4>
        <p :style="{'visibility': isHiddenComp}">
          <slot name="sub"></slot>
        </p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, computed } from "vue"

const props = withDefaults(defineProps<{
  isHidden?: boolean
  img?: string
}>(), {
  isHidden: false
}) 

const { isHidden, img } = toRefs(props)

const isHiddenComp = computed(()=> isHidden.value? 'hidden': 'visible')
</script>

<style lang="scss" scoped>
.container {
  // width: 95%;
  margin: 0 auto;
  width: 12rem;
  :deep(.card)> div {
    padding: 1rem;
    padding-bottom: 0.5rem;
  }
  .picture {
    width: 9rem;
    height: 9rem;
    margin: 0 auto;
  }
  .words {
    margin-top: 0.25rem;
    h4 {
      @include one-line-hide();
    }
    p {
      font-size: 12px;
      @include one-line-hide();
    }
  }
}
</style>