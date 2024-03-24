import { defineStore } from 'pinia'
import { tagSearch } from '@/api/commonAPI'
import type { tagSearchQuery } from '@/api/dataInterfaces'

export const useSearchResultStore = defineStore('searchResult', {
  state: () => {
    return {
      tagSearch: {}
    }
  },
  getters: {
    
  },
  actions: {
    async getTagSearch(query: tagSearchQuery) {
      const result: any = await tagSearch(query)
    }
  }
})
