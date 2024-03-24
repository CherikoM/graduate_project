export default (fn: Function, wait: number)=> {
  let timer: ReturnType<typeof setTimeout> | null = null
  return function(){
      if(timer !== null){
          clearTimeout(timer);
      }
      timer = setTimeout(fn,wait);
  }
}