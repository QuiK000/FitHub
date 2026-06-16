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

export const formatDate = (value?: string | null) => {
  if (!value) return i18n.t('common:messages.noData')
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(value))
}

export const formatDateShort = (value?: string | null) => {
  if (!value) return i18n.t('common:messages.noData')
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))
}

export const formatDateTime = (value?: string | null) => {
  if (!value) return i18n.t('common:messages.noData')
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(value))
}

export const clampPercentage = (value: number) =>
  Math.max(0, Math.min(100, Number.isFinite(value) ? value : 0))

export const formatCurrency = (value: number | null | undefined, currency = 'USD') => {
  if (value == null) return '0'
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(value)
}

export type IconType = ComponentType<SVGProps<SVGSVGElement>>

