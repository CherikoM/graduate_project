export default (timeout: number) => {
  return new Promise<void>((resolve)=>{
    setTimeout(()=>{
      resolve();
    }, timeout)
  })
}