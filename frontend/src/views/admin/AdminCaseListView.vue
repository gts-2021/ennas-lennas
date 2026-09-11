<template>
  <div class="admin-page">
    <div class="container py-8">
      <!-- Header -->
      <div class="page-header flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 class="page-title">{{ t('admin.listTitle') }}</h1>
          <p class="text-muted">{{ t('admin.listSubtitle') }}</p>
        </div>
        <router-link to="/admin" class="btn btn-secondary btn-sm">
          {{ t('admin.backToDashboard') }}
        </router-link>
      </div>

      <!-- Filters & Search Bar -->
      <div class="filters-bar card mt-6">
        <div class="search-box">
          <Search :size="18" class="search-icon" />
          <input 
            type="text" 
            v-model="searchTerm" 
            @input="onSearchInput"
            :placeholder="t('admin.searchAdminPlaceholder')" 
            class="search-input"
          />
        </div>

        <div class="filter-group">
          <label class="filter-label">{{ t('common.status') }} :</label>
          <select v-model="selectedStatus" @change="fetchCases(0)" class="form-select status-select">
            <option value="">{{ t('common.allStatuses') }}</option>
            <option value="SUBMITTED">{{ t('status.SUBMITTED') }}</option>
            <option value="UNDER_REVIEW">{{ t('status.UNDER_REVIEW') }}</option>
            <option value="NEED_MORE_INFO">{{ t('status.NEED_MORE_INFO') }}</option>
            <option value="APPROVED">{{ t('status.APPROVED') }}</option>
            <option value="PUBLISHED">{{ t('status.PUBLISHED') }}</option>
            <option value="IN_PROGRESS">{{ t('status.IN_PROGRESS') }}</option>
            <option value="COMPLETED">{{ t('status.COMPLETED') }}</option>
            <option value="CLOSED">{{ t('status.CLOSED') }}</option>
            <option value="REJECTED">{{ t('status.REJECTED') }}</option>
          </select>
        </div>
      </div>

      <!-- Cases Data Table -->
      <div class="card table-card mt-6">
        <div v-if="loading" class="loading-state text-center py-10 text-muted">
          {{ t('common.loading') }}
        </div>

        <div v-else-if="cases.length === 0" class="empty-state text-center py-12">
          <Inbox :size="48" class="empty-icon mb-3" />
          <p class="text-muted">{{ t('common.noResults') }}</p>
        </div>

        <div v-else class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>{{ t('caseDetail.refLabel') }}</th>
                <th>{{ t('wizard.titleLabel') }}</th>
                <th>{{ t('admin.requester') }}</th>
                <th>{{ t('common.wilaya') }}</th>
                <th>{{ t('common.category') }}</th>
                <th>{{ t('common.urgency') }}</th>
                <th>{{ t('common.status') }}</th>
                <th>{{ t('admin.receivedOn') }}</th>
                <th class="text-right">{{ t('common.actions') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in cases" :key="item.id">
                <td>
                  <span class="ref-code-table">{{ item.reference }}</span>
                </td>
                <td>
                  <div class="cell-title" :title="item.rawTitle">
                    {{ item.rawTitle }}
                  </div>
                </td>
                <td>
                  <span v-if="item.requester">
                    {{ item.requester.firstName }} {{ item.requester.lastName }}
                    <div class="cell-sub">{{ item.requester.phone }}</div>
                  </span>
                </td>
                <td>{{ item.wilayaNameFr }}</td>
                <td>{{ item.categoryNameFr }}</td>
                <td>
                  <span class="badge" :class="getUrgencyBadge(item.urgency)">
                    {{ t(`urgency.${item.urgency}`) }}
                  </span>
                </td>
                <td>
                  <span class="badge" :class="getStatusBadge(item.status)">
                    {{ t(`status.${item.status}`) }}
                  </span>
                </td>
                <td>{{ formatDate(item.createdAt) }}</td>
                <td class="text-right">
                  <router-link :to="`/admin/cases/${item.id}`" class="btn btn-primary btn-sm">
                    <span>{{ t('admin.inspect') }}</span>
                    <ArrowRight :size="14" />
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination-bar mt-4">
          <button 
            class="btn btn-secondary btn-sm" 
            :disabled="currentPage === 0"
            @click="fetchCases(currentPage - 1)"
          >
            ← {{ t('common.previous') }}
          </button>
          <span class="page-indicator">
            {{ t('common.page', { page: currentPage + 1, total: totalPages }) }}
          </span>
          <button 
            class="btn btn-secondary btn-sm" 
            :disabled="currentPage >= totalPages - 1"
            @click="fetchCases(currentPage + 1)"
          >
            {{ t('common.next') }} →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Search, Inbox, ArrowRight } from 'lucide-vue-next'
import apiClient from '@/api/client'

const route = useRoute()
const { t, locale } = useI18n()

const cases = ref([])
const loading = ref(false)
const searchTerm = ref('')
const selectedStatus = ref(route.query.status || '')
const currentPage = ref(0)
const totalPages = ref(1)

let searchTimeout = null

onMounted(() => {
  fetchCases(0)
})

watch(() => route.query.status, (newStatus) => {
  selectedStatus.value = newStatus || ''
  fetchCases(0)
})

function onSearchInput() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    fetchCases(0)
  }, 350)
}

