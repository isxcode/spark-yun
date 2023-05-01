export interface BasePagination {
  currentPage: number
  pageSize: number
  totalItems: number
  searchKeyWord: string
}

export const defaultPagination: BasePagination = {
  currentPage: 1,
  pageSize: 10,
  totalItems: 0,
  searchKeyWord: ''
}
