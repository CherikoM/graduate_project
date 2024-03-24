<template>
  <div class="contribute-item">
    <div class="words">
      <div class="main-info">
        {{ toNow(item.auditTime) }}，{{ item.change ? "修改了" : "新增了" }}{{ areaStr(item.area) }}条目：<span
          @click="router.push(`/info/${item.area}/${item.area === 'styles' ? item.areaName : item.areaId}`)"
          class="click-to-other">{{ item.areaName }}</span>
      </div>
      <div class="changes" v-if="item.change">
        修改了{{ JSON.parse(item.change).join(", ") }}
      </div>
    </div>
    <div class="thanks">
      <div class="thank-box" v-if="!isMe">
        <div class="thank-box-move" :class="{ 'move': item.isThank }" @click="thank(item, index as number)">
          <IconGood />
          <IconGoodFill />
        </div>
      </div>
      <div class="thank-box" v-else>
        <IconAchievement v-if="item.size === 0" />
        <IconAchievementFill v-else />
      </div>
      <div class="thank-count" :style="{ 'opacity': isMe || item.isThank ? 1 : 0 }">
        已有{{ item.size }}人感谢<span v-show="isMe">您</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed } from "vue"
import router from "@/router"

import { ElMessage } from "element-plus"

import IconGood from '@/components/icons/IconGood.vue'
import IconGoodFill from '@/components/icons/IconGoodFill.vue'
import IconAchievement from '@/components/icons/IconAchievement.vue'
import IconAchievementFill from '@/components/icons/IconAchievementFill.vue'

import { doThankContribute, undoThankContribute } from "@/api/userAPI"
import getIdAndName from '@/common/getIdAndName'

const props = defineProps<{
  item: any
  isMe: any
  userId: string | number
  index: number | string
}>()

const { item, isMe, userId } = toRefs(props)

const { id, name } = getIdAndName()

const toNow = computed(() => (date: Date) => {
  //@ts-ignore
  const res = (Date.now() - new Date(date)) / 1000 / 60 / 60 / 24
  const floorRes = Math.floor(res)
  if (res < 0.1) {
    return "不久前"
  } else if (res < 1) {
    return "今天"
  } else if (floorRes < 7) {
    return `${floorRes}天前`
  } else if (floorRes < 28) {
    return `${Math.floor(floorRes / 7)}周前`
  } else if (floorRes < 365) {
    return `${Math.floor(floorRes / 30)}个月前`
  } else {
    return `${Math.floor(floorRes / 365)}年前`
  }
})

const areaStr = computed(() => (area: string) => {
  let res = ""
  switch (area) {
    case "releases":
      res = "发行"
      break
    case "artists":
      res = "艺人"
      break
    case "labels":
      res = "厂牌"
      break
    case "styles":
      res = "风格"
      break
  }
  return res
})

const thank = async (item: any, index: number) => {
  if (userId.value != item.userId) {
    if (item.isThank) {
      const result = await undoThankContribute(item.area, item.id, id)
      if (result) {
        item.size--
        item.isThank = false
        ElMessage.success("取消感谢成功！")
        return
      } else {
        ElMessage.error("取消感谢失败！")
      }
    } else {
      const result = await doThankContribute(item.area, item.id, id)
      if (result) {
        item.size++
        item.isThank = true
        ElMessage.success("感谢成功！")
        return
      } else {
        ElMessage.error("感谢失败！")
      }
    }
  } else {
    ElMessage.error("自己不能感谢自己！")
  }

}
</script>

<style scoped lang="scss">
.contribute-item {
  margin: 1rem auto;
  width: 30rem;
  padding: 1rem;
  background-color: #fff;
  display: flex;

  .words {
    width: 24rem;
    @include one-line-hide();

    .main-info {
      @include one-line-hide();

    }

    .changes {
      @include one-line-hide();
      font-size: 0.75rem;
      color: gray;
      text-indent: 2rem
    }
  }

  .thanks {
    display: flex;
    flex-direction: column;
    align-items: center;

    .thank-box {
      width: 1.5rem;
      height: 1.5rem;
      overflow: hidden;
      cursor: pointer;

      .thank-box-move {
        transform: translateY(0);
        transition: transform 0.5s;

      }

      .move {
        transform: translateY(-1.87rem);
      }
    }

    .thank-count {
      white-space: nowrap;
      transform: scale(0.6);
      opacity: 0;
      transition: 0.25s;
    }

    &:hover>.thank-count {
      opacity: 1 !important;
    }
  }
}

.click-to-other {
  @include click-to-other();
}

svg {
  width: 1.5rem;
  height: 1.5rem;
}
</style>