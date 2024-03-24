import request from "@/api/request"

export const addCollection = async(collection: any)=> {
  const str = JSON.stringify(collection)
  const result = await request(`/collection/add`, str, 'put')
  console.log(result)
  return result
}

export const getCollectionByUserId = async(userId: number, pageNum: number, pageSize: number)=> {
  return await request(`/collection/all/${userId}`, {pageNum, pageSize})
}

export const addItemToList = async(colId: number, area: string, areaId: number)=> {
  return await request(`/collection/addItem`, {colId, area, areaId}, "post")
}

export const getCollectionContent = async(colId: number, pageNum: number, pageSize: number)=> {
  return await request(`/collection/content`, {colId, pageNum, pageSize})
}

export const getCollectionById = async(id: number, userId: number)=> {
  return await request(`/collection/id`, {id, userId})
}

export const likeCollection = async(colId: number, userId: number)=> {
  return await request(`/collection/like`, {colId, userId}, "post")
}

export const dislikeCollection = async(colId: number, userId: number)=> {
  return await request(`/collection/dislike`, {colId, userId}, "post")
}

export const getUserLikeList = async(userId: number, pageNum: number, pageSize: number)=> {
  return await request(`/collection/userLike`, {userId, pageNum, pageSize})
}

export const delCollection = async(colId: number)=> {
  return await request(`/collection/del/${colId}`, {}, "post")
}