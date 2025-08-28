import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import OAuthCallback from "@/views/OAuthCallback.vue";
import SignInPage from "@/components/SignInPage.vue";
import RegisterPage from "@/components/RegisterPage.vue";
import UserPage from "@/components/UserPage.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/signin',
      name: 'signIn',
      component: SignInPage,
      meta: {requiresGuest: true}
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterPage,
      meta: {requiresGuest: true}
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: UserPage,
      meta: {requiresAuth: true}
    },
    // {
    //   path: '/composition/dashboard',
    //   name: 'composition-dashboard',
    //   component: UserPageNew,
    //   meta: {requiresAuth: true}
    // },
    {
      path: '/oauth/callback',
      name: 'oauth',
      component: OAuthCallback,
    }
  ],
})

export default router
