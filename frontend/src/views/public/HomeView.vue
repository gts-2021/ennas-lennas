<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container hero-container">
        <div class="hero-content text-center">
          <div class="hero-tag">
            <HeartHandshake :size="16" />
            <span>{{ t('home.heroBadge') }}</span>
          </div>

          <h1 class="hero-title">
            <span class="title-ar">الناس للناس</span>
            <span class="title-fr">{{ t('home.heroTitle') }}</span>
          </h1>

          <p class="hero-lead">
            {{ t('home.heroSubtitle') }}
          </p>

          <div class="hero-cta-group">
            <router-link to="/demande-aide" class="btn btn-primary btn-lg shadow-glow">
              <PlusCircle :size="20" />
              <span>{{ t('home.ctaNeed') }}</span>
            </router-link>
            <router-link to="/cases" class="btn btn-secondary btn-lg">
              <Heart :size="20" />
              <span>{{ t('home.ctaHelp') }}</span>
            </router-link>
          </div>

          <div class="hero-reassurance">
            <span class="reassurance-item">
              <ShieldCheck :size="16" class="text-success" />
              {{ t('home.pillar2Title') }}
            </span>
            <span class="reassurance-dot">•</span>
            <span class="reassurance-item">
              <Lock :size="16" class="text-success" />
              {{ t('home.pillar1Title') }}
            </span>
            <span class="reassurance-dot">•</span>
            <span class="reassurance-item">
              <CheckCircle :size="16" class="text-success" />
              {{ t('home.pillar3Title') }}
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- Urgents Cases Section -->
    <section v-if="urgentCases.length > 0" class="section section-urgent">
      <div class="container">
        <div class="section-title-wrap flex justify-between items-center mb-6">
          <div>
            <div class="urgent-badge-header">
              <AlertTriangle :size="16" />
              <span>{{ t('home.urgentSectionTitle') }}</span>
            </div>
            <h2 class="section-heading">{{ t('home.urgentSectionSubtitle') }}</h2>
          </div>
          <router-link to="/cases?urgency=HIGH" class="view-all-link">
            {{ t('home.viewAllCases') }}
          </router-link>
        </div>

        <div class="grid grid-cols-1 sm-grid-cols-2 lg-grid-cols-3 gap-6">
          <div v-for="item in urgentCases" :key="item.reference" class="card case-card">
            <div class="case-img-wrap" v-if="item.publicImageUrl">
              <img :src="item.publicImageUrl" :alt="item.title" class="case-img" />
              <span class="badge badge-urgent-critical case-float-badge">{{ t('urgency.CRITICAL') }}</span>
            </div>
            <div class="case-body">
              <div class="case-meta flex justify-between items-center mb-2">
                <span class="case-cat">{{ getCategoryName(item) }}</span>
                <span class="case-loc">📍 {{ getWilayaName(item) }}</span>
              </div>
              <h3 class="case-title">{{ item.title }}</h3>
              <div class="case-footer flex justify-between items-center mt-4">
                <div class="case-amount" v-if="item.amountNeeded">
                  <span class="amount-val">{{ formatNumber(item.amountNeeded) }} {{ t('common.dzd') }}</span>
                  <span class="amount-label">{{ t('cases.neededAmount') }}</span>
                </div>
                <div v-else></div>
                <router-link :to="`/cases/${item.reference}`" class="btn btn-primary btn-sm">
                  {{ t('cases.viewCase') }}
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- How It Works Section -->
    <section class="section section-how">
      <div class="container text-center">
        <span class="badge badge-status-published mb-2">{{ t('home.heroBadge') }}</span>
        <h2 class="section-heading">{{ t('home.howItWorksTitle') }}</h2>
        <p class="section-subtitle">
          {{ t('home.howItWorksSubtitle') }}
        </p>

        <div class="steps-grid mt-8">
          <div class="step-box">
            <div class="step-num">1</div>
            <div class="step-icon-circle">
              <FileEdit :size="24" />
            </div>
            <h3 class="step-box-title">{{ t('home.step1Title') }}</h3>
            <p class="step-box-desc">
              {{ t('home.step1Desc') }}
            </p>
          </div>

          <div class="step-box">
            <div class="step-num">2</div>
            <div class="step-icon-circle">
              <ShieldCheck :size="24" />
            </div>
            <h3 class="step-box-title">{{ t('home.step2Title') }}</h3>
            <p class="step-box-desc">
              {{ t('home.step2Desc') }}
            </p>
          </div>

          <div class="step-box">
            <div class="step-num">3</div>
            <div class="step-icon-circle">
              <Globe :size="24" />
            </div>
            <h3 class="step-box-title">{{ t('home.step3Title') }}</h3>
            <p class="step-box-desc">
              {{ t('home.step3Desc') }}
            </p>
          </div>

          <div class="step-box">
            <div class="step-num">4</div>
            <div class="step-icon-circle">
              <HeartHandshake :size="24" />
            </div>
            <h3 class="step-box-title">{{ t('home.step4Title') }}</h3>
            <p class="step-box-desc">
              {{ t('home.step4Desc') }}
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Latest Cases Section -->
    <section class="section section-latest">
      <div class="container">
        <div class="section-title-wrap flex justify-between items-center mb-6">
          <div>
            <h2 class="section-heading">{{ t('cases.title') }}</h2>
            <p class="text-muted text-sm">{{ t('home.heroSubtitle') }}</p>
          </div>
          <router-link to="/cases" class="view-all-link">
            {{ t('home.viewAllCases') }}
          </router-link>
        </div>

        <div v-if="loading" class="text-center py-8 text-muted">
          {{ t('common.loading') }}
        </div>

        <div v-else-if="latestCases.length === 0" class="card text-center py-10">
          <p class="text-muted">{{ t('common.noResults') }}</p>
          <router-link to="/demande-aide" class="btn btn-primary btn-sm mt-3">
            {{ t('nav.needHelp') }}
          </router-link>
        </div>

        <div v-else class="grid grid-cols-1 sm-grid-cols-2 lg-grid-cols-3 gap-6">
          <div v-for="item in latestCases" :key="item.reference" class="card case-card">
            <div class="case-img-wrap" v-if="item.publicImageUrl">
              <img :src="item.publicImageUrl" :alt="item.title" class="case-img" />
            </div>
            <div class="case-body">
              <div class="case-meta flex justify-between items-center mb-2">
                <span class="case-cat">{{ getCategoryName(item) }}</span>
                <span class="case-loc">📍 {{ getWilayaName(item) }}</span>
              </div>
              <h3 class="case-title">{{ item.title }}</h3>
              <div class="case-footer flex justify-between items-center mt-4">
                <span class="badge" :class="getUrgencyBadge(item.urgency)">
                  {{ t(`urgency.${item.urgency}`) }}
                </span>
                <router-link :to="`/cases/${item.reference}`" class="btn btn-secondary btn-sm">
                  {{ t('cases.viewCase') }}
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Quote Banner -->
    <section class="section section-social">
      <div class="container">
        <div class="social-banner card text-center">
          <div class="social-content" style="max-width: 800px; margin: 0 auto;">
            <div class="social-tag">{{ t('home.quoteText') }}</div>
            <h2 class="social-title mt-3">{{ t('home.heroTitle') }}</h2>
            <p class="social-desc mt-2">
              {{ t('footer.quote') }}
            </p>
            <div class="social-actions mt-4 flex justify-center gap-4">
              <router-link to="/demande-aide" class="btn btn-primary">
                {{ t('home.ctaNeed') }}
              </router-link>
              <router-link to="/cases" class="btn btn-secondary">
                {{ t('home.ctaHelp') }}
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { 
  HeartHandshake, PlusCircle, Heart, ShieldCheck, Lock, CheckCircle, 
  AlertTriangle, FileEdit, Globe 
} from 'lucide-vue-next'
import apiClient from '@/api/client'

