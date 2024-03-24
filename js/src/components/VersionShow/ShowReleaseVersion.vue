<template>
  <div class="version-info-main">
    <h4>
      <slot></slot>
    </h4>
    <el-divider class="divider" style="margin-top: 0.5rem;" />
    <div class="picture" v-if="info.picture">
      <h4 :class="{ 'change': hasChange('picture') }">封面</h4>
      <div class="pic">
        <img class="pic" :src="'http://localhost:8080/img/'+(info.picture? info.picture: 'noImg.png')">
      </div>
    </div>
    <div class="genre" v-if="info.genre">
      <h4 :class="{ 'change': hasChange('genre') }">流派</h4>
      <div class="gen">
        <span v-for="(g, index) in info.genre" :key="index">
          <span>{{ g }}</span><span>{{ index === info.genre.length - 1 ? "" : ", " }}</span>
        </span>
      </div>
    </div>
    <div class="style">
      <h4 :class="{ 'change': hasChange('style') }">风格</h4>
      <div class="sty" v-if="info.style">
        <span v-for="(s, index) in info.style" :key="index">
          <span>{{ s }}</span><span>{{ index === info.style.length - 1 ? "" : ", " }}</span>
        </span>
      </div>
    </div>
    <div class="format" v-if="info.format">
      <h4 :class="{ 'change': hasChange('format') }">格式</h4>
      <div class="fmt">
        {{ info.format }}
      </div>
    </div>
    <div class="form" v-if="info.form">
      <h4 :class="{ 'change': hasChange('form') }">形式</h4>
      <div class="fom">
        {{ info.form }}
      </div>
    </div>
    <div class="country" v-if="info.country">
      <h4 :class="{ 'change': hasChange('country') }">国家/地区</h4>
      <div class="cou">
        {{ info.country }}
      </div>
    </div>
    <div class="date" v-if="info.releaseDate">
      <h4 :class="{ 'change': hasChange('releaseDate') }">发行日期</h4>
      <div class="dat">
        {{ info.releaseDate }}
      </div>
    </div>
    <div class="artist" v-if="info.artist">
      <h4 :class="{ 'change': hasChange('artist') }">艺人</h4>
      <div class="art">
        <span class="a-item" v-for="(a, index) in info.artist" :key="a.id">
          <span>{{ a.name }}</span><span>{{ index === info.artist.length - 1 ? "" : ", " }}</span>
        </span>
      </div>
    </div>
    <div class="label" v-if="info.releaseLabel">
      <h4 :class="{ 'change': hasChange('label') }">厂牌</h4>
      <div class="art">
        <span class="a-item" v-for="(l, index) in info.releaseLabel" :key="l.id">
          <span>{{ l.name }}</span><span>{{ index === info.releaseLabel.length - 1 ? "" : ", " }}</span>
        </span>
      </div>
    </div>
    <div class="tracklist" v-if="info.tracklist">
      <h4 :class="{ 'change': hasChange('tracklist') }">曲目表</h4>
      <div class="track">
        <div class="t" v-for="(track, index) in info.tracklist" :key="index">
          <div class="main">
            <div class="position-title">
              <div class="position">
                {{ track.position }}
              </div>
              <div class="title">
                <span v-if="track.artist">
                  <span v-for="(a, index) in track.artist" :key="index">
                    <span>
                      {{ a.name }}
                    </span>
                    <span>
                      {{ index === track.artist.length - 1 ? "" : "&" }}
                    </span>
                  </span>
                </span>
                {{ track.title }}
              </div>
            </div>

            <div class="duration">
              {{ track.duration }}
            </div>
          </div>
          <div class="extra" v-if="track.extraartists">
            <div class="extra-artist" v-for="(ea, index) in track.extraartists" :key="index">
              -{{ ea.role }}:&nbsp;<span>{{ ea.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="credits" v-if="info.credits">
      <h4 :class="{ 'change': hasChange('credits') }">幕后人员</h4>
      <div class="cre">
        <div class="c-item" v-for="a in info.credits" :key="a.id">
          {{ a.role }}:&nbsp;<span>{{ a.name }}</span>
        </div>
      </div>
    </div>
    <div class="main-release" v-if="info.mainRelease && info.mainRelease.title">
      <h4 :class="{ 'change': hasChange('mainRelease') }">发行样板</h4>
      <div>{{ info.mainRelease.title }}({{ info.mainRelease.isNew ? "新创建" :
        info.mainRelease.id
      }})<span v-if="info.isBase">(基准版)</span></div>
    </div>
    <div class="supplement" v-if="info.supplement">
      <h4 :class="{ 'change': hasChange('supplement') }">补充说明</h4>
      <p>{{ info.supplement }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, computed } from 'vue'

const props = defineProps<{
  info: any
  change?: string[]
}>()

const { info, change } = toRefs(props)

const hasChange = computed(() => (words: string) => {
  if(!change?.value) return false
  if (!change.value) return false
  return change.value.indexOf(words) >= 0
})

</script>

<style scoped lang="scss">
.version-info-main {
  padding: 1rem;
  border-radius: 0.25rem;
  box-shadow: var(--el-box-shadow);
  width: 97%;
  height: fit-content;
  margin: 0 auto;
  background-color: #fff;

  &>div {
    margin-bottom: 1rem;
  }

  h4 {
    font-size: 1.1rem;
    margin-bottom: 0.5rem;
  }

  .picture {
    .pic {
      width: 12rem;
      height: 12rem;
      background-color: pink;
      margin: 0 auto;
    }
  }

  .tracklist {
    .main {
      display: flex;
      justify-content: space-between;

      .position-title {
        display: flex;

        .title {
          margin-left: 0.5rem;
        }
      }
    }

    .extra {
      font-size: 0.75rem;
      text-indent: 2rem;
    }
  }

  .supplement {
    p {
      text-indent: 2rem;
    }
  }
}

.change {
  color: red;
}
</style>