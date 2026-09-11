<template>
  <div class="admin-detail-page">
    <div class="container py-8">
      <!-- Back Navigation & Header Bar -->
      <div class="detail-topbar flex justify-between items-center flex-wrap gap-4 mb-6">
        <div class="flex items-center gap-3">
          <router-link to="/admin/cases" class="btn btn-secondary btn-sm">
            <ArrowLeft :size="16" />
            <span>{{ t('admin.backToCases') }}</span>
          </router-link>
          <span class="ref-pill">{{ caseData?.reference }}</span>
          <span class="badge" :class="getStatusBadge(caseData?.status)">
            {{ t('status.' + caseData?.status) || caseData?.status }}
          </span>
        </div>

        <!-- Workflow Action Bar -->
        <div class="workflow-actions flex gap-2 flex-wrap">
          <template v-if="caseData?.status === 'SUBMITTED'">
            <button class="btn btn-primary btn-sm" @click="handleTransition('review')">
              <Clock :size="16" />
              <span>{{ t('admin.takeUnderReview') }}</span>
            </button>
            <button class="btn btn-danger btn-sm" @click="openReasonModal('reject')">
              <XCircle :size="16" />
              <span>{{ t('admin.rejectCase') }}</span>
            </button>
          </template>

          <template v-if="caseData?.status === 'UNDER_REVIEW'">
            <button class="btn btn-secondary btn-sm" @click="openReasonModal('request-info')">
              <HelpCircle :size="16" />
              <span>{{ t('admin.requestMoreInfo') }}</span>
            </button>
            <button class="btn btn-primary btn-sm" @click="handleTransition('approve')">
              <CheckCircle :size="16" />
              <span>{{ t('admin.approveCase') }}</span>
            </button>
            <button class="btn btn-danger btn-sm" @click="openReasonModal('reject')">
              <XCircle :size="16" />
              <span>{{ t('admin.rejectCase') }}</span>
            </button>
          </template>

          <template v-if="caseData?.status === 'NEED_MORE_INFO'">
            <button class="btn btn-primary btn-sm" @click="handleTransition('review')">
              <RotateCcw :size="16" />
              <span>{{ t('admin.resumeReview') }}</span>
            </button>
          </template>

          <template v-if="caseData?.status === 'APPROVED'">
            <button class="btn btn-primary btn-sm btn-publish" @click="openPublishModal">
              <Globe :size="16" />
              <span>{{ t('admin.publishCase') }}</span>
            </button>
          </template>

          <template v-if="caseData?.status === 'PUBLISHED'">
            <button class="btn btn-secondary btn-sm" @click="handleTransition('in-progress')">
              <Activity :size="16" />
              <span>{{ t('admin.markInProgress') }}</span>
            </button>
            <button class="btn btn-primary btn-sm" @click="handleTransition('completed')">
              <CheckCircle :size="16" />
              <span>{{ t('admin.markCompleted') }}</span>
            </button>
            <button class="btn btn-secondary btn-sm" @click="openReasonModal('close')">
              <Archive :size="16" />
              <span>{{ t('admin.closeCase') }}</span>
            </button>
          </template>

          <template v-if="caseData?.status === 'IN_PROGRESS'">
            <button class="btn btn-primary btn-sm" @click="handleTransition('completed')">
              <CheckCircle :size="16" />
              <span>{{ t('admin.markCompleted') }}</span>
            </button>
            <button class="btn btn-secondary btn-sm" @click="openReasonModal('close')">
              <Archive :size="16" />
              <span>{{ t('admin.closeCase') }}</span>
            </button>
          </template>

          <template v-if="caseData?.status === 'COMPLETED'">
            <button class="btn btn-primary btn-sm" @click="openReasonModal('close')">
              <Archive :size="16" />
              <span>{{ t('admin.closePermanently') }}</span>
            </button>
          </template>
        </div>
      </div>

      <div v-if="loading" class="text-center py-12 text-muted">
        {{ t('admin.loadingCase') }}
      </div>

      <div v-else-if="caseData" class="split-view-grid">
        <!-- VOLET GAUCHE : DONNÉES PRIVÉES (CONFIDENTIELLES) -->
        <div class="private-panel card">
          <div class="panel-header-badge private-tag">
            <Lock :size="16" />
            <span>{{ t('admin.privatePanelTitle') }}</span>
          </div>

          <!-- Requester Details -->
          <div class="detail-section mt-4">
            <h3 class="section-title-sm">{{ t('admin.requesterAndContacts') }}</h3>
            <div class="info-row">
              <span class="info-label">{{ t('common.fullName') }} :</span>
              <span class="info-val font-semibold">{{ caseData.requester?.firstName }} {{ caseData.requester?.lastName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">{{ t('admin.directPhone') }}</span>
              <span class="info-val">
                <a :href="`tel:${caseData.requester?.phone}`" class="phone-link">
                  <Phone :size="14" />
                  {{ caseData.requester?.phone }}
                </a>
              </span>
            </div>
            <div class="info-row" v-if="caseData.requester?.email">
              <span class="info-label">{{ t('common.email') }} :</span>
              <span class="info-val">{{ caseData.requester?.email }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">{{ t('common.wilaya') }} :</span>
              <span class="info-val">
                {{ locale === 'ar' ? (caseData.communeNameAr || caseData.communeNameFr) : caseData.communeNameFr }} 
                ({{ locale === 'ar' ? (caseData.wilayaNameAr || caseData.wilayaNameFr) : caseData.wilayaNameFr }})
              </span>
            </div>
          </div>

          <!-- Raw Submission -->
          <div class="detail-section mt-4">
            <h3 class="section-title-sm">{{ t('admin.rawSubmission') }}</h3>
            <div class="raw-title-box">{{ caseData.rawTitle }}</div>
            <div class="raw-desc-box">{{ caseData.rawDescription }}</div>
          </div>

          <!-- Documents Justificatifs -->
          <div class="detail-section mt-4">
            <h3 class="section-title-sm">{{ t('admin.supportingDocs', { count: caseData.documents?.length || 0 }) }}</h3>
            <div v-if="caseData.documents?.length === 0" class="text-muted text-sm italic">
              {{ t('admin.noDocsUploaded') }}
            </div>
            <div v-else class="documents-list">
              <div v-for="doc in caseData.documents" :key="doc.id" class="doc-card">
                <div class="flex items-center gap-3">
                  <FileText :size="20" class="text-primary" />
                  <div>
                    <div class="doc-name">{{ doc.originalFilename }}</div>
                    <div class="doc-meta">{{ (doc.fileSize / 1024).toFixed(1) }} Ko • {{ doc.mimeType }}</div>
                  </div>
                </div>
                <div class="doc-actions">
                  <a 
                    v-if="doc.downloadUrl" 
                    :href="doc.downloadUrl" 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    class="btn btn-secondary btn-sm"
                  >
                    <ExternalLink :size="14" />
                    <span>{{ t('admin.viewDoc') }}</span>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- Internal Admin Notes -->
          <div class="detail-section mt-4">
            <h3 class="section-title-sm">{{ t('admin.internalNotesTitle') }}</h3>
            <textarea 
              v-model="editForm.internalNotes" 
              rows="3" 
              class="form-textarea"
              :placeholder="t('admin.internalNotesPlaceholder')"
            ></textarea>
            <button class="btn btn-secondary btn-sm mt-2" @click="saveChanges">
              {{ t('admin.saveNotes') }}
            </button>
          </div>
        </div>

        <!-- VOLET DROIT : DONNÉES PUBLIQUES (MODÉRATION & ÉDITION) -->
        <div class="public-panel card">
          <div class="panel-header-badge public-tag">
            <Globe :size="16" />
            <span>{{ t('admin.publicPanelTitle') }}</span>
          </div>

          <div class="public-edit-form mt-4">
            <div class="form-group">
              <label class="form-label">{{ t('admin.publicTitleLabel') }}</label>
              <input 
                type="text" 
                v-model="editForm.publicTitle" 
                class="form-input" 
                :placeholder="t('admin.publicTitlePlaceholder')" 
              />
              <span class="field-hint">{{ t('admin.publicTitleHint') }}</span>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('admin.publicDescLabel') }}</label>
              <textarea 
                v-model="editForm.publicDescription" 
                rows="6" 
                class="form-textarea" 
                :placeholder="t('admin.publicDescPlaceholder')"
              ></textarea>
            </div>

            <div class="grid sm-grid-cols-2 gap-4">
              <div class="form-group">
                <label class="form-label">{{ t('common.urgency') }}</label>
                <select v-model="editForm.urgency" class="form-select">
                  <option value="LOW">LOW ({{ t('urgency.LOW') }})</option>
                  <option value="MEDIUM">MEDIUM ({{ t('urgency.MEDIUM') }})</option>
                  <option value="HIGH">HIGH ({{ t('urgency.HIGH') }})</option>
                  <option value="CRITICAL">CRITICAL ({{ t('urgency.CRITICAL') }})</option>
                </select>
              </div>

              <div class="form-group">
                <label class="form-label">{{ t('admin.estimatedAmount') }}</label>
                <input 
                  type="number" 
                  v-model.number="editForm.amountNeeded" 
                  class="form-input" 
                  placeholder="Ex: 50000" 
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('admin.publicImageLabel') }}</label>
              <input 
                type="text" 
                v-model="editForm.publicImageUrl" 
                class="form-input" 
                placeholder="https://images.unsplash.com/photo-..." 
              />
              <span class="field-hint">{{ t('admin.publicImageHint') }}</span>
            </div>

            <div v-if="editForm.publicImageUrl" class="image-preview-box mb-4">
              <img :src="editForm.publicImageUrl" :alt="t('admin.publicPreview')" class="preview-img" />
            </div>

            <div class="flex justify-between items-center mt-6">
              <button class="btn btn-secondary" @click="saveChanges">
                <Save :size="16" />
                <span>{{ t('admin.saveChanges') }}</span>
              </button>

              <button 
                v-if="caseData.status === 'APPROVED'" 
                class="btn btn-primary" 
                @click="openPublishModal"
              >
                <Globe :size="16" />
                <span>{{ t('admin.publishNow') }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Motif / Complément / Clôture -->
      <div v-if="activeModal" class="modal-overlay" @click.self="activeModal = null">
        <div class="modal-content">
          <h3 class="modal-title">{{ modalTitle }}</h3>
          <p class="modal-desc">{{ modalDesc }}</p>

          <div class="form-group mt-4">
            <label class="form-label">{{ modalInputLabel }}</label>
            <textarea v-model="modalReason" rows="4" class="form-textarea" required></textarea>
          </div>

          <div class="flex justify-between mt-6">
            <button class="btn btn-secondary" @click="activeModal = null">{{ t('common.cancel') }}</button>
            <button class="btn btn-primary" @click="confirmModalAction">{{ t('common.confirm') }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { 
  ArrowLeft, Clock, XCircle, HelpCircle, CheckCircle, Globe, Activity, Archive, 
  RotateCcw, Lock, Phone, FileText, ExternalLink, Save 
} from 'lucide-vue-next'
import apiClient from '@/api/client'

const { t, locale } = useI18n()
const route = useRoute()
const caseId = route.params.id

const caseData = ref(null)
const loading = ref(true)

const editForm = reactive({
  publicTitle: '',
  publicDescription: '',
  publicImageUrl: '',
  urgency: 'MEDIUM',
  amountNeeded: null,
  internalNotes: ''
})

const activeModal = ref(null)
const modalTitle = ref('')
const modalDesc = ref('')
const modalInputLabel = ref('')
const modalReason = ref('')
let pendingAction = null

onMounted(async () => {
  await fetchCaseDetail()
})

async function fetchCaseDetail() {
  loading.value = true
  try {
    const res = await apiClient.get(`/admin/cases/${caseId}`)
    if (res.success && res.data) {
      caseData.value = res.data
      editForm.publicTitle = res.data.publicTitle || res.data.rawTitle || ''
      editForm.publicDescription = res.data.publicDescription || res.data.rawDescription || ''
      editForm.publicImageUrl = res.data.publicImageUrl || ''
      editForm.urgency = res.data.urgency || 'MEDIUM'
      editForm.amountNeeded = res.data.amountNeeded
      editForm.internalNotes = res.data.requester?.internalNotes || ''
    }
  } catch (err) {
    alert(err.message)
  } finally {
    loading.value = false
  }
}

async function saveChanges() {
  try {
    const res = await apiClient.patch(`/admin/cases/${caseId}`, editForm)
    if (res.success) {
      caseData.value = res.data
      alert(t('admin.changesSaved'))
    }
  } catch (err) {
    alert('Erreur: ' + err.message)
  }
}

async function handleTransition(endpoint, body = {}) {
  try {
    const res = await apiClient.post(`/admin/cases/${caseId}/${endpoint}`, body)
    if (res.success) {
      caseData.value = res.data
      alert(res.message || t('common.success'))
    }
  } catch (err) {
    alert('Erreur: ' + err.message)
  }
}

function openPublishModal() {
  if (!editForm.publicTitle || editForm.publicTitle.length < 5) {
    alert(t('admin.publishValidationTitle'))
    return
  }
  if (!editForm.publicDescription || editForm.publicDescription.length < 10) {
    alert(t('admin.publishValidationDesc'))
    return
  }

  if (confirm(t('admin.publishConfirm', { ref: caseData.value.reference }))) {
    handleTransition('publish', {
      publicTitle: editForm.publicTitle,
      publicDescription: editForm.publicDescription,
      publicImageUrl: editForm.publicImageUrl
    })
  }
}

function openReasonModal(action) {
  pendingAction = action
  modalReason.value = ''
  if (action === 'reject') {
    modalTitle.value = t('admin.modalRejectTitle')
    modalDesc.value = t('admin.modalRejectDesc')
    modalInputLabel.value = t('admin.modalRejectInput')
  } else if (action === 'request-info') {
    modalTitle.value = t('admin.modalInfoTitle')
    modalDesc.value = t('admin.modalInfoDesc')
    modalInputLabel.value = t('admin.modalInfoInput')
  } else if (action === 'close') {
    modalTitle.value = t('admin.modalCloseTitle')
    modalDesc.value = t('admin.modalCloseDesc')
    modalInputLabel.value = t('admin.modalCloseInput')
  }
  activeModal.value = true
}

async function confirmModalAction() {
  if (!modalReason.value.trim()) {
    alert(t('admin.fieldRequired'))
    return
  }

  activeModal.value = null
  await handleTransition(pendingAction, { reason: modalReason.value.trim() })
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
.admin-detail-page {
  min-height: calc(100vh - 140px);
  background: var(--bg-main);
}

.py-8 { padding: 2rem 0; }
.mb-6 { margin-bottom: 1.5rem; }
.mt-4 { margin-top: 1rem; }
.mt-6 { margin-top: 1.5rem; }
.mt-2 { margin-top: 0.5rem; }
.mb-4 { margin-bottom: 1rem; }

.ref-pill {
  font-family: monospace;
  font-weight: 700;
  font-size: 1.1rem;
  background: var(--primary-subtle);
  color: var(--primary);
  padding: 0.3rem 0.75rem;
  border-radius: var(--radius-sm);
  border: 1px solid rgba(27, 77, 62, 0.2);
}

.btn-publish {
  background-color: var(--status-published);
}

.split-view-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

@media (min-width: 1024px) {
  .split-view-grid {
    grid-template-columns: 1fr 1fr;
  }
}

.panel-header-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.85rem;
  border-radius: var(--radius-md);
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.private-tag {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}

.public-tag {
  background: #f0fdf4;
  color: #15803d;
  border: 1px solid #bbf7d0;
}

.section-title-sm {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.75rem;
  padding-bottom: 0.35rem;
  border-bottom: 1px solid var(--border-light);
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.35rem 0;
  font-size: 0.9rem;
}

.info-label {
  color: var(--text-muted);
}

.phone-link {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  color: var(--primary);
  font-weight: 600;
}

.raw-title-box {
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.raw-desc-box {
  background: var(--bg-subtle);
  padding: 0.85rem;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  line-height: 1.5;
  white-space: pre-wrap;
}

.documents-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.doc-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-subtle);
  padding: 0.75rem 1rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
}

.doc-name {
  font-size: 0.85rem;
  font-weight: 600;
}

.doc-meta {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.preview-img {
  width: 100%;
  max-height: 180px;
  object-fit: cover;
  border-radius: var(--radius-md);
  margin-top: 0.5rem;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 0.5rem;
}

.modal-desc {
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
}
</style>
