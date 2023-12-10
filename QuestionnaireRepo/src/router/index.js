import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'list',
      component: () => import('../views/front/List.vue')
    },
    {
      path: '/ans',
      name: 'ans',
      component: () => import('../views/front/Ans.vue')
    },
    {
      path: '/questionnaire2',
      name: 'questionnaire2',
      component: () => import('../views/back/Questionnaire2.vue')
    },
    {
      path: '/questionnaire',
      name: 'questionnaire',
      component: () => import('../views/back/Questionnaire.vue')
    },
    {
      path: '/questionnaire1',
      name: 'questionnaire1',
      component: () => import('../views/back/Questionnaire1.vue')
    },
    {
      path: '/questionnaire2-1',
      name: 'questionnaire2-1',
      component: () => import('../views/back/Questionnaire2-1.vue')
    },
    
  ]
})

export default router
