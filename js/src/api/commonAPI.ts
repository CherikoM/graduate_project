import request from '@/api/request'

import type { tagSearchQuery } from '@/api/dataInterfaces'

export const tagSearch = async(query: tagSearchQuery)=> {
  const result = await request(`/common/search`, query, 'GET')
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const uploadImg = async(data: any)=> {
  const response = await fetch(`/apiTest/common/uploadImg`, {
    method: "PUT",
    body: data,
    headers: {
      token: localStorage.getItem("accessToken") || ""
    }
  })
  const result = await response.json()
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

