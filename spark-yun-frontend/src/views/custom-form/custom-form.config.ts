export interface BreadCrumb {
    name: string;
    code: string;
    hidden?: boolean;
}

export interface Pagination {
    currentPage: number;
    pageSize: number;
    total: number;
}

export const PaginationParam: Pagination = {
    currentPage: 1,
    pageSize: 10,
    total: 0
}
