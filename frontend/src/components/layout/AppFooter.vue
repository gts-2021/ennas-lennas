<template>
  <footer class="app-footer">
    <div class="container footer-container">
      <div class="footer-top">
        <div class="footer-brand">
          <div class="brand-title">{{ t('footer.brandTitle') }}</div>
          <p class="brand-quote">
            {{ t('footer.quote') }}
          </p>
          <div class="privacy-pill">
            <Shield :size="15" />
            <span>{{ t('footer.privacyNotice') }}</span>
          </div>
        </div>

        <div class="footer-links-group">
          <div class="links-column">
            <h4>{{ t('footer.platform') }}</h4>
            <router-link to="/">{{ t('nav.home') }}</router-link>
            <router-link to="/cases">{{ t('nav.allCases') }}</router-link>
            <router-link to="/demande-aide">{{ t('nav.needHelp') }}</router-link>
          </div>

          <div class="links-column">
            <h4>{{ t('footer.socialContact') }}</h4>
            <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
              Instagram Ennas Lennas
            </a>
            <router-link to="/admin/login">{{ t('footer.moderationSpace') }}</router-link>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <div>© {{ currentYear }} {{ t('footer.rights') }}</div>
        <div class="footer-badge">{{ t('footer.algeriaEthic') }}</div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Shield } from 'lucide-vue-next'

const { t } = useI18n()
const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.app-footer {
  background-color: #0f231b;
  color: #d1ded8;
  margin-top: auto;
  border-top: 1px solid #1c3d31;
  padding: 3.5rem 0 2rem 0;
}

.footer-top {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 3rem;
  margin-bottom: 3rem;
}

.brand-title {
  font-family: var(--font-ar);
  font-size: 1.4rem;
  font-weight: 800;
  color: #ffffff;
  margin-bottom: 0.75rem;
}

.brand-quote {
  color: #a3b8af;
  font-size: 0.95rem;
  max-width: 520px;
  line-height: 1.6;
  margin-bottom: 1.25rem;
  font-style: italic;
}

.privacy-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.06);
  padding: 0.5rem 0.85rem;
  border-radius: var(--radius-md);
  font-size: 0.8rem;
  color: #86efac;
  border: 1px solid rgba(134, 239, 172, 0.2);
}

.footer-links-group {
  display: flex;
  gap: 3rem;
}

.links-column {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.links-column h4 {
  color: #ffffff;
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.links-column a {
  color: #a3b8af;
  font-size: 0.9rem;
  transition: var(--transition);
}

.links-column a:hover {
  color: #ffffff;
  transform: translateX(2px);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.85rem;
  color: #789085;
}

.footer-badge {
  font-weight: 500;
  color: #a3b8af;
}

@media (max-width: 768px) {
  .footer-top {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  .footer-links-group {
    flex-direction: column;
    gap: 1.5rem;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>
