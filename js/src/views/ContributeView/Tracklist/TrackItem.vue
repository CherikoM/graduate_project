<template>
  <div class="track">
    <div class="new-track-switch click-to-other" v-if="type==='new-track'" @click="switchChange">添加曲目</div>
    <div class="track-main" v-if="type!=='new-track'">
      <div class="info">
        <div class="main-info">
          <div class="position-artist-title">
            <div class="position">
              {{ track.position }}
            </div>
            <div class="artist" v-show="track.artist && track.artist.length > 0">
              <span v-for="(a, index) in track.artist" :key="a.id">
                <span @click="router.push(`/info/artist/${a.id}`)" class="click-to-other">{{ a.name
                }}</span>{{
  index === track.artist.length - 1 ? '' : '&' }}
              </span>
              &nbsp;
            </div>
            <div class="title">
              {{ track.title }}
            </div>
          </div>
          <div class="duration">
            {{ track.duration }}
          </div>
        </div>
        <div class="extra-artists">
          <div class="extra-artist" v-for="a in track.extraartists" :key="a.id">
            - {{ a.role && a.role !== '' ? a.role + ': ' : '' }} {{ a.name }}
          </div>
        </div>
      </div>
      <div class="operation">
        <el-button plain text size="small" class="modify" @click="trackModify(track)">
          {{ track.open ? '完成' : '修改' }}
        </el-button>
        <el-button plain text size="small" class="del" @click="emit('onTrackDelete', index)">删除</el-button>
      </div>
    </div>
    <div class="track-modify" v-show="track.open">
      <div class="words-modify">
        <div class="new-track-title">
          <h5>标题</h5>
          <el-input v-model="track.title" size="small" />
        </div>
        <div class="duration-position">
          <div class="new-track-duration">
            <h5>时长</h5>
            <el-input v-model="track.duration" size="small" />
          </div>
          <div class="new-track-position">
            <h5>标号</h5>
            <el-input v-model="track.position" size="small" />
          </div>
        </div>
      </div>
      <div class="artist-modify">
        <h5>主要艺人</h5>
        <SearchAddArtist :artist="track.artist" size="small" :type="'many'" />
        
        <h5 style="margin-top: 1rem;">其他艺人</h5>
        <SearchAddArtist :artist="track.extraartists" size="small" :type="'many'">
          <template #info-small="{ item }">
            <RoleSlot :item="item">
              <span v-show="
                //@ts-ignore
                item.role
              ">: </span>
              <span class="name click-to-other" @click="router.push(`/info/artists/${item.id}`)">
                {{ item.name }}</span>
              <span class="id">({{ item.id }})</span>
            </RoleSlot>
          </template>
        </SearchAddArtist>
      </div>
      <div class="add" v-if="type==='new-track'">
        <el-button plain class="add-btn" size="small" @click="emit('onTrackAdd')">添加</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs } from 'vue'

import router from '@/router'

import { ElMessage } from 'element-plus'

import SearchAddArtist from '../searchAdd/SearchAddArtist.vue'
import RoleSlot from '../searchAdd/RoleSlot.vue'

const props = defineProps<{
  track: any,
  index?: number,
  type?: string
}>()

const emit = defineEmits(['onTrackAdd', 'onTrackDelete'])

const { track, type } = toRefs(props)

//roleBox dom
const roleBox = ref(null)

const switchChange = ()=> {
  track.value.open = !track.value.open
}

/**
 * 检测曲目的数据，标题和标号不能为空
 * @param track 
 */
const trackModify = (track: any) => {
  if (!track.open) {
    track.open = !track.open
  } else {
    if (track.title === '') {
      ElMessage({
        message: "曲目标题不能为空！",
        type: 'error'
      })
    } else if (track.position === '') {
      ElMessage({
        message: "曲目标号不能为空！",
        type: 'error'
      })
    } else {
      track.open = !track.open
    }
  }
}


const addNewTrack = ()=> {
  emit('onTrackAdd')
}
</script>

<style scoped lang="scss">
.track {
  background-color: #fff;
  border-radius: 0.25rem;
  padding: 0.25rem;
  margin-bottom: 0.5rem;

  box-shadow: val(--el-box-shadow-light);

  .new-track-switch {
    text-align: center;
  }

  .track-main {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;

    .info {
      width: 85%;

      .main-info {
        display: flex;
        justify-content: space-between;

        .position-artist-title {
          display: flex;

          .position {
            margin-right: 1rem;
          }
        }
      }

      .extra-artists {
        .extra-artist {
          text-indent: 4rem;
          font-size: 0.75rem;
        }
      }
    }

    .operation {
      flex: none;

      .del {
        margin-left: -0.1rem;
      }
    }
  }

  .track-modify {
    display: flex;
    flex-direction: column;
    padding: 1rem;

    .words-modify {
      margin-bottom: 1rem;

      h5 {
        flex: none;
        font-size: 0.75rem;
        margin-right: 1rem;
      }

      .new-track-title {
        display: flex;
        align-items: center;
        margin-bottom: 0.5rem;
      }

      .duration-position {
        display: flex;
        justify-content: space-between;

        &>div {
          width: 48%;
          display: flex;
          align-items: center;
        }
      }
    }

    .add {
      margin-top: 1rem;
      display: flex;
      justify-content: flex-end;
      .add-btn {
        margin-right: 0%;
        
      }
    }
  }
}



.click-to-other {
  @include click-to-other();
}
</style>