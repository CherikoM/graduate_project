import request from "@/api/request"

import type { releaseData } from "@/api/dataInterfaces"

import sleep from "@/common/sleep"

export const getReleaseById = async (id: number) => {
  const result = await request(`/release/id/${id}`)
  if (result.error) {
    return result.error
  } else if (result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const getReleasesByIds = async (ids: number[]) => {
  const result = await request(`/release/ids`, ids)
  if (result.error) {
    return result.error
  } else if (result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const getReleaseVerions = async (mainId: number) => {
  const result = await request(`/release/version/${mainId}`)
  if (result.isSuccess) {
    return result.data
  } else {
    return null
  }
}

export const getArtistReleaseCount = async (artistId: number) => {
  const result = await request(`/release/artist/count/${artistId}`)
  if (result.isSuccess) {
    return result.data
  } else {
    return null
  }
}

export const getArtistRelease = async (artistId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/release/artist/release/${artistId}`, { pageNum, pageSize })
  if (result.isSuccess) {
    return result.data.data
  } else {
    return null
  }
}

export const getArtistFeatured = async (artistId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/release/artist/featured/${artistId}`, { pageNum, pageSize })
  if (result.isSuccess) {
    return result.data.data
  } else {
    return null
  }
}

export const getArtistBehind = async (artistId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/release/artist/behind/${artistId}`, { pageNum, pageSize })
  if (result.error) {
    return result.error
  } else if (result.isSuccess) {
    return result.data.data
  } else {
    return result.message
  }
}

export const getLabelRelease = async (labelId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/release/label/${labelId}`, { pageNum, pageSize })
  if (result.error) {
    return result.error
  } else if (result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const getMainReleaseById = async (mainReleaseId: number) => {
  const result = await request(`/release/main/id/${mainReleaseId}`)
  if (result.error) {
    return result.error
  } else if (result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const getMainReleaseByKeyword = async (keyword: string, pageNum: number, pageSize: number) => {
  const result = await request(`/release/main/search`, { keyword, pageNum, pageSize })
  if (result.error) {
    return result.error
  } else if (result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const addReleaseVersion = async (newVersion: any) => {
  const str = JSON.stringify(newVersion)
  const result = await request(`/release/version/add`, str, "PUT", {
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

export const getMission = async () => {
  // const result = {
  //     auditorId: null,
  //     change: null,
  //     gmtCreated: "2023-03-30 13:15:48",
  //     gmtModified: "2023-03-30 13:15:48",
  //     id: 3,
  //     info: "{\"form\": \"3\", \"genre\": [\"Electronic\"], \"style\": [\"Dubstep\", \"Riddim Dubstep\"], \"title\": \"111\", \"artist\": [{\"id\": 3619817, \"name\": \"Barely Alive\"}], \"format\": \"4\", \"isBase\": false, \"mainId\": 1061731, \"country\": \"US\", \"credits\": null, \"picture\": null, \"tracklist\": [{\"num\": 0, \"open\": false, \"title\": \"1\", \"artist\": [], \"duration\": \"\", \"position\": \"1\", \"extraartists\": []}], \"supplement\": \"11\", \"mainRelease\": {\"id\": 1061731, \"isNew\": false, \"title\": \"We Are Barely Alive\"}, \"releaseDate\": \"2023-03-03\", \"releaseLabel\": [{\"id\": 999909, \"name\": \"Disciple (3)\"}]}",
  //     isPass: "wait",
  //     new: true,
  //     reference: "1",
  //     release: {
  //       artist: null,
  //       country: null,
  //       credits: null,
  //       form: null,
  //       format: null,
  //       genre: null,
  //       gmtCreated: null,
  //       gmtModified: null,
  //       id: null,
  //       isBase: false,
  //       mainRelease: null,
  //       otherVersion: null,
  //       picture: null,
  //       releaseDate: null,
  //       releaseLabel: null,
  //       style: null,
  //       supplement: null,
  //       title: null,
  //       tracklist: null,
  //     },
  //     title: "111",
  //     userId: 2,
  //     userName: "2",
  // }

  // await sleep(1000)

  // return result
  
  const result = await request(`/release/mission`, {}, "get", {
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

export const updateReleaseVersion = async (releaseHistory: any) => {
  const str = JSON.stringify(releaseHistory)
  const result = await request(`/release/version/update`, str, "POST", {
    token: localStorage.getItem("accessToken") || ""
  })
  if (result.error) {
    return {
      message: result.error,
      isSuccess: false
    }
  } else if (result.isSuccess) {
    return {
      message: result.data,
      isSuccess: true
    }
  } else {
    return {
      message: result.message,
      isSuccess: false
    }
  }
}

export const updateRelease = async (release: any) => {
  const str = JSON.stringify(release)
  const result = await request(`/release/update`, str, "POST", {
    token: localStorage.getItem("accessToken") || ""
  })
  if (result.error) {
    return {
      message: result.error,
      isSuccess: false
    }
  } else if (result.isSuccess) {
    return {
      message: result.data,
      isSuccess: true
    }
  } else {
    return {
      message: result.message,
      isSuccess: false
    }
  }
}

export const addRelease = async (release: any) => {
  const str = JSON.stringify(release)
  const result = await request(`/release/add`, str, "PUT", {
    token: localStorage.getItem("accessToken") || ""
  })
  if (result.error) {
    return {
      message: result.error,
      isSuccess: false
    }
  } else if (result.isSuccess) {
    return {
      data: result.data,
      isSuccess: true
    }
  } else {
    return {
      message: result.message,
      isSuccess: false
    }
  }
}

export const getAllReleaseVersion = async (releaseId: number, userId: number)=> {
  const result = await request(`/release/version/all`, {releaseId, userId})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const addMainRelease = async (mainRelease: any)=> {
  const str = JSON.stringify(mainRelease)
  const result = await request(`/release/main/add`, str, "PUT", {
    token: localStorage.getItem("accessToken") || ""
  })
  if (result.error) {
    return {
      message: result.error,
      isSuccess: false
    }
  } else if (result.isSuccess) {
    return {
      data: result.data,
      isSuccess: true
    }
  } else {
    return {
      message: result.message,
      isSuccess: false
    }
  }
}

export const startModify = async(releaseId: number, userId: number)=> {
  return await request(`/release/startModify`, {releaseId, userId}, "get", {
    token: localStorage.getItem("accessToken") || ""
  })
}

export const auditRelease = async(auditMap: any)=> {
  const str = JSON.stringify(auditMap)
  const result = await request(`/release/audit`, str, "post", {
    token: localStorage.getItem("accessToken") || ""
  })
  return result.isSuccess
}