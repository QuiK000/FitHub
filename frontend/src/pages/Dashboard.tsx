import { useEffect, useMemo, useState, type ComponentType, type SVGProps } from 'react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  Activity,
  ArrowRight,
  BarChart3,
  CalendarDays,
  CheckCircle2,
  Clock3,
  CreditCard,
  Droplets,
  Dumbbell,
  HeartPulse,
  LineChart,
  Target,
  Trophy,
  Users2,
} from 'lucide-react'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import { getMyActiveMembership } from '../services/membership.service'
import { getTodayWaterIntake } from '../services/nutrition.service'
import {
  getActiveGoals,
  getLatestBodyMeasurement,
} from '../services/progress.service'
import {
  getMyActiveAssignments,
  getTrainingSessions,
} from '../services/workout.service'
import { useAuthStore } from '../store/useAuthStore'
import type { MembershipResponse } from '../types'
import type { DailyWaterIntakeResponse } from '../types'
import type {
  BodyMeasurementResponse,
  GoalResponse,
} from '../types'
import type {
  ClientWorkoutPlanResponse,
  TrainingSessionResponse,
} from '../types'

type DashboardData = {
  membership: MembershipResponse | null
  water: DailyWaterIntakeResponse | null
  assignments: ClientWorkoutPlanResponse[]
  nextSession: TrainingSessionResponse | null
  goals: GoalResponse[]
  latestMeasurement: BodyMeasurementResponse | null
}

type DashboardErrors = Partial<Record<keyof DashboardData, string>>

const todayIso = () => new Date().toISOString().slice(0, 10)

