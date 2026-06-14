type StatusPillProps = {
  label: string
}

export const StatusPill = ({ label }: StatusPillProps) => (
  <span className="inline-flex items-center rounded-full bg-primary/10 px-2.5 py-1 text-xs font-semibold text-primary">
    {label}
  </span>
)
