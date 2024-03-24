export default ()=> {
  const auditorId = localStorage.getItem("auditorId")

  if(!auditorId) {
    return 0
  }
  //@ts-ignore
  return auditorId*1
}