const Dashboard = () => {
  const { t } = useTranslation('dashboard')
  const user = useAuthStore((state) => state.user)
  const roles = useAuthStore((state) => state.roles)
  const isClient = roles.includes('CLIENT')
  const [data, setData] = useState<DashboardData>({
    membership: null,
    water: null,
    assignments: [],
    nextSession: null,
    goals: [],
    latestMeasurement: null,
  })
  const [errors, setErrors] = useState<DashboardErrors>({})
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (!isClient) {
      setIsLoading(false)
      return
    }

    const loadDashboard = async () => {
      setIsLoading(true)
      setErrors({})

      const [
        membershipResult,
        waterResult,
        assignmentsResult,
        sessionsResult,
        goalsResult,
        measurementResult,
      ] = await Promise.allSettled([
        getMyActiveMembership(),
        getTodayWaterIntake(),
        getMyActiveAssignments(),
        getTrainingSessions(0, 12),
        getActiveGoals(0, 3),
        getLatestBodyMeasurement(),
      ])

      const nextErrors: DashboardErrors = {}

      const membership = unwrapResult(
        membershipResult,
        'membership',
        nextErrors,
        null,
      )
      const water = unwrapResult(waterResult, 'water', nextErrors, null)
      const assignments = unwrapResult(
        assignmentsResult,
        'assignments',
        nextErrors,
        [],
      )
      const sessionsPage = unwrapResult(
        sessionsResult,
        'nextSession',
        nextErrors,
        { content: [], totalElements: 0, totalPages: 0, number: 0, size: 0 },
      )
      const goalsPage = unwrapResult(goalsResult, 'goals', nextErrors, {
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 0,
      })
      const latestMeasurement = unwrapResult(
        measurementResult,
        'latestMeasurement',
        nextErrors,
        null,
      )

      setData({
        membership,
        water,
        assignments,
        nextSession: getNextUpcomingSession(sessionsPage.content),
        goals: goalsPage.content,
        latestMeasurement,
      })
      setErrors(nextErrors)
      setIsLoading(false)
    }

    void loadDashboard()
  }, [isClient])

  const greetingName =
    user?.clientProfile?.firstname ??
    user?.clientProfile?.lastname ??
    user?.email?.split('@')[0] ??
    'there'

  const activeAssignment = data.assignments[0] ?? null
  const waterProgress = clampPercentage(data.water?.progress ?? 0)
  const membershipDays = data.membership
    ? getDaysRemaining(data.membership.endDate)
    : null
  const membershipLabel = data.membership
    ? formatEnum(data.membership.type)
    : 'No active plan'

  const focusCards = useMemo(
    () => [
      {
        title: t('membership.title'),
        value: membershipDays === null ? t('membership.notActive') : t('membership.days', { count: membershipDays }),
        label:
          membershipDays === null
            ? t('membership.explorePlan')
            : t('membership.validUntil', { date: formatDate(data.membership?.endDate) }),
        icon: CreditCard,
        tone: 'bg-emerald-500',
      },
      {
        title: t('water.title'),
        value: `${Math.round(waterProgress)}%`,
        label: t('water.mlLogged', { total: data.water?.totalMl ?? 0, target: data.water?.targetMl ?? user?.clientProfile?.dailyWaterTarget ?? 0 }),
        icon: Droplets,
        tone: 'bg-sky-500',
      },
      {
        title: t('workoutPlan.title'),
        value: activeAssignment
          ? `${Math.round(activeAssignment.completionPercentage ?? 0)}%`
          : t('workoutPlan.none'),
        label: activeAssignment?.workoutPlan.name ?? t('workoutPlan.noActive'),
        icon: Dumbbell,
        tone: 'bg-violet-500',
      },
      {
        title: t('goals.title'),
        value: data.goals.length.toString(),
        label:
          data.goals[0]?.title ?? t('goals.noGoals'),
        icon: Target,
        tone: 'bg-amber-500',
      },
    ],
    [
      activeAssignment,
      data.goals,
      data.membership?.endDate,
      data.water?.targetMl,
      data.water?.totalMl,
      membershipDays,
      user?.clientProfile?.dailyWaterTarget,
      waterProgress,
    ],
  )

  if (!isClient) {
    return (
      <div className="space-y-6">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Dashboard
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            Welcome back, {user?.trainerProfile?.firstname ?? user?.email?.split('@')[0] ?? 'Admin'}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            Overview of your gym operations and key metrics.
          </p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <MetricCard
            icon={Users2}
            label="Quick Access"
            value="—"
            tone="bg-blue-500"
          />
          <MetricCard
            icon={Dumbbell}
            label="Workouts"
            value="—"
            tone="bg-violet-500"
          />
          <MetricCard
            icon={CalendarDays}
            label="Sessions"
            value="—"
            tone="bg-emerald-500"
          />
          <MetricCard
            icon={BarChart3}
            label="Analytics"
            value="—"
            tone="bg-amber-500"
          />
        </div>

        <Card>
          <CardContent className="flex min-h-48 flex-col items-center justify-center p-8 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10">
              <Activity className="h-6 w-6 text-primary" />
            </div>
            <h2 className="mt-4 text-lg font-bold text-foreground">Trainer Dashboard</h2>
            <p className="mt-2 max-w-md text-sm text-muted-foreground">
              Use the navigation sidebar to access workout plans, sessions, analytics, and client management.
            </p>
            <div className="mt-5 flex flex-wrap justify-center gap-2">
              <ActionLink to="/workouts" label="Manage Plans" icon={Dumbbell} />
              <ActionLink to="/sessions" label="View Sessions" icon={CalendarDays} />
              <ActionLink to="/analytics" label="Analytics" icon={BarChart3} />
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('badge')}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {t('title', { name: greetingName })}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            {t('subtitle', { date: formatDate(todayIso()) })}
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <ActionLink to="/workouts" label={t('openWorkouts')} icon={Dumbbell} />
          <ActionLink to="/progress" label={t('trackProgress')} icon={LineChart} />
        </div>
      </div>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {isLoading
          ? Array.from({ length: 4 }).map((_, index) => (
              <SkeletonCard key={index} className="h-36" />
            ))
          : focusCards.map((card) => <MetricCard key={card.title} {...card} />)}
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,1.25fr),minmax(360px,0.75fr)]">
        <Card className="overflow-hidden">
          <CardHeader>
            <div className="flex items-center justify-between gap-4">
              <div>
                <CardTitle>{t('todayPlan.title')}</CardTitle>
                <CardDescription>
                  {t('todayPlan.desc')}
                </CardDescription>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
                <Activity className="h-5 w-5 text-primary" />
              </div>
            </div>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="grid gap-4 md:grid-cols-2">
                <SkeletonBlock />
                <SkeletonBlock />
              </div>
            ) : (
              <div className="grid gap-4 md:grid-cols-2">
                <WorkoutPlanPanel assignment={activeAssignment} />
                <NextSessionPanel session={data.nextSession} />
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>{t('water.hydrationTitle')}</CardTitle>
            <CardDescription>
              {t('water.hydrationDesc')}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <SkeletonBlock />
            ) : (
              <WaterPanel
                water={data.water}
                fallbackTarget={user?.clientProfile?.dailyWaterTarget ?? null}
              />
            )}
          </CardContent>
        </Card>
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,0.9fr),minmax(0,1.1fr)]">
        <Card>
          <CardHeader>
            <CardTitle>{t('membership.statusTitle')}</CardTitle>
            <CardDescription>
              {t('membership.statusDesc')}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <SkeletonBlock />
            ) : (
              <MembershipPanel
                membership={data.membership}
                daysRemaining={membershipDays}
                membershipLabel={membershipLabel}
              />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>{t('goals.activeGoalsTitle')}</CardTitle>
            <CardDescription>
              {t('goals.activeGoalsDesc')}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-3">
                <SkeletonLine />
                <SkeletonLine />
                <SkeletonLine />
              </div>
            ) : (
              <ProgressPanel
                goals={data.goals}
                latestMeasurement={data.latestMeasurement}
              />
            )}
          </CardContent>
        </Card>
      </section>

      {Object.keys(errors).length > 0 && !isLoading && (
        <div className="rounded-2xl border border-amber-500/30 bg-amber-500/10 px-4 py-3 text-sm text-amber-700 dark:text-amber-200">
          {t('errors.loadFailed')}
        </div>
      )}
    </div>
  )
}

