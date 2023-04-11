export interface BasePagination {
  currentPage: number
  pageSize: number
  totalItems: number
}

export const defaultPagination: BasePagination = {
  currentPage: 1,
  pageSize: 10,
  totalItems: 0
}
