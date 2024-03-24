<template>
  <div class="container" ref="containerDOM">

    <div class="loading" v-loading="loading" v-show="loading"></div>

    <div class="audit-home" v-if="!area">

      <h2 class="h2-title">总量统计</h2>
      <h3>您一共审核：</h3>
      <div class="count">
        <div class="releases">
          <div class="you">
            发行<span class="big-number">{{ auditorNum.auCount.releases }}</span>条
          </div>
          <div class="other">所有人共审核{{ auditorNum.aCount.releases }}条</div>
        </div>
        <div class="artists">
          <div class="you">
            发行<span class="big-number">{{ auditorNum.auCount.artists }}</span>条
          </div>
          <div class="other">所有人共审核{{ auditorNum.aCount.artists }}条</div>
        </div>
        <div class="labels">
          <div class="you">
            发行<span class="big-number">{{ auditorNum.auCount.labels }}</span>条
          </div>
          <div class="other">所有人共审核{{ auditorNum.aCount.labels }}条</div>
        </div>
        <div class="styles">
          <div class="you">
            发行<span class="big-number">{{ auditorNum.auCount.styles }}</span>条
          </div>
          <div class="other">所有人共审核{{ auditorNum.aCount.styles }}条</div>
        </div>
      </div>
      <el-divider class="divider" />
      <h2 class="h2-title">审核分区</h2>

      <div class="enter-audit">
        <ul class="audit-main">
          <li>
            <div class="audit-block">
              <div class="title">
                发行条目审核
              </div>
              <div class="done">
                <div class="description">
                  今天已审核
                </div>
                <div class="number">
                  <span class="big-number">{{ auditorNum.tuCount.releases }}</span>条
                </div>
                <p>所有人今日审核{{ auditorNum.tCount.releases }}条</p>
                <el-button plain @click="router.push(`/audit/releases`)">开始审核</el-button>
              </div>
            </div>
          </li>
          <li>
            <div class="audit-block">
              <div class="title">
                艺人条目审核
              </div>
              <div class="done">
                <div class="description">
                  今天已审核
                </div>
                <div class="number">
                  <span class="big-number">{{ auditorNum.tuCount.artists }}</span>条
                </div>
                <p>所有人今日审核{{ auditorNum.tCount.artists }}条</p>
                <el-button plain @click="router.push(`/audit/artists`)">开始审核</el-button>
              </div>
            </div>
          </li>
          <li>
            <div class="audit-block">
              <div class="title">
                厂牌条目审核
              </div>
              <div class="done">
                <div class="description">
                  今天已审核
                </div>
                <div class="number">
                  <span class="big-number">{{ auditorNum.tuCount.labels }}</span>条
                </div>
                <p>所有人今日审核{{ auditorNum.tCount.labels }}条</p>
                <el-button plain @click="router.push(`/audit/labels`)">开始审核</el-button>
              </div>
            </div>
          </li>
          <li>
            <div class="audit-block">
              <div class="title">
                曲风条目审核
              </div>
              <div class="done">
                <div class="description">
                  今天已审核
                </div>
                <div class="number">
                  <span class="big-number">{{ auditorNum.tuCount.styles }}</span>条
                </div>
                <p>所有人今日审核{{ auditorNum.tCount.styles }}条</p>
                <el-button plain @click="router.push(`/audit/styles`)">开始审核</el-button>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>



    <el-affix :offset="120"
      style="width: 20rem; position: absolute; z-index: 10; right: 0;text-align: right; white-space: nowrap;"
      v-if="!loading && haveData">

      <span class="count" v-show="rest > 0">请{{ rest }}秒后作出判决</span>
      <span class="mention" v-show="rest <= 0 && !toBottom">请完整浏览后作出判决</span>
      <el-button type="success" plain :disabled="cantSubmit || !toBottom" @click="submit(true)"
        style="margin-left: 1rem;">通过</el-button>
      <el-button type="danger" plain :disabled="cantSubmit || !toBottom" @click="submit(false)">不通过</el-button>
    </el-affix>

    <div class="main-datas" ref="mainDatasDOM">
      <ReleaseAudit v-if="area === 'releases' && !loading && haveData" :all-data="allData" :now-version="nowVersion"
        :new-version="newVersion" ref="releaseAudit"></ReleaseAudit>

      <ArtistAudit v-if="area === 'artists' && !loading && haveData" :all-data="allData" :now-version="nowVersion"
        :new-version="newVersion" ref="artistAudit"></ArtistAudit>

      <LabelAudit v-if="area === 'labels' && !loading && haveData" :all-data="allData" :now-version="nowVersion"
        :new-version="newVersion" ref="labelAudit"></LabelAudit>

      <StyleAudit v-if="area === 'styles' && !loading && haveData" :all-data="allData" :now-version="nowVersion"
        :new-version="newVersion" ref="styleAudit"></StyleAudit>
    </div>


    <el-empty description="No mission now" v-if="!loading && !haveData && area" class="empty">
      <el-button plain @click="router.push(`/audit`)">回到审核入口</el-button>
      <el-button plain @click="router.push(`/lib`)">回到信息库</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue"

