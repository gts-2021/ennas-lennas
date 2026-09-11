<template>
  <div class="case-detail-page">
    <div class="container py-8">
      <!-- Breadcrumb / Back -->
      <div class="mb-6">
        <router-link to="/cases" class="btn btn-secondary btn-sm">
          <ArrowLeft :size="16" />
          <span>{{ t('caseDetail.backToList') }}</span>
        </router-link>
      </div>

      <div v-if="loading" class="text-center py-12 text-muted">
        {{ t('common.loading') }}
      </div>

      <div v-else-if="error" class="card text-center py-12">
        <AlertCircle :size="48" class="text-danger mb-3" />
        <h2 class="text-lg font-bold text-main">{{ error }}</h2>
        <router-link to="/cases" class="btn btn-primary btn-sm mt-4">
          {{ t('caseDetail.backToList') }}
        </router-link>
      </div>

      <div v-else-if="caseItem" class="detail-container">
        <!-- Main Card -->
        <div class="card detail-card">
          <!-- Header Meta -->
          <div class="detail-header flex justify-between items-start flex-wrap gap-4 mb-4">
            <div>
              <div class="flex items-center gap-2 mb-2">
                <span class="ref-badge">{{ caseItem.reference }}</span>
                <span class="badge" :class="getUrgencyBadge(caseItem.urgency)">
                  {{ t(`urgency.${caseItem.urgency}`) }}
                </span>
                <span class="badge badge-status-published">
                  {{ t(`status.${caseItem.status}`) }}
                </span>
              </div>
              <h1 class="case-main-title">{{ caseItem.title }}</h1>
            </div>

            <div class="detail-quick-actions flex gap-2">
              <button class="btn btn-secondary btn-sm" @click="openShareModal">
                <Share2 :size="16" />
                <span>{{ t('common.share') }}</span>
              </button>
              <button class="btn btn-primary btn-sm" @click="showHelpModal = true">
                <Heart :size="16" />
                <span>{{ t('caseDetail.helpBtn') }}</span>
              </button>
            </div>
          </div>

          <!-- Featured Image if any -->
          <div v-if="caseItem.publicImageUrl" class="detail-img-wrap mb-6">
            <img :src="caseItem.publicImageUrl" :alt="caseItem.title" class="detail-img" />
          </div>

          <!-- Key Badges Strip -->
          <div class="meta-strip flex flex-wrap gap-4 mb-6">
            <div class="meta-item">
              <span class="meta-label">{{ t('common.category') }}</span>
              <span class="meta-val">🏷️ {{ getCategoryName(caseItem) }}</span>
            </div>

            <div class="meta-item">
              <span class="meta-label">{{ t('common.wilaya') }}</span>
              <span class="meta-val">📍 {{ getCommuneName(caseItem) }}, {{ getWilayaName(caseItem) }}</span>
            </div>

            <div class="meta-item" v-if="caseItem.amountNeeded">
              <span class="meta-label">{{ t('cases.neededAmount') }}</span>
              <span class="meta-val font-bold text-primary">{{ formatNumber(caseItem.amountNeeded) }} {{ t('common.dzd') }}</span>
            </div>

            <div class="meta-item">
              <span class="meta-label">{{ t('common.date') }}</span>
              <span class="meta-val">📅 {{ formatDate(caseItem.publishedAt) }}</span>
            </div>
          </div>

          <!-- Public Description -->
          <div class="detail-description-section mb-8">
            <h3 class="section-subtitle-sm">{{ t('caseDetail.descriptionTitle') }}</h3>
            <div class="description-text">
              {{ caseItem.description }}
            </div>
          </div>

          <!-- Bottom CTA Banner -->
          <div class="cta-banner card">
            <div class="cta-content">
              <h3 class="cta-title">{{ t('caseDetail.offerModalTitle') }}</h3>
              <p class="cta-desc">
                {{ t('caseDetail.offerModalSubtitle') }}
              </p>
            </div>
            <div class="cta-buttons">
              <button class="btn btn-primary btn-lg" @click="showHelpModal = true">
                <Heart :size="20" />
                <span>{{ t('caseDetail.helpBtn') }}</span>
              </button>
              <button class="btn btn-secondary btn-lg" @click="openShareModal">
                <Share2 :size="20" />
                <span>{{ t('caseDetail.shareTitle') }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- MODAL "JE VEUX AIDER" -->
      <div v-if="showHelpModal" class="modal-overlay" @click.self="showHelpModal = false">
        <div class="modal-content">
          <div class="modal-header mb-4">
            <span class="badge badge-status-published mb-1">{{ t('nav.brandAr') }}</span>
            <h2 class="modal-title">{{ t('caseDetail.offerModalTitle') }}</h2>
            <p class="modal-subtitle">
              {{ t('caseDetail.refLabel') }} <strong>{{ caseItem?.reference }}</strong> — {{ caseItem?.title }}
            </p>
          </div>

          <div v-if="offerSuccess" class="text-center py-6">
            <div class="success-icon mb-3">
              <CheckCircle :size="48" class="text-success" />
            </div>
            <h3 class="font-bold text-lg mb-2">{{ t('caseDetail.offerSuccessTitle') }}</h3>
            <p class="text-muted text-sm mb-4">
              {{ t('caseDetail.offerSuccessDesc') }}
            </p>
            <button class="btn btn-primary" @click="closeOfferModal">
              {{ t('common.close') }}
            </button>
          </div>

          <form v-else @submit.prevent="submitOffer">
            <div v-if="offerError" class="alert-box error mb-4">
              <AlertCircle :size="16" />
              <span>{{ offerError }}</span>
            </div>

            <div class="grid sm-grid-cols-2 gap-4">
              <div class="form-group">
                <label class="form-label">{{ t('common.firstName') }} <span class="required">*</span></label>
                <input type="text" v-model="offerForm.firstName" class="form-input" placeholder="Karim" required />
              </div>
              <div class="form-group">
                <label class="form-label">{{ t('common.lastName') }} <span class="required">*</span></label>
                <input type="text" v-model="offerForm.lastName" class="form-input" placeholder="Benali" required />
              </div>
            </div>

            <div class="grid sm-grid-cols-2 gap-4">
              <div class="form-group">
                <label class="form-label">{{ t('common.phone') }} <span class="required">*</span></label>
                <input type="tel" v-model="offerForm.phone" class="form-input" placeholder="0550123456" required />
              </div>
              <div class="form-group">
                <label class="form-label">{{ t('common.email') }}</label>
                <input type="email" v-model="offerForm.email" class="form-input" placeholder="karim@example.dz" />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('caseDetail.offerTypeLabel') }} <span class="required">*</span></label>
              <select v-model="offerForm.offerType" class="form-select" required>
                <option :value="t('caseDetail.offerTypeDonation')">{{ t('caseDetail.offerTypeDonation') }}</option>
                <option :value="t('caseDetail.offerTypeDirect')">{{ t('caseDetail.offerTypeDirect') }}</option>
                <option :value="t('caseDetail.offerTypeService')">{{ t('caseDetail.offerTypeService') }}</option>
                <option :value="t('caseDetail.offerTypeTransport')">{{ t('caseDetail.offerTypeTransport') }}</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('caseDetail.offerMessageLabel') }} <span class="required">*</span></label>
              <textarea 
                v-model="offerForm.message" 
                rows="3" 
                class="form-textarea" 
                :placeholder="t('caseDetail.offerMessagePlaceholder')" 
                required
              ></textarea>
            </div>

            <div class="flex justify-between items-center mt-6">
              <button type="button" class="btn btn-secondary" @click="showHelpModal = false">
                {{ t('common.cancel') }}
              </button>
              <button type="submit" class="btn btn-primary" :disabled="isSendingOffer">
                <span v-if="isSendingOffer">{{ t('common.loading') }}</span>
                <span v-else>{{ t('caseDetail.submitOffer') }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- MODAL PARTAGE SOCIAL (Instagram / Copie / Web Share) -->
      <div v-if="showShareModal" class="modal-overlay" @click.self="showShareModal = false">
        <div class="modal-content">
          <div class="modal-header mb-4">
            <h2 class="modal-title">{{ t('caseDetail.shareTitle') }}</h2>
            <p class="modal-subtitle">
              {{ t('caseDetail.shareDesc') }}
            </p>
          </div>

          <!-- Copy Link -->
          <div class="share-option-box mb-4">
            <div class="font-semibold text-sm mb-1">{{ t('common.copyLink') }} :</div>
            <div class="flex gap-2">
              <input type="text" :value="shareUrl" readonly class="form-input text-sm" />
              <button class="btn btn-secondary btn-sm" @click="copyLink">
                <Copy :size="16" />
                <span>{{ copiedLink ? t('common.linkCopied') : t('common.copyLink') }}</span>
              </button>
            </div>
          </div>

          <!-- Format Instagram Ready -->
          <div class="share-option-box mb-4">
            <div class="font-semibold text-sm mb-1">{{ t('caseDetail.copyStory') }} :</div>
            <textarea :value="instagramCaption" readonly rows="5" class="form-textarea text-sm"></textarea>
            <button class="btn btn-secondary btn-sm mt-2" @click="copyInstagramText">
              <Copy :size="16" />
              <span>{{ copiedInsta ? t('caseDetail.storyCopied') : t('caseDetail.copyStory') }}</span>
            </button>
          </div>

          <div class="flex justify-end mt-4">
            <button class="btn btn-primary btn-sm" @click="showShareModal = false">
              {{ t('common.close') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { 
  ArrowLeft, Heart, Share2, Copy, AlertCircle, CheckCircle 
} from 'lucide-vue-next'
import apiClient from '@/api/client'

const route = useRoute()
const reference = route.params.reference
const { t, locale } = useI18n()

const caseItem = ref(null)
const loading = ref(true)
const error = ref(null)

const showHelpModal = ref(false)
const showShareModal = ref(false)

const isSendingOffer = ref(false)
const offerSuccess = ref(false)
const offerError = ref(null)

const copiedLink = ref(false)
const copiedInsta = ref(false)

const offerForm = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  email: '',
  offerType: '',
  message: ''
})

function getCategoryName(item) {
  if (locale.value === 'ar' && item.categoryNameAr) return item.categoryNameAr
  return item.categoryNameFr || ''
}

function getWilayaName(item) {
  if (locale.value === 'ar' && item.wilayaNameAr) return item.wilayaNameAr
  return item.wilayaNameFr || ''
}

function getCommuneName(item) {
  if (locale.value === 'ar' && item.communeNameAr) return item.communeNameAr
  return item.communeNameFr || ''
}

onMounted(async () => {
  offerForm.offerType = t('caseDetail.offerTypeDonation')
  await fetchCase()
})

async function fetchCase() {
  loading.value = true
  error.value = null
  try {
    const res = await apiClient.get(`/cases/${reference}`)
    if (res.success && res.data) {
      caseItem.value = res.data
    }
  } catch (err) {
    error.value = err.message || t('common.error')
  } finally {
    loading.value = false
  }
}

async function submitOffer() {
  isSendingOffer.value = true
  offerError.value = null

  try {
    const res = await apiClient.post(`/cases/${reference}/help-offers`, offerForm)
    if (res.success) {
      offerSuccess.value = true
    }
  } catch (err) {
    offerError.value = err.message || t('common.error')
  } finally {
    isSendingOffer.value = false
  }
}

function closeOfferModal() {
  showHelpModal.value = false
  offerSuccess.value = false
  offerForm.firstName = ''
  offerForm.lastName = ''
  offerForm.phone = ''
  offerForm.email = ''
  offerForm.message = ''
}

const shareUrl = computed(() => window.location.href)

const instagramCaption = computed(() => {
  if (!caseItem.value) return ''
  const isAr = locale.value === 'ar'
  if (isAr) {
    return `📢 حالة تضامنية [${caseItem.value.reference}]\n` +
           `📍 الولاية : ${getWilayaName(caseItem.value)} (${getCommuneName(caseItem.value)})\n` +
           `🏷️ نوع الاحتياج : ${getCategoryName(caseItem.value)}\n` +
           (caseItem.value.amountNeeded ? `💰 التقدير : ${formatNumber(caseItem.value.amountNeeded)} دج\n` : '') +
           `\n« ${caseItem.value.title} »\n\n` +
           `تفاصيل التحقق وتقديم المساعدة المباشرة على :\n` +
           `${window.location.href}\n\n` +
           `#الناس_للناس #EnnasLennas #تضامن_الجزائر #الجزائر`
  }
  return `📢 CAS SOLIDAIRE [${caseItem.value.reference}]\n` +
         `📍 Wilaya : ${caseItem.value.wilayaNameFr} (${caseItem.value.communeNameFr})\n` +
         `🏷️ Besoin : ${caseItem.value.categoryNameFr}\n` +
         (caseItem.value.amountNeeded ? `💰 Estimation : ${formatNumber(caseItem.value.amountNeeded)} DA\n` : '') +
         `\n« ${caseItem.value.title} »\n\n` +
         `Détails vérifiés & proposition d'aide directe sur :\n` +
         `${window.location.href}\n\n` +
         `#الناس_للناس #EnnasLennas #SolidariteAlgerie #Algerie #DZ`
})

function openShareModal() {
  if (navigator.share) {
    navigator.share({
      title: caseItem.value.title,
      text: `${caseItem.value.reference} - Ennas Lennas`,
      url: window.location.href
    }).catch(() => {
      showShareModal.value = true
    })
  } else {
    showShareModal.value = true
  }
}

function copyLink() {
  navigator.clipboard.writeText(shareUrl.value)
  copiedLink.value = true
  setTimeout(() => copiedLink.value = false, 2500)
}

function copyInstagramText() {
  navigator.clipboard.writeText(instagramCaption.value)
  copiedInsta.value = true
  setTimeout(() => copiedInsta.value = false, 2500)
}

function formatNumber(num) {
  if (!num) return '0'
  return new Intl.NumberFormat('fr-FR').format(num)
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
</script>

<style scoped>
.case-detail-page {
  min-height: calc(100vh - 140px);
  background: var(--bg-main);
}

.py-8 { padding: 2rem 0; }
.mb-6 { margin-bottom: 1.5rem; }
.mb-4 { margin-bottom: 1rem; }
.mb-8 { margin-bottom: 2rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-1 { margin-bottom: 0.25rem; }
.mt-4 { margin-top: 1rem; }
.mt-6 { margin-top: 1.5rem; }
.mt-2 { margin-top: 0.5rem; }

.ref-badge {
  font-family: monospace;
  font-size: 0.95rem;
  font-weight: 700;
  background: var(--primary-subtle);
  color: var(--primary);
  padding: 0.25rem 0.65rem;
  border-radius: var(--radius-sm);
  border: 1px solid rgba(27, 77, 62, 0.2);
}

.case-main-title {
  font-size: 1.85rem;
  font-weight: 700;
  color: var(--text-main);
  line-height: 1.3;
}

.detail-img-wrap {
  max-height: 420px;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.detail-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.meta-strip {
  background: var(--bg-subtle);
  padding: 1rem 1.5rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
}

.meta-item {
  display: flex;
  flex-direction: column;
}

.meta-label {
  font-size: 0.75rem;
  color: var(--text-muted);
  text-transform: uppercase;
  font-weight: 600;
}

.meta-val {
  font-size: 0.95rem;
  font-weight: 500;
  margin-top: 0.15rem;
}

.section-subtitle-sm {
  font-size: 1.15rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.75rem;
}

.description-text {
  font-size: 1.05rem;
  line-height: 1.7;
  color: var(--text-main);
  white-space: pre-wrap;
}

.cta-banner {
  background: linear-gradient(135deg, #f0fdf4 0%, #e8f5ed 100%);
  border: 1px solid #bbf7d0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1.5rem;
  padding: 2rem;
}

.cta-title {
  font-size: 1.35rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 0.35rem;
}

.cta-desc {
  font-size: 0.95rem;
  color: var(--text-muted);
  max-width: 600px;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

/* Modals */
.modal-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--primary);
}

.modal-subtitle {
  font-size: 0.9rem;
  color: var(--text-muted);
}

.share-option-box {
  background: var(--bg-subtle);
  padding: 1rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
}
</style>
