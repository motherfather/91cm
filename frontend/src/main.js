import 'babel-polyfill'
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import axios from 'axios'
import VueSession from 'vue-session'
import "@/assets/css/app.scss"
import './assets/css/main.css'
import AlertModal from "./plugins/AlertModal";
import './assets/font/iconmonstr/css/iconmonstr-iconic-font.min.css';
import 'animate.css'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'

Date.prototype.addDays = function (days) { 
  var date = new Date(this.valueOf());
  date.setDate(date.getDate() + days);
  return date;
}
Vue.use(AlertModal)
Vue.use(Vuetify)
Vue.use(VueSession, { persist: true})
Vue.prototype.$eventBus = new Vue();
Vue.prototype.$http = axios
Vue.use(BootstrapVue)
Vue.config.productionTip = false

new Vue({
  vuetify: new Vuetify({
    icons: {
      iconfont: "mdiSvg"
    }
  }),
  router,
  store,
  render: h => h(App)
}).$mount('#app')
