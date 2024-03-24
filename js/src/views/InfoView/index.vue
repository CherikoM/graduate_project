<!-- eslint-disable vue/no-use-v-if-with-v-for -->
<template>
  <div class="all-container" v-loading="loading">
    <div class="info-container" v-if="success && !loading && connect">
      <div class="base-info" v-if="area !== 'styles'">
        <div class="base-info-main">
          <div class="left">
            <div class="picture">
              <img class="picture" :src="img">
            </div>
            <div class="modify" v-if="isLogin()">
              <span class="click-to-other" @click="router.push(`/history/${area}/${id}`)">历史版本</span> /
              <span class="click-to-other" @click="router.push(`/contribute/${area}/${id}`)">信息纠错</span>
            </div>
          </div>

          <div class="words-info">
            <ReleaseWords :areaDatas='releaseDatas' v-if="area === 'releases'" />
            <ArtistWords :areaDatas="artistDatas" v-if="area === 'artists'" />
            <LabelWords :areaDatas="labelDatas" v-if="area === 'labels'" />
          </div>
        </div>

        <AddToCollection class="add-to-list" :area="area+''" :area-id="id" />

      </div>
      <div class="list-info" v-if="area !== 'styles'">
        <ReleaseList :areaDatas='releaseDatas' :id="id * 1" v-if="area === 'releases'" />
        <ArtistList :area-datas="artistDatas" :id="id * 1" :pageSize="pageSize" v-if="area === 'artists'" />
        <LabelList :area-datas="labelDatas" :id="id * 1" :getCatno="getCatno" v-if="area === 'labels'" />
      </div>
      <StyleInfo :areaDatas="styleDatas" v-if="area === 'styles'" />
    </div>
    <div class="get-failed" v-if="!success && !loading && connect">
      <el-empty class="empty-remind" description="未找到资料">
        <el-button @click="router.push(`/contribute/${area}`)" plain>去贡献资料</el-button>
      </el-empty>
    </div>
    <div class="get-failed" v-if="!connect">
      <el-empty class="empty-remind" description="网络连接失败">
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import router from '@/router'
import { getReleaseById, getArtistReleaseCount, getArtistRelease, getArtistFeatured, getArtistBehind, getLabelRelease } from '@/api/releaseAPI'
import { getArtistById } from '@/api/artistAPI'
import { getLabelById } from '@/api/labelAPI'
import { getStyleByEnName } from "@/api/styleAPI"

import ReleaseWords from '@/views/InfoView/releases/ReleaseWords.vue'
import ReleaseList from '@/views/InfoView/releases/ReleaseList.vue'
import ArtistWords from '@/views/InfoView/artists/ArtistWords.vue'
import ArtistList from '@/views/InfoView/artists/ArtistList.vue'
import LabelWords from '@/views/InfoView/labels/LabelWords.vue'
import LabelList from '@/views/InfoView/labels/LabelList.vue'
import StyleInfo from './styles/StyleInfo.vue'
import AddToCollection from './AddToCollection.vue'

import type { releaseData, artistData, labelData } from '@/api/dataInterfaces'
import sleep from '@/common/sleep'
import isLogin from '@/common/isLogin'

const { params } = useRoute()

const id = ref(params.id as any)
const area = ref(params.area)

const pageSize = ref(10)

const loading = ref(true)
const success = ref(false)
const connect = ref(true)

const img = ref('http://localhost:8080/img/')

//releases相关的数据
const releaseDatas = ref({
  //@ts-ignore
  data: ref<releaseData>({}),
  releaseActiveName: 'tracklist',
})

//artists相关的数据
const artistDatas = ref({
  //@ts-ignore
  data: ref<artistData>({}),
  artistActiveName: 'releases',
  //@ts-ignore
  tabInfo: ref({
    releaseInfo: {
      count: 0,
      currentPage: 1,
      //@ts-ignore
      infoMain: ref<releaseData[]>([]),
    },
    featuredInfo: {
      count: 0,
      currentPage: 0,
      //@ts-ignore
      infoMain: ref<releaseData[]>([])
    },
    behindInfo: {
      count: 0,
      currentPage: 0,
      infoMain: ref<releaseData[]>([])
    }
  }),

  pageNum: 1,
  pageSize: 10
})

const labelDatas = ref({
  //@ts-ignore
  data: ref<labelData>({}),
  totalCount: 1,
  pageNum: 1,
  pageSize: 10,
  labelRelease: ref<releaseData[]>([]),
})

