<template>
  <div class="admin-layout">
    <div class="container py-8">
      <!-- Top Bar -->
      <div class="admin-topbar">
        <div>
          <h1 class="page-title">{{ t('admin.dashboardTitle') }}</h1>
          <p class="text-muted">{{ t('admin.dashboardSubtitle') }}</p>
        </div>
        <div class="flex gap-3">
          <router-link to="/admin/cases" class="btn btn-primary btn-sm">
            <Inbox :size="16" />
            <span>{{ t('admin.manageCases') }}</span>
          </router-link>
          <router-link to="/admin/help-offers" class="btn btn-secondary btn-sm">
            <HeartHandshake :size="16" />
            <span>{{ t('admin.helpOffersCount', { count: stats.newHelpOffers || 0 }) }}</span>
          </router-link>
        </div>
      </div>

      <!-- KPI Grid -->
      <div class="kpi-grid mt-6">
        <div class="kpi-card" @click="navigateWithFilter('')">
          <div class="kpi-icon-box blue">
            <Inbox :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiTotal') }}</span>
            <span class="kpi-value">{{ stats.totalReceived }}</span>
          </div>
        </div>

        <div class="kpi-card highlight-urgent" @click="navigateWithFilter('SUBMITTED')">
          <div class="kpi-icon-box amber">
            <Clock :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiSubmitted') }}</span>
            <span class="kpi-value">{{ stats.submitted }}</span>
          </div>
        </div>

        <div class="kpi-card" @click="navigateWithFilter('UNDER_REVIEW')">
          <div class="kpi-icon-box orange">
            <Activity :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiReview') }}</span>
            <span class="kpi-value">{{ stats.underReview }}</span>
          </div>
        </div>

        <div class="kpi-card" @click="navigateWithFilter('NEED_MORE_INFO')">
          <div class="kpi-icon-box purple">
            <HelpCircle :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiInfo') }}</span>
            <span class="kpi-value">{{ stats.needMoreInfo }}</span>
          </div>
        </div>

        <div class="kpi-card" @click="navigateWithFilter('PUBLISHED')">
          <div class="kpi-icon-box green">
            <Globe :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiPublished') }}</span>
            <span class="kpi-value">{{ stats.published }}</span>
          </div>
        </div>

        <div class="kpi-card" @click="navigateWithFilter('IN_PROGRESS')">
          <div class="kpi-icon-box indigo">
            <HeartHandshake :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiInProgress') }}</span>
            <span class="kpi-value">{{ stats.inProgress }}</span>
          </div>
        </div>

        <div class="kpi-card" @click="navigateWithFilter('COMPLETED')">
          <div class="kpi-icon-box emerald">
            <CheckCircle :size="22" />
          </div>
          <div class="kpi-info">
            <span class="kpi-label">{{ t('admin.kpiCompleted') }}</span>
            <span class="kpi-value">{{ stats.completed }}</span>
          </div>
        </div>
      </div>

      <!-- Quick Actions & Help Offers Preview -->
      <div class="dashboard-sections mt-8">
        <div class="card section-card">
          <div class="section-header">
            <h2 class="section-title">{{ t('admin.recentOffersTitle') }}</h2>
            <router-link to="/admin/help-offers" class="link-more">{{ t('admin.viewAll') }}</router-link>
          </div>

          <div v-if="loadingOffers" class="text-center py-6 text-muted">
            {{ t('common.loading') }}
          </div>
          <div v-else-if="recentOffers.length === 0" class="text-center py-8 text-muted">
            {{ t('admin.noRecentOffers') }}
          </div>
          <div v-else class="table-responsive">
            <table class="data-table">
              <thead>
                <tr>
                  <th>{{ t('common.date') }}</th>
                  <th>{{ t('caseDetail.refLabel') }}</th>
                  <th>{{ t('admin.requester') }}</th>
                  <th>{{ t('caseDetail.offerTypeLabel') }}</th>
                  <th>{{ t('common.status') }}</th>
                  <th>{{ t('common.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="offer in recentOffers" :key="offer.id">
                  <td>{{ formatDate(offer.createdAt) }}</td>
                  <td>
                    <strong>{{ offer.caseReference }}</strong>
                  </td>
                  <td>{{ offer.firstName }} {{ offer.lastName }} ({{ offer.phone }})</td>
                  <td>{{ offer.offerType }}</td>
                  <td>
                    <span class="badge" :class="getOfferBadgeClass(offer.status)">
                      {{ t(`offerStatus.${offer.status}`) }}
                    </span>
                  </td>
                  <td>
                    <router-link :to="`/admin/cases/${offer.caseId}`" class="btn btn-secondary btn-sm">
                      {{ t('common.details') }}
                    </router-link>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Inbox, Clock, HelpCircle, Globe, Activity, CheckCircle, HeartHandshake } from 'lucide-vue-next'
import apiClient from '@/api/client'

const router = useRouter()
const { t, locale } = useI18n()

const stats = ref({
  totalReceived: 0,
  submitted: 0,
  underReview: 0,
  needMoreInfo: 0,
  published: 0,
  inProgress: 0,
  completed: 0,
  totalHelpOffers: 0,
  newHelpOffers: 0
})

const recentOffers = ref([])
const loadingOffers = ref(false)

onMounted(async () => {
  await fetchStats()
  await fetchRecentOffers()
})

async function fetchStats() {
  try {
    const res = await apiClient.get('/admin/dashboard/stats')
    if (res.success) {
      stats.value = res.data
    }
  } catch (err) {
    console.error('Erreur chargement stats:', err)
  }
}

async function fetchRecentOffers() {
  loadingOffers.value = true
  try {
    const res = await apiClient.get('/admin/help-offers?size=5')
    if (res.success && res.data) {
      recentOffers.value = res.data.content
    }
  } catch (err) {
    console.error('Erreur chargement offres:', err)
  } finally {
    loadingOffers.value = false
  }
}

function navigateWithFilter(status) {
  if (status) {
    router.push({ path: '/admin/cases', query: { status } })
  } else {
    router.push('/admin/cases')
  }
}

function formatDate(isoStr) {
  if (!isoStr) return '-'
  return new Date(isoStr).toLocaleDateString(locale.value === 'ar' ? 'ar-DZ' : 'fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function getOfferBadgeClass(status) {
  switch (status) {
    case 'NEW': return 'badge-status-review'
    case 'CONTACTED': return 'badge-status-submitted'
    case 'ACCEPTED': return 'badge-status-inprogress'
    case 'COMPLETED': return 'badge-status-completed'
    default: return ''
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: calc(100vh - 140px);
  background: var(--bg-main);
}

.py-8 { padding: 2rem 0; }
.mt-6 { margin-top: 1.5rem; }
.mt-8 { margin-top: 2rem; }

.admin-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--primary);
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

@media (min-width: 768px) {
  .kpi-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1200px) {
  .kpi-grid {
    grid-template-columns: repeat(7, 1fr);
  }
}

.kpi-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 1.25rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  transition: var(--transition);
  box-shadow: var(--shadow-sm);
}

.kpi-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-light);
}

