<template>
  <div class="wizard-page">
    <div class="container wizard-container">
      <div class="wizard-card">
        <!-- Header -->
        <div class="wizard-header text-center">
          <span class="badge badge-status-published">
            <ShieldCheck :size="14" />
            {{ t('wizard.privacyPledge') }}
          </span>
          <h1 class="wizard-title">{{ t('wizard.pageTitle') }}</h1>
          <p class="wizard-subtitle">
            {{ t('wizard.pageSubtitle') }}
          </p>
        </div>

        <!-- Stepper Indicator -->
        <div class="stepper-header" v-if="currentStep < 5">
          <div 
            v-for="(step, idx) in steps" 
            :key="idx"
            class="step-item"
            :class="{ active: currentStep === idx + 1, completed: currentStep > idx + 1 }"
          >
            <div class="step-circle">
              <Check v-if="currentStep > idx + 1" :size="16" />
              <span v-else>{{ idx + 1 }}</span>
            </div>
            <span class="step-label">{{ step.title }}</span>
          </div>
        </div>

        <!-- Step 1: Catégorie / Type de besoin -->
        <div v-if="currentStep === 1" class="step-content">
          <h2 class="step-heading">{{ t('wizard.step1Title') }}</h2>
          <p class="step-desc">{{ t('wizard.step1Subtitle') }}</p>

          <div class="categories-grid">
            <div 
              v-for="cat in referenceStore.categories" 
              :key="cat.id"
              class="category-option"
              :class="{ selected: form.categoryId === cat.id }"
              @click="form.categoryId = cat.id"
            >
              <div class="cat-icon-box">
                <component :is="getCategoryIcon(cat.code)" :size="24" />
              </div>
              <div class="cat-names">
                <div class="cat-name-fr">{{ cat.nameFr }}</div>
                <div class="cat-name-ar">{{ cat.nameAr }}</div>
              </div>
            </div>
          </div>
          <div v-if="errors.categoryId" class="form-error mt-2">{{ errors.categoryId }}</div>

          <div class="step-actions">
            <div></div>
            <button class="btn btn-primary" @click="nextStep(1)">
              <span>{{ t('common.next') }}</span>
              <ArrowRight :size="18" />
            </button>
          </div>
        </div>

        <!-- Step 2: Coordonnées privées -->
        <div v-if="currentStep === 2" class="step-content">
          <h2 class="step-heading">{{ t('wizard.step2Title') }}</h2>
          <p class="step-desc">{{ t('wizard.step2Subtitle') }}</p>

          <div class="grid sm-grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">{{ t('common.firstName') }} <span class="required">*</span></label>
              <input type="text" v-model="form.firstName" class="form-input" placeholder="Mohamed" />
              <div v-if="errors.firstName" class="form-error">{{ errors.firstName }}</div>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('common.lastName') }} <span class="required">*</span></label>
              <input type="text" v-model="form.lastName" class="form-input" placeholder="Brahimi" />
              <div v-if="errors.lastName" class="form-error">{{ errors.lastName }}</div>
            </div>
          </div>

          <div class="grid sm-grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">{{ t('common.phone') }} <span class="required">*</span></label>
              <input 
                type="tel" 
                v-model="form.phone" 
                class="form-input" 
                placeholder="0550123456" 
              />
              <span class="field-hint">05, 06, 07...</span>
              <div v-if="errors.phone" class="form-error">{{ errors.phone }}</div>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('common.email') }}</label>
              <input type="email" v-model="form.email" class="form-input" placeholder="exemple@email.com" />
              <div v-if="errors.email" class="form-error">{{ errors.email }}</div>
            </div>
          </div>

          <div class="grid sm-grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">{{ t('common.wilaya') }} <span class="required">*</span></label>
              <select v-model="form.wilayaId" class="form-select" @change="onWilayaChange">
                <option :value="null" disabled>{{ t('common.allWilayas') }}</option>
                <option v-for="w in referenceStore.wilayas" :key="w.id" :value="w.id">
                  {{ w.code }} - {{ locale === 'ar' && w.nameAr ? w.nameAr : w.nameFr }}
                </option>
              </select>
              <div v-if="errors.wilayaId" class="form-error">{{ errors.wilayaId }}</div>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('common.commune') }} <span class="required">*</span></label>
              <select v-model="form.communeId" class="form-select" :disabled="!form.wilayaId || availableCommunes.length === 0">
                <option :value="null" disabled>
                  {{ !form.wilayaId ? t('common.wilaya') : t('common.commune') }}
                </option>
                <option v-for="c in availableCommunes" :key="c.id" :value="c.id">
                  {{ locale === 'ar' && c.nameAr ? c.nameAr : c.nameFr }}
                </option>
              </select>
              <div v-if="errors.communeId" class="form-error">{{ errors.communeId }}</div>
            </div>
          </div>

          <div class="step-actions">
            <button class="btn btn-secondary" @click="currentStep--">
              <ArrowLeft :size="18" />
              <span>{{ t('common.back') }}</span>
            </button>
            <button class="btn btn-primary" @click="nextStep(2)">
              <span>{{ t('common.next') }}</span>
              <ArrowRight :size="18" />
            </button>
          </div>
        </div>

        <!-- Step 3: Description de la situation -->
        <div v-if="currentStep === 3" class="step-content">
          <h2 class="step-heading">{{ t('wizard.step3Title') }}</h2>
          <p class="step-desc">{{ t('wizard.step3Subtitle') }}</p>

          <div class="form-group">
            <label class="form-label">{{ t('wizard.titleLabel') }} <span class="required">*</span></label>
            <input 
              type="text" 
              v-model="form.title" 
              class="form-input" 
              :placeholder="t('wizard.titlePlaceholder')" 
            />
            <div v-if="errors.title" class="form-error">{{ errors.title }}</div>
          </div>

          <div class="form-group">
            <label class="form-label">{{ t('wizard.descLabel') }} <span class="required">*</span></label>
            <textarea 
              v-model="form.description" 
              rows="5" 
              class="form-textarea" 
              :placeholder="t('wizard.descPlaceholder')"
            ></textarea>
            <div v-if="errors.description" class="form-error">{{ errors.description }}</div>
          </div>

          <div class="grid sm-grid-cols-2 gap-4">
            <div class="form-group">
              <label class="form-label">{{ t('wizard.urgencyTitle') }}</label>
              <select v-model="form.urgency" class="form-select">
                <option value="LOW">{{ t('urgency.LOW') }}</option>
                <option value="MEDIUM">{{ t('urgency.MEDIUM') }}</option>
                <option value="HIGH">{{ t('urgency.HIGH') }}</option>
                <option value="CRITICAL">{{ t('urgency.CRITICAL') }}</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">{{ t('wizard.amountLabel') }}</label>
              <input 
                type="number" 
                v-model.number="form.amountNeeded" 
                class="form-input" 
                placeholder="40000" 
                min="0"
                step="1000"
              />
            </div>
          </div>

          <div class="step-actions">
            <button class="btn btn-secondary" @click="currentStep--">
              <ArrowLeft :size="18" />
              <span>{{ t('common.back') }}</span>
            </button>
            <button class="btn btn-primary" @click="nextStep(3)">
              <span>{{ t('common.next') }}</span>
              <ArrowRight :size="18" />
            </button>
          </div>
        </div>

        <!-- Step 4: Documents justificatifs -->
        <div v-if="currentStep === 4" class="step-content">
          <h2 class="step-heading">{{ t('wizard.step4Title') }}</h2>
          <p class="step-desc">
            {{ t('wizard.step4Subtitle') }}
          </p>

          <div class="upload-dropzone" @dragover.prevent @drop.prevent="handleFileDrop">
            <UploadCloud :size="40" class="upload-icon" />
            <div class="upload-title">{{ t('wizard.dropzoneText') }}</div>
            <div class="upload-hint">{{ t('wizard.fileFormats') }}</div>
            <input 
              type="file" 
              multiple 
              accept=".pdf,image/jpeg,image/png,image/webp" 
              ref="fileInput" 
              class="hidden-file-input"
              @change="handleFileSelect"
            />
            <button type="button" class="btn btn-secondary btn-sm mt-3" @click="$refs.fileInput.click()">
              {{ t('wizard.browseFiles') }}
            </button>
          </div>

          <!-- File Preview List -->
          <div v-if="selectedFiles.length > 0" class="file-list">
            <div v-for="(file, idx) in selectedFiles" :key="idx" class="file-chip">
              <FileText :size="18" class="file-icon" />
              <div class="file-info">
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">{{ (file.size / 1024).toFixed(1) }} Ko</span>
              </div>
              <button type="button" class="file-remove" @click="removeFile(idx)" title="Retirer ce fichier">
                <X :size="16" />
              </button>
            </div>
          </div>

          <div v-if="submitError" class="alert-box error mt-4">
            <AlertCircle :size="18" />
            <span>{{ submitError }}</span>
          </div>

          <div class="step-actions">
            <button class="btn btn-secondary" @click="currentStep--">
              <ArrowLeft :size="18" />
              <span>{{ t('common.back') }}</span>
            </button>
            <button class="btn btn-primary" :disabled="isSubmitting" @click="submitHelpRequest">
              <span v-if="isSubmitting">{{ t('wizard.submitting') }}</span>
              <span v-else>{{ t('wizard.submitBtn') }}</span>
              <Send :size="18" />
            </button>
          </div>
        </div>

        <!-- Step 5: Confirmation rassurante -->
        <div v-if="currentStep === 5" class="step-content text-center confirmation-step">
          <div class="success-icon-wrapper">
            <CheckCircle2 :size="64" class="icon-success" />
          </div>

          <h2 class="confirm-title">{{ t('wizard.successTitle') }}</h2>
          
          <div class="ref-badge-card">
            <div class="ref-label">{{ t('wizard.successRef') }}</div>
            <div class="ref-code">{{ submissionResult?.reference }}</div>
          </div>

          <div class="confirmation-notice">
            <p>
              {{ t('wizard.successDesc') }}
            </p>
          </div>

          <div class="confirm-actions">
            <router-link to="/" class="btn btn-primary">
              <Home :size="18" />
              <span>{{ t('wizard.backHome') }}</span>
            </router-link>
            <router-link to="/cases" class="btn btn-secondary">
              {{ t('nav.allCases') }}
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { 
  HeartPulse, Utensils, GraduationCap, Shirt, Home, Refrigerator, Car, HelpCircle,
  ShieldCheck, Check, ArrowRight, ArrowLeft, UploadCloud, FileText, X, AlertCircle, Send, CheckCircle2
} from 'lucide-vue-next'
import { useReferenceStore } from '@/stores/reference'
import apiClient from '@/api/client'

