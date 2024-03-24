import request from '@/api/request'

export const addCommit = async (comment: any) => {
  const str = JSON.stringify(comment)
  const result = await request(`/comment/add`, str, "PUT", {
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

export const getTopCommentByArea = async (area: number, areaId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/comment/topComment`, {
    area, areaId, pageNum, pageSize
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

export const getSubComment = async (parentId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/comment/subComment`, {
    parentId, pageNum, pageSize
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

export const getTopCommentByUser = async (userId: number, pageNum: number, pageSize: number) => {
  const result = await request(`/comment/userComment`, {
    userId, pageNum, pageSize
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