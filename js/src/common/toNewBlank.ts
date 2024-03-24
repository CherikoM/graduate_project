import router from "@/router"

export default (path: string, query: any = {}) => {
  const url = router.resolve({ path, query })
  window.open(url.href, '_blank')
}