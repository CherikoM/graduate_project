<template>
  <div class="contribute">
    <div class="contribute-title">
      <h1 v-if="id">修改{{ form.name }}({{ id }})</h1>
      <h1 v-else>创建新条目</h1>
    </div>
    <div class="contribute-inner">
      <div class="artist-contribute">
        <el-form :model="form" label-width="120px" label-position="top" size="large" class="form-main">
          <div class="name" ref="nameDOM">
            <h4>名称</h4>
            <el-input v-model="form.name" :disabled="!!id" />
            <p class="character-limit" :class="{ 'character-limit-warning': nameRestChar < 0 }" v-if="!id">
              {{ nameRestChar >= 0 ? `还可以输入${nameRestChar}字` : `已超出${nameRestChar}字` }}
            </p>
          </div>
          <el-divider class="divider" />
          <div class="pic">
            <h4>上传封面</h4>
            <UploadImg :img="img ? img : ''" @on-pic-update="(picName) => { form.picture = picName }" />
          </div>
          <el-divider class="divider" />
          <div class="parent">
            <h4>父厂牌</h4>
            <SearchAddLabel :label="form.parent" :filt="id" :type="'only'">
            </SearchAddLabel>
          </div>
          <p class="not-found-p">找不到需要的组合？<span class="click-to-other"
              @click="toNewBlank(`/contribute/artists`)">去编写</span></p>
          <el-divider class="divider" />
          <div class="official">
            <h4>官网</h4>
            <ul>
              <li v-for="(item, index) in form.official" :key="index" class="official-url">
                {{ index + 1 }} {{ item }} <span class="click-to-other del"
                  @click="form.official.splice(index, 1)">删除</span>
              </li>
            </ul>
            <div class="add-area">
              <el-input v-model="officialKeyword" @keydown.enter="addOfficial"></el-input>
              <el-button plain @click="addOfficial">添加</el-button>
            </div>

          </div>
          <el-divider class="divider" />
          <div class="contact">
            <h4>联系方式</h4>
            <ul>
              <li v-for="(item, index) in form.contact" :key="index" class="contact-methods">
                {{ index + 1 }} {{ item }} <span class="click-to-other del"
                  @click="form.contact.splice(index, 1)">删除</span>
              </li>
            </ul>
            <div class="add-area">
              <el-input v-model="contactKeyword" @keydown.enter="addContact"></el-input>
              <el-button plain @click="addContact">添加</el-button>
            </div>

          </div>
          <el-divider class="divider" />
          <div class="profile" ref="profileDOM">
            <h4>简介</h4>
            <el-input v-model="form.profile" :rows="3" type="textarea" placeholder="输入文字" />
            <p class="character-limit" :class="{ 'character-limit-warning': profileRestChar < 0 }">
              {{ profileRestChar >= 0 ? `还可以输入${profileRestChar}字` : `已超出${profileRestChar}字` }}
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

import SearchAddLabel from "../searchAdd/SearchAddLabel.vue"

import UploadImg from '../../../components/UploadImg.vue'

import { addLabelVersion } from "@/api/labelAPI"

import getIdAndName from "@/common/getIdAndName"

const props = defineProps<{
  id: number | string,
  form: {
    name: string,
    picture: string,
    parent: any[],
    profile: string,
    official: any[],
    contact: any[],
  },
  img: string | null,
  origin: any,
  containerDOM: any,
}>()

const emits = defineEmits([`onSubmit`])

const { id, form, img, origin, containerDOM } = toRefs(props)

const nameDOM = ref(null)
const typeDOM = ref(null)
const profileDOM = ref(null)
const referenceDOM = ref(null)

const isSubmitSuccess = ref(false)

//标题相关变量
const nameRestChar = computed(() => 100 - form.value.name.length)
const profileRestChar = computed(() => {
  if (!form.value.profile) {
    return 5000
  }
  return 5000 - form.value.profile.length
})
const referenceRestChar = computed(() => {
  if (!reference.value) {
    return 1000
  }
  return 1000 - reference.value.length
})

const officialKeyword = ref('')
const contactKeyword = ref('')