const { t, locale } = useI18n()
const referenceStore = useReferenceStore()

const currentStep = ref(1)
const isSubmitting = ref(false)
const submitError = ref(null)
const submissionResult = ref(null)
const availableCommunes = ref([])
const selectedFiles = ref([])

const steps = computed(() => [
  { title: t('wizard.step1') },
  { title: t('wizard.step2') },
  { title: t('wizard.step3') },
  { title: t('wizard.step4') }
])

const form = reactive({
  categoryId: null,
  firstName: '',
  lastName: '',
  phone: '',
  email: '',
  wilayaId: null,
  communeId: null,
  title: '',
  description: '',
  urgency: 'MEDIUM',
  amountNeeded: null
})

const errors = reactive({})

onMounted(async () => {
  await referenceStore.fetchCategories()
  await referenceStore.fetchWilayas()
})

async function onWilayaChange() {
  form.communeId = null
  if (form.wilayaId) {
    availableCommunes.value = await referenceStore.fetchCommunes(form.wilayaId)
  } else {
    availableCommunes.value = []
  }
}

function getCategoryIcon(code) {
  switch (code) {
    case 'HEALTH': return HeartPulse
    case 'FOOD': return Utensils
    case 'EDUCATION': return GraduationCap
    case 'CLOTHING': return Shirt
    case 'HOUSING': return Home
    case 'EQUIPMENT': return Refrigerator
    case 'TRANSPORT': return Car
    default: return HelpCircle
  }
}

