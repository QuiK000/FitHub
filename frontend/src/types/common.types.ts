export type ISODateString = string
export type ISODateTimeString = string

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface MessageResponse {
  message: string
  timestamp: ISODateTimeString
}

export interface ValidationError {
  field: string
  code: string
  message: string
}

export interface ErrorResponse {
  message: string
  code: string
  validationErrors: ValidationError[]
}
