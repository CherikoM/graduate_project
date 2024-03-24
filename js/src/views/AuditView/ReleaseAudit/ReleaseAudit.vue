<template>
  <div class="audit-releases" ref="auditReleaseDOM">

    <h2 class="h2-title">
      发行审核界面
    </h2>

    <div class="audit-releases-main">

      <div class="release-compare">
        <h3>
          <span v-if="!allData.new">对比</span>{{ allData.title }}({{ allData.new ? "新发行" : allData.release?.id }})
        </h3>
        <div class="compare-container"
          :style="{ 'grid-template-columns': allData.new ? 'repeat(1, 100%)' : 'repeat(2, 50%)' }">
          <div class="now-version">
            <ShowReleaseVersion :info="nowVersion" v-if="!allData.new">
              当前版本
            </ShowReleaseVersion>
          </div>
          <div class="new-version">
            <ShowReleaseVersion :info="newVersion" :change="allData.change">
              新版本
            </ShowReleaseVersion>
          </div>
        </div>

      </div>
      <div class="release-reference">
        <h3>信息来源(用户<span>{{ allData.userName }}</span>提交)</h3>
        <p>{{ allData.reference }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue'

import { auditRelease } from "@/api/releaseAPI"
import { format, form } from "@/api/dataInterfaces"
import { ElMessage } from "element-plus"

const props = defineProps<{
  allData: any,
  nowVersion: any,
  newVersion: any
}>()


const { allData, nowVersion, newVersion } = toRefs(props)

const submit = async (isPass: boolean, initCb: Function) => {
  let fmt = null
  let fom = null
  if (typeof (newVersion.value.format * 1) === "number") {
    fmt = newVersion.value.format * 1
  } else if (typeof (newVersion.value.format) === "string") {
    //@ts-ignore
    fmt = format[newVersion.value.format] + 1
  }

  if (typeof (newVersion.value.form * 1) === "number") {
    fom = newVersion.value.form * 1
  } else if (typeof (newVersion.value.form) === "string") {
    //@ts-ignore
    fom = form[newVersion.value.form] + 1
  }

  let mainData = null
  if (newVersion.value.mainRelease?.isNew) {
    //Map中添加mainRelease数据
    mainData = {
      title: newVersion.value.mainRelease.title
    }
  }

  const releaseData = {
    id: nowVersion.value.id,
    mainId: newVersion.value.mainRelease?.id,
    title: newVersion.value.title,
    artist: JSON.stringify(newVersion.value.artist),
    picture: newVersion.value.picture,
    releaseLabel: JSON.stringify(newVersion.value.releaseLabel),
    format: fmt,
    form: fom,
    country: newVersion.value.country,
    releaseDate: newVersion.value.releaseDate,
    genre: JSON.stringify(newVersion.value.genre),
    style: newVersion.value.style? JSON.stringify(newVersion.value.style): null,
    tracklist: JSON.stringify(newVersion.value.tracklist),
    credits: newVersion.value.credits? JSON.stringify(newVersion.value.credits): null,
    supplement: newVersion.value.supplement,
  }

  const auditorId = localStorage.getItem("auditorId")
  const historyData = {
    id: allData.value.id,
    releaseId: nowVersion.value.id,
    auditorId: auditorId,
    userId: allData.value.userId,
    isPass: isPass ? 1 : 2,
    isNew: allData.value.new ? 1 : 0
  }

  const audit = {
    releaseData: JSON.stringify(releaseData),
    historyData: JSON.stringify(historyData),
    mainData: newVersion.value.mainRelease?.isNew ? JSON.stringify(mainData) : null
  }

  console.log(audit)
  console.log(releaseData, historyData, mainData)

  if (await auditRelease(audit)) {
    ElMessage.success("审核成功！")
  } else {
    ElMessage.error("未知原因审核失败！")
  }
  initCb()
}

defineExpose({
  submit
})

</script>

<style scoped lang="scss">
.audit-releases {
  .release-compare {
    h3 {
      margin: 0.75rem 0;
    }

    .compare-container {
      display: grid;
      width: 100%;
      grid-template-columns: repeat(2, 50%);

      @media screen and (max-width: 800px) {
        grid-template-columns: repeat(1, 100%);
        row-gap: 1rem;
      }

      &>div {

        // height: 10rem;
        &>div {
          padding: 1rem;
          border-radius: 0.25rem;
          box-shadow: var(--el-box-shadow);
          width: 97%;
          height: 100%;
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
        }
      }
    }
  }
}

.h2-title {
  // margin: 1rem 0;
  font-size: 2rem;
}

h3 {
  font-size: 1.25rem;
  margin: 1rem 0;
}
</style>