function handleFileSelect(e) {
  const files = Array.from(e.target.files)
  addFiles(files)
}

function handleFileDrop(e) {
  const files = Array.from(e.dataTransfer.files)
  addFiles(files)
}

function addFiles(files) {
  for (const f of files) {
    if (f.size > 5 * 1024 * 1024) {
      alert(`Le fichier ${f.name} dépasse 5 Mo.`)
      continue
    }
    selectedFiles.value.push(f)
  }
}

function removeFile(index) {
  selectedFiles.value.splice(index, 1)
}

function validateStep(step) {
  Object.keys(errors).forEach(key => delete errors[key])

  if (step === 1) {
    if (!form.categoryId) {
      errors.categoryId = 'Veuillez sélectionner un type d\'aide.'
      return false
    }
  }

  if (step === 2) {
    if (!form.firstName.trim()) errors.firstName = 'Le prénom est requis.'
    if (!form.lastName.trim()) errors.lastName = 'Le nom est requis.'
    
    const phoneRegex = /^(0|\+213|00213)[5-7][0-9]{8}$/
    if (!form.phone.trim()) {
      errors.phone = 'Le numéro de téléphone est requis.'
    } else if (!phoneRegex.test(form.phone.trim().replace(/\s+/g, ''))) {
      errors.phone = 'Format algérien requis (ex: 0550123456, 0661123456, 0770123456).'
    }

    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      errors.email = 'Format d\'email invalide.'
    }

    if (!form.wilayaId) errors.wilayaId = 'Veuillez sélectionner une wilaya.'
    if (!form.communeId) errors.communeId = 'Veuillez sélectionner une commune.'

    return Object.keys(errors).length === 0
  }

  if (step === 3) {
    if (!form.title.trim() || form.title.trim().length < 5) {
      errors.title = 'Le titre doit comporter au moins 5 caractères.'
    }
    if (!form.description.trim() || form.description.trim().length < 10) {
      errors.description = 'La description doit comporter au moins 10 caractères.'
    }
    return Object.keys(errors).length === 0
  }

  return true
}

