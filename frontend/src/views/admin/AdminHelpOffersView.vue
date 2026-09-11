<template>
  <div class="admin-page">
    <div class="container py-8">
      <!-- Header -->
      <div class="page-header flex justify-between items-center flex-wrap gap-4">
        <div>
          <h1 class="page-title">{{ t('admin.offersTitle') }}</h1>
          <p class="text-muted">{{ t('admin.offersSubtitle') }}</p>
        </div>
        <router-link to="/admin" class="btn btn-secondary btn-sm">
          <span>{{ t('admin.backToDashboard') }}</span>
        </router-link>
      </div>

      <!-- Filter bar -->
      <div class="filters-bar card mt-6">
        <div class="filter-group">
          <label class="filter-label">{{ t('admin.filterByStatus') }}</label>
          <select v-model="selectedStatus" @change="fetchOffers(0)" class="form-select status-select">
            <option value="">{{ t('admin.allOffers') }}</option>
            <option value="NEW">NEW ({{ t('offerStatus.NEW') }})</option>
            <option value="CONTACTED">CONTACTED ({{ t('offerStatus.CONTACTED') }})</option>
            <option value="ACCEPTED">ACCEPTED ({{ t('offerStatus.ACCEPTED') }})</option>
            <option value="COMPLETED">COMPLETED ({{ t('offerStatus.COMPLETED') }})</option>
            <option value="CANCELLED">CANCELLED ({{ t('offerStatus.CANCELLED') }})</option>
          </select>
        </div>
      </div>

      <!-- Offers Table -->
      <div class="card table-card mt-6">
        <div v-if="loading" class="text-center py-10 text-muted">
          {{ t('admin.loadingOffers') }}
        </div>

        <div v-else-if="offers.length === 0" class="text-center py-12">
          <HeartHandshake :size="48" class="empty-icon mb-3" />
          <p class="text-muted">{{ t('admin.noOffersFound') }}</p>
        </div>

        <div v-else class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>{{ t('admin.colDate') }}</th>
                <th>{{ t('admin.colCase') }}</th>
                <th>{{ t('admin.colBenefactor') }}</th>
                <th>{{ t('admin.colHelpType') }}</th>
                <th>{{ t('admin.colMessage') }}</th>
                <th>{{ t('admin.colStatus') }}</th>
                <th>{{ t('admin.colActions') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="offer in offers" :key="offer.id">
                <td>{{ formatDate(offer.createdAt) }}</td>
                <td>
                  <router-link :to="`/admin/cases/${offer.caseId}`" class="case-ref-link">
                    {{ offer.caseReference }}
                  </router-link>
                  <div class="cell-sub" :title="offer.caseTitle">{{ offer.caseTitle }}</div>
                </td>
                <td>
                  <div class="font-semibold">{{ offer.firstName }} {{ offer.lastName }}</div>
                  <div class="phone-link mt-1">
                    <a :href="`tel:${offer.phone}`">
                      <Phone :size="12" /> {{ offer.phone }}
                    </a>
                  </div>
                  <div v-if="offer.email" class="cell-sub">{{ offer.email }}</div>
                </td>
                <td>
                  <span class="badge badge-urgent-low">{{ offer.offerType }}</span>
                </td>
                <td>
                  <div class="offer-message-box">{{ offer.message }}</div>
                </td>
                <td>
                  <span class="badge" :class="getOfferBadge(offer.status)">
                    {{ t('offerStatus.' + offer.status) || offer.status }}
                  </span>
                </td>
                <td>
                  <button class="btn btn-secondary btn-sm" @click="openStatusModal(offer)">
                    {{ t('admin.processOffer') }}
                  </button>
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
            @click="fetchOffers(currentPage - 1)"
          >
            {{ t('common.previous') }}
          </button>
          <span class="page-indicator">
            {{ t('common.page', { page: currentPage + 1, total: totalPages }) }}
          </span>
          <button 
            class="btn btn-secondary btn-sm" 
            :disabled="currentPage >= totalPages - 1"
            @click="fetchOffers(currentPage + 1)"
          >
            {{ t('common.next') }}
          </button>
        </div>
      </div>

      <!-- Update Status Modal -->
      <div v-if="activeOffer" class="modal-overlay" @click.self="activeOffer = null">
        <div class="modal-content">
          <h3 class="modal-title">{{ t('admin.updateOfferModalTitle') }}</h3>
          <p class="modal-desc">
            {{ t('admin.benefactorLabel') }} <strong>{{ activeOffer.firstName }} {{ activeOffer.lastName }}</strong> ({{ activeOffer.phone }})
          </p>

          <div class="form-group mt-4">
            <label class="form-label">{{ t('admin.newStatusLabel') }}</label>
            <select v-model="updateForm.status" class="form-select">
              <option value="NEW">NEW ({{ t('offerStatus.NEW') }})</option>
              <option value="CONTACTED">CONTACTED ({{ t('offerStatus.CONTACTED') }})</option>
              <option value="ACCEPTED">ACCEPTED ({{ t('offerStatus.ACCEPTED') }})</option>
              <option value="COMPLETED">COMPLETED ({{ t('offerStatus.COMPLETED') }})</option>
              <option value="CANCELLED">CANCELLED ({{ t('offerStatus.CANCELLED') }})</option>
            </select>
          </div>

          <div class="form-group mt-4">
            <label class="form-label">{{ t('admin.followupNotesLabel') }}</label>
            <textarea 
              v-model="updateForm.adminNotes" 
              rows="3" 
              class="form-textarea" 
              :placeholder="t('admin.followupNotesPlaceholder')"
            ></textarea>
          </div>

          <div class="flex justify-between mt-6">
            <button class="btn btn-secondary" @click="activeOffer = null">{{ t('common.cancel') }}</button>
            <button class="btn btn-primary" @click="saveOfferStatus">{{ t('common.save') }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { HeartHandshake, Phone } from 'lucide-vue-next'
import apiClient from '@/api/client'

const { t, locale } = useI18n()

const offers = ref([])
const loading = ref(false)
const selectedStatus = ref('')
const currentPage = ref(0)
const totalPages = ref(1)

const activeOffer = ref(null)
const updateForm = reactive({
  status: 'CONTACTED',
  adminNotes: ''
})

onMounted(() => {
  fetchOffers(0)
})

async function fetchOffers(page = 0) {
  loading.value = true
  try {
    let url = `/admin/help-offers?page=${page}&size=15`
    if (selectedStatus.value) {
      url += `&status=${selectedStatus.value}`
    }
    const res = await apiClient.get(url)
    if (res.success && res.data) {
      offers.value = res.data.content
      currentPage.value = res.data.page
      totalPages.value = res.data.totalPages
    }
  } catch (err) {
    console.error('Erreur chargement offres:', err)
  } finally {
    loading.value = false
  }
}

function openStatusModal(offer) {
  activeOffer.value = offer
  updateForm.status = offer.status
  updateForm.adminNotes = offer.adminNotes || ''
}

async function saveOfferStatus() {
  try {
    const res = await apiClient.patch(`/admin/help-offers/${activeOffer.value.id}/status`, updateForm)
    if (res.success) {
      activeOffer.value = null
      await fetchOffers(currentPage.value)
    }
  } catch (err) {
    alert('Erreur: ' + err.message)
  }
}

function formatDate(isoStr) {
  if (!isoStr) return '-'
  return new Date(isoStr).toLocaleDateString(locale.value === 'ar' ? 'ar-DZ' : 'fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function getOfferBadge(status) {
  switch (status) {
    case 'NEW': return 'badge-status-review'
    case 'CONTACTED': return 'badge-status-submitted'
    case 'ACCEPTED': return 'badge-status-inprogress'
    case 'COMPLETED': return 'badge-status-completed'
    case 'CANCELLED': return 'badge-urgent-critical'
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

.case-ref-link {
  font-weight: 700;
  color: var(--primary);
  font-family: monospace;
}

.cell-sub {
  font-size: 0.78rem;
  color: var(--text-muted);
  max-width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.offer-message-box {
  max-width: 250px;
  font-size: 0.85rem;
  line-height: 1.4;
  color: var(--text-main);
}

.phone-link a {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  color: var(--primary);
  font-size: 0.825rem;
  font-weight: 600;
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
</style>
