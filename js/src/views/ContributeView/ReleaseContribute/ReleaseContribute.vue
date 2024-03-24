<template>
  <div class="contribute">
    <div class="contribute-title">
      <h1 v-if="id">修改{{ form.title }}({{ id }})</h1>
      <h1 v-else>创建新条目</h1>
    </div>
    <div class="contribute-inner">
      <div class="release-contribute">
        <el-form :model="form" label-width="120px" label-position="top" size="large" class="form-main">
          <div class="title" ref="titleDOM">
            <h4>标题</h4>
            <el-input v-model="form.title" :disabled="!!id" />
            <p class="character-limit" :class="{ 'character-limit-warning': titleRestChar < 0 }" v-if="!id">
              {{ titleRestChar >= 0 ? `还可以输入${titleRestChar}字` : `已超出${titleRestChar}字` }}
            </p>
          </div>
          <el-divider class="divider" />
          <div class="cover">
            <h4>上传封面</h4>
            <UploadImg :img="img ? img : ''" @on-pic-update="(picName) => { form.picture = picName }" />
          </div>
          <el-divider class="divider" />
          <div class="genre-style">
            <div class="genre" ref="genreDOM">
              <h4>流派</h4>
              <div class="style-tags">
                <el-tag v-for="(tag, index) in form.genre" :key="tag" closable @close="tagDelete(tag, index, 'genres')">
                  {{ tag }}
                </el-tag>
              </div>
              <div class="style-main">
                <el-autocomplete v-model="genreKeyword"
                  :fetch-suggestions="(queryString: string, cb: Function) => { searchStyle(queryString, cb, 'genres') }"
                  clearable placeholder="选择流派" style="width:100%;"
                  @select="(item: tag) => { styleSelect(item, 'genres') }" />
              </div>
            </div>
            <div class="style">
              <h4>风格</h4>
              <div class="style-tags">
                <el-tag v-for="(tag, index) in form.style" :key="tag" closable @close="tagDelete(tag, index, 'styles')">
                  {{ tag }}
                </el-tag>
              </div>
              <div class="style-main">
                <el-autocomplete v-model="styleKeyword"
                  :fetch-suggestions="(queryString: string, cb: Function) => { searchStyle(queryString, cb, 'styles') }"
                  clearable placeholder="选择风格" style="width:100%;"
                  @select="(item: tag) => { styleSelect(item, 'styles') }" />
              </div>
            </div>
          </div>
          <p class="not-found-p">找不到需要的风格/流派？<span class="click-to-other"
              @click="toNewBlank(`/contribute/styles`)">去编写</span></p>
          <el-divider class="divider" />
          <div class="format-form">
            <div class="format">
              <h4>格式</h4>
              <el-select v-model="form.format" placeholder="选择格式" class="deep-box">
                <el-option label="CD" value="1" />
                <el-option label="Vinyl" value="2" />
                <el-option label="cassette" value="3" />
                <el-option label="digital" value="4" />
                <el-option label="other" value="5" />
              </el-select>
            </div>
            <div class="form">
              <h4>形式</h4>
              <el-select v-model="form.form" placeholder="选择形式" class="deep-box">
                <el-option label="LP" value="1" />
                <el-option label="EP" value="2" />
                <el-option label="single" value="3" />
                <el-option label="cooperation" value="4" />
                <el-option label="other" value="5" />
              </el-select>
            </div>
          </div>
          <el-divider class="divider" />
          <div class="country">
            <h4>国家/地区</h4>
            <p v-show="form.country && form.country !== ''">
              已选择国家/地区：{{ form.country }}
              <span class="click-to-other" @click="form.country = ''">清除</span>
            </p>
            <el-autocomplete class="deep-box" v-model="countryKeyword" placeholder="选择国家/地区"
              :fetch-suggestions="getCountry" @select="countrySelected" />
          </div>
          <el-divider class="divider" />
          <div class="release-releaseDate" ref="dateDOM">
            <h4>发行日期</h4>
            <el-date-picker v-model="form.releaseDate" type="date" placeholder="选择发行日期" size="large"
              value-format="YYYY-MM-DD" />
          </div>
          <el-divider class="divider" />
          <div class="artist" ref="artistDOM">
            <h4>艺人</h4>
            <SearchAddArtist :artist="form.artist" />
          </div>
          <p class="not-found-p">找不到需要的艺人？<span class="click-to-other"
              @click="toNewBlank(`/contribute/artists`)">去编写</span></p>
          <el-divider class="divider" />
          <div class="label" ref="labelDOM">
            <h4>厂牌</h4>
            <SearchAddLabel :label="form.releaseLabel" />
          </div>
          <p class="not-found-p">找不到需要的厂牌？<span class="click-to-other"
              @click="toNewBlank(`/contribute/styles`)">去编写</span></p>
          <el-divider class="divider" />
          <div class="tracklist" ref="tracklistDOM">
            <h4>曲目表</h4>
            <TrackList :tracklist="form.tracklist" />
            <TrackItem :track="newTrack" type="new-track" @on-track-add="submitNewTrack" />
          </div>
          <el-divider class="divider" />
          <div class="behind">
            <h4>幕后人员</h4>
            <SearchAddArtist :artist="form.credits" :type="'many'">
              <template #info="{ item }">
                <RoleSlot :item="item">
                  <span class="name click-to-other" @click="router.push(`/info/artists/${item.id}`)">
                    {{ item.name }}</span>
                  <span class="id">({{ item.id }})</span>
                </RoleSlot>
              </template>
            </SearchAddArtist>
          </div>
          <el-divider class="divider" />
          <div class="other-version">
            <h4>发行样板</h4>
            <!-- mainRelease是异步值 -->
            <SearchAddMainRelease :default-value="mainRelease"
              @on-main-release-change="updateMainRelease"/>
          </div>
          <el-divider class="divider" />
          <div class="supplement" ref="supplementDOM">
            <h4>补充说明</h4>
            <el-input v-model="form.supplement" :rows="3" type="textarea" placeholder="输入文字" />
            <p class="character-limit" :class="{ 'character-limit-warning': supplementRestChar < 0 }">
              {{ supplementRestChar >= 0 ? `还可以输入${supplementRestChar}字` : `已超出${supplementRestChar}字` }}
            </p>
          </div>
          <el-divider class="divider" />
          <div class="reference" ref="referenceDOM">
            <h4>信息来源</h4>
            <el-input v-model="reference" :rows="3" type="textarea" placeholder="输入文字" />
            <p class="character-limit" :class="{ 'character-limit-warning': referenceRestChar < 0 }">
              {{ referenceRestChar >= 0 ? `还可以输入${referenceRestChar}字` : `已超出${referenceRestChar}字` }}
            </p>
          </div>
          <div class="form-end">
            <el-form-item class="btns">
              <el-button plain @click="onSubmit">提交</el-button>
              <el-button plain v-if="!id" @click="onReset">重置</el-button>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, computed, toRaw } from "vue"