function nextStep(step) {
  if (validateStep(step)) {
    currentStep.value++
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

async function submitHelpRequest() {
  isSubmitting.value = true
  submitError.value = null

  try {
    const formData = new FormData()
    formData.append('categoryId', form.categoryId)
    formData.append('firstName', form.firstName.trim())
    formData.append('lastName', form.lastName.trim())
    formData.append('phone', form.phone.trim().replace(/\s+/g, ''))
    if (form.email) formData.append('email', form.email.trim())
    formData.append('wilayaId', form.wilayaId)
    formData.append('communeId', form.communeId)
    formData.append('title', form.title.trim())
    formData.append('description', form.description.trim())
    formData.append('urgency', form.urgency)
    if (form.amountNeeded) formData.append('amountNeeded', form.amountNeeded)

    for (const file of selectedFiles.value) {
      formData.append('documents', file)
    }

    const res = await apiClient.post('/help-requests', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (res.success) {
      submissionResult.value = res.data
      currentStep.value = 5
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
  } catch (err) {
    submitError.value = err.message || 'Échec de la soumission de votre demande.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.wizard-page {
  padding: 3rem 0;
  min-height: calc(100vh - 140px);
}

.wizard-container {
  max-width: 800px;
}

.wizard-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: 2.5rem;
}

.wizard-header {
  margin-bottom: 2rem;
}

.wizard-title {
  font-size: 1.85rem;
  font-weight: 700;
  color: var(--primary);
  margin: 0.75rem 0 0.5rem 0;
}

.wizard-subtitle {
  color: var(--text-muted);
  font-size: 0.95rem;
  max-width: 620px;
  margin: 0 auto;
}

.step-heading {
  font-size: 1.35rem;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.35rem;
}

.step-desc {
  color: var(--text-muted);
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

@media (min-width: 640px) {
  .categories-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.category-option {
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  padding: 1.25rem 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: var(--transition);
  background: var(--bg-surface);
}

.category-option:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.category-option.selected {
  border-color: var(--primary);
  background: var(--primary-subtle);
  box-shadow: 0 0 0 2px var(--primary);
}

.cat-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--bg-subtle);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.75rem;
  transition: var(--transition);
}

.category-option.selected .cat-icon-box {
  background: var(--primary);
  color: #ffffff;
}

.cat-name-fr {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-main);
}

.cat-name-ar {
  font-family: var(--font-ar);
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-top: 0.2rem;
}

.field-hint {
  font-size: 0.78rem;
  color: var(--text-muted);
  margin-top: 0.25rem;
}

.upload-dropzone {
  border: 2px dashed var(--border-medium);
  border-radius: var(--radius-lg);
  padding: 2.5rem 1.5rem;
  text-align: center;
  background: var(--bg-subtle);
  transition: var(--transition);
  cursor: pointer;
}

.upload-dropzone:hover {
  border-color: var(--primary);
  background: #f0fdf4;
}

.upload-icon {
  color: var(--primary);
  margin-bottom: 0.75rem;
}

.upload-title {
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 0.35rem;
}

.upload-hint {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.hidden-file-input {
  display: none;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 1.25rem;
}

.file-chip {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: var(--bg-subtle);
  padding: 0.6rem 1rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
}

.file-icon {
  color: var(--primary);
}

.file-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.file-name {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-main);
}

.file-size {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.file-remove {
  background: transparent;
  border: none;
  cursor: pointer;
  color: #ef4444;
  padding: 0.25rem;
  border-radius: var(--radius-sm);
}

.file-remove:hover {
  background: #fee2e2;
}

.alert-box {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.85rem 1.25rem;
  border-radius: var(--radius-md);
  font-size: 0.9rem;
}

.alert-box.error {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
}

.step-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 2.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-light);
}

/* Confirmation */
.confirmation-step {
  padding: 2rem 0;
}

.success-icon-wrapper {
  margin-bottom: 1.5rem;
}

.icon-success {
  color: var(--status-completed);
}

.confirm-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 1.5rem;
}

.ref-badge-card {
  display: inline-block;
  background: var(--primary-subtle);
  border: 1px solid rgba(27, 77, 62, 0.2);
  padding: 1rem 2rem;
  border-radius: var(--radius-md);
  margin-bottom: 1.75rem;
}

.ref-label {
  font-size: 0.85rem;
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.ref-code {
  font-size: 1.85rem;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: 0.08em;
  font-family: monospace;
}

.confirmation-notice {
  max-width: 580px;
  margin: 0 auto 2rem auto;
  line-height: 1.7;
}

.notice-muted {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-top: 0.75rem;
}

.confirm-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}
</style>
