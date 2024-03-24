<template>
  <div class="info-container" ref="containerDOM">
    <div class="loading" v-loading="!isLoadFinish"></div>

    <ReleaseContribute v-if="isLoadFinish && !isSubmitSuccess && canModify && area === 'releases'" :id="id" :area="area"
      :form="releaseForm.form" :reference="releaseForm.reference" :img="releaseForm.img" :origin="origin" :main-release="releaseForm.mainRelease"
      :containerDOM="containerDOM" @on-submit="isSubmitSuccess = true"></ReleaseContribute>

    <ArtistContribute 
      v-if="isLoadFinish && !isSubmitSuccess && canModify && area === 'artists'" 
      :id="id" 
      :area="area"
      :form="artistForm.form" 
      :reference="artistForm.reference" 
      :img="artistForm.img" 
      :origin="origin" 
      :containerDOM="containerDOM" 
      @on-submit="isSubmitSuccess = true"></ArtistContribute>

    <LabelContribute
      v-if="isLoadFinish && !isSubmitSuccess && canModify && area === 'labels'" 
      :id="id"
      :area="area"
      :form="labelForm.form"
      :reference="labelForm.reference"
      :img="labelForm.img"
      :origin="origin"
      :containerDOM="containerDOM"
      @on-submit="isSubmitSuccess = true"></LabelContribute>

    <StyleContribute
      v-if="isLoadFinish && !isSubmitSuccess && canModify && area === 'styles'"
      :id="id"
      :styleId="styleForm.styleId"
      :form="styleForm.form"
      :reference="styleForm.reference"
      :origin="origin"
      :containerDOM="containerDOM"
      @on-submit="isSubmitSuccess = true"></StyleContribute>

    <div class="submit-success" v-show="isSubmitSuccess">
      <div class="submit-success-main" style="text-align: center; margin-top: 4rem;">
        <el-icon size="15rem">
          <CircleCheck />
        </el-icon>
        <p style="font-size: 1.5rem; margin-bottom: 0.5rem;">您的编辑已成功提交，感谢您的贡献！</p>
        <p>编辑内容将在通过审核后展示</p>
        <div class="buttons">
          <el-button plain v-if="id" @click="router.push(`/info/${area}/${id}`)">回到条目页</el-button>
          <el-button plain @click="router.push(`/lib`)">回到信息库</el-button>
        </div>
      </div>

    </div>

    <el-empty description="有新更改尚未审核，暂时无法进入编辑" v-if="!canModify && isLoadFinish" class="empty">
      <el-button plain @click="router.push(`/lib`)">回到信息库</el-button>
      <el-button plain @click="router.push(`/info/${area}/${id}`)">回到发行详情</el-button>
    </el-empty>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

import { useRoute } from 'vue-router'
import router from '@/router'
import { onBeforeRouteLeave } from 'vue-router'

import { ElMessage } from 'element-plus'
import { CircleCheck } from "@element-plus/icons-vue"
import ReleaseContribute from './ReleaseContribute/ReleaseContribute.vue'
import ArtistContribute from "./ArtistContribute/ArtistContribute.vue"
import LabelContribute from '@/views/ContributeView/LabelContribute/LabelContribute.vue'
import StyleContribute from '@/views/ContributeView/StyleContribute/StyleContribute.vue'

import { getReleaseById, startModify as rStartModify } from '@/api/releaseAPI'
import { getArtistById, startModify as aStartModify } from '@/api/artistAPI'
import { getLabelById, startModify as lStartModify } from "@/api/labelAPI"
import { getStyleByEnName, startModify as sStartModify } from "@/api/styleAPI"

import { checkToken } from "@/api/userAPI"

import { useStyleStore } from '@/stores/style'

import sleep from '@/common/sleep'
import getIdAndName from '@/common/getIdAndName'

const { params } = useRoute()

const isLoadFinish = ref(false)
const isSubmitSuccess = ref(false)

//从url中取得params参数：id和area
const id = ref(params.id as any)
const area = ref(params.area as unknown as string)

const containerDOM = ref(null)

//从pinia仓库取得数据
const styleStore = useStyleStore()

let origin: any = {}

//表单
const releaseForm = reactive({
  form: {
    title: '',
    picture: '',
    format: '',
    form: '',
    mainId: null,
    genre: [],
    style: [],
    country: '',
    releaseDate: '',
    artist: [],
    releaseLabel: [],
    tracklist: [],
    credits: [],
    mainRelease: {
      title: '',
      id: 0,
      isNew: false
    },
    supplement: '',
  },

  reference: '',

  mainRelease: {
    defaultTitle: '',
    defaultId: 0
  },

  img: ''
})

const artistForm = reactive({
  form: {
    name: "",
    profile: "",
    type: "",
    realName: "",
    nickname: "",
    belong: [],
    official: [],
    picture: ""
  },
  reference: '',
  img: ""
})

const labelForm = reactive({
  form: {
    name: "",
    picture: "",
    parent: [],
    profile: "",
    official: [],
    contact: [],
  },
  reference: '',
  img: ""
})

const styleForm = reactive({
  form: {
    name: "",
    enName: "",
    otherName: [],
    type: "0",
    description: "",
    belong: []
  },
  reference: "",
  styleId: 0
})