const { t, locale } = useI18n()

const urgentCases = ref([])
const latestCases = ref([])
const loading = ref(true)

function getCategoryName(item) {
  if (locale.value === 'ar' && item.categoryNameAr) return item.categoryNameAr
  return item.categoryNameFr || ''
}

function getWilayaName(item) {
  if (locale.value === 'ar' && item.wilayaNameAr) return item.wilayaNameAr
  return item.wilayaNameFr || ''
}

onMounted(async () => {
  try {
    const [urgRes, latRes] = await Promise.all([
      apiClient.get('/cases/urgent?limit=3'),
      apiClient.get('/cases/latest?limit=6')
    ])
    if (urgRes.success) urgentCases.value = urgRes.data
    if (latRes.success) latestCases.value = latRes.data
  } catch (err) {
    console.error('Erreur chargement accueil:', err)
  } finally {
    loading.value = false
  }
})

function formatNumber(num) {
  if (!num) return '0'
  return new Intl.NumberFormat('fr-FR').format(num)
}

function getUrgencyBadge(urgency) {
  switch (urgency) {
    case 'CRITICAL': return 'badge-urgent-critical'
    case 'HIGH': return 'badge-urgent-high'
    case 'MEDIUM': return 'badge-urgent-medium'
    default: return 'badge-urgent-low'
  }
}
</script>