const styleDatas = ref({
  data: ref<any>({})
})

const getCatno = (releases: releaseData[]) => {
  releases.forEach(release => {
    let catno
    const labels = release.releaseLabel as { id: number, catno: string, name: string }[]
    if (labels) {
      labels.forEach(label => {
        if (label.id == id.value) {
          catno = label.catno
        }
      })
      release.catno = catno
    }
  })
}


//挂载时获取数据
onMounted(async () => {
  let data
  let middle
  switch (area.value) {
    case 'releases':
      data = await getReleaseById(id.value) as releaseData
      if (typeof (data) !== 'string' && data) {
        if(data.picture) {
          img.value += data.picture
        } else {
          img.value += 'noImg.png'
        }
        releaseDatas.value.data = data
        success.value = true
      } else if (data === 'Net error!') {
        connect.value = false
      }
      await sleep(1000)
      loading.value = false
      break
    case 'artists':
      data = await getArtistById(id.value) as artistData
      if (typeof (data) !== 'string' && data) {
        artistDatas.value.data = data
        if(data.picture) {
          img.value += data.picture
        } else {
          img.value += 'noImg.png'
        }
        middle = await getArtistReleaseCount(id.value);
        artistDatas.value.tabInfo.releaseInfo.count = middle.release
        artistDatas.value.tabInfo.featuredInfo.count = middle.featured
        artistDatas.value.tabInfo.behindInfo.count = middle.behind
        middle = await getArtistRelease(id.value, 1, pageSize.value)
        artistDatas.value.tabInfo.releaseInfo.infoMain = middle as releaseData[]
        success.value = true
      } else if (data === 'Net error!') {
        connect.value = false
      }
      await sleep(1000)
      loading.value = false
      break
    case 'labels':
      data = await getLabelById(id.value) as labelData
      if (typeof (data) !== 'string' && data) {
        labelDatas.value.data = data
        if(data.picture) {
          img.value += data.picture
        } else {
          img.value += 'noImg.png'
        }
        middle = await getLabelRelease(id.value, labelDatas.value.pageNum, labelDatas.value.pageSize)
        getCatno(middle.data)
        labelDatas.value.labelRelease = middle.data
        labelDatas.value.totalCount = middle.totalCount
        success.value = true
      } else if (data === 'Net error!') {
        connect.value = false
      }
      await sleep(1000)
      loading.value = false
      break
    case "styles":
      data = await getStyleByEnName(id.value)
      if (typeof (data) !== "string" && data) {
        styleDatas.value.data = data
        success.value = true
      } else if (data === "Net error!") {
        connect.value = false
      }
      await sleep(1000)
      loading.value = false
      break
  }
})
</script>

<style lang="scss" scoped>
.info-container {
  @include container();

  .base-info {
    padding: 2rem;
    display: flex;
    justify-content: space-between;

    .base-info-main {
      display: flex;

      .left {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-left: 1rem;
        margin-right: 1rem;

        // margin-right: 2rem;
        .picture {
          width: 12rem;
          height: 12rem;
          flex: none;
        }

        .modify {
          font-size: 0.75rem;
        }
      }

      .words-info {
        margin-left: 1rem;
        margin-right: 2rem;
      }
    }

    .add-to-list {
      flex: none;
      margin-right: 1rem;
    }


  }

  .list-info {
    width: 90%;
    margin: 0 auto;
    background-color: #fff;
    padding: 0.5rem 2rem 1rem;
    border-radius: 0.25rem;
    box-shadow: var(--el-box-shadow);

    .supplement-content {
      margin-top: 0.25rem;
      text-indent: 2rem;
    }

    .loading,
    .no-more {
      margin-top: 1rem;
      width: 100%;
      text-align: center;
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

      .artist-release {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .year {
          width: fit-content;
          flex: none;
          margin-left: 1rem;
        }
      }

      .cover-title-label {
        display: flex;
        align-items: flex-end;
        min-width: 0;
        flex: 1;

        .cover {
          width: 4rem;
          height: 4rem;
          background-color: pink;
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
      &>.title {
        margin: 0.5rem 0;
        font-size: 1.5rem;
      }

      .label-release-list {
        margin: 0.5rem auto;
      }
    }
  }

}

.click-to-other {
  cursor: pointer;

  &:hover {
    color: $test-color;
  }
}

.empty-remind {
  margin-top: 20vh;
}
</style>