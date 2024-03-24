<template>
  <el-tabs v-model="areaDatas.artistActiveName" @tab-click="onTabClick" class="artist-tabs">
    <el-tab-pane label="主要发行" name="releases" v-if="areaDatas.tabInfo.releaseInfo.count > 0">
      <div class="pane-core" v-if="areaDatas.artistActiveName === 'releases'">
        <ul v-infinite-scroll="releaseLoad" class="list" :infinite-scroll-disabled="releaseDisabled">
          <li v-for="item in areaDatas.tabInfo.releaseInfo.infoMain" :key="item.id" class="list-item artist-release-item">
            <div class="artist-release">
              <div class="cover-title-label">
                <div class="cover">
                  <img class="cover" :src="'http://localhost:8080/img/'+(item.picture? item.picture: 'noImg.png')">
                </div>
                <div class="title-label">
                  <div class="title">
                    <span class="click-to-other" @click="router.push(`/info/releases/${item.id}`)">
                      {{ item.title }}
                    </span> ({{ item.format }}{{ item.form && ' - ' + item.form }})
                  </div>
                  <div class="label">
                    <span class="click-to-other" @click="router.push(`/info/labels/${item.releaseLabel[0].id}`)">{{
                      item.releaseLabel[0].name }}</span> -
                    {{ item.releaseLabel[0].catno }}
                  </div>
                </div>
              </div>


              <div class="year">
                {{
                  //@ts-ignore
                  item.releaseDate.slice(0, 4)
                }}
              </div>
            </div>
          </li>
        </ul>
        <p v-if="releaseLoading" class="loading">正在加载中...</p>
        <p v-if="releaseNoMore" class="no-more">没有更多了</p>
      </div>
    </el-tab-pane>
    <el-tab-pane label="参与发行" name="featured" v-if="areaDatas.tabInfo.featuredInfo.count > 0">
      <div class="pane-core" v-if="areaDatas.artistActiveName === 'featured'">
        <ul v-infinite-scroll="featuredLoad" class="list" :infinite-scroll-disabled="featuredDisabled">
          <li v-for="item in areaDatas.tabInfo.featuredInfo.infoMain" :key="item.id"
            class="list-item artist-release-item">
            <div class="artist-release">
              <div class="cover-title-label">
                <div class="cover">
                  <img class="cover" :src="'http://localhost:8080/img/'+(item.picture? item.picture: 'noImg.png')">
                </div>
                <div class="title-label">
                  <div class="title">
                    <span class="click-to-other" @click="router.push(`/info/releases/${item.id}`)">
                      {{ item.title }}
                    </span> ({{ item.format }}{{ item.form && ' - ' + item.form }})
                  </div>
                  <div class="label">
                    <span class="click-to-other" @click="router.push(`/info/labels/${item.releaseLabel[0].id}`)">{{
                      item.releaseLabel[0].name }}</span> -
                    {{ item.releaseLabel[0].catno }}
                  </div>
                </div>
              </div>


              <div class="year">
                {{
                  //@ts-ignore
                  item.releaseDate.slice(0, 4)
                }}
              </div>
            </div>
          </li>
        </ul>
        <p v-if="featuredLoading" class="loading">正在加载中...</p>
        <p v-if="featuredNoMore" class="no-more">没有更多了</p>
      </div>
    </el-tab-pane>
    <el-tab-pane label="幕后制作" name="behind" v-if="areaDatas.tabInfo.behindInfo.count > 0">
      <div class="pane-core" v-if="areaDatas.artistActiveName === 'behind'">
        <ul v-infinite-scroll="behindLoad" class="list" :infinite-scroll-disabled="behindDisabled">
          <li v-for="item in areaDatas.tabInfo.behindInfo.infoMain" :key="item.id" class="list-item artist-release-item">
            <div class="artist-release">
              <div class="cover-title-label">
                <div class="cover">
                  <img class="picture" :src="'http://localhost:8080/img/'+(item.picture? item.picture: 'noImg.png')">
                </div>
                <div class="title-label">
                  <div class="title">
                    <span class="click-to-other" @click="router.push(`/info/releases/${item.id}`)">
                      {{ item.title }}
                    </span> ({{ item.format }}{{ item.form && ' - ' + item.form }})
                  </div>
                  <div class="label">
                    <span class="click-to-other" @click="router.push(`/info/labels/${item.releaseLabel[0].id}`)">{{
                      item.releaseLabel[0].name }}</span> -
                    {{ item.releaseLabel[0].catno }}
                  </div>
                </div>
              </div>
              <div class="year">
                {{
                  //@ts-ignore
                  item.releaseDate.slice(0, 4)
                }}
              </div>
            </div>
          </li>
        </ul>
        <p v-if="behindLoading" class="loading">正在加载中...</p>
        <p v-if="behindNoMore" class="no-more">没有更多了</p>
      </div>
    </el-tab-pane>
    <el-tab-pane label="评论区" name="comment">
      <CommentArea :area="2" :area-id="id" :area-name="areaDatas.data.name" />
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import { toRefs, computed, ref } from 'vue'
import router from '@/router'
import type { TabsPaneContext } from 'element-plus'

