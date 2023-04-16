export interface BasePagination {
  currentPage: number
  pageSize: number
  totalItems: number
  searchContent: string
}

export const defaultPagination: BasePagination = {
  currentPage: 1,
  pageSize: 10,
  totalItems: 0,
  searchContent: ''
}