async function fetchCases(page = 0) {
  loading.value = true
  try {
    let url = `/admin/cases?page=${page}&size=15`
    if (selectedStatus.value) {
      url += `&status=${selectedStatus.value}`
    }
    if (searchTerm.value.trim()) {
      url += `&search=${encodeURIComponent(searchTerm.value.trim())}`
    }

    const res = await apiClient.get(url)
    if (res.success && res.data) {
      cases.value = res.data.content
      currentPage.value = res.data.page
      totalPages.value = res.data.totalPages
    }
  } catch (err) {
    console.error('Erreur chargement cas admin:', err)
  } finally {
    loading.value = false
  }
}

function formatDate(isoStr) {
  if (!isoStr) return '-'
  return new Date(isoStr).toLocaleDateString(locale.value === 'ar' ? 'ar-DZ' : 'fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function getUrgencyBadge(urgency) {
  switch (urgency) {
    case 'CRITICAL': return 'badge-urgent-critical'
    case 'HIGH': return 'badge-urgent-high'
    case 'MEDIUM': return 'badge-urgent-medium'
    default: return 'badge-urgent-low'
  }
}

function getStatusBadge(status) {
  switch (status) {
    case 'SUBMITTED': return 'badge-status-submitted'
    case 'UNDER_REVIEW': return 'badge-status-review'
    case 'NEED_MORE_INFO': return 'badge-urgent-high'
    case 'APPROVED': return 'badge-status-inprogress'
    case 'PUBLISHED': return 'badge-status-published'
    case 'IN_PROGRESS': return 'badge-status-inprogress'
    case 'COMPLETED': return 'badge-status-completed'
    case 'CLOSED': return 'badge-status-submitted'
    default: return ''
  }
}
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 140px);
  background: var(--bg-main);
}

.py-8 { padding: 2rem 0; }
.mt-4 { margin-top: 1rem; }
.mt-6 { margin-top: 1.5rem; }
.mb-3 { margin-bottom: 0.75rem; }

.page-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--primary);
}

.filters-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
  padding: 1rem 1.5rem;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 260px;
}

.search-icon {
  position: absolute;
  left: 0.85rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
}

.search-input {
  width: 100%;
  padding: 0.6rem 0.85rem 0.6rem 2.5rem;
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-md);
  font-size: 0.9rem;
}

.search-input:focus {
  outline: none;
  border-color: var(--primary);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-muted);
}

.status-select {
  padding: 0.5rem 0.75rem;
  font-size: 0.85rem;
  min-width: 180px;
}

.table-card {
  padding: 0;
  overflow: hidden;
}

.table-responsive {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.875rem;
}

.data-table th {
  padding: 0.85rem 1rem;
  background: var(--bg-subtle);
  color: var(--text-muted);
  font-weight: 600;
  border-bottom: 1px solid var(--border-medium);
}

.data-table td {
  padding: 0.85rem 1rem;
  border-bottom: 1px solid var(--border-light);
  vertical-align: middle;
}

.data-table tr:hover td {
  background: #f8faf9;
}

.ref-code-table {
  font-weight: 700;
  color: var(--primary);
  font-family: monospace;
  font-size: 0.9rem;
}

.cell-title {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.cell-sub {
  font-size: 0.78rem;
  color: var(--text-muted);
}

.empty-icon {
  color: var(--border-medium);
}

.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--border-light);
}

.page-indicator {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.text-right { text-align: right; }
</style>
