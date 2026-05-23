import type { ComponentType, SVGProps } from 'react'
import { ArrowRight } from 'lucide-react'

type ShellPageProps = {
  title: string
  description: string
  eyebrow?: string
  icon: ComponentType<SVGProps<SVGSVGElement>>
}

const ShellPage = ({
  title,
  description,
  eyebrow = 'Coming soon',
  icon: Icon,
}: ShellPageProps) => {
  return (
    <div className="mx-auto flex min-h-[60vh] max-w-4xl items-center">
      <section className="w-full rounded-2xl border border-border bg-card p-6 shadow-soft md:p-8">
        <div className="flex flex-col gap-6 md:flex-row md:items-start md:justify-between">
          <div>
            <div className="mb-5 inline-flex h-11 w-11 items-center justify-center rounded-xl bg-primary/10">
              <Icon className="h-5 w-5 text-primary" />
            </div>
            <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              {eyebrow}
            </p>
            <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
              {title}
            </h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-muted-foreground">
              {description}
            </p>
          </div>

          <div className="inline-flex items-center gap-2 rounded-xl border border-border bg-muted px-3 py-2 text-xs font-medium text-muted-foreground">
            Planned module
            <ArrowRight className="h-3.5 w-3.5" />
          </div>
        </div>
      </section>
    </div>
  )
}

export default ShellPage
