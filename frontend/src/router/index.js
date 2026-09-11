import { createRouter, createWebHistory } from 'vue-router'
import i18n from '@/i18n'

const routes = [
  // Public Portal
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/public/HomeView.vue'),
    meta: { titleKey: 'nav.home' }
  },
  {
    path: '/cases',
    name: 'cases',
    component: () => import('@/views/public/CaseListView.vue'),
    meta: { titleKey: 'nav.allCases' }
  },
  {
    path: '/cases/:reference',
    name: 'case-detail',
    component: () => import('@/views/public/CaseDetailView.vue'),
    meta: { titleKey: 'common.details' }
  },
  {
    path: '/demande-aide',
    name: 'help-request',
    component: () => import('@/views/public/HelpRequestWizard.vue'),
    meta: { titleKey: 'nav.needHelp' }
  },

  // Admin Portal
  {
    path: '/admin/login',
    name: 'admin-login',
    component: () => import('@/views/admin/AdminLoginView.vue'),
    meta: { titleKey: 'admin.loginTitle' }
  },
  {
    path: '/admin',
    name: 'admin-dashboard',
    component: () => import('@/views/admin/AdminDashboardView.vue'),
    meta: { requiresAuth: true, titleKey: 'admin.dashboardTitle' }
  },
  {
    path: '/admin/cases',
    name: 'admin-cases',
    component: () => import('@/views/admin/AdminCaseListView.vue'),
    meta: { requiresAuth: true, titleKey: 'admin.listTitle' }
  },
  {
    path: '/admin/cases/:id',
    name: 'admin-case-detail',
    component: () => import('@/views/admin/AdminCaseDetailView.vue'),
    meta: { requiresAuth: true, titleKey: 'admin.splitPrivate' }
  },
  {
    path: '/admin/help-offers',
    name: 'admin-help-offers',
    component: () => import('@/views/admin/AdminHelpOffersView.vue'),
    meta: { requiresAuth: true, titleKey: 'admin.offersTitle' }
  },

  // Fallback
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const brand = i18n.global.locale.value === 'ar' ? 'الناس للناس' : 'Ennas Lennas'
  if (to.meta.titleKey) {
    const localizedTitle = i18n.global.t(to.meta.titleKey)
    document.title = `${localizedTitle} — ${brand}`
  } else if (to.meta.title) {
    document.title = to.meta.title
  } else {
    document.title = brand
  }

  const token = localStorage.getItem('ennas_token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'admin-login' })
  } else {
    next()
  }
})

export default router