import { getArtistRelease, getArtistFeatured, getArtistBehind } from '@/api/releaseAPI'
import type { releaseData } from '@/api/dataInterfaces'

import CommentArea from '@/components/CommentArea.vue'

const props = defineProps<{
  areaDatas: any
  id: number
  pageSize: number
}>()

const { areaDatas, id, pageSize } = toRefs(props)

//#region
const onTabClick = (pane: TabsPaneContext) => {
  const name = pane.props.name
  if (name === 'featured' && areaDatas.value.tabInfo.featuredInfo.currentPage <= 0) {
    featuredLoad()
  } else if (name === 'behind' && areaDatas.value.tabInfo.behindInfo.currentPage <= 0) {
    behindLoad()
  }
  areaDatas.value.artistActiveName = name
}

const releaseLoading = ref(false)
const releaseNoMore = computed(() => areaDatas.value.tabInfo.releaseInfo.infoMain.length >= areaDatas.value.tabInfo.releaseInfo.count)
const releaseDisabled = computed(() => releaseLoading.value || releaseNoMore.value)
const releaseLoad = () => {
  releaseLoading.value = true
  setTimeout(async () => {
    areaDatas.value.tabInfo.releaseInfo.currentPage++
    const middle = await getArtistRelease(id.value, areaDatas.value.tabInfo.releaseInfo.currentPage, pageSize.value)
    if (middle) {
      areaDatas.value.tabInfo.releaseInfo.infoMain.push(...middle as releaseData[])
    }
    releaseLoading.value = false
  }, 2000)

}

const featuredLoading = ref(false)
const featuredNoMore = computed(() => areaDatas.value.tabInfo.featuredInfo.infoMain.length >= areaDatas.value.tabInfo.featuredInfo.count)
const featuredDisabled = computed(() => featuredLoading.value || featuredNoMore.value)
const featuredLoad = () => {
  featuredLoading.value = true
  setTimeout(async () => {
    areaDatas.value.tabInfo.featuredInfo.currentPage++
    const middle = await getArtistFeatured(id.value, areaDatas.value.tabInfo.featuredInfo.currentPage, pageSize.value)
    if (middle) {
      areaDatas.value.tabInfo.featuredInfo.infoMain.push(...middle as releaseData[])
    }
    featuredLoading.value = false
  }, 2000)
}

const behindLoading = ref(false)
const behindNoMore = computed(() => areaDatas.value.tabInfo.behindInfo.infoMain.length >= areaDatas.value.tabInfo.behindInfo.count)
const behindDisabled = computed(() => behindLoading.value || behindNoMore.value)
const behindLoad = () => {
  behindLoading.value = true
  setTimeout(async () => {
    areaDatas.value.tabInfo.behindInfo.currentPage++
    const middle = await getArtistBehind(id.value, areaDatas.value.tabInfo.behindInfo.currentPage, pageSize.value)
    if (middle) {
      areaDatas.value.tabInfo.behindInfo.infoMain.push(...middle as releaseData[])
    }
    behindLoading.value = false
  }, 2000)
}


//#endregion
</script>

<style scoped lang="scss">
.loading,
.no-more {
  margin-top: 1rem;
  width: 100%;
  text-align: center;
}

.list-item {
  margin-top: 0.5rem;

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

.click-to-other {
  @include click-to-other()
}
</style>