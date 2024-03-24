import request from "@/api/request"
import sleep from "@/common/sleep"

export const getArtistById = async (id: number)=> {
  const result = await request(`/artist/id/${id}`)
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const getArtistsByIds =async (ids: number[]) => {
  const result = await request(`/artist/ids`, {ids})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const addArtistVersion = async (newVersion: any) => {
  const str = JSON.stringify(newVersion)
  const result = await request(`/artist/version/add`, str, "PUT", {
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
  // const result = {
  //   "id": 2,
  //   "artist": {
  //     "id": 3619817,
  //     "name": "Barely Alive",
  //     "profile": "Electronic duo from Massachusetts, USA.\r\nMatt Meier and Willie Watkins",
  //     "type": "group",
  //     "realName": "Matt Meier, Willie Watkins",
  //     "nickname": null,
  //     "belong": [
  //       {
  //         "name": "Disciple (23)",
  //         "id": 7353111,
  //         "isActive": true
  //       }
  //     ],
  //     "official": [
  //       "http://barelyalivemusic.com",
  //       "http://facebook.com/barelyalivemusic",
  //       "http://soundcloud.com/barelyalive",
  //       "http://twitter.com/barelyaliveus",
  //       "http://youtube.com/barelyalivehd",
  //       "http://instagram.com/barelyaliveus"
  //     ],
  //     "picture": null,
  //     "gmtCreated": "2023-01-28 16:45:56",
  //     "gmtModified": "2023-04-05 14:20:38",
  //     "active": false
  //   },
  //   "artistName": "Barely Alive",
  //   "change": [
  //     "nickname",
  //     "picture"
  //   ],
  //   "userId": 1,
  //   "userName": "123",
  //   "info": "{\"name\": \"Barely Alive\", \"type\": 1, \"belong\": [{\"id\": 7353111, \"name\": \"Disciple (23)\", \"isActive\": true}], \"picture\": \"a0758b88-1580-4cf8-ba06-b9dd6374bf6e.png\", \"profile\": \"Electronic duo from Massachusetts, USA.\\r\\nMatt Meier and Willie Watkins\", \"nickname\": \"Microwave\", \"official\": [\"http://barelyalivemusic.com\", \"http://facebook.com/barelyalivemusic\", \"http://soundcloud.com/barelyalive\", \"http://twitter.com/barelyaliveus\", \"http://youtube.com/barelyalivehd\", \"http://instagram.com/barelyaliveus\"], \"realName\": \"Matt Meier, Willie Watkins\"}",
  //   "reference": "123",
  //   "auditorId": null,
  //   "isPass": "wait",
  //   "gmtCreated": "2023-04-05 22:45:01",
  //   "gmtModified": "2023-04-05 22:45:01",
  //   "new": false
  // }

  // await sleep(1000)

  // return result
  
  const result = await request(`/artist/mission`, {}, "get", {
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

export const auditArtist = async(auditMap: any)=> {
  const str = JSON.stringify(auditMap)
  const result = await request(`/artist/audit`, str, "post", {
    token: localStorage.getItem("accessToken") || ""
  })
  console.log(result)
  return result.isSuccess
}

export const startModify = async(artistId: number, userId: number)=> {
  return await request(`/artist/startModify`, {artistId, userId}, "get", {
    token: localStorage.getItem("accessToken") || ""
  })
}

export const getAllArtistVersion = async (artistId: number, userId: number)=> {
  const result = await request(`/artist/version/all`, {artistId, userId})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}