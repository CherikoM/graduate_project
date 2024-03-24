import jwtDecode from "jwt-decode"

export default ()=> {
  const accessToken = localStorage.getItem("accessToken")

  let decode: any

  if(!accessToken) {
    return {
      id: null,
      name: null,
    }
  }

  //解码accessToken，用于取得过期时间
  try {
    decode = jwtDecode(accessToken)
  } catch(e) {
    return {
      id: null,
      name: null,
    }
  }

  return({
    id: decode.id,
    name: decode.name,
  })

}