import { useRoute } from "vue-router"
import router from "@/router"

import { getMission as rGetMission } from "@/api/releaseAPI"
import { getMission as aGetMission } from "@/api/artistAPI"
import { getMission as lGetMission } from "@/api/labelAPI"
import { getMission as sGetMission } from "@/api/styleAPI"
import { auditorVerify, checkToken, getAuditData } from "@/api/userAPI"
import { ElMessage } from "element-plus"
// import * as echarts from "echarts"

import ReleaseAudit from "./ReleaseAudit/ReleaseAudit.vue"
import ArtistAudit from "./ArtistAudit/ArtistAudit.vue"
import LabelAudit from "./LabelAudit/LabelAudit.vue"
import StyleAudit from "./StyleAudit/StyleAudit.vue"

import sleep from "@/common/sleep"

const div = ref(null)

const { params } = useRoute()

const area = ref(params.area)
const auditorId = localStorage.getItem("auditorId")

const loading = ref(true)

const containerDOM = ref()
const auditReleaseDOM = ref(null)
const mainDatasDOM = ref()

let auditorNum = reactive({
  aCount: {
    releases: 0,
    artists: 0,
    labels: 0,
    styles: 0
  },
  auCount: {
    releases: 0,
    artists: 0,
    labels: 0,
    styles: 0
  },
  tCount: {
    releases: 0,
    artists: 0,
    labels: 0,
    styles: 0
  },
  tuCount: {
    releases: 0,
    artists: 0,
    labels: 0,
    styles: 0
  }
})

const releaseAudit = ref()
const artistAudit = ref()
const labelAudit = ref()
const styleAudit = ref()

// const initChart = () => {
//   const divChart = echarts.init(div.value as unknown as HTMLElement)
//   divChart.setOption({
//     xAxis: {
//       type: "category",
//       data: [
//         "一月",
//         "二月",
//         "三月",
//         "四月",
//         "五月",
//         "六月",
//         "七月",
//         "八月",
//         "九月",
//         "十月",
//         "十一月",
//         "十二月"
//       ]
//     },
//     tooltip: {
//       trigger: "axis"
//     },
//     yAxis: {
//       type: "value"
//     },
//     series: [
//       {
//         data: [
//           820,
//           932,
//           901,
//           934,
//           1290,
//           1330,
//           1320,
//           801,
//           102,
//           230,
//           4321,
//           4129
//         ],
//         type: "line",
//         smooth: true
//       }
//     ]
//   })
// }

const haveData = ref(false)

const nowVersion = ref<any>({})
const newVersion = ref<any>({})
const allData = ref<any>({})

const cantSubmit = ref(true)
const toBottom = ref(false)

const rest = ref(15)

let timer: any = null
let to: any = null

const submit = async (isPass: boolean) => {
  const verify = await auditorVerify()
  if (verify.isSuccess) {
    switch (area.value) {
      case "releases":
        releaseAudit.value.submit(isPass, auditInit)
        return
      case "artists":
        artistAudit.value.submit(isPass, auditInit)
        return
      case "labels":
        labelAudit.value.submit(isPass, auditInit)
        return
      case "styles":
        styleAudit.value.submit(isPass, auditInit)
    }
  } else {
    ElMessage({
      message: verify.message,
      type: "error"
    })
  }




  // console.log(historyData, releaseData)
}

const auditInit = async () => {
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
  const auditorId = localStorage.getItem("auditorId")
  if (!auditorId) {
    ElMessage.error("您还不是审核员！")
    router.push(`/lib`)
  }

  //进行数据展示初始化

  loading.value = true

  clearInterval(timer)
  clearTimeout(to)

  console.log("new init")

  let success = false

  switch (area.value) {
    case "releases":
      success = await releaseInitCore()
      break
    case "artists":
      success = await artistInitCore()
      break
    case "labels":
      success = await labelInitCore()
      break
    case "styles":
      success = await styleInitCore()
      break
  }

  if (!area.value && auditorId) {
    const count = await getAuditData(auditorId as unknown as number)
    const cd = count.data
    console.log(count)
    auditorNum = cd
    console.log(auditorNum)
  }

  if (!success) {
    nowVersion.value = {}
    newVersion.value = {}
    allData.value = {}

    haveData.value = false
  }

  sleep(500)

  loading.value = false

}

