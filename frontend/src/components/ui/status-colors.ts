export const membershipStatusColors: Record<string, string> = {
  ACTIVE: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
  EXPIRED: 'bg-muted text-muted-foreground',
  FROZEN: 'bg-blue-500/10 text-blue-600 dark:text-blue-400',
  CANCELLED: 'bg-red-500/10 text-red-600 dark:text-red-400',
  CREATED: 'bg-amber-500/10 text-amber-600 dark:text-amber-400',
}

export const sessionStatusColors: Record<string, string> = {
  SCHEDULED: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
  COMPLETED: 'bg-muted text-muted-foreground',
  CANCELLED: 'bg-red-500/10 text-red-600 dark:text-red-400',
  IN_PROGRESS: 'bg-blue-500/10 text-blue-600 dark:text-blue-400',
}

export const paymentStatusColors: Record<string, string> = {
  PAID: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
  PENDING: 'bg-amber-500/10 text-amber-600 dark:text-amber-400',
  COMPLETED: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
  FAILED: 'bg-red-500/10 text-red-600 dark:text-red-400',
}

export const assignmentStatusColors: Record<string, string> = {
  ASSIGNED: 'bg-amber-500/10 text-amber-600 dark:text-amber-400',
  IN_PROGRESS: 'bg-blue-500/10 text-blue-600 dark:text-blue-400',
  COMPLETED: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
  CANCELLED: 'bg-red-500/10 text-red-600 dark:text-red-400',
}
