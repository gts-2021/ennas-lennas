import { createI18n } from 'vue-i18n'
import fr from './locales/fr.json'
import ar from './locales/ar.json'

const savedLocale = localStorage.getItem('ennas_locale') || 'fr'

// Set initial document attributes
document.documentElement.setAttribute('lang', savedLocale)
document.documentElement.setAttribute('dir', savedLocale === 'ar' ? 'rtl' : 'ltr')

const i18n = createI18n({
  legacy: false, // Composition API mode
  locale: savedLocale,
  fallbackLocale: 'fr',
  messages: {
    fr,
    ar
  }
})

export function setAppLocale(locale) {
  if (locale !== 'fr' && locale !== 'ar') return
  i18n.global.locale.value = locale
  localStorage.setItem('ennas_locale', locale)
  document.documentElement.setAttribute('lang', locale)
  document.documentElement.setAttribute('dir', locale === 'ar' ? 'rtl' : 'ltr')
}

export function getCurrentLocale() {
  return i18n.global.locale.value
}

export default i18n
