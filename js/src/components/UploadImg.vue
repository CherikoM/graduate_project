<template>
  <div class="uploader-container">
    <el-dialog title="图片剪裁" v-model="dialogVisible" append-to-body>
      <div class="cropper-content">
        <div class="cropper">
          <VueCropper
            ref="cropper"
            class="vue-cropper"
            :img="option.img"
            :outputSize="option.outputSize"
            :outputType="option.outputType"
            :info="option.info"
            :canScale="option.canScale"
            :autoCrop="option.autoCrop"
            :autoCropWidth="option.autoCropWidth"
            :autoCropHeight="option.autoCropHeight"
            :fixed="option.fixed"
            :fixedNumber="option.fixedNumber"
            :full="option.full"
            :fixedBox="option.fixedBox"
            :canMove="option.canMove"
            :canMoveBox="option.canMoveBox"
            :original="option.original"
            :centerBox="option.centerBox"
            :height="option.height"
            :infoTrue="option.infoTrue"
            :maxImgSize="option.maxImgSize"
            :enlarge="option.enlarge"
            :mode="option.mode">
        </VueCropper>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="finish" :loading="loading">确认</el-button>
        </div>
      </template>
    </el-dialog>
    <el-upload class="avatar-uploader" action="114514" :show-file-list="false" name="file"
      :on-change="changeUpload" :auto-upload="false" :http-request="httpRequest">
      <img v-if="imageUrl" :src="imageUrl" class="avatar" />
      <el-icon v-else class="avatar-uploader-icon">
        <Plus />
      </el-icon>
    </el-upload>
    <p class="uploader-description">
      {{ description? description: (imageUrl === ''? '添加封面': '修改封面') }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, toRefs, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

import type { UploadProps } from 'element-plus'

import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'

import { uploadImg } from '@/api/commonAPI'

const emits = defineEmits(['onPicUpdate'])

const props = defineProps<{
  img: string | null | undefined
  description?: string
}>()

const { img, description } = toRefs(props)

//为了接收到异步props的值
watch(img, (newValue)=> {
  if(newValue && newValue !== '') {
    imageUrl.value = newValue
  }
})

const imageUrl = ref('')

const fileName = ref('')

const cropper = ref(null)

const dialogVisible = ref(false)

const option = ref({
    img: '',             //裁剪图片的地址
    outputSize: 1,       //裁剪生成图片的质量(可选0.1 - 1)
    outputType: 'jpeg',  //裁剪生成图片的格式（jpeg || png || webp）
    info: true,          //图片大小信息
    canScale: true,      //图片是否允许滚轮缩放
    autoCrop: true,      //是否默认生成截图框
    autoCropWidth: 230,  //默认生成截图框宽度
    autoCropHeight: 150, //默认生成截图框高度
    fixed: true,         //是否开启截图框宽高固定比例
    fixedNumber: [1, 1], //截图框的宽高比例
    full: true,         //false按原比例裁切图片，不失真
    fixedBox: false,      //固定截图框大小，不允许改变
    canMove: true,      //上传图片是否可以移动
    canMoveBox: true,    //截图框能否拖动
    original: false,     //上传图片按照原始比例渲染
    centerBox: true,    //截图框是否被限制在图片里面
    height: true,        //是否按照设备的dpr 输出等比例图片
    infoTrue: true,     //true为展示真实输出图片宽高，false展示看到的截图框宽高
    maxImgSize: 1000,    //限制图片最大宽度和高度
    enlarge: 1,          //图片根据截图框输出比例倍数
    mode: '100%'  //图片默认渲染方式
})

const finish = ()=> {
  const c = cropper.value as any
  c.getCropBlob(async (data: any)=> {
    let file = new window.File([data], fileName.value)
    let formData = new FormData()
    formData.append("file", file)
    const result = await uploadImg(formData)
    imageUrl.value = 'http://localhost:8080/img/'+ result
    dialogVisible.value = false
    emits('onPicUpdate', result)
  })
}

const httpRequest = ()=> {
  
}

const loading = ref(false)
const changeUpload = (file: any, fileList: any) => {
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('上传文件大小不能超过 5MB!')
    return false
  }
  //提前存一下文件名字 一会转文件的时候能用
  fileName.value = file.name
  let url = URL.createObjectURL(file.raw)
  // 上传成功后将图片地址赋值给裁剪框显示图片
  nextTick(() => {
    option.value.img = url
    dialogVisible.value = true
  })
  return file
}

</script>

<style scoped lang="scss">
.avatar-uploader {
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
}

.cropper {
  text-align: center;
  width: auto;
  height: 400px;
}

:deep(.vue-cropper) {
  .cropper-crop-box {
    .crop-info+span {
      display: block;
      position: relative;
      width: 100%;
      height: 100%;
      top: -100%;
      left: 0;
      z-index: -1;
    }
  }
}
</style>
<style lang="scss">
.uploader-container {
  display: flex;
  flex-direction: column;
  align-items: center;

  .avatar-uploader {

    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
      background-color: #fff;
    }

    .el-upload:hover {
      border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
    }

  }

  .uploader-description {
    margin-top: 0.5rem;
  }

}
</style>