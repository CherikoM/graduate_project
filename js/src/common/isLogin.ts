export default ()=> {
  const accessToken = window.localStorage.getItem("accessToken")
  const refreshToken = window.localStorage.getItem("refreshToken")
  if(accessToken && accessToken !== null && refreshToken && refreshToken !== null) {
    return true
  } else {
    return false
  }
}