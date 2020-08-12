import Vue from 'vue'
import App from './App.vue'
import BootstrapVue from 'bootstrap-vue'
import axios from 'axios'
import SortedTablePlugin from "vue-sorted-table";
import VueRouter from 'vue-router';
import router from './router/index';
import VueCookie from 'vue-cookie';


import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'


Vue.config.productionTip = false
Vue.use(BootstrapVue);
Vue.use(SortedTablePlugin);
Vue.use(VueRouter);
Vue.use(VueCookie);
Vue.prototype.$axios = axios;

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')


