<template>
  <div class="contribute">
    <div class="contribute-title">
      <h1 v-if="id">修改{{ id }}</h1>
      <h1 v-else>创建新条目</h1>
    </div>
    <div class="contribute-inner">
      <div class="artist-contribute">
        <el-form :model="form" label-width="120px" label-position="top" size="large" class="form-main">
          <div class="enName" ref="enNameDOM">
            <h4>名称</h4>
            <el-input v-model="form.enName" :disabled="!!id" />
            <p class="character-limit" :class="{ 'character-limit-warning': enNameRestChar < 0 }" v-if="!id">
              {{ enNameRestChar >= 0 ? `还可以输入${enNameRestChar}字` : `已超出${enNameRestChar}字` }}
            </p>
          </div>
          <el-divider class="divider" />
          <div class="name" ref="nameDOM">
            <h4>中文译名</h4>
            <el-input v-model="form.name" />
            <p class="character-limit" :class="{ 'character-limit-warning': nameRestChar < 0 }">
              {{ nameRestChar >= 0 ? `还可以输入${nameRestChar}字` : `已超出${nameRestChar}字` }}
            </p>
          </div>
          <el-divider class="divider" />
          <div class="other-name">
            <h4>别名</h4>
            <el-tag v-for="(item, index) in form.otherName" :key="index" size="large" class="mx-1" effect="plain" round
              closable @close="form.otherName.splice(index, 1)">
              {{ item }}
            </el-tag>
            <div class="add-other-name" style="margin-top: 0.5rem; display: flex;">
              <el-input v-model="otherName" @keydown.enter="addOtherName" />
              <el-button plain @click="addOtherName">添加</el-button>
            </div>
          </div>
          <el-divider class="divider" />
          <div class="belong" ref="belongDOM">
            <h4>父风格</h4>
            <el-tag v-for="(item, index) in form.belong" :key="index" size="large" class="mx-1" effect="plain" round
              closable @close="form.otherName.splice(index, 1)" v-show="form.type != 0">
              {{ item }}
            </el-tag>
            <div class="add-other-name" style="margin-top: 0.5rem; display: flex;">
              <el-autocomplete v-model="styleKeyword" :fetch-suggestions="searchStyle" clearable placeholder="选择流派"
                style="width:100%;" @select="styleSelect" :disabled="
                  //@ts-ignore
                  form.type == 0 || form.type == 'genre'" />
            </div>
            <p class="not-found-p">找不到需要的风格？<span class="click-to-other"
                @click="toNewBlank(`/contribute/styles`)">去编写</span></p>
          </div>
          <el-divider class="divider" />
          <div class="type" ref="typeDOM">
            <h4>类型</h4>
            <el-select v-model="form.type" placeholder="选择类型" @change="typeChange">
              <el-option label="genre" value="0" />
              <el-option label="primary" value="1" />
              <el-option label="subdivide" value="2" />
            </el-select>
          </div>
          <el-divider class="divider" />
          <div class="rich-text" ref="descriptionDOM">
            <h4>正文</h4>
            <div style="border: 1px solid #ccc">
              <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig"
                mode="default" />
              <Editor style="height: 500px; overflow-y: hidden;" v-model="form.description" :defaultConfig="editorConfig"
                mode="default" @onCreated="handleCreated" />
            </div>
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
import { ref, toRefs, computed, toRaw, onBeforeUnmount, shallowRef, onMounted } from "vue"

import router from "@/router"

import { ElMessage } from 'element-plus'
import { ElMessageBox } from 'element-plus'


import type { tag } from "@/api/dataInterfaces"
import { canUseEnName, addStyleVersion } from "@/api/styleAPI"

import { useStyleStore } from '@/stores/style'

import getIdAndName from "@/common/getIdAndName"



