import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
      additionalData:
       '@import "@/assets/styles/common.scss"; @import "@/assets/styles/clear.scss"; @import "@/assets/styles/colors.scss"; @import "element-plus/theme-chalk/src/message.scss"; @import "element-plus/theme-chalk/src/message-box.scss";',
      },
    },
  },
  server: {
    cors: true, // 允许跨域

    // 设置代理
    proxy: {
      '/apiTest': { //apiTest是自行设置的请求前缀，按照这个来匹配请求，有这个字段的请求，就会进到代理来
        target: 'http://localhost:8080',
        changeOrigin: true, //是否跨域
        rewrite: (path) => path.replace('/apiTest', '') //重写匹配的字段
      }
    }
  }
})
