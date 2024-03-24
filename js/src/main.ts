import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import VueCropper from 'vue-cropper'

import './assets/styles/main.scss'

// import 'element-plus/theme-chalk/src/message.scss'
// import 'element-plus/theme-chalk/src/message-box.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueCropper)

app.mount('#app')