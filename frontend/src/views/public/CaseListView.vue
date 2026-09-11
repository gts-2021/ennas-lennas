<template>
  <div class="case-list-page">
    <div class="container py-8">
      <!-- Title & Intro -->
      <div class="page-header text-center mb-8">
        <h1 class="page-title">{{ t('cases.title') }}</h1>
        <p class="page-subtitle">
          {{ t('cases.subtitle') }}
        </p>
      </div>

      <!-- Filter Controls -->
      <div class="filter-panel card mb-8">
        <div class="grid grid-cols-1 sm-grid-cols-2 lg-grid-cols-4 gap-4">
          <!-- Category -->
          <div class="form-group mb-0">
            <label class="form-label">{{ t('common.category') }}</label>
            <select v-model="filters.categoryId" @change="applyFilters" class="form-select">
              <option :value="null">{{ t('common.allCategories') }}</option>
              <option v-for="cat in referenceStore.categories" :key="cat.id" :value="cat.id">
                {{ locale === 'ar' && cat.nameAr ? cat.nameAr : cat.nameFr }}
              </option>
            </select>
          </div>

          <!-- Wilaya -->
          <div class="form-group mb-0">
            <label class="form-label">{{ t('common.wilaya') }}</label>
            <select v-model="filters.wilayaId" @change="applyFilters" class="form-select">
              <option :value="null">{{ t('common.allWilayas') }}</option>
              <option v-for="w in referenceStore.wilayas" :key="w.id" :value="w.id">
                {{ w.code }} - {{ locale === 'ar' && w.nameAr ? w.nameAr : w.nameFr }}
              </option>
            </select>
          </div>

          <!-- Urgency -->
          <div class="form-group mb-0">
            <label class="form-label">{{ t('common.urgency') }}</label>
            <select v-model="filters.urgency" @change="applyFilters" class="form-select">
              <option :value="null">{{ t('common.allUrgencies') }}</option>
              <option value="CRITICAL">{{ t('urgency.CRITICAL') }}</option>
              <option value="HIGH">{{ t('urgency.HIGH') }}</option>
              <option value="MEDIUM">{{ t('urgency.MEDIUM') }}</option>
              <option value="LOW">{{ t('urgency.LOW') }}</option>
            </select>
          </div>

          <!-- Actions -->
          <div class="flex items-end gap-2">
            <button class="btn btn-secondary flex-1" @click="resetFilters">
              <RotateCcw :size="16" />
              <span>{{ t('cases.resetFilters') }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Results Grid -->
      <div v-if="loading" class="text-center py-12 text-muted">
        {{ t('common.loading') }}
      </div>

      <div v-else-if="cases.length === 0" class="card empty-card text-center py-12">
        <Inbox :size="48" class="text-muted mb-3" />
        <h3 class="empty-title">{{ t('common.noResults') }}</h3>
        <p class="text-muted max-w-sm mx-auto mt-2">
          {{ t('cases.subtitle') }}
        </p>
        <button class="btn btn-primary btn-sm mt-4" @click="resetFilters">
          {{ t('cases.resetFilters') }}
        </button>
      </div>

      <div v-else>
        <div class="results-meta mb-4 flex justify-between items-center text-sm text-muted">
          <span>{{ totalElements }} {{ t('cases.resultsFound', { count: totalElements }) }}</span>
        </div>

        <div class="grid grid-cols-1 sm-grid-cols-2 lg-grid-cols-3 gap-6">
          <div v-for="item in cases" :key="item.reference" class="card case-card">
            <div class="case-img-wrap" v-if="item.publicImageUrl">
              <img :src="item.publicImageUrl" :alt="item.title" class="case-img" />
              <span class="badge case-float-badge" :class="getUrgencyBadge(item.urgency)">
                {{ t(`urgency.${item.urgency}`) }}
              </span>
            </div>
            <div class="case-body">
              <div class="case-meta flex justify-between items-center mb-2">
                <span class="case-cat">{{ getCategoryName(item) }}</span>
                <span class="case-loc">📍 {{ getWilayaName(item) }}</span>
              </div>

              <h3 class="case-title" :title="item.title">
                {{ item.title }}
              </h3>

              <div class="case-footer flex justify-between items-center mt-4">
                <div class="case-amount" v-if="item.amountNeeded">
                  <span class="amount-val">{{ formatNumber(item.amountNeeded) }} {{ t('common.dzd') }}</span>
                  <span class="amount-label">{{ t('cases.neededAmount') }}</span>
                </div>
                <div v-else class="case-amount text-muted text-sm">
                  {{ t('caseDetail.offerTypeDonation') }}
                </div>

                <router-link :to="`/cases/${item.reference}`" class="btn btn-primary btn-sm">
                  {{ t('cases.viewCase') }}
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination-bar mt-8 flex justify-center items-center gap-4">
          <button 
            class="btn btn-secondary btn-sm" 
            :disabled="currentPage === 0"
            @click="loadPage(currentPage - 1)"
          >
            ← {{ t('common.previous') }}
          </button>
          <span class="page-num text-sm text-muted">
            {{ t('common.page', { page: currentPage + 1, total: totalPages }) }}
          </span>
          <button 
            class="btn btn-secondary btn-sm" 
            :disabled="currentPage >= totalPages - 1"
            @click="loadPage(currentPage + 1)"
          >
            {{ t('common.next') }} →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Inbox, RotateCcw } from 'lucide-vue-next'