const releaseInitCore = async () => {
  const result = await rGetMission()

  if (typeof (result) !== "string") {
    nowVersion.value = result.release
    newVersion.value = JSON.parse(result.info)
    console.log(newVersion, "newVersion")
    // console.log(result.data.change)

    allData.value = result

    initFinishCore()
    return true
  } else {
    return false
  }


}

const artistInitCore = async () => {
  const result = await aGetMission()
  console.log(result)

  if (typeof (result) !== "string") {
    nowVersion.value = result.artist
    newVersion.value = JSON.parse(result.info)

    allData.value = result

    initFinishCore()
    return true
  } else {
    return false
  }
}

const labelInitCore = async () => {
  const result = await lGetMission()
  console.log(result)

  if (typeof (result) !== "string") {
    nowVersion.value = result.label
    newVersion.value = JSON.parse(result.info)

    allData.value = result

    initFinishCore()
    return true
  } else {
    return false
  }
}

const styleInitCore = async () => {
  const result = await sGetMission()
  console.log(result)

  if (typeof (result) !== "string") {
    nowVersion.value = result.style
    newVersion.value = JSON.parse(result.info)

    console.log(nowVersion.value, newVersion.value)

    allData.value = result

    initFinishCore()
    return true
  } else {
    return false
  }
}

const initFinishCore = () => {
  rest.value = 15
  toBottom.value = false
  cantSubmit.value = true


  timer = setInterval(() => {
    rest.value = rest.value - 1;
    if (rest.value <= 0) {
      // if (mainDatasDOM.value.offsetTop < containerDOM.value.offsetHeight) {
      //   console.log("???")
      //   toBottom.value = true
      // }
      clearInterval(timer)
      cantSubmit.value = false
    }
  }, 1000)

  to = setTimeout(() => {
    ElMessage({
      message: "任务已超时！",
      type: "error"
    })
    clearTimeout(to)
    auditInit()
  }, 1000 * 60 * 9.5)

  haveData.value = true

  const container = containerDOM.value as unknown as HTMLElement
  console.log(container)
  container.scrollTo({
    top: 0,
    behavior: "smooth"
  })
}

onMounted(async () => {
  // initChart()

  auditInit()

  console.log(123)
  console.log(containerDOM.value.offsetHeight)
  console.log(mainDatasDOM.value)
  console.log(mainDatasDOM.value.offsetTop)



  const container = containerDOM.value as unknown as HTMLElement
  let timeout: any = null
  container.addEventListener("scroll", () => {
    if (timeout !== null) clearTimeout(timeout)
    timeout = setTimeout(() => {
      if (container.scrollTop >= container.scrollHeight - container.offsetHeight - 100) {
        toBottom.value = true
      }
    }, 500)
  })
})
</script>

<style scoped lang="scss">
.container {
  @include container();
  padding: 2rem;

  .audit-home {

    .count {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr 1fr;
      grid-row-gap: 1rem;
      text-align: center;
      .other {
        font-size: 0.8rem;
      }
      @media screen and (max-width: 720px) {
        grid-template-columns: 1fr 1fr;
      }
    }
    .enter-audit {
      margin-top: 1rem;
      width: 100%;

      .audit-main {
        display: grid;
        grid-template-columns: repeat(4, 25%);

        @media screen and (max-width: 720px) {
          grid-template-columns: repeat(2, 50%);
        }

        .audit-block {
          border-radius: 0.25rem;
          box-shadow: var(--el-box-shadow);
          text-align: center;
          min-width: 9rem;
          width: 80%;
          height: 13rem;
          background-color: #fff;
          margin: 0 auto;
          padding: 0.5rem;

          .title {
            font-size: 1.25rem;
            margin-top: 0.25rem;
          }

          .done {
            margin-top: 0.25rem;
            display: flex;
            flex-direction: column;
            align-items: center;

            .number {
              margin-top: -0.5rem;
              width: 80%;
              word-break: break-all;
              white-space: normal;
            }

            p {
              font-size: 0.75rem;
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

.divider {
  margin-top: 1.25rem;
  margin-bottom: 1.25rem;
}

.big-number {
  font-size: 3rem;
  color: red;
}

.loading {
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  margin-top: 45vh;
}

.empty {
  margin-top: 10vh;
}
</style>