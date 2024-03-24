import { defineStore } from 'pinia'
import type { styleData, tag } from '../api/dataInterfaces'
import { getAllGenreAPI, getAllStyleAPI, getGenreAndPrimary, getStyleGroupAPI } from '@/api/styleAPI'
import { styleDataType, styleType } from '@/api/dataInterfaces'

export const useStyleStore = defineStore('style', {
  state: () => {
    return {
      genreList: [],
      styleList: [],
      genreAndPrimaryList: [],
      styleGroup: [],
    }
  },
  getters: {
    genreTagList(state) {
      return state.genreList.map((style: styleData) => {
        const value: string = style.name || style.enName
        const result: tag = { value, data: styleDataType.genre, name: style.name, enName: style.enName, otherName: style.otherName }
        return result
      })
    },
    styleTagList(state) {
      return state.styleList.map((style: styleData) => {
        const value: string = style.name || style.enName
        const result: tag = { value, data: styleDataType.style, name: style.name, enName: style.enName, otherName: style.otherName }
        return result
      })
    },
    genrePrimaryTagList(state) {
      return state.genreAndPrimaryList.map((style: styleData) => {
        const value: string = style.name || style.enName
        //@ts-ignore
        const result: tag = { value, type: style.type, name: style.name, enName: style.enName, otherName: style.otherName }
        return result
      })
    },
  },
  actions: {
    async getGenreList() {
      if (this.genreList?.length <= 0) {
        const result: any = await getAllGenreAPI()
        if (result.isSuccess) {
          let data = result.data
          data = data.map((res: any) => {
            res.otherName = res.otherName ? JSON.parse(res.otherName) : null
            res.belong = res.belong ? JSON.parse(res.belong) : null
            //@ts-ignore
            res.type = "genre"
            return res
          })
          this.genreList = data
          return data
        }
        else return result.message
      } else {
        return this.genreList
      }
    },
    async getStyleList() {
      if (this.styleList?.length <= 0) {
        const result: any = await getAllStyleAPI()
        if (result.isSuccess) {
          let data = result.data
          data = data.map((res: any) => {
            res.otherName = res.otherName ? JSON.parse(res.otherName) : null
            res.belong = res.belong ? JSON.parse(res.belong) : null
            //@ts-ignore
            res.type = "style"
            return res
          })
          this.styleList = data
          return data
        }
        else return result.message
      } else {
        return this.styleList
      }
    },
    async getGenreAndPrimary() {
      if (this.genreAndPrimaryList?.length <= 0) {
        const result: any = await getGenreAndPrimary()
        if (result.isSuccess) {
          let data = result.data
          data = data.map((res: any) => {
            res.otherName = res.otherName ? JSON.parse(res.otherName) : null
            res.belong = res.belong ? JSON.parse(res.belong) : null
            if(res.type==0) {
              res.type = "genre"
            } else if(res.type==1) {
              res.type = "primary"
            }
            //@ts-ignore
            return res
          })
          this.genreAndPrimaryList = data
          return data
        }
        else return result.message
      } else {
        return this.genreAndPrimaryList
      }
    },
    async getStyleGroup() {
      if(this.styleGroup.length <= 0) {
        const result: any = await getStyleGroupAPI()
        if (result.isSuccess) {
          return result.data
        } else {
          return result.message
        }
      } else {
        return this.styleGroup
      }
    }
  }
})
