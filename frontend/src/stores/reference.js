import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '@/api/client'

export const useReferenceStore = defineStore('reference', () => {
  const categories = ref([])
  const wilayas = ref([])
  const communesCache = ref({}) // { [wilayaId]: [...] }
  const loading = ref(false)

  async function fetchCategories() {
    if (categories.value.length > 0) return categories.value
    try {
      const res = await apiClient.get('/categories')
      if (res.success) {
        categories.value = res.data
      }
      return categories.value
    } catch (err) {
      console.error('Erreur chargement categories:', err)
      return []
    }
  }

  async function fetchWilayas() {
    if (wilayas.value.length > 0) return wilayas.value
    try {
      const res = await apiClient.get('/locations/wilayas')
      if (res.success) {
        wilayas.value = res.data
      }
      return wilayas.value
    } catch (err) {
      console.error('Erreur chargement wilayas:', err)
      return []
    }
  }

  async function fetchCommunes(wilayaId) {
    if (!wilayaId) return []
    if (communesCache.value[wilayaId]) {
      return communesCache.value[wilayaId]
    }
    try {
      const res = await apiClient.get(`/locations/wilayas/${wilayaId}/communes`)
      if (res.success) {
        communesCache.value[wilayaId] = res.data
        return res.data
      }
      return []
    } catch (err) {
      console.error('Erreur chargement communes:', err)
      return []
    }
  }

  return {
    categories,
    wilayas,
    communesCache,
    loading,
    fetchCategories,
    fetchWilayas,
    fetchCommunes
  }
})
