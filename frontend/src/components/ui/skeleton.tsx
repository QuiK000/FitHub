type SkeletonCardProps = {
  className?: string
}

export const SkeletonCard = ({ className = '' }: SkeletonCardProps) => (
  <div className={`animate-pulse rounded-2xl border border-border bg-card ${className}`}>
    <div className="h-full rounded-2xl bg-muted/80" />
  </div>
)

export const SkeletonBlock = ({ className = '' }: SkeletonCardProps) => (
  <div className={`h-44 animate-pulse rounded-2xl bg-muted ${className}`} />
)

export const SkeletonLine = () => (
  <div className="h-14 animate-pulse rounded-xl bg-muted" />
)
