import request from "@/api/request"
import type { labelData } from "./dataInterfaces"

import sleep from "@/common/sleep"

export const getLabelById = async (id: number)=> {
  const result = await request(`/label/id/${id}`)
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const getLabelsByIds = async(ids: number[])=> {
  const result = await request(`/label/ids`, {ids})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const startModify = async(labelId: number, userId: number)=> {
  return await request(`/label/startModify`, {labelId, userId}, "get", {
    token: localStorage.getItem("accessToken") || ""
  })
}

export const addLabelVersion = async (newVersion: any) => {
  const str = JSON.stringify(newVersion)
  const result = await request(`/label/version/add`, str, "PUT", {
    token: localStorage.getItem("accessToken") || ""
  })
  if (result.error) {
    return {
      message: result.error,
      isSuccess: false
    }
  } else if (result.isSuccess) {
    return {
      message: "提交成功！",
      isSuccess: true
    }
  } else {
    return {
      message: result.message,
      isSuccess: false
    }
  }
}

export const getMission = async()=> {
//   const result = {
//   auditTime: null,
//   auditorId: null,
//   change: ["parent"],
//   gmtCreated: "2023-04-17 18:00:49",
//   gmtModified: "2023-04-17 18:00:49",
//   id: 6,
//   info: "{\"name\": \"Rushdown\", \"contact\": null, \"picture\": null, \"profile\": \"Worldwide collective label founded by [a5044503]\", \"official\": null}",
//   isPass: "wait",
//   label: {
//     children: null,
//     contact: null,
//     gmtCreated: "2023-01-28 16:21:17",
//     gmtModified: "2023-01-28 16:21:17",
//     id: 1009395,
//     name: "Rushdown",
//     official: null,
//     parentId: null,
//     parentName: null,
//     picture: null,
//     profile: "Worldwide collective label founded by [a5044503]",
//   },
//   labelName: "Rushdown",
//   new: false,
//   reference: "23",
//   thank: false,
//   thanks: 0,
//   userId: 1,
//   userName: "123"
// }
//   await sleep(1000)
//   return result
  
  const result = await request(`/label/mission`, {}, "get", {
    token: localStorage.getItem("accessToken") || ""
  })
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const auditLabel = async(auditMap: any)=> {
  const str = JSON.stringify(auditMap)
  const result = await request(`/label/audit`, str, "post", {
    token: localStorage.getItem("accessToken") || ""
  })
  console.log(result)
  return result.isSuccess
}

export const getAllLabelVersion = async (labelId: number, userId: number)=> {
  const result = await request(`/label/version/all`, {labelId, userId})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}