import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import type { IEditorConfig } from "@wangeditor/editor"

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 模拟 ajax 异步获取内容
onMounted(async () => {
  await styleStore.getGenreList()
  const result = styleStore.genreTagList
  form.value.belong = form.value.belong.map(b => {
    result.some(r => {
      if (r.enName == b) {
        if (r.name) {
          b = r.name
        }
        return true
      }
    })
    return b
  })

  switch (form.value.type as unknown as string) {
    case "genre":
      form.value.type = "0"
      break
    case "primary":
      form.value.type = "1"
      break
    case "subdivide":
      form.value.type = "2"
      break
  }
})



const toolbarConfig = {}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor: any) => {
  editorRef.value = editor // 记录 editor 实例，重要！

}

const editorConfig: Partial<IEditorConfig> = {  // TS 语法
  placeholder: 'Please enter...',
}
// editorConfig.MENU_CONF['uploadImage'] = {
//   server: "/apiTest/common/upload",
//   maxFileSize: 5 * 1024 * 1024,

// }














const props = defineProps<{
  id: number | string,
  form: {
    name: string,
    enName: string,
    otherName: string[],
    type: number | string,
    description: string,
    belong: string[]
  },
  origin: any,
  containerDOM: any,
  styleId: number
}>()

const emits = defineEmits([`onSubmit`])

const { id, form, origin, containerDOM, styleId } = toRefs(props)

const enNameDOM = ref(null)
const nameDOM = ref(null)
const typeDOM = ref(null)
const referenceDOM = ref(null)
const descriptionDOM = ref()
const belongDOM = ref()

const isSubmitSuccess = ref(false)

//标题相关变量
const enNameRestChar = computed(() => 100 - form.value.enName.length)
const nameRestChar = computed(() => 50 - form.value.name.length)
const referenceRestChar = computed(() => {
  if (!reference.value) {
    return 1000
  }
  return 1000 - reference.value.length
})

const otherName = ref("")

const addOtherName = () => {
  if (otherName.value && otherName.value !== "") {
    form.value.otherName.push(otherName.value)
    otherName.value = ""
  }
}



const reference = ref("")

const toNewBlank = (path: string, query: any = {}) => {
  const url = router.resolve({ path, query })
  window.open(url.href, '_blank')
}


const styleStore = useStyleStore()

const styleKeyword = ref('')

/**
 * 输入关键词时执行的操作
 * @param queryString 
 * @param cb 
 * @param area 
 */
const searchStyle = async (queryString: string, cb: Function) => {

  let result
  if (form.value.type == 1) {
    await styleStore.getGenreList()
    result = styleStore.genreTagList
  } else if (form.value.type == 2) {
    await styleStore.getGenreAndPrimary()
    result = styleStore.genrePrimaryTagList
  }

  result = result?.filter((r: any) => {
    return !form.value.belong.some(b => {
      if (r.name == b || r.enName == b) {
        return true
      } else {
        return false
      }
    })
  })

  const qs = queryString.toUpperCase()
  result = result?.filter((r: any) => {
    if (r.name.toUpperCase().indexOf(qs) >= 0 || r.enName.toUpperCase().indexOf(qs) >= 0) {
      return true
    } else return false
  })

  cb(result)
}

/**
 * 选择选项时执行的操作
 * @param item 
 */
const styleSelect = (item: tag) => {
  //@ts-ignore
  document.activeElement?.blur()
  styleKeyword.value = ""
  form.value.belong.push(item.value)
}