<style scoped>
.hero-section {
  padding: 4.5rem 0 3.5rem 0;
  background: linear-gradient(180deg, #f0fdf4 0%, var(--bg-main) 100%);
  border-bottom: 1px solid var(--border-light);
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: #e8f5ed;
  color: var(--primary);
  padding: 0.4rem 1rem;
  border-radius: var(--radius-full);
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 1.25rem;
}

.hero-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.25rem;
}

.title-ar {
  font-family: var(--font-ar);
  font-size: 3rem;
  font-weight: 800;
  color: var(--primary);
  line-height: 1.1;
}

.title-fr {
  font-size: 1.85rem;
  font-weight: 700;
  color: var(--text-main);
  max-width: 780px;
}

.hero-lead {
  font-size: 1.15rem;
  color: var(--text-muted);
  max-width: 680px;
  margin: 0 auto 2rem auto;
  line-height: 1.6;
}

.hero-cta-group {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.25rem;
  flex-wrap: wrap;
  margin-bottom: 2.5rem;
}

.shadow-glow {
  box-shadow: 0 4px 20px rgba(27, 77, 62, 0.35);
}

.hero-reassurance {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
  font-size: 0.875rem;
  color: var(--text-muted);
}

.reassurance-item {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
}

.text-success { color: var(--status-completed); }

.section {
  padding: 4rem 0;
}

.section-urgent {
  background: #fffdfb;
}

.urgent-badge-header {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  color: #dc2626;
  font-size: 0.85rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.25rem;
}

.section-heading {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-main);
}

.section-subtitle {
  color: var(--text-muted);
  font-size: 1rem;
  max-width: 620px;
  margin: 0.5rem auto 0 auto;
}

.view-all-link {
  color: var(--primary);
  font-weight: 600;
  font-size: 0.95rem;
  transition: var(--transition);
}

.view-all-link:hover {
  transform: translateX(3px);
}

/* Case Card */
.case-card {
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.case-img-wrap {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.case-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.case-card:hover .case-img {
  transform: scale(1.04);
}

.case-float-badge {
  position: absolute;
  top: 10px;
  left: 10px;
}

.case-body {
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.case-meta {
  font-size: 0.8rem;
  color: var(--text-muted);
  font-weight: 500;
}

.case-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--text-main);
  line-height: 1.4;
  margin-bottom: auto;
}

.amount-val {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--primary);
}

.amount-label {
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-left: 0.25rem;
}

/* Steps Grid */
.steps-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 1.5rem;
}

@media (min-width: 640px) {
  .steps-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .steps-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.step-box {
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 2rem 1.5rem;
  position: relative;
  transition: var(--transition);
}

.step-box:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-light);
}

.step-num {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--border-medium);
  opacity: 0.6;
}

.step-icon-circle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--primary-subtle);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.25rem auto;
}

.step-box-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.5rem;
}

.step-box-desc {
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
}

/* Social Banner */
.social-banner {
  background: linear-gradient(135deg, #1b4d3e 0%, #133e26 100%);
  color: #ffffff;
  padding: 3rem;
  border-radius: var(--radius-lg);
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  align-items: center;
}

@media (min-width: 768px) {
  .social-banner {
    grid-template-columns: 2fr 1fr;
  }
}

.social-tag {
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: var(--secondary);
  margin-bottom: 0.5rem;
}

.social-title {
  font-size: 1.85rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
}

.social-desc {
  color: #c5d8cf;
  font-size: 1rem;
  max-width: 540px;
  line-height: 1.6;
}

.social-visual {
  display: flex;
  justify-content: center;
  color: rgba(255, 255, 255, 0.2);
}
</style>