import router from "@/router"

import { ElMessage } from 'element-plus'
import { ElMessageBox } from 'element-plus'

import SearchAddArtist from '../searchAdd/SearchAddArtist.vue'
import SearchAddLabel from '../searchAdd/SearchAddLabel.vue'
import SearchAddMainRelease from '../searchAdd/SearchAddMainRelease.vue'
import TrackItem from '../Tracklist/TrackItem.vue'
import TrackList from '../Tracklist/TrackList.vue'
import RoleSlot from '../searchAdd/RoleSlot.vue'
import UploadImg from '../../../components/UploadImg.vue'

import type { tag } from "@/api/dataInterfaces"
import { getMainReleaseById, getReleaseById, addReleaseVersion } from '@/api/releaseAPI'

import { useCommonStore } from '@/stores/common'
import { useStyleStore } from '@/stores/style'

import getIdAndName from "@/common/getIdAndName"
import makeNum from '@/common/makeNum'

//从pinia仓库取得数据
const commonStore = useCommonStore()
const styleStore = useStyleStore()

const props = defineProps<{
  id: number | string,
  form: {
    title: string
    picture: string
    format: string
    form: string
    mainId: number | null
    genre: any[]
    style: any[]
    country: string
    releaseDate: string
    artist: any[]
    releaseLabel: any[]
    tracklist: any[]
    credits: any[]
    mainRelease: any
    supplement: string
  },
  img: string | null,
  origin: any,
  mainRelease: any,
  containerDOM: any,
}>()

