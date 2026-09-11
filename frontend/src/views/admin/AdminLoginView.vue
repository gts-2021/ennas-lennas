<template>
  <div class="login-page">
    <div class="login-box card">
      <div class="login-header text-center">
        <div class="admin-icon-wrapper">
          <ShieldCheck :size="32" />
        </div>
        <h1 class="login-title">{{ t('admin.loginTitle') }}</h1>
        <p class="login-subtitle">{{ t('admin.loginSubtitle') }}</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div v-if="authStore.error" class="alert-box error mb-4">
          <AlertCircle :size="18" />
          <span>{{ authStore.error }}</span>
        </div>

        <div class="form-group">
          <label class="form-label">{{ t('common.email') }}</label>
          <input 
            type="email" 
            v-model="email" 
            class="form-input" 
            placeholder="admin@ennaslennas.org" 
            required 
            autocomplete="email"
          />
        </div>

        <div class="form-group">
          <label class="form-label">{{ t('common.password') || 'Mot de passe' }}</label>
          <input 
            type="password" 
            v-model="password" 
            class="form-input" 
            placeholder="••••••••" 
            required 
            autocomplete="current-password"
          />
        </div>

        <button type="submit" class="btn btn-primary btn-block mt-4" :disabled="authStore.loading">
          <span v-if="authStore.loading">{{ t('common.loading') }}</span>
          <span v-else>{{ t('admin.loginBtn') }}</span>
          <LogIn :size="18" />
        </button>

        <div class="login-footer text-center mt-4">
          <router-link to="/" class="back-link">
            ← {{ t('caseDetail.backToList') }}
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ShieldCheck, LogIn, AlertCircle } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

const email = ref('admin@ennaslennas.org')
const password = ref('')

async function handleLogin() {
  try {
    const success = await authStore.login(email.value, password.value)
    if (success) {
      router.push('/admin')
    }
  } catch (err) {
    // Handled in store
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1.5rem;
  background: var(--bg-subtle);
}

.login-box {
  max-width: 440px;
  width: 100%;
  padding: 2.5rem;
  background: var(--bg-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
}

.admin-icon-wrapper {
  width: 60px;
  height: 60px;
  margin: 0 auto 1rem auto;
  border-radius: 50%;
  background: var(--primary-subtle);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 0.25rem;
}

.login-subtitle {
  color: var(--text-muted);
  font-size: 0.9rem;
}

.login-form {
  margin-top: 1.75rem;
}

.btn-block {
  width: 100%;
}

.back-link {
  font-size: 0.85rem;
  color: var(--text-muted);
  transition: var(--transition);
}

.back-link:hover {
  color: var(--primary);
}

.mb-4 { margin-bottom: 1rem; }
.mt-4 { margin-top: 1rem; }
.text-center { text-align: center; }
</style>
