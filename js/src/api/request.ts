/**
 * 封装fetch
 */
export default async (url: string = '', data: object | string = {}, type: string = 'GET', header: object = {
  'Accept': 'application/json',
  // 'Content-Type': 'application/json;charset=UTF-8',
}) => {
  //设置基础路径
  const baseUrl: string = "/apiTest"
  //将请求方式的小写转换成大写
  type = type.toUpperCase()
  //请求地址的拼接
  url = baseUrl + url


  const requestConfig: object = {
    credentials: 'same-origin',
    method: type,
    headers: {
      "Access-Control-Allow-Origin": "*",
      // 'Accept': 'application/json',
      'Content-Type': 'application/json;charset=UTF-8',
      ...header
    },
    mode: "cors", // 用来决定是否允许跨域请求  值有 三个 same-origin，no-cors（默认）以及 cores;
    // cache: "no-store" // 是否缓存请求资源 可选值有 default 、 no-store 、 reload 、 no-cache 、 force-cache 或者 only-if-cached 。
    cache: "default" // 是否缓存请求资源 可选值有 default 、 no-store 、 reload 、 no-cache 、 force-cache 或者 only-if-cached 。
  }


  if (typeof(data) !== 'string') {
    if (typeof (data) === 'object') {
      //数据拼接字符串
      let dataStr: string = ''
      Object.keys(data).forEach(key => {
        //@ts-ignore
        const item: string = Array.isArray(data[key]) ? data[key].join(',') : data[key]
        //@ts-ignore
        if (typeof (item) !== "undefined" && item !== null) {
          dataStr += key + '=' + item + '&';
        }
      })
      if (dataStr !== '') {
        dataStr = dataStr.slice(0, dataStr.lastIndexOf('&'));
        url = url + '?' + dataStr;
      }
      // console.log(url)
    } else if (typeof (data) === 'string') {
      Object.defineProperty(requestConfig, "body", {
        value: data
      })
      
    }
  }

  if (type === 'POST' || type === "PUT") {
    Object.defineProperty(requestConfig, 'body', {
      value: data
    })
  }


  try {
    const response = await fetch(url, requestConfig);
    const responseJson = await response.json();
    return responseJson
  } catch (error: any) {
    // throw new Error(error)
    return { error: 'Net error!' }
  }
}