const emits = defineEmits([`onSubmit`])

const { id, form, img, origin, containerDOM } = toRefs(props)

const genreDOM = ref(null)
const dateDOM = ref(null)
const titleDOM = ref(null)
const artistDOM = ref(null)
const labelDOM = ref(null)
const tracklistDOM = ref(null)
const supplementDOM = ref(null)
const referenceDOM = ref(null)

const isSubmitSuccess = ref(false)

//标题相关变量
const titleRestChar = computed(() => 200 - form.value.title.length)
const supplementRestChar = computed(() => {
  if (!form.value.supplement) {
    return 1000
  }
  return 1000 - form.value.supplement.length
})
const referenceRestChar = computed(() => {
  if (!reference.value) {
    return 1000
  }
  return 1000 - reference.value.length
})

//国家/地区相关变量
//#region 
//自动补全选择框的输入内容
const countryKeyword = ref('')

//从仓库里取得国家/地区列表
const countryList = [...commonStore.countryTagList]
//#endregion


//流派/风格相关变量
//#region 
//自动补全选择框的输入内容
const genreKeyword = ref('')
const styleKeyword = ref('')
//#endregion

//曲目表相关变量
//#region 
const newTrack = ref({
  title: '',
  duration: '',
  position: '',
  artist: [],
  extraartists: [],
  open: false
})
//#endregion

const updateMainRelease = (item: any) => {
  form.value.mainRelease = { ...item }
}


const reference = ref("")



//流派/风格的自动补全框
//#region
/**
 * 输入关键词时执行的操作
 * @param queryString 
 * @param cb 
 * @param area 
 */
const searchStyle = async (queryString: string, cb: Function, area: string) => {
  let result: tag[] = []
  switch (area) {
    case 'genres':
      if (styleStore.genreList.length <= 0) {
        await styleStore.getGenreList()
      }
      result = styleStore.genreTagList
      break
    case 'styles':
      if (styleStore.styleList.length <= 0) {
        await styleStore.getStyleList()
      }
      result = styleStore.styleTagList
  }
  result = [...result]
  let filted: tag[] = []
  switch (area) {
    case 'genres':
      filted = result.filter(r => {
        return form.value.genre.every(g => {
          return g !== r.name && g != r.enName
        })
      })
      break
    case 'styles':
      filted = result.filter(r => {
        return form.value.style.every(s => {
          return s !== r.name && s != r.enName
        })
      })
      break
  }

  filted = filted.filter(r => {
    //@ts-ignore
    return r.name?.toLowerCase().indexOf(queryString.toLowerCase()) >= 0 || r.enName?.toLowerCase().indexOf(queryString.toLowerCase()) >= 0
  })
  cb(filted)
}

/**
 * 选择选项时执行的操作
 * @param item 
 */
const styleSelect = (item: tag, area: string) => {
  //@ts-ignore
  document.activeElement?.blur()
  switch (area) {
    case 'genres':
      genreKeyword.value = ''
      //@ts-ignore
      form.value.genre.push(item.value)
      break
    case 'styles':
      styleKeyword.value = ''
      //@ts-ignore
      form.value.style.push(item.value)
      break
  }
}

const tagDelete = (t: tag, index: number, area: string) => {
  switch (area) {
    case 'genres':
      form.value.genre.splice(index, 1)
      break
    case 'styles':
      form.value.style.splice(index, 1)
      break
  }
}
//#endregion




//国家/地区的自动补全框
//#region 
/**
 * 输入关键词时执行的操作
 * @param queryString 
 * @param cb 
 */
const getCountry = (queryString: string, cb: Function) => {
  const list = countryList.filter(country => country.value.indexOf(queryString) >= 0)
  cb(list)
}

/**
 * 选择选项时执行的操作
 * @param item 
 */
const countrySelected = (item: any) => {
  form.value.country = item.value
}
//#endregion


//曲目表相关函数
//#region
const submitNewTrack = () => {
  if (newTrack.value.title === '') {
    ElMessage({
      message: "曲目标题不能为空！",
      type: 'error'
    })
  } else if (newTrack.value.position === '') {
    ElMessage({
      message: "曲目标号不能为空！",
      type: 'error'
    })
  } else {
    newTrack.value.open = !newTrack.value.open
    //@ts-ignore
    form.value.tracklist.push({ ...newTrack.value, num: makeNum() })
    newTrack.value.title = ''
    newTrack.value.duration = ''
    newTrack.value.position = ''
    newTrack.value.artist = []
    newTrack.value.extraartists = []
  }
}
//#endregion

