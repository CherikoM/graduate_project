export default (): number=> {
  let num: number = 0
  function getNum(): number {
    return num++
  }
  return getNum()
}