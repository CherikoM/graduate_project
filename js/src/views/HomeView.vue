<template>
  <div class="container">
    <div class="content" ref="content">
      <h1>我是<br>标题</h1>
      <div class="sub-title" @click="router.push(`/lib`)">进入→</div>
    </div>
    <div class="imgs" ref="imgs">
      <img draggable="false" src="@/assets/homeimgs/1.png" data-speed="-5" data-deg="-20" class="layer">
      <img draggable="false" src="@/assets/homeimgs/2.png" data-speed="5" data-deg="10" class="layer">
      <img draggable="false" src="@/assets/homeimgs/3.png" data-speed="2" data-deg="30" class="layer">
      <img draggable="false" src="@/assets/homeimgs/4.png" data-speed="6" data-deg="-30" class="layer">
      <img draggable="false" src="@/assets/homeimgs/5.png" data-speed="8" data-deg="-10" class="layer">
      <img draggable="false" src="@/assets/homeimgs/6.png" data-speed="-2" data-deg="-40" class="layer">
      <img draggable="false" src="@/assets/homeimgs/7.png" data-speed="4" data-deg="-40" class="layer">
      <img draggable="false" src="@/assets/homeimgs/8.png" data-speed="-9" data-deg="15" class="layer">
      <img draggable="false" src="@/assets/homeimgs/9.png" data-speed="6" data-deg="-15" class="layer">
      <img draggable="false" src="@/assets/homeimgs/10.png" data-speed="-7" data-deg="10" class="layer">
      <img draggable="false" src="@/assets/homeimgs/11.png" data-speed="-5" data-deg="-30" class="layer">
      <img draggable="false" src="@/assets/homeimgs/12.png" data-speed="5" data-deg="15" class="layer">
    </div>

  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import isMobile from '@/common/isMobile'

//图片
const imgs = ref<any>()
const content = ref()
//router实例
const router = useRouter()

/**
 * 实现首页的视差特效
 * @param e 
 */
function parallax(e: any) {
  const children = imgs?.value?.children
  if (!isMobile() && children) {
    for (let i = 0; i < children.length; i++) {
      const speed = children[i].getAttribute('data-speed')
      const deg = children[i].getAttribute('data-deg')

      const x = (window.innerWidth - e.pageX * speed) / 100
      const y = (window.innerHeight - e.pageY * speed) / 100

      children[i].style.transform = `translateX(${x}px) translateY(${y}px) rotate(${deg}deg)`
    }
  }
}

onMounted(() => {
  //在页面挂载完成时，添加视差特效
  document.addEventListener("mousemove", parallax)
  // imgs.value.classList.remove("img-hidden")
  let timer: any
  timer = setTimeout(() => {
    imgs.value.classList.add("img-show")
    clearTimeout(timer)
    timer = setTimeout(() => {
      content.value.classList.add("content-show")
    }, 1000)
  }, 100)

})
</script>

<style scoped lang="scss">
.container {
  position: relative;
  width: 100vw;
  max-width: 1280px;
  height: calc(100vh - 4rem);
  overflow: hidden;
  left: 0;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(229, 231, 234);

  .content {
    position: absolute;
    z-index: 100;
    display: flex;
    align-items: center;
    transform: translateY(-10rem);
    opacity: 0;
    transition: 0.5s;

    h1 {
      color: rgb(63, 62, 71);
      font-size: 5rem;
      // transform: translateY(3rem);
      // opacity: 0;
      transition: all 1s;
      user-select: none;
    }

    .ready {
      transform: translateY(0);
      opacity: 1;
    }

    .sub-title {
      margin-left: 3rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      color: rgb(134, 134, 150);
      font-size: 1.5rem;

      &:hover {
        color: rgb(0, 171, 131) !important;
      }
    }

  }

  .content-show {
    transform: translateY(0);
    opacity: 1;
  }

  .imgs {
    position: absolute;
    left: 0;
    width: 100%;
    height: 100%;
    user-select: none;

    top: 10rem;
    opacity: 0;
    transition: 1s;

    img {
      position: absolute;

      &:first-child {
        width: 12vh;
        left: 90vw;
        top: 3vh;
        transform: rotate(-20deg);
      }

      &:nth-child(2) {
        width: 20vh;
        left: 4vw;
        top: 65vh;
        transform: rotate(10deg);
      }

      &:nth-child(3) {
        width: 10vh;
        left: 50vw;
        top: 30vh;
        transform: rotate(30deg);
      }

      &:nth-child(4) {
        width: 10vh;
        left: 60vw;
        top: 60vh;
        transform: rotate(-30deg);
      }

      &:nth-child(5) {
        width: 12vh;
        left: 35vw;
        top: 45vh;
        transform: rotate(-10deg);
      }

      &:nth-child(6) {
        width: 12vh;
        left: 30vw;
        top: 8vh;
        transform: rotate(-40deg);
      }

      &:nth-child(7) {
        width: 18vh;
        left: 80vw;
        top: 80vh;
        transform: rotate(-40deg);
      }

      &:nth-child(8) {
        width: 18vh;
        left: 80vw;
        top: 35vh;
        transform: rotate(15deg);
      }

      &:nth-child(9) {
        width: 12vh;
        left: 15vw;
        top: 35vh;
        transform: rotate(-15deg);
      }

      &:nth-child(10) {
        width: 15vh;
        left: 60vw;
        top: -5vh;
        transform: rotate(10deg);
      }

      &:nth-child(11) {
        width: 20vh;
        left: -5vw;
        top: 0vh;
        transform: rotate(-30deg);
      }

      &:nth-child(12) {
        width: 12vh;
        left: 40vw;
        top: 80vh;
        transform: rotate(15deg);
      }
    }
  }

  .img-show {
    top: 0;
    opacity: 1;
  }
}</style>