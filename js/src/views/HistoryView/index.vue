<template>
  <div class="container">
    <h2 v-show="title">{{ title }}<span v-if="area !== 'styles'">({{ id }})</span>的历史版本</h2>
    <ul class="history-list">
      <li v-for="(item, index) in histories" :key="item.id" class="history-item">
        <div class="info">{{ histories.length - index }}&nbsp;{{ item.gmtModified }}&nbsp;
          Submit by <span @click="router.push(`/user/${item.userId}`)" class="click-to-other">{{ item.userName }}</span>
        </div>
        <div class="change-show">
          <div class="changes">
            <span v-if="item.change && item.change.length > 0">
              (<span v-for="(c, index) in item.change" :key="index">
                {{ c + (item.change.length === index + 1 ? "" : ", ") }}
              </span>)
            </span>
            <span v-else-if="item.new">new!</span>
          </div>
          <div class="show click-to-other" @click="showInfo(item)">
            查看
          </div>
          <div class="thank click-to-other" @click="thank(item)" :style="{'visibility': item.userId!==userId? 'visible': 'hidden'}">
            {{ item.thank ? "已感谢" : "感谢" }}
          </div>
        </div>

      </li>
    </ul>
    <div class="release-show" v-if="area === 'releases'">
      <ShowReleaseVersion :info="info" :change="change" v-show="showHistory">
        更新于{{ time }}的版本<span v-show="thanks > 0" class="thank-count">（已有{{ thanks }}人感谢）</span>
      </ShowReleaseVersion>
    </div>
    <div class="artist-show" v-if="area === 'artists'">
      <ShowArtistVersion :info="info" :change="change" v-show="showHistory">
        更新于{{ time }}的版本<span v-show="thanks > 0" class="thank-count">（已有{{ thanks }}人感谢）</span>
      </ShowArtistVersion>
    </div>
    <div class="label-show" v-if="area === 'labels'">
      <ShowLabelVersion :info="info" :change="change" v-show="showHistory">
        更新于{{ time }}的版本<span v-show="thanks > 0" class="thank-count">（已有{{ thanks }}人感谢）</span>
      </ShowLabelVersion>
    </div>
    <div class="style-show" v-if="area === 'styles'">
      <ShowStyleVersion :info="info" :change="change" v-show="showHistory">
        更新于{{ time }}的版本<span v-show="thanks > 0" class="thank-count">（已有{{ thanks }}人感谢）</span>
      </ShowStyleVersion>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRoute } from "vue-router"
import router from "@/router"

import { ElMessage } from "element-plus"

import { getAllReleaseVersion } from "@/api/releaseAPI"
import { getAllArtistVersion } from "@/api/artistAPI"
import { getAllLabelVersion } from "@/api/labelAPI"
import { getAllStyleVersion } from "@/api/styleAPI"
import { undoThankContribute, doThankContribute } from "@/api/userAPI"

import ShowReleaseVersion from "@/components/VersionShow/ShowReleaseVersion.vue"
import ShowArtistVersion from "@/components/VersionShow/ShowArtistVersion.vue"
import ShowLabelVersion from "@/components/VersionShow/ShowLabelVersion.vue"


import getIdAndName from "@/common/getIdAndName"

const { params } = useRoute()


const id = ref(params.id as any)
const area = ref(params.area as string)

const userId = getIdAndName().id

const title = ref("")

const histories = ref<any>({})

const showHistory = ref(false)
const info = ref<any>({})
const change = ref<string[]>([])
const time = ref<any>()
const thanks = ref(0)

const showInfo = (item: any) => {
  showHistory.value = true
  info.value = JSON.parse(item.info)
  change.value = item.change
  time.value = item.gmtModified
  thanks.value = item.thanks
}

const thank = async (item: any) => {
  if (item.userId != userId) {
    if (item.thank) {
      const result = await undoThankContribute(area.value, item.id, userId)
      if (result) {
        item.thanks--
        thanks.value--
        item.thank = false
        ElMessage.success("取消感谢成功！")
      } else {
        ElMessage.error("取消感谢失败！")
      }
    } else {
      const result = await doThankContribute(area.value, item.id, userId)
      if (result) {
        item.thanks++
        thanks.value++
        item.thank = true
        ElMessage.success("感谢成功！")
      } else {
        ElMessage.error("感谢失败！")
      }
    }
  } else {
    ElMessage.error("自己不能感谢自己！")
  }

}

onMounted(async () => {
  let result
  switch (area.value) {
    case "releases":
      result = await getAllReleaseVersion(id.value, userId)
      title.value = result[0].title
      histories.value = result
      break
    case "artists":
      result = await getAllArtistVersion(id.value, userId)
      title.value = result[0].artistName
      histories.value = result
      break
    case "labels":
      result = await getAllLabelVersion(id.value, userId)
      title.value = result[0].labelName
      histories.value = result
      break
    case "styles":
      result = await getAllStyleVersion(id.value, userId)
      title.value = id.value
      histories.value = result
      break
  }

})
</script>

<style lang="scss" scoped>
.container {
  @include container();
  padding: 2rem;

  .history-list {
    margin-top: 0.5rem;
    margin-bottom: 2rem;

    .history-item {
      display: flex;
      justify-content: space-between;
      cursor: default;
      padding: 0.5rem;
      border-radius: 0.25rem;
      // box-shadow: var(--el-box-shadow-light);
      transition: 0.25s;
      margin-top: 0.5rem;

      &:hover {
        box-shadow: var(--el-box-shadow);
        background-color: #fff;

        .change-show {
          .show {
            opacity: 1;
          }

          .thank {
            opacity: 1;
          }
        }

      }

      .change-show {
        display: flex;

        .changes {
          margin-right: 1rem;
          color: gray;
        }

        .show {
          opacity: 0;
        }

        .thank {
          margin-left: 1rem;
          width: 3rem;
          text-align: center;
          opacity: 0;
        }
      }
    }
  }
}

h2 {
  font-size: 2rem;
}

.click-to-other {
  @include click-to-other()
}

.thank-count {
  color: gray;

}
</style>