import { Link } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import type { IconType } from '../../lib/utils'

type EmptyStateProps = {
  icon: IconType
  title: string
  description: string
  actionLabel?: string
  to?: string
}

export const EmptyState = ({
  icon: Icon,
  title,
  description,
  actionLabel,
  to,
}: EmptyStateProps) => (
  <div className="flex min-h-48 flex-col items-center justify-center rounded-2xl border border-dashed border-border bg-muted/30 p-8 text-center">
    <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-background">
      <Icon className="h-6 w-6 text-muted-foreground" />
    </div>
    <p className="mt-4 text-sm font-semibold text-foreground">{title}</p>
    <p className="mt-1 max-w-sm text-sm text-muted-foreground">{description}</p>
    {actionLabel && to && (
      <Link
        to={to}
        className="mt-4 inline-flex w-fit items-center gap-1.5 text-sm font-semibold text-primary hover:underline"
      >
        {actionLabel}
        <ArrowRight className="h-4 w-4" />
      </Link>
    )}
  </div>
)