const unwrapResult = <T,>(
  result: PromiseSettledResult<T>,
  key: keyof DashboardData,
  errors: DashboardErrors,
  fallback: T,
): T => {
  if (result.status === 'fulfilled') return result.value
  console.error(`Failed to load dashboard ${String(key)}`, result.reason)
  errors[key] = 'Failed to load'
  return fallback
}

const getNextUpcomingSession = (sessions: TrainingSessionResponse[]) => {
  const now = Date.now()
  return (
    sessions
      .filter((session) => new Date(session.startTime).getTime() >= now)
      .sort(
        (a, b) =>
          new Date(a.startTime).getTime() - new Date(b.startTime).getTime(),
      )[0] ?? null
  )
}

const getDaysRemaining = (endDate: string) => {
  const diff = new Date(endDate).getTime() - Date.now()
  return Math.max(0, Math.ceil(diff / 86_400_000))
}

const clampPercentage = (value: number) =>
  Math.max(0, Math.min(100, Number.isFinite(value) ? value : 0))

const formatDate = (value?: string | null) => {
  if (!value) return 'Not set'
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(value))
}

const formatTime = (value: string) =>
  new Intl.DateTimeFormat(undefined, {
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(value))

const formatEnum = (value: string) =>
  value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')

type IconType = ComponentType<SVGProps<SVGSVGElement>>

type MetricCardProps = {
  title?: string
  value: string
  label: string
  icon: IconType
  tone: string
}

const MetricCard = ({ title, value, label, icon: Icon, tone }: MetricCardProps) => (
  <motion.div
    initial={{ opacity: 0, y: 10 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.25 }}
  >
    <Card className="h-full">
      <CardContent className="flex min-h-[140px] items-start justify-between gap-4 p-5">
        <div className="flex flex-col justify-between">
          <div>
            <p className="text-xs font-medium text-muted-foreground">{title}</p>
            <p className="mt-2 text-2xl font-bold text-foreground">{value}</p>
          </div>
          <p className="mt-3 line-clamp-2 text-xs text-muted-foreground">{label}</p>
        </div>
        <div className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${tone}`}>
          <Icon className="h-5 w-5 text-white" />
        </div>
      </CardContent>
    </Card>
  </motion.div>
)

const WorkoutPlanPanel = ({
  assignment,
}: {
  assignment: ClientWorkoutPlanResponse | null
}) => {
  if (!assignment) {
    return (
      <EmptyState
        icon={Dumbbell}
        title="No active workout plan"
        description="Once a trainer assigns your plan, today's training focus will appear here."
        actionLabel="Browse workouts"
        to="/workouts"
      />
    )
  }

  const progress = clampPercentage(assignment.completionPercentage ?? 0)

  return (
    <div className="rounded-2xl border border-border bg-muted/40 p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Active workout plan
          </p>
          <h3 className="mt-2 text-lg font-semibold text-foreground">
            {assignment.workoutPlan.name}
          </h3>
          <p className="mt-1 text-sm text-muted-foreground">
            {formatEnum(assignment.workoutPlan.difficultyLevel)} ·{' '}
            {assignment.workoutPlan.sessionsPerWeek} sessions/week
          </p>
        </div>
        <StatusPill label={formatEnum(assignment.status)} />
      </div>

      <ProgressBar value={progress} className="mt-5" />
      <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
        <span>{Math.round(progress)}% complete</span>
        <span>
          {assignment.completedWorkouts ?? 0} / {assignment.totalWorkouts ?? 0}{' '}
          workouts
        </span>
      </div>
    </div>
  )
}

const NextSessionPanel = ({
  session,
}: {
  session: TrainingSessionResponse | null
}) => {
  if (!session) {
    return (
      <EmptyState
        icon={CalendarDays}
        title="No upcoming sessions"
        description="Join a class or book a personal session when the next schedule opens."
        actionLabel="View sessions"
        to="/sessions"
      />
    )
  }

  return (
    <div className="rounded-2xl border border-border bg-muted/40 p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Next session
          </p>
          <h3 className="mt-2 text-lg font-semibold text-foreground">
            {formatEnum(session.type)} training
          </h3>
          <p className="mt-1 text-sm text-muted-foreground">
            With {session.trainer.firstname} {session.trainer.lastname}
          </p>
        </div>
        <CalendarDays className="h-5 w-5 text-primary" />
      </div>

      <div className="mt-5 grid gap-3 text-sm sm:grid-cols-2">
        <InfoTile
          icon={Clock3}
          label="Starts"
          value={`${formatDate(session.startTime)} · ${formatTime(session.startTime)}`}
        />
        <InfoTile
          icon={Users2}
          label="Capacity"
          value={`${session.currentParticipants}/${session.maxParticipants}`}
        />
      </div>
    </div>
  )
}

const WaterPanel = ({
  water,
  fallbackTarget,
}: {
  water: DailyWaterIntakeResponse | null
  fallbackTarget: number | null
}) => {
  const target = water?.targetMl ?? fallbackTarget ?? 0
  const total = water?.totalMl ?? 0
  const progress = target > 0 ? clampPercentage((total / target) * 100) : 0

  if (!water && !fallbackTarget) {
    return (
      <EmptyState
        icon={Droplets}
        title="No hydration target yet"
        description="Set your daily water target from your profile to start tracking intake."
        actionLabel="Update profile"
        to="/profile"
      />
    )
  }

  return (
    <div>
      <div className="flex items-end justify-between">
        <div>
          <p className="text-3xl font-bold text-foreground">{total} ml</p>
          <p className="mt-1 text-sm text-muted-foreground">
            of {target} ml target
          </p>
        </div>
        <div className="rounded-full bg-sky-500/10 px-3 py-1 text-sm font-semibold text-sky-600 dark:text-sky-300">
          {Math.round(progress)}%
        </div>
      </div>
      <ProgressBar value={progress} className="mt-5" />
      {water?.intakes.length ? (
        <p className="mt-3 text-xs text-muted-foreground">
          {water.intakes.length} intake{water.intakes.length === 1 ? '' : 's'}{' '}
          logged today.
        </p>
      ) : (
        <p className="mt-3 text-xs text-muted-foreground">
          No water logged today. Start with one glass and build momentum.
        </p>
      )}
    </div>
  )
}

const MembershipPanel = ({
  membership,
  daysRemaining,
  membershipLabel,
}: {
  membership: MembershipResponse | null
  daysRemaining: number | null
  membershipLabel: string
}) => {
  if (!membership) {
    return (
      <EmptyState
        icon={CreditCard}
        title="No active membership"
        description="You do not have an active membership yet. Explore plans to unlock training access."
        actionLabel="Explore plans"
        to="/memberships"
      />
    )
  }

  return (
    <div className="space-y-4">
      <div className="rounded-2xl bg-primary p-5 text-primary-foreground">
        <p className="text-sm opacity-80">Active plan</p>
        <div className="mt-3 flex items-end justify-between gap-4">
          <div>
            <p className="text-2xl font-bold">{membershipLabel}</p>
            <p className="mt-1 text-sm opacity-80">
              {daysRemaining} day{daysRemaining === 1 ? '' : 's'} remaining
            </p>
          </div>
          <CheckCircle2 className="h-8 w-8 opacity-90" />
        </div>
      </div>
      <div className="grid gap-3 text-sm sm:grid-cols-2">
        <InfoTile
          icon={CalendarDays}
          label="Valid until"
          value={formatDate(membership.endDate)}
        />
        <InfoTile
          icon={Trophy}
          label="Visits left"
          value={membership.visitsLeft === null ? 'Unlimited' : String(membership.visitsLeft)}
        />
      </div>
    </div>
  )
}

const ProgressPanel = ({
  goals,
  latestMeasurement,
}: {
  goals: GoalResponse[]
  latestMeasurement: BodyMeasurementResponse | null
}) => (
  <div className="grid gap-4 lg:grid-cols-2">
    <div className="space-y-3">
      {goals.length ? (
        goals.map((goal) => (
          <div key={goal.id} className="rounded-2xl border border-border p-4">
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-sm font-semibold text-foreground">
                  {goal.title}
                </p>
                <p className="mt-1 text-xs text-muted-foreground">
                  {formatEnum(goal.goalType)} · {goal.currentValue}/
                  {goal.targetValue} {goal.unit.toLowerCase()}
                </p>
              </div>
              <StatusPill label={`${Math.round(goal.progressPercentage)}%`} />
            </div>
            <ProgressBar value={goal.progressPercentage} className="mt-4" />
          </div>
        ))
      ) : (
        <EmptyState
          icon={Target}
          title="No active goals"
          description="Create your first goal to turn progress into something visible."
          actionLabel="Open progress"
          to="/progress"
        />
      )}
    </div>

    {latestMeasurement ? (
      <div className="rounded-2xl border border-border p-4">
        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
          Latest measurement
        </p>
        <p className="mt-2 text-sm text-muted-foreground">
          {formatDate(latestMeasurement.measurementDate)}
        </p>
        <div className="mt-4 grid grid-cols-2 gap-3">
          <MeasurementTile label="Weight" value={latestMeasurement.weight} unit="kg" />
          <MeasurementTile
            label="Body fat"
            value={latestMeasurement.bodyFatPercentage}
            unit="%"
          />
          <MeasurementTile
            label="Muscle"
            value={latestMeasurement.muscleMass}
            unit="kg"
          />
          <MeasurementTile label="BMI" value={latestMeasurement.bmi} unit="" />
        </div>
      </div>
    ) : (
      <EmptyState
        icon={HeartPulse}
        title="No measurements yet"
        description="Log your first body measurement to unlock trend tracking."
        actionLabel="Add measurement"
        to="/progress"
      />
    )}
  </div>
)

const MeasurementTile = ({
  label,
  value,
  unit,
}: {
  label: string
  value: number | null
  unit: string
}) => (
  <div className="rounded-xl bg-muted px-3 py-2">
    <p className="text-xs text-muted-foreground">{label}</p>
    <p className="mt-1 text-sm font-semibold text-foreground">
      {value === null ? '-' : `${value}${unit ? ` ${unit}` : ''}`}
    </p>
  </div>
)

const ProgressBar = ({
  value,
  className,
}: {
  value: number
  className?: string
}) => (
  <div className={`h-2 overflow-hidden rounded-full bg-muted ${className ?? ''}`}>
    <div
      className="h-full rounded-full bg-primary transition-all duration-500"
      style={{ width: `${clampPercentage(value)}%` }}
    />
  </div>
)

const StatusPill = ({ label }: { label: string }) => (
  <span className="inline-flex items-center rounded-full bg-primary/10 px-2.5 py-1 text-xs font-semibold text-primary">
    {label}
  </span>
)

const ActionLink = ({
  to,
  label,
  icon: Icon,
}: {
  to: string
  label: string
  icon: IconType
}) => (
  <Link
    to={to}
    className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-card px-4 text-sm font-semibold text-foreground shadow-soft transition-all hover:bg-accent"
  >
    <Icon className="h-4 w-4" />
    {label}
  </Link>
)

const EmptyState = ({
  icon: Icon,
  title,
  description,
  actionLabel,
  to,
}: {
  icon: IconType
  title: string
  description: string
  actionLabel: string
  to: string
}) => (
  <div className="flex h-full min-h-44 flex-col justify-between rounded-2xl border border-dashed border-border bg-muted/30 p-4">
    <div>
      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-background">
        <Icon className="h-5 w-5 text-muted-foreground" />
      </div>
      <p className="mt-4 text-sm font-semibold text-foreground">{title}</p>
      <p className="mt-1 text-sm text-muted-foreground">{description}</p>
    </div>
    <Link
      to={to}
      className="mt-4 inline-flex w-fit items-center gap-1.5 text-sm font-semibold text-primary hover:underline"
    >
      {actionLabel}
      <ArrowRight className="h-4 w-4" />
    </Link>
  </div>
)

const InfoTile = ({
  icon: Icon,
  label,
  value,
}: {
  icon: IconType
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

const SkeletonCard = ({ className = '' }: { className?: string }) => (
  <div className={`animate-pulse rounded-2xl border border-border bg-card ${className}`}>
    <div className="h-full rounded-2xl bg-muted/80" />
  </div>
)

const SkeletonBlock = () => (
  <div className="h-44 animate-pulse rounded-2xl bg-muted" />
)

const SkeletonLine = () => (
  <div className="h-14 animate-pulse rounded-xl bg-muted" />
)

export default Dashboard