const typeChange = async (val: any) => {
  if (val == 1) {
    await styleStore.getGenreList()
    const result = styleStore.genreTagList

    form.value.belong = form.value.belong.filter(b => {
      return result.some(r => {
        if (b == r.enName || b == r.name) {
          return true
        } else return false
      })
    })
  }
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

      const canUseName = await canUseEnName(form.value.enName)
      if (!canUseName.isSuccess && !id.value) {
        ElMessage.error(canUseName.message)
      } else {
        if (validateTest()) {
          const submitForm = await makeUpForm()
          //更新条目的情况
          if (id.value) {
            const change = formCompare(submitForm)
            if (change.length <= 0) {
              ElMessage.error("请更改内容后再提交！")
            } else {
              const submitContent = {
                isNew: 0,
                styleId: styleId.value,
                styleEnName: form.value.enName,
                change: JSON.stringify(change),
                userId,
                userName,
                info: JSON.stringify(submitForm),
                reference: reference.value,
                isPass: 0
              }
              const res = await addStyleVersion(submitContent)
              if (res.isSuccess) {
                isSubmitSuccess.value = true
                emits("onSubmit")
              } else {
                ElMessage.error(res.message)
              }
            }
            //新条目的情况
          } else {
            const submitContent = {
              isNew: 1,
              styleEnName: form.value.enName,
              userId,
              userName,
              info: JSON.stringify(submitForm),
              reference: reference.value,
              isPass: 0
            }
            const res = await addStyleVersion(submitContent)
            if (res.isSuccess) {
              isSubmitSuccess.value = true
              emits("onSubmit")
            } else {
              ElMessage.error("Unknow Error")
            }
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
      form.value.enName = ''
      form.value.otherName.length = 0
      form.value.type = 0,
        form.value.description = ""
      form.value.belong.length = 0
    })
    .catch(() => {
    })

}

/**
 * 填入数据的合规性测试
 */
const validateTest = () => {
  if (!form.value.enName || form.value.enName === '') {
    ElMessage.error("请输入名称！")
    scrollAndMention(enNameDOM.value as unknown as HTMLElement)
    return false
  } else if (enNameRestChar.value < 0) {
    ElMessage.error("名称字数过多！")
    scrollAndMention(enNameDOM.value as unknown as HTMLElement)
    return false
  } else if (nameRestChar.value < 0) {
    ElMessage.error("译名字数过多！")
    scrollAndMention(nameDOM.value as unknown as HTMLElement)
    return false
  } else if ((form.value.type == 1 || form.value.type == 2)
    && (!form.value.belong || form.value.belong.length <= 0)) {
    ElMessage.error("请输入父风格")
    scrollAndMention(belongDOM.value as unknown as HTMLElement)
    return false
  } else if (!form.value.description || form.value.description.length <= 0) {
    ElMessage.error("请输入描述正文")
    scrollAndMention(descriptionDOM.value as unknown as HTMLElement)
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
const makeUpForm = async () => {
  const submitForm = toRaw(form.value)

  if (submitForm.name === "") {
    //@ts-ignore
    submitForm.name = null
  }
  if (!submitForm.otherName || submitForm.otherName.length <= 0) {
    //@ts-ignore
    submitForm.otherName = null
  }
  if (submitForm.belong && submitForm.belong.length > 0) {
    await styleStore.getGenreAndPrimary()
    const tag = styleStore.genrePrimaryTagList
    submitForm.belong = submitForm.belong.map((b: string) => {
      tag.some(t => {
        if (t.name == b) {
          b = t.enName as string
          return true
        } else return false
      })
      return b
    })
  } else {
    //@ts-ignore
    submitForm.belong = null
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
    if (origin.value.name !== submitForm.name) {
      changes.push('name')
    }
    if (origin.value.otherName?.length !== submitForm.otherName?.length
      || origin.value.otherName?.some((o: string) => submitForm.otherName?.indexOf(o) < 0)) {
      changes.push('other name')
    }
    if (
      !((origin.value.type === submitForm.type)
        || (origin.value.type=="genre" && submitForm.type==0)
        || (origin.value.type=="primary" && submitForm.type==1)
        || (origin.value.type=="subdivide" && submitForm.type==2))
      ) {
      changes.push('type')
    }
    if (origin.value.description !== submitForm.description) {
      changes.push('description')
    }
    if (origin.value.nickname !== submitForm.nickname) {
      changes.push('nickname')
    }
    if (origin.value.belong?.length !== submitForm.belong?.length
      || origin.value.belong?.some((o: string) => submitForm.belong?.indexOf(o) < 0)) {
      changes.push('belong')
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

    .official {
      .official-url {
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
}
</style>