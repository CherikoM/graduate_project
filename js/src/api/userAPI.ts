import request from '@/api/request'
import jwtDecode from "jwt-decode"

import getIdAndName from '@/common/getIdAndName'

export const userRegister =async (user: string)=> {
  const result = await request(`/user/register`, user, 'PUT')
  if(result.error) {
    return {
      isSuccess: false,
      message: result.error
    }
  } else if(!result.isSuccess) {
    return {
      isSuccess: false,
      message: result.message
    }
  } else if(result.isSuccess) {
    return {
      isSuccess: true,
      message: result.data
    }
  } else {
    return {
      isSuccess: false,
      message: "Unknow error!"
    }
  }
}

export const userLogin = async(user:string)=> {
  const result = await request(`/user/login`, user, 'POST')
  if(result.error) {
    return {
      isSuccess: false,
      message: result.error
    }
  } else if(!result.isSuccess) {
    return {
      isSuccess: false,
      message: result.message
    }
  } else if(result.isSuccess) {
    return {
      isSuccess: true,
      message: result.data
    }
  } else {
    return {
      isSuccess: false,
      message: "Unknow error!"
    }
  }
}

export const getUserById = async(targetId: number, userId: number)=> {
  const result = await request(`/user/id`, {targetId, userId})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const checkToken = async()=> {
  const accessToken = localStorage.getItem("accessToken")
  const refreshToken = localStorage.getItem("refreshToken")
  let shouldRefresh = false
  let message = "登陆验证通过"

  if(!accessToken || !refreshToken) {
    //前端没有对应的token，说明用户未曾登录
    localStorage.removeItem("accessToken")
    return {
      isLogin: false,
      message: "登录信息不合法"
    }
  }

  let decode: any

  //解码accessToken，用于取得过期时间
  try {
    decode = jwtDecode(accessToken)
  } catch(e) {
    return {
      isLogin: false,
      message: "请登录后再执行操作"
    }
  }

  //当前时间戳（毫秒单位）
  const now = Date.now()

  //token过期的时间戳（秒单位）
  const tokenExp = decode.exp

  //剩余时间（分钟单位）
  const restTime = (tokenExp - Math.ceil(now/1000))/60

  //如果accessToken剩余时间小于30分钟，提醒后端需要刷新token
  if(restTime < 30) {
    shouldRefresh = true
  }

  //获取后端检查token的结果
  const result = await request(`/user/checkToken`, {}, "GET", {
    accessToken, refreshToken, shouldRefresh
  })

  //结果有accessToken，说明accessToken需要刷新
  if(result.data?.accessToken) {
    localStorage.setItem("accessToken", result.data.accessToken)
    //结果有refreshToken，说明refreshToken需要刷新
    if(result.data.refreshToken) {
      localStorage.setItem("refreshToken", result.data.refreshToken)
    }
  }

  //结果有message（错误信息）
  if(result.message) {
    message = result.message
  }  


  //返回登陆验证的状态，isSuccess
  return {isLogin:result.isSuccess, message: message}

}

export const auditorVerify = async()=> {
  const userId = getIdAndName().id
  const auditorId = localStorage.getItem("auditorId")

  console.log(auditorId)

  if(auditorId==null) {
    return {
      isSuccess: false,
      message: "You're not auditor"
    }
  }

  const result = await request(`/user/audit/verify`, {auditorId, userId}, "get", {
    token: localStorage.getItem("accessToken") || ""
  })
  if(result.error) {
    return {
      isSuccess: false,
      message: result.error
    }
  } else if(result.isSuccess) {
    return {
      isSuccess: true,
      message: result.data
    }
  } else {
    return {
      isSuccess: false,
      message: result.message
    }
  }
}

export const getUserContribute = async (contributorId: number, userId: number, pageNum: number = 1, pageSize: number = 15) => {
  const result = await request(`/user/contribute/all`, {contributorId, userId, pageNum, pageSize})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.message
  }
}

export const doThankContribute = async(area: string, historyId:number, userId: number)=> {
  const result = await request(`/user/contribute/doThank`, {area, historyId, userId})
  console.log(result)
  return result.isSuccess
}

export const undoThankContribute = async(area: string, historyId:number, userId: number)=> {
  const result = await request(`/user/contribute/undoThank`, {area, historyId, userId})
  console.log(result)
  return result.isSuccess
}

export const myFollows = async(userId: number, pageNum: number, pageSize: number)=> {
  const result = await request(`/user/follow/myFollows`, {userId, pageNum, pageSize})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.error
  }
}

export const myFollowers = async(userId: number, pageNum: number, pageSize: number)=> {
  const result = await request(`/user/follow/myFollowers`, {userId, pageNum, pageSize})
  if(result.error) {
    return result.error
  } else if(result.isSuccess) {
    return result.data
  } else {
    return result.error
  }
}

export const doFollow = async(target: number, userId: number, userName: string)=> {
  return await request(`/user/follow/do/${target}`, {userId, userName})
}

export const undoFollow = async(target: number, userId: number, userName: string)=> {
  return await request(`/user/follow/undo/${target}`, {userId, userName})
}

export const getAuditData = async(auditorId: number) => {
  return await request(`/user/audit/data`, {auditorId})
}

export const logout = async(userId: number)=> {
  return await request(`/user/logout/${userId}`, {}, "post")
}

export const update = async(userDO: any)=> {
  const str = JSON.stringify(userDO)
  return await request(`/user/update`, str, "post")
}