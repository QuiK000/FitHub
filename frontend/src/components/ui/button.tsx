import * as React from 'react'
import { cn } from '../../lib/utils'

export type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'default' | 'outline' | 'ghost'
  size?: 'default' | 'sm' | 'lg' | 'icon'
}

const baseStyles =
  'inline-flex items-center justify-center gap-2 rounded-xl text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500/80 focus-visible:ring-offset-2 focus-visible:ring-offset-slate-950 disabled:cursor-not-allowed disabled:opacity-70'

const variantStyles: Record<NonNullable<ButtonProps['variant']>, string> = {
  default:
    'bg-gradient-to-r from-emerald-400 via-cyan-400 to-sky-500 text-slate-950 shadow-soft-glow hover:brightness-110',
  outline:
    'border border-slate-800/80 bg-slate-900/80 text-slate-100 hover:border-emerald-500/70 hover:bg-slate-900',
  ghost: 'text-slate-300 hover:bg-slate-900/80 hover:text-slate-50',
}

const sizeStyles: Record<NonNullable<ButtonProps['size']>, string> = {
  default: 'h-10 px-4 py-2',
  sm: 'h-8 px-3 text-xs',
  lg: 'h-11 px-5 text-base',
  icon: 'h-9 w-9',
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = 'default', size = 'default', ...props }, ref) => {
    return (
      <button
        ref={ref}
        className={cn(
          baseStyles,
          variantStyles[variant],
          sizeStyles[size],
          className,
        )}
        {...props}
      />
    )
  },
)

Button.displayName = 'Button'