const addOfficial = () => {
  if (/[a-zA-z]+:\/\/[^\s]*/.test(officialKeyword.value)) {
    const test = form.value.official.every(o => {
      return o !== officialKeyword.value
    })
    if (!test) {
      ElMessage.error("输入的官网有重复！")
    } else {
      form.value.official.push(officialKeyword.value)
      officialKeyword.value = ""
    }
  } else {
    ElMessage.error("输入的网址格式有误！")
  }
}

const addContact = ()=> {
  const test = form.value.contact.every(c => {
      return c !== contactKeyword.value
    })
    if (!test) {
      ElMessage.error("输入的官网有重复！")
    } else {
      form.value.contact.push(contactKeyword.value)
      contactKeyword.value = ""
    }
}

const reference = ref("")

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
              labelId: id.value as unknown as number,
              labelName: form.value.name,
              change: JSON.stringify(change),
              userId,
              userName,
              info: JSON.stringify(submitForm),
              reference: reference.value,
              isPass: 0
            }
            const res = await addLabelVersion(submitContent)
            if (res.isSuccess) {
              isSubmitSuccess.value = true
              emits("onSubmit")
            } else {
              ElMessage.error(res.message)
            }
          }
          //新条目的情况
        } else {
          delete submitForm.parent
          const submitContent = {
            isNew: 1,
            labelId: id.value as unknown as number,
            labelName: form.value.name,
            userId,
            userName,
            info: JSON.stringify(submitForm),
            reference: reference.value,
            isPass: 0
          }
          const res = await addLabelVersion(submitContent)
          if (res.isSuccess) {
            isSubmitSuccess.value = true
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
      form.value.name = ''
      form.value.picture = ''
      form.value.profile = ''
      form.value.parent = []
      form.value.official = []
      form.value.contact = [""]
    })
    .catch(() => {
    })

}

/**
 * 填入数据的合规性测试
 */
const validateTest = () => {
  if (!form.value.name || form.value.name === '') {
    ElMessage.error("请输入名称！")
    scrollAndMention(nameDOM.value as unknown as HTMLElement)
    return false
  } else if (nameRestChar.value < 0) {
    ElMessage.error("名称字数过多！")
    scrollAndMention(nameDOM.value as unknown as HTMLElement)
    return false
  } else if (profileRestChar.value < 0) {
    ElMessage.error("简介字数过多！")
    scrollAndMention(profileDOM.value as unknown as HTMLElement)
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
  let timer: any = null
  timer = setInterval(() => {
    h4.classList.remove("blink")
    clearInterval(timer)
  }, 2000)
}

/**
 * 提交数据的整理
 */
const makeUpForm = () => {
  const submitForm: any = {...toRaw(form.value)}

  if (submitForm.profile === "") {
    //@ts-ignore
    submitForm.profile = null
  }
  if (submitForm.parent[0]?.id && submitForm.parent[0]?.name) {
    submitForm.parentId = submitForm.parent[0].id
    submitForm.parentName = submitForm.parent[0].name
  }
  if (!submitForm.official || submitForm.official.length <= 0) {
    //@ts-ignore
    submitForm.official = null
  }
  if (!submitForm.contact || submitForm.contact.length <= 0) {
    //@ts-ignore
    submitForm.contact = null
  }
  if (submitForm.picture === "") {
    //@ts-ignore
    submitForm.picture = null
  }

  delete submitForm.parent

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
    if (origin.value.parentId !== submitForm.parentId || origin.value.parentName !== submitForm.parentName) {
      changes.push('parent')
    }
    if (origin.value.profile !== submitForm.profile) {
      changes.push('profile')
    }
    if (origin.value.official?.length !== submitForm.official?.length
      || origin.value.official?.some((o: string) => submitForm.official?.indexOf(o) < 0)) {
      changes.push('official')
    }
    if (origin.value.contact?.length !== submitForm.contact?.length
      || origin.value.contact?.some((c: string) => submitForm.contact?.indexOf(c) < 0)) {
      changes.push('contact')
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

  .artist-contribute {

    .names {
      display: flex;
      justify-content: space-between;

      &>div {
        width: 48%;
      }
    }

    .official,
    .contact {

      .official-url,
      .contact-methods {
        .del {
          display: none;
        }

        &:hover .del {
          display: inline;
        }

        &:last-child {
          margin-bottom: 1rem;
        }
      }

      .add-area {
        display: flex;
      }
    }
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
}</style>