import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import store from './store'
import './permission'

Vue.use(ElementUI, { size: 'small' })

// 创建Vue实例并挂载到#app
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
