<template>
  <div class="friend-list">
    <FriendItem v-for="item in friendData" :key="item.id" :item="item" class="item" />
  </div>
</template>

<script setup lang="ts">
import FriendItem from './FriendItem.vue'

const props = defineProps<{
  friendData: any[]
}>()


</script>

<style lang="scss" scoped>
.friend-list {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 2%;
  row-gap: 1rem;

  .item {
    width: 90%;
  }


}

@media screen and (max-width: 720px) {
  .friend-list {
    grid-template-columns: 1fr;
  }
}

@media screen and (min-width: 1200px) {
  .friend-list {
    grid-template-columns: 1fr 1fr 1fr;
  }
}
</style>