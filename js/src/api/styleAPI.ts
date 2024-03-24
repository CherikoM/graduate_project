import request from "@/api/request"

import type { tagSearchQuery } from "@/api/dataInterfaces"

import { styleType } from "@/api/dataInterfaces"
import sleep from "@/common/sleep"

export const getAllGenreAPI = async ()=> {
  return await request(`/style/allGenre`)
}

export const getAllStyleAPI = async ()=> {
  return await request(`/style/allStyle`)
}

export const getGenreAndPrimary = async ()=> {
  return await request(`/style/genrePrimary`)
}

export const getStyleGroupAPI = async ()=> {
  return await request(`/style/styleGroup`)
}

export const getStyleByEnName = async (enName: string)=> {
  const result = await request(`/style/name/${enName}`)
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const startModify = async(styleName: string, userId: number)=> {
  return await request(`/style/startModify`, {styleName, userId}, "get", {
    token: localStorage.getItem("accessToken") || ""
  })
  // await sleep(1000)
  // return {isSuccess: true}
}

export const canUseEnName = async (enName: string)=> {
  return await request(`/style/canUse/${enName}`)
}

export const addStyleVersion = async (newVersion: any) => {
  const str = JSON.stringify(newVersion)
  const result = await request(`/style/version/add`, str, "PUT", {
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
  //   "style": {
  //     "id": 3,
  //     "name": "电子",
  //     "enName": "Electronic",
  //     "value": "电子",
  //     "otherName": null,
  //     "type": "genre",
  //     "description": "efsfgsrgergrsg",
  //     "belong": null,
  //     "branch": null,
  //     "gmtCreated": "2023-02-03 21:13:43",
  //     "gmtModified": "2023-02-03 21:19:36"
  //   },
  //   "styleEnName": "Electronic",
  //   "change": [
  //     "type",
  //     "description"
  //   ],
  //   "userId": 1,
  //   "userName": "123",
  //   "info": "{\"name\": \"电子\", \"type\": \"0\", \"belong\": null, \"enName\": \"Electronic\", \"otherName\": null, \"description\": \"<h1></h1><h1>Ut placeat unde qui obcaecati fugiat. </h1><p>Lorem ipsum dolor sit amet. A provident maxime id veniam quodAd asperiores et quisquam iste ex voluptatem officia cum consequatur voluptas eum minus fugiat. Id dolores saepe <strong>Et laborum et illo quae qui esse vitae</strong> ea dicta commodi sit quos eaque sit earum optio. </p><ul><li>Est quia reprehenderit non fugiat quas. </li><li>Sed ipsum obcaecati in mollitia cumque. </li></ul><h2>Ut atque maxime aut quidem adipisci? </h2><p>Non internos beataecum repellendus qui reprehenderit quia. Sit internos rerum vel voluptatem blanditiis <strong>Aut sequi est vitae omnis est dolores voluptatum</strong>. Ut consectetur voluptatum et internos sintnon esse sit temporibus vero! Ad dignissimos rationeAut consequatur qui architecto itaque est nostrum dolorem aut numquam laborum qui nulla dolor. </p><p>Est laudantium pariatur. </p><p>Et voluptas tenetur et expedita harum rem nesciunt rerum est magnam tempora. </p><p>Ea cumque recusandae. </p><p>At totam laboriosam ut voluptatem molestiae. </p><p>Est adipisci itaque? </p><p>Et sint doloremque a Quis delectus ad culpa odit. </p><blockquote>Ut facilis dolores hic inventore voluptates hic ullam facilis et error deserunt aut dolores enim. </blockquote><pre><code >&lt;!-- Et repellendus totam ut dolorem placeat. --&gt;&lt;est&gt;Cum blanditiis dolores?&lt;/est&gt;&lt;doloribus&gt;Eum explicabo neque non fuga esse.&lt;/doloribus&gt;&lt;qui&gt;Et distinctio quos.&lt;/qui&gt;</code></pre><h3>Et iusto voluptatem non quibusdam quae. </h3><p>Et nihil velitUt dolores ea sint voluptatem et incidunt esse aut voluptate culpa ut itaque tenetur. Nam fugit obcaecati <em>Ea laboriosam id distinctio voluptatem</em>. At esse dolore non debitis quasi <strong>Ad voluptate et nihil doloribus</strong> non unde similique sit dolorem quia ut repellendus porro. Sed ipsa molestiaeVel possimus vel enim rerum et suscipit consequatur nam modi magni. </p><ol><li>Ut autem nesciunt aut accusamus consequatur. </li><li>A quia dolore aut omnis odio? </li><li>Aut sunt nisi et repellat omnis. </li><li>Quo voluptatem fuga vel ipsum assumenda. </li></ol><p><br></p>\"}",
  //   "reference": "123",
  //   "auditorId": null,
  //   "isPass": "wait",
  //   "gmtCreated": "2023-04-08 20:20:46",
  //   "gmtModified": "2023-04-08 20:20:46",
  //   "new": false
  // }

  // await sleep(1000)

  // return result
  
  const result = await request(`/style/mission`, {}, "get", {
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

export const auditStyle = async(auditMap: any)=> {
  const str = JSON.stringify(auditMap)
  const result = await request(`/style/audit`, str, "post", {
    token: localStorage.getItem("accessToken") || ""
  })
  console.log(result)
  return result.isSuccess
}

export const getAllStyleVersion = async (styleName: string, userId: number)=> {
  const result = await request(`/style/version/all`, {styleName, userId})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}