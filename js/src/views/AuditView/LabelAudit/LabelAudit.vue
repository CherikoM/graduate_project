<template>
  <div class="audit-artists" ref="auditArtistDOM">

    <h2 class="h2-title">
      厂牌审核界面
    </h2>

    <div class="audit-releases-main">

      <div class="release-compare">
        <h3>
          <span v-if="!allData.new">对比</span>{{ allData.labelName }}({{ allData.new ? "新厂牌" : allData.label?.id }})
        </h3>
        <div class="compare-container"
          :style="{ 'grid-template-columns': allData.new ? 'repeat(1, 100%)' : 'repeat(2, 50%)' }">
          <div class="now-version">
            <ShowLabelVersion :info="nowVersion" v-if="!allData.new">
              当前版本
            </ShowLabelVersion>
          </div>
          <div class="new-version">
            <ShowLabelVersion :info="newVersion" :change="allData.change">
              新版本
            </ShowLabelVersion>
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

import { auditLabel } from "@/api/labelAPI"
import { ElMessage } from "element-plus"

const props = defineProps<{
  allData: any,
  nowVersion: any,
  newVersion: any
}>()

const { allData, nowVersion, newVersion } = toRefs(props)

const submit = async (isPass: boolean, initCb: Function) => {
  const labelData = {
    id: nowVersion.value.id,
    name: newVersion.value.name,
    picture: newVersion.value.picture,
    parentId: newVersion.value.parentId,
    parentName: newVersion.value.parentName,
    profile: newVersion.value.profile,
    official: newVersion.value.official? JSON.stringify(newVersion.value.official): null,
    contact: newVersion.value.contact? JSON.stringify(newVersion.value.contact): null,
  }

  const auditorId = localStorage.getItem("auditorId")
  const historyData = {
    id: allData.value.id,
    labelId: nowVersion.value.id,
    auditorId: auditorId,
    userId: allData.value.userId,
    isPass: isPass ? 1 : 2,
    isNew: allData.value.new ? 1 : 0
  }


  const audit = {
    labelData: JSON.stringify(labelData),
    historyData: JSON.stringify(historyData),
  }



  if (await auditLabel(audit)) {
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
.audit-artists {
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