const toNewBlank = (path: string, query: any = {}) => {
  const url = router.resolve({ path, query })
  window.open(url.href, '_blank')
}


/**
 * 点击提交时的操作
 */
const onSubmit = async () => {
  ElMessageBox.confirm('确定提交？')
    .then(async () => {
      const { id: userId, name: userName } = getIdAndName()
      if (!userId || !userName) {
        ElMessage.error("登陆验证失败，请重新登陆后再操作")
        router.push(`/login`)
      }

      if (validateTest()) {
        const submitForm = makeUpForm()
        //更新条目的情况
        if (id.value) {
          const change = formCompare(submitForm)
          if (change.length <= 0) {
            ElMessage.error("请更改内容后再提交！")
          } else {
            const submitContent = {
              isNew: 0,
              releaseId: id.value as unknown as number,
              title: form.value.title,
              change: JSON.stringify(change),
              userId,
              userName,
              info: JSON.stringify(submitForm),
              reference: reference.value,
              isPass: 0
            }
            const res = await addReleaseVersion(submitContent)
            if (res.isSuccess) {
              // isSubmitSuccess.value = true
              emits("onSubmit")
            } else {
              ElMessage.error(res.message)
            }
          }
          //新条目的情况
        } else {
          const submitContent = {
            isNew: 1,
            title: form.value.title,
            userId,
            userName,
            info: JSON.stringify(submitForm),
            reference: reference.value,
            isPass: 0
          }
          console.log(submitContent, submitForm)
          const res = await addReleaseVersion(submitContent)
          if (res.isSuccess) {
            // isSubmitSuccess.value = true
            emits("onSubmit")
          } else {
            ElMessage.error("Unknow Error")
          }
        }

      }
    })
    .catch((e) => {
      // catch error
      console.log(e)
    })
}

/**
 * 点击重置时的操作
 */
const onReset = () => {
  ElMessageBox.confirm('确定重置所有内容？')
    .then(() => {
      form.value.title = ''
      form.value.picture = ''
      form.value.format = '',
        form.value.form = '',
        form.value.genre = []
      form.value.style = [],
        form.value.country = '',
        form.value.releaseDate = '',
        form.value.artist = [],
        form.value.releaseLabel = [],
        form.value.tracklist = [],
        form.value.credits = [],
        form.value.mainRelease = {},
        form.value.supplement = ''
    })
    .catch(() => {
    })

}

/**
 * 填入数据的合规性测试
 */