import { useReferenceStore } from '@/stores/reference'
import apiClient from '@/api/client'

const route = useRoute()
const referenceStore = useReferenceStore()
const { t, locale } = useI18n()

const cases = ref([])
const loading = ref(true)
const currentPage = ref(0)
const totalPages = ref(1)
const totalElements = ref(0)

const filters = reactive({
  categoryId: null,
  wilayaId: null,
  urgency: route.query.urgency || null
})

function getCategoryName(item) {
  if (locale.value === 'ar' && item.categoryNameAr) return item.categoryNameAr
  return item.categoryNameFr || ''
}

function getWilayaName(item) {
  if (locale.value === 'ar' && item.wilayaNameAr) return item.wilayaNameAr
  return item.wilayaNameFr || ''
}

onMounted(async () => {
  await Promise.all([
    referenceStore.fetchCategories(),
    referenceStore.fetchWilayas()
  ])
  await fetchCases(0)
})

async function fetchCases(page = 0) {
  loading.value = true
  try {
    let url = `/cases?page=${page}&size=12`
    if (filters.categoryId) url += `&categoryId=${filters.categoryId}`
    if (filters.wilayaId) url += `&wilayaId=${filters.wilayaId}`
    if (filters.urgency) url += `&urgency=${filters.urgency}`

    const res = await apiClient.get(url)
    if (res.success && res.data) {
      cases.value = res.data.content
      currentPage.value = res.data.page
      totalPages.value = res.data.totalPages
      totalElements.value = res.data.totalElements
    }
  } catch (err) {
    console.error('Erreur chargement catalogue:', err)
  } finally {
    loading.value = false
  }
}

function applyFilters() {
  fetchCases(0)
}

function resetFilters() {
  filters.categoryId = null
  filters.wilayaId = null
  filters.urgency = null
  fetchCases(0)
}

function loadPage(page) {
  fetchCases(page)
  window.scrollTo({ top: 180, behavior: 'smooth' })
}

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
.case-list-page {
  min-height: calc(100vh - 140px);
  background: var(--bg-main);
}

.py-8 { padding: 2rem 0; }
.mb-8 { margin-bottom: 2rem; }
.mb-4 { margin-bottom: 1rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-0 { margin-bottom: 0; }
.mt-4 { margin-top: 1rem; }
.mt-8 { margin-top: 2rem; }

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 0.5rem;
}

.page-subtitle {
  color: var(--text-muted);
  max-width: 680px;
  margin: 0 auto;
  font-size: 1rem;
}

.filter-panel {
  padding: 1.25rem 1.5rem;
  background: var(--bg-surface);
}

/* Card styling */
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

.empty-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-main);
}
</style>
