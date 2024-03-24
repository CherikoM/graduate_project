const enum styleType {genre, style}
const enum styleDataType { genre = "genre", style = "style" }
const enum tagData { format = "format", form = "form", country = "country", decade = "decade" }
const enum format { CD, vinyl, cassette, digital, other }
const enum form { LP, EP, single, cooperation, other }
const enum artistType { Artist, Group }

type styleData = {
  id: number,
  name?: string,
  enName: string,
  otherName?: Array<string>,
  type: styleType,
  description: string,
  belong: Array<{id: number, name: string, enName: string}>,
  gmtCreated: Date
  gmtModified: Date
}

type tag = {
  value: string
  data: string
  name?: string
  enName?: string
  otherName?: Array<string>
}

type tagSearchQuery = {
  keyword?: string | Array<string> | null
  genre?: string | Array<String> | null
  style?: string | Array<string> | null
  format?: string | Array<string> | null
  form?: string | Array<string> | null
  country?: string | Array<string> | null
  decade?: string | Array<string> | null
  area: string
  order: number
  currentPage: number
  pageSize: number
}

type releaseData = {
  id: number,
  mainId: number | null,
  otherVersion: {
    id: number, 
    releaseDate: string, 
    isBase: boolean, 
    title: string
  }[],
  isBase: number,
  title: string,
  artist: Array<any> | string,
  picture: string | null,
  releaseLabel: Array<any> | string,
  format: number,
  form: number,
  country: string,
  releaseDate: Date,
  genre: Array<string> | string
  style: Array<string> | string
  tracklist: Array<any> | string
  credits: Array<any> | string
  supplement: string,
  catno: string | undefined
}

type artistData = {
  id: number,
  name: string,
  profile: string,
  type: number,
  realName: string,
  nickname: string,
  belong: Array<any> | string,
  official: Array<string> | string,
  picture: string
}

type labelData = {
  id: number
  name: string
  picture: string
  parentId: number
  parentName:string
  children: labelData[] | null
  profile: string
  official: string[] | string
  contact: string[] | string

}

export type{styleData, tag, tagSearchQuery, releaseData, artistData, labelData}
export { styleType, styleDataType, tagData, format, form, artistType }