const canModify = ref(true)

const releaseInit = async () => {
  await styleStore.getGenreList()
  await styleStore.getStyleList()
  if (id.value) {
    const result = await getReleaseById(id.value)
    origin = result
    if (typeof (result) !== 'string') {
      releaseForm.form.title = result.title
      releaseForm.form.picture = result.picture
      releaseForm.img = result.picture

      releaseForm.form.form = result.form
      releaseForm.form.format = result.format

      const genres = result.genre
      genres.forEach((g: string, index: number) => {
        styleStore.genreTagList.forEach(gl => {
          if (g === gl.name || g === gl.enName) {
            genres[index] = gl.value
          }
        })
      })
      releaseForm.form.genre = genres

      const styles = result.style
      styles.forEach((s: string, index: number) => {
        styleStore.styleTagList.forEach(sl => {
          if (s === sl.name || s === sl.enName) {
            styles[index] = sl.value
          }
        })
      })
      releaseForm.form.style = styles

      releaseForm.form.style = result.style
      releaseForm.form.country = result.country
      releaseForm.form.releaseDate = result.releaseDate
      releaseForm.form.artist = result.artist
      releaseForm.form.releaseLabel = result.releaseLabel
      releaseForm.form.tracklist = result.tracklist

      if (result.credits) {
        const newCredits: any[] = []
        result.credits.forEach((e: any) => {
          newCredits.push({ ...e })
        })
        //@ts-ignore
        releaseForm.form.credits = newCredits
      }


      if (result.mainRelease) {
        const mr = result.mainRelease
        releaseForm.form.mainRelease.title = mr.title
        releaseForm.form.mainRelease.id = mr.id

        releaseForm.mainRelease.defaultTitle = mr.title
        releaseForm.mainRelease.defaultId = mr.id


        result.mainRelease = { title: mr.title, id: mr.id, isNew: false }
      }
      releaseForm.form.supplement = result.supplement
      sleep(1000)
    } else {
      ElMessage.error(result)
    }
  }
  isLoadFinish.value = true
}

const artistInit = async () => {
  if (id.value) {
    const result = await getArtistById(id.value)
    origin = result
    if (typeof (result) !== "string") {
      artistForm.form.name = result.name
      artistForm.form.profile = result.profile
      artistForm.form.type = result.type
      artistForm.form.realName = result.realName
      artistForm.form.nickname = result.nickname
      artistForm.form.belong = result.belong? result.belong: []
      artistForm.form.official = result.official? result.official: []
      artistForm.form.picture = result.picture
      artistForm.img = result.picture

      sleep(1000)
    } else {
      ElMessage.error(result)
    }
  }
  isLoadFinish.value = true

}

const labelInit = async () => {
  if (id.value) {
    const result = await getLabelById(id.value)
    origin = result
    if (typeof (result) !== "string") {
      labelForm.form.name = result.name
      labelForm.form.profile = result.profile
      if(result.parentId && result.parentName) {
        //@ts-ignore
        labelForm.form.parent.push({ id:result.parentId, name:result.parentName})
      }
      labelForm.form.official = result.official? result.official: []
      labelForm.form.contact = result.contact? result.contact: []
      labelForm.form.picture = result.picture
      labelForm.img = result.picture

      sleep(1000)
    } else {
      ElMessage.error(result)
    }
  }
  isLoadFinish.value = true

}

const styleInit = async () => {
  if (id.value && id.value !== "") {
    const result = await getStyleByEnName(id.value)
    origin = result
    if (typeof (result) !== "string") {
      styleForm.form.name = result.name
      styleForm.form.enName = result.enName
      styleForm.form.otherName = result.otherName? result.otherName: []
      styleForm.form.type = result.type
      styleForm.form.description = result.description
      styleForm.form.belong = result.belong? result.belong: []
      styleForm.styleId = result.id
      sleep(1000)
    } else {
      ElMessage.error(result)
    }
  }
  isLoadFinish.value = true

}

onMounted(async () => {
  //登陆检查
  const loginTest = await checkToken()
  if (!loginTest.isLogin) {
    ElMessage({
      message: loginTest.message,
      type: "error"
    })
    localStorage.removeItem("accessToken")
    localStorage.removeItem("refreshToken")
    router.push(`/login`)
    return
  }

  //重复检查
  const userId = getIdAndName().id
  if (userId && id.value) {
    let canM 
    switch(area.value) {
      case "releases":
        canM = await rStartModify(id.value, userId)
        break
      case "artists":
        canM = await aStartModify(id.value, userId)
        break
      case "labels":
        canM = await lStartModify(id.value, userId)
        break
      case "styles":
        canM = await sStartModify(id.value, userId)
        break
    }
    
    if (!canM?.isSuccess) {
      canModify.value = false
    } else {
      canModify.value = true

      //执行各分区的呈现操作
      switch (area.value) {
        case "releases":
          await releaseInit()
          break
        case "artists":
          await artistInit()
          break
        case "labels":
          await labelInit()
          break
        case "styles":
          await styleInit()
          break
      }
    }
  }

  await sleep(500)

  isLoadFinish.value = true
})
</script>

<style scoped lang="scss">
.info-container {
  position: relative;
  @include container();
  padding: 2rem;
}

.loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: transparent;
}
</style>