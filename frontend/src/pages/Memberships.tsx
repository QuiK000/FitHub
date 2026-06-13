import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
  CalendarDays,
  CheckCircle2,
  CreditCard,
  PauseCircle,
  XCircle,
} from 'lucide-react'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import {
  getMyActiveMembership,
  getMyMembershipHistory,
  type MembershipResponse,
} from '../services/membership.service'

const Memberships = () => {
  const { t } = useTranslation('memberships')
  const [active, setActive] = useState<MembershipResponse | null>(null)
  const [history, setHistory] = useState<MembershipResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      setIsLoading(true)
      try {
        const [activeResult, historyResult] = await Promise.allSettled([
          getMyActiveMembership(),
          getMyMembershipHistory(),
        ])
        if (activeResult.status === 'fulfilled') setActive(activeResult.value)
        if (historyResult.status === 'fulfilled') setHistory(historyResult.value.memberships)
      } catch {
        // handled by Promise.allSettled
      } finally {
        setIsLoading(false)
      }
    }
    void load()
  }, [])

  const daysRemaining = active
    ? Math.max(0, Math.ceil((new Date(active.endDate).getTime() - Date.now()) / 86_400_000))
    : null

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('title')}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {t('title')}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            {t('subtitle')}
          </p>
        </div>
      </div>

      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-2">
          <div className="h-56 animate-pulse rounded-2xl bg-muted" />
          <div className="h-56 animate-pulse rounded-2xl bg-muted" />
        </div>
      ) : (
        <>
          <section className="grid gap-4 md:grid-cols-2">
            <Card className="overflow-hidden">
              <CardHeader>
                <CardTitle>Active Plan</CardTitle>
                <CardDescription>Your current membership status.</CardDescription>
              </CardHeader>
              <CardContent>
                {active ? (
                  <div className="space-y-4">
                    <div className="rounded-2xl bg-primary p-5 text-primary-foreground">
                      <p className="text-sm opacity-80">Plan type</p>
                      <div className="mt-3 flex items-end justify-between gap-4">
                        <div>
                          <p className="text-2xl font-bold">{formatEnum(active.type)}</p>
                          <p className="mt-1 text-sm opacity-80">
                            {daysRemaining} day{daysRemaining === 1 ? '' : 's'} remaining
                          </p>
                        </div>
                        <StatusIcon status={active.status} />
                      </div>
                    </div>
                    <div className="grid gap-3 text-sm sm:grid-cols-2">
                      <InfoTile
                        icon={CalendarDays}
                        label="Valid from"
                        value={formatDate(active.startDate)}
                      />
                      <InfoTile
                        icon={CalendarDays}
                        label="Valid until"
                        value={formatDate(active.endDate)}
                      />
                      <InfoTile
                        icon={CreditCard}
                        label="Status"
                        value={formatEnum(active.status)}
                      />
                      <InfoTile
                        icon={CheckCircle2}
                        label="Visits left"
                        value={active.visitsLeft === null ? 'Unlimited' : String(active.visitsLeft)}
                      />
                    </div>
                  </div>
                ) : (
                  <div className="flex min-h-40 flex-col items-center justify-center text-center">
                    <CreditCard className="h-8 w-8 text-muted-foreground/40" />
                    <p className="mt-3 text-sm font-semibold text-foreground">No active membership</p>
                    <p className="mt-1 text-sm text-muted-foreground">
                      Contact the gym to get started with a plan.
                    </p>
                  </div>
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Membership Summary</CardTitle>
                <CardDescription>Quick overview of your access.</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <SummaryRow
                    label="Total memberships"
                    value={history.length.toString()}
                  />
                  <SummaryRow
                    label="Active"
                    value={history.filter((m) => m.status === 'ACTIVE').length.toString()}
                  />
                  <SummaryRow
                    label="Expired"
                    value={history.filter((m) => m.status === 'EXPIRED').length.toString()}
                  />
                  <SummaryRow
                    label="Cancelled"
                    value={history.filter((m) => m.status === 'CANCELLED').length.toString()}
                  />
                </div>
              </CardContent>
            </Card>
          </section>

          {history.length > 0 && (
            <section>
              <h2 className="mb-4 text-lg font-semibold text-foreground">History</h2>
              <div className="divide-y divide-border overflow-hidden rounded-2xl border border-border">
                {history.map((membership) => (
                  <HistoryRow key={membership.id} membership={membership} />
                ))}
              </div>
            </section>
          )}
        </>
      )}
    </div>
  )
}

const StatusIcon = ({ status }: { status: string }) => {
  if (status === 'ACTIVE') return <CheckCircle2 className="h-8 w-8 opacity-90" />
  if (status === 'FROZEN') return <PauseCircle className="h-8 w-8 opacity-90" />
  return <XCircle className="h-8 w-8 opacity-90" />
}

const InfoTile = ({
  icon: Icon,
  label,
  value,
}: {
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>
  label: string
  value: string
}) => (
  <div className="rounded-xl bg-background px-3 py-3">
    <div className="flex items-center gap-2 text-xs text-muted-foreground">
      <Icon className="h-4 w-4" />
      {label}
    </div>
    <p className="mt-2 text-sm font-semibold text-foreground">{value}</p>
  </div>
)

const SummaryRow = ({ label, value }: { label: string; value: string }) => (
  <div className="flex items-center justify-between rounded-xl bg-muted px-4 py-3">
    <span className="text-sm text-muted-foreground">{label}</span>
    <span className="text-sm font-semibold text-foreground">{value}</span>
  </div>
)

const HistoryRow = ({ membership }: { membership: MembershipResponse }) => {
  const statusColors: Record<string, string> = {
    ACTIVE: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
    EXPIRED: 'bg-muted text-muted-foreground',
    FROZEN: 'bg-blue-500/10 text-blue-600 dark:text-blue-400',
    CANCELLED: 'bg-red-500/10 text-red-600 dark:text-red-400',
    CREATED: 'bg-amber-500/10 text-amber-600 dark:text-amber-400',
  }

  return (
    <div className="grid gap-3 bg-card p-4 md:grid-cols-[minmax(0,1fr),auto,auto,auto]">
      <div>
        <p className="font-semibold text-foreground">{formatEnum(membership.type)} plan</p>
        <p className="mt-1 text-xs text-muted-foreground">
          {formatDate(membership.startDate)} — {formatDate(membership.endDate)}
        </p>
      </div>
      <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${statusColors[membership.status] ?? 'bg-muted text-muted-foreground'}`}>
        {formatEnum(membership.status)}
      </span>
      <div className="text-sm text-muted-foreground">
        {membership.visitsLeft === null ? 'Unlimited visits' : `${membership.visitsLeft} visits left`}
      </div>
    </div>
  )
}

const formatEnum = (value: string) =>
  value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')

const formatDate = (value: string) =>
  new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(value))

export default Memberships