.kpi-icon-box {
  width: 46px;
  height: 46px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.kpi-icon-box.blue { background: #eff6ff; color: #2563eb; }
.kpi-icon-box.amber { background: #fffbeb; color: #d97706; }
.kpi-icon-box.orange { background: #fff7ed; color: #ea580c; }
.kpi-icon-box.purple { background: #faf5ff; color: #9333ea; }
.kpi-icon-box.green { background: #f0fdf4; color: #16a34a; }
.kpi-icon-box.indigo { background: #eef2ff; color: #4f46e5; }
.kpi-icon-box.emerald { background: #ecfdf5; color: #059669; }

.kpi-info {
  display: flex;
  flex-direction: column;
}

.kpi-label {
  font-size: 0.8rem;
  color: var(--text-muted);
  font-weight: 500;
}

.kpi-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-main);
  line-height: 1.2;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.25rem;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-main);
}

.link-more {
  color: var(--primary);
  font-size: 0.875rem;
  font-weight: 600;
}

.table-responsive {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.9rem;
}

.data-table th {
  padding: 0.75rem 1rem;
  background: var(--bg-subtle);
  color: var(--text-muted);
  font-weight: 600;
  border-bottom: 1px solid var(--border-medium);
}

.data-table td {
  padding: 0.85rem 1rem;
  border-bottom: 1px solid var(--border-light);
  color: var(--text-main);
}

.data-table tr:hover td {
  background: #f8faf9;
}
</style>
