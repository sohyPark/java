import Vue from 'vue'
import VueRouter from 'vue-router';
import Login from '../components/Login'
import HelloWorld from "../components/Login";
import DocList from "../components/DocList";

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.config.productionTip = false
Vue.use(VueRouter);

const router =  new VueRouter({
  mode: 'history',
  routes:[
    {
      path: '/',
      name: HelloWorld,
      component: HelloWorld
    },
    {
      path: '/login',
      name: Login,
      component: Login,
      props: true
    },
    {
      path: '/doc',
      name: DocList,
      component: DocList,
      props: true
    }
  ]
})

export default router;