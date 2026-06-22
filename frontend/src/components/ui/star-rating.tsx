import { useState } from 'react'
import { Star } from 'lucide-react'
import { cn } from '../../lib/utils'

type StarRatingProps = {
  value: number
  onChange?: (value: number) => void
  max?: number
  size?: 'sm' | 'md'
  readonly?: boolean
}

export const StarRating = ({
  value,
  onChange,
  max = 5,
  size = 'md',
  readonly = false,
}: StarRatingProps) => {
  const [hovered, setHovered] = useState(0)

  const sizeClass = size === 'sm' ? 'h-4 w-4' : 'h-5 w-5'

  return (
    <div className="flex gap-0.5" onMouseLeave={() => setHovered(0)}>
      {Array.from({ length: max }, (_, i) => i + 1).map((star) => (
        <button
          key={star}
          type="button"
          disabled={readonly}
          onClick={() => onChange?.(star)}
          onMouseEnter={() => !readonly && setHovered(star)}
          className={cn(
            'transition-colors disabled:cursor-default',
            readonly && 'cursor-default',
          )}
        >
          <Star
            className={cn(
              sizeClass,
              (hovered || value) >= star
                ? 'fill-amber-400 text-amber-400'
                : 'fill-none text-muted-foreground',
            )}
          />
        </button>
      ))}
    </div>
  )
}
