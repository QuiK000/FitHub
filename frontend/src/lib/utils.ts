import type { ComponentType, SVGProps } from 'react'
import i18n from '../i18n/config'

export function cn(
  ...inputs: Array<string | false | null | undefined>
): string {
  return inputs.filter(Boolean).join(' ')
}

export const formatEnum = (value: string) =>
  value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')

export const parseAppDate = (value?: string | null) => {
  if (!value) return null

  const normalizedValue = value.includes('T')
    ? value
    : value.replace(' ', 'T')
  const date = new Date(normalizedValue)

  return Number.isNaN(date.getTime()) ? null : date
}

export const getAppDateTimeMs = (value?: string | null) =>
  parseAppDate(value)?.getTime() ?? Number.NaN

export const toBackendDateTime = (value: string) => {
  if (!value) return value

  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return `${value} 00:00:00`
  }

  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) {
    return `${value.replace('T', ' ')}:00`
  }

  if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/.test(value)) {
    return value.replace('T', ' ')
  }

  const date = parseAppDate(value)
  if (!date) return value

  const pad = (part: number) => String(part).padStart(2, '0')

  return [
    `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`,
    `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`,
  ].join(' ')
}

export const toDateInputValue = (date = new Date()) => {
  const pad = (part: number) => String(part).padStart(2, '0')

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

export const formatDate = (value?: string | null) => {
  if (!value) return i18n.t('common:messages.noData')
  const date = parseAppDate(value)
  if (!date) return i18n.t('common:messages.noData')

  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(date)
}

export const formatDateShort = (value?: string | null) => {
  if (!value) return i18n.t('common:messages.noData')
  const date = parseAppDate(value)
  if (!date) return i18n.t('common:messages.noData')

  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
  }).format(date)
}

export const formatDateTime = (value?: string | null) => {
  if (!value) return i18n.t('common:messages.noData')
  const date = parseAppDate(value)
  if (!date) return i18n.t('common:messages.noData')

  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(date)
}

export const clampPercentage = (value: number) =>
  Math.max(0, Math.min(100, Number.isFinite(value) ? value : 0))

export const formatCurrency = (value: number | null | undefined, currency = 'USD') => {
  if (value == null) value = 0
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(value)
}

export type IconType = ComponentType<SVGProps<SVGSVGElement>>
