type MeasurementTileProps = {
  label: string
  value: string | number
  unit?: string
}

export const MeasurementTile = ({ label, value, unit }: MeasurementTileProps) => (
  <div className="rounded-xl bg-muted px-3 py-2">
    <p className="text-xs text-muted-foreground">{label}</p>
    <p className="text-sm font-semibold text-foreground">
      {value}{unit ? ` ${unit}` : ''}
    </p>
  </div>
)