const validateTest = () => {
  if (!form.value.title || form.value.title === '') {
    ElMessage.error("请输入标题！")
    scrollAndMention(titleDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.genre || form.value.genre.length <= 0) {
    ElMessage.error("请添加流派！")
    scrollAndMention(genreDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.releaseDate || form.value.releaseDate === '') {
    ElMessage.error("请输入发行时间！")
    scrollAndMention(dateDOM.value as unknown as HTMLElement)
    return false
  } else if (new Date(form.value.releaseDate).valueOf() > Date.now()) {
    ElMessage.error("发行时间不合理！")
    scrollAndMention(dateDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.artist || form.value.artist.length <= 0) {
    ElMessage.error("请添加艺人！")
    scrollAndMention(artistDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.releaseLabel || form.value.releaseLabel.length <= 0) {
    ElMessage.error("请添加发行厂牌！")
    scrollAndMention(labelDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.tracklist || form.value.tracklist.length <= 0) {
    ElMessage.error("请添加曲目！")
    scrollAndMention(tracklistDOM.value as unknown as HTMLElement)
    return false
    //@ts-ignore
  } else if (titleRestChar.value < 0) {
    ElMessage.error("标题字数过多！")
    scrollAndMention(titleDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.tracklist.every(t => !t.open)) {
    ElMessage("请修改好曲目列表！")
    scrollAndMention(tracklistDOM.value as unknown as HTMLElement)
    return false
  } else if (supplementRestChar.value < 0) {
    ElMessage.error("补充说明字数过多！")
    scrollAndMention(supplementDOM.value as unknown as HTMLElement)
    return false
  } else if (reference.value === '') {
    ElMessage.error("请输入信息来源！")
    scrollAndMention(referenceDOM.value as unknown as HTMLElement)
    return false
  } else if (referenceRestChar.value < 0) {
    ElMessage.error("信息来源字数过多！")
    scrollAndMention(referenceDOM.value as unknown as HTMLElement)
    return false
  } else {
    return true
  }
}

/**
 * 滚屏+文字高亮动画
 * @param target 目标dom
 * @param parent 父dom
 */
const scrollAndMention = (target: HTMLElement) => {
  const h4 = target.getElementsByTagName('h4')[0]
  containerDOM.value.scrollTo({
    top: target.offsetTop,
    behavior: "smooth"
  })

  h4.classList.add("blink")
  let timer:any = null
  timer = setInterval(()=> {
    h4.classList.remove("blink")
    clearInterval(timer)
  },2000)
}

/**
 * 提交数据的整理
 */
const makeUpForm = () => {
  const submitForm = toRaw(form.value)
  submitForm.genre.forEach((g, index) => {
    styleStore.genreTagList.forEach((gl) => {
      if (g === gl.name) {
        submitForm.genre[index] = gl.enName
      }
    })
  })
  submitForm.style.forEach((s, index) => {
    styleStore.styleTagList.forEach((sl) => {
      if (s === sl.name) {
        submitForm.style[index] = sl.enName
      }
    })
  })
  submitForm.artist = form.value.artist.map(a => {
    return {
      id: a.id,
      name: a.name
    }
  })
  submitForm.releaseLabel = form.value.releaseLabel.map(l => {
    return {
      id: l.id,
      name: l.name,
      catno: l.catno
    }
  })
  submitForm.tracklist = form.value.tracklist.map(t => {
    t.artist = t.artist ? t.artist.map((a: any) => {
      return {
        id: a.id,
        name: a.name
      }
    }) : null
    t.extraartists = t.extraartists ? t.extraartists.map((a: any) => {
      return {
        id: a.id,
        name: a.name,
        role: a.role
      }
    }) : null
    return t
  })
  if (submitForm.credits && submitForm.credits.length > 0) {
    submitForm.credits = form.value.credits.map(a => {
      return {
        id: a.id,
        name: a.name,
        role: a.role
      }
    })
  } else {
    //@ts-ignore
    submitForm.credits = null
  }
  if (submitForm.country === "") {
    //@ts-ignore
    submitForm.country = null
  }
  if (submitForm.form === "") {
    //@ts-ignore
    submitForm.form = null
  }
  if (submitForm.format === "") {
    //@ts-ignore
    submitForm.format = null
  }
  if (submitForm.picture === "") {
    //@ts-ignore
    submitForm.picture = null
  }
  if (!submitForm.style || submitForm.style.length < 0) {
    //@ts-ignore
    submitForm.style = null
  }
  if (submitForm.supplement === "") {
    //@ts-ignore
    submitForm.supplement = null
  }
  if (!submitForm.mainRelease?.title || submitForm.mainRelease?.title === "") {
    submitForm.mainRelease = null
    submitForm.mainId = null
  } else {
    if (!submitForm.mainRelease.isNew) {
      submitForm.mainId = submitForm.mainRelease.id
    }
    submitForm.mainRelease.isNew = submitForm.mainRelease.isNew?.value
  }
  return submitForm
}

/**
 * 对比更改字段
 * @param submitForm 
 */
const formCompare = (submitForm: any) => {
  const changes = []
  if (id.value) {
    if (origin.value.picture !== submitForm.picture) {
      changes.push('picture')
    }
    if (origin.value.format !== submitForm.format) {
      changes.push('format')
    }
    if (origin.value.form !== submitForm.form) {
      changes.push('form')
    }
    if (JSON.stringify(origin.value.genre) !== JSON.stringify(submitForm.genre)) {
      changes.push('genre')
    }
    if (JSON.stringify(origin.value.style) !== JSON.stringify(submitForm.style)) {
      changes.push('style')
    }
    if (origin.value.country !== submitForm.country) {
      changes.push('country')
    }
    if (origin.value.releaseDate !== submitForm.releaseDate) {
      changes.push('releaseDate')
    }
    let aindex = 0
    let aflag = false
    while (aindex < origin.value.artist.length || aindex < submitForm.artist.length) {
      const o = origin.value.artist[aindex]
      const s = submitForm.artist[aindex]
      if (o?.id !== s?.id || o?.name !== s?.name) {
        aflag = true
        break
      }
      aindex++
    }
    if (aflag) changes.push('artist')

    let lindex = 0
    let lflag = false
    while (lindex < origin.value.releaseLabel.length || lindex < submitForm.releaseLabel.length) {
      const o = origin.value.releaseLabel[lindex]
      const s = submitForm.releaseLabel[lindex]
      if (o?.id !== s?.id || o?.name !== s?.name || o?.catno !== s?.catno) {
        lflag = true
        break
      }
      lindex++
    }
    if (lflag) changes.push('label')

    if (JSON.stringify(origin.value.tracklist) !== JSON.stringify(submitForm.tracklist)) {
      changes.push('tracklist')
    }


    if (origin.value.credits && origin.value.credits.length > 0 && submitForm.credits && submitForm.credits.length > 0) {
      let cindex = 0
      let cflag = false
      while (cindex < origin.value.credits.length || cindex < submitForm.credits.length) {
        const o = origin.value.credits[cindex]
        const s = submitForm.credits[cindex]
        if (o?.id !== s?.id || o?.name !== s?.name || o?.role !== s?.role) {
          cflag = true
          break
        }
        cindex++
      }
      if (cflag) changes.push('credits')
    }

    if (origin.value.mainRelease.id !== submitForm.mainId) {
      changes.push('mainRelease')
    }
    if (origin.value.supplement !== submitForm.supplement) {
      changes.push('supplement')
    }
  }
  return changes
}

</script>

<style scoped lang="scss">
.contribute-title {
  // margin-bottom: 1rem;
}

.contribute-inner {
  padding: 1rem 2rem;

  .form-main:deep(.el-form-item__label) {
    font-size: 1rem;
    margin-top: 1rem;
  }

  .release-contribute {

    .format-form {
      display: flex;
      justify-content: space-between;

      &>div {
        width: 48%;
      }
    }

    .genre-style {
      display: flex;
      justify-content: space-between;

      &>div {
        width: 48%;

        .style-tags {
          margin-bottom: 0.25rem;
        }

        .style-main {
          display: flex;
          flex-direction: column;


        }
      }
    }

    .tracklist {}

  }

  .form-end {
    margin-top: 2rem;
    display: flex;
    justify-content: center;

    .btns {}
  }
}

h4 {
  font-size: 1.2rem;
  margin: 1rem 0;
}

.divider {
  margin-top: 1.25rem;
}

.click-to-other {
  @include click-to-other();
}

:deep(.deep-box) {
  width: 100%;
}

.not-found-p {
  margin-top: 0.5rem;
  text-align: center;
}

.character-limit {
  margin-top: 0.5rem;
  text-align: right;
}

.character-limit-warning {
  color: red;
}


@keyframes blink {
  0% {
    color: #333333
  }

  50% {
    color: red
  }

  100% {
    color: #333333
  }
}

/* 添加兼容性前缀 */
@-webkit-keyframes blink {
  0% {
    color: #333333
  }

  50% {
    color: red
  }

  100% {
    color: #333333
  }
}

@-moz-keyframes blink {
  0% {
    color: #333333
  }

  50% {
    color: red
  }

  100% {
    color: #333333
  }
}

@-ms-keyframes blink {
  0% {
    color: #333333
  }

  50% {
    color: red
  }

  100% {
    color: #333333
  }
}

@-o-keyframes blink {
  0% {
    color: #333333
  }

  50% {
    color: red
  }

  100% {
    color: #333333
  }
}

.blink {
  color: #333333;
  animation: blink 0.5s linear infinite;
  /* 其它浏览器兼容性前缀 */
  -webkit-animation: blink 0.5s linear infinite;
  -moz-animation: blink 0.5s linear infinite;
  -ms-animation: blink 0.5s linear infinite;
  -o-animation: blink 0.5s linear infinite;
}
</style>