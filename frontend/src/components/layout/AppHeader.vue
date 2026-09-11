<template>
  <header class="app-header">
    <div class="container header-container">
      <router-link to="/" class="logo-brand">
        <div class="logo-icon">
          <HeartHandshake :size="24" class="icon-brand" />
        </div>
        <div class="brand-text">
          <span class="brand-ar">الناس للناس</span>
          <span class="brand-fr">Ennas Lennas</span>
        </div>
      </router-link>

      <nav class="nav-links">
        <router-link to="/" class="nav-link" active-class="active">{{ t('nav.home') }}</router-link>
        <router-link to="/cases" class="nav-link" active-class="active">{{ t('nav.allCases') }}</router-link>
        
        <template v-if="authStore.isAuthenticated">
          <router-link to="/admin" class="nav-link admin-pill" active-class="active">
            <ShieldCheck :size="16" />
            <span>{{ t('nav.admin') }}</span>
          </router-link>
        </template>
      </nav>

      <div class="header-actions">
        <button class="lang-toggle" @click="toggleLanguage" :title="locale === 'fr' ? 'Passer en Arabe' : 'Passer en Français'">
          {{ locale === 'fr' ? 'العربية' : 'Français' }}
        </button>

        <router-link to="/demande-aide" class="btn btn-primary btn-sm">
          <PlusCircle :size="16" />
          <span>{{ t('nav.needHelp') }}</span>
        </router-link>

        <template v-if="authStore.isAuthenticated">
          <button class="btn btn-secondary btn-sm" @click="handleLogout" :title="t('nav.logout')">
            <LogOut :size="16" />
          </button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { HeartHandshake, PlusCircle, ShieldCheck, LogOut } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { setAppLocale } from '@/i18n'

const router = useRouter()
const authStore = useAuthStore()
const { t, locale } = useI18n()

function toggleLanguage() {
  const next = locale.value === 'fr' ? 'ar' : 'fr'
  setAppLocale(next)
}

function handleLogout() {
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
.app-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--border-light);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-sm);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.logo-brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-icon {
  width: 42px;
  height: 42px;
  background: var(--primary-subtle);
  color: var(--primary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-ar {
  font-family: var(--font-ar);
  font-size: 1.15rem;
  font-weight: 800;
  color: var(--primary);
  line-height: 1.1;
}

.brand-fr {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-link {
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-muted);
  padding: 0.5rem 0.75rem;
  border-radius: var(--radius-sm);
  transition: var(--transition);
}

.nav-link:hover, .nav-link.active {
  color: var(--primary);
  background: var(--primary-subtle);
}

.admin-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  background: #f1f5f9;
  color: #334155;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.lang-toggle {
  background: transparent;
  border: 1px solid var(--border-medium);
  padding: 0.35rem 0.75rem;
  border-radius: var(--radius-sm);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  color: var(--text-main);
  transition: var(--transition);
}

.lang-toggle:hover {
  background: var(--bg-subtle);
  border-color: var(--primary);
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
}
</style>
