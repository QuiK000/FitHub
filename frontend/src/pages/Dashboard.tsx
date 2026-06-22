import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import {
  Activity,
  BarChart3,
  CalendarDays,
  CheckCircle2,
  Clock3,
  CreditCard,
  DollarSign,
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
import {
  getMyTrainerAnalytics,
  getDashboardAnalytics,
} from '../services/dashboard.service'
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
import type { TrainerAnalyticsResponse, DashboardAnalyticsResponse } from '../types'
import {
  formatDate,
  clampPercentage,
  formatCurrency,
  getAppDateTimeMs,
  parseAppDate,
  type IconType,
} from '../lib/utils'
import toast from '../utils/toast'
import { useMountedRef } from '../utils/useMountedRef'
import { ProgressBar } from '../components/ui/progress-bar'
import { EmptyState } from '../components/ui/empty-state'
import { SkeletonCard, SkeletonBlock, SkeletonLine } from '../components/ui/skeleton'
import { MetricCard } from '../components/ui/metric-card'
import { StatusBadge, assignmentStatusColors } from '../components/ui/status-badge'
import { InfoTile } from '../components/ui/info-tile'

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
  const { t } = useTranslation(['dashboard', 'common'])
  const user = useAuthStore((state) => state.user)
  const roles = useAuthStore((state) => state.roles)
  const isClient = roles.includes('CLIENT')
  const isTrainer = roles.includes('TRAINER')
  const isAdmin = roles.includes('ADMIN')
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
  const [trainerAnalytics, setTrainerAnalytics] = useState<TrainerAnalyticsResponse | null>(null)
  const [adminAnalytics, setAdminAnalytics] = useState<DashboardAnalyticsResponse | null>(null)
  const mounted = useMountedRef()

  useEffect(() => {
    let cancelled = false

    if (!isClient) {
      const loadTrainerAdminData = async () => {
        setIsLoading(true)

        const safetyTimeout = setTimeout(() => {
          if (mounted.current && !cancelled) setIsLoading(false)
        }, 10_000)

        try {
          if (isTrainer) {
            const data = await getMyTrainerAnalytics()
            if (mounted.current && !cancelled) setTrainerAnalytics(data)
          } else if (isAdmin) {
            const data = await getDashboardAnalytics()
            if (mounted.current && !cancelled) setAdminAnalytics(data)
          }
        } catch {
          if (mounted.current && !cancelled) toast.error(t('errors.loadFailed'))
        } finally {
          clearTimeout(safetyTimeout)
          if (mounted.current && !cancelled) setIsLoading(false)
        }
      }
      void loadTrainerAdminData()
      return () => { cancelled = true }
    }

    const loadDashboard = async () => {
      setIsLoading(true)
      setErrors({})

      const safetyTimeout = setTimeout(() => {
        if (mounted.current && !cancelled) setIsLoading(false)
      }, 10_000)

      try {
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

        if (mounted.current && !cancelled) {
          setData({
            membership,
            water,
            assignments,
            nextSession: getNextUpcomingSession(sessionsPage.content),
            goals: goalsPage.content,
            latestMeasurement,
          })
          setErrors(nextErrors)
        }
      } catch {
        if (mounted.current && !cancelled) {
          toast.error(t('errors.loadFailed'))
        }
      } finally {
        clearTimeout(safetyTimeout)
        if (mounted.current && !cancelled) setIsLoading(false)
      }
    }

    void loadDashboard()
    return () => { cancelled = true }
  }, [isClient, isTrainer, isAdmin])

  const greetingName =
    user?.clientProfile?.firstname ??
    user?.clientProfile?.lastname ??
    user?.email?.split('@')[0] ??
    t('fallbacks.there')

  const activeAssignment = data.assignments[0] ?? null
  const waterProgress = clampPercentage(data.water?.progress ?? 0)
  const membershipDays = data.membership
    ? getDaysRemaining(data.membership.endDate)
    : null

  const focusCards = useMemo(
    () => [
      {
        title: t('membership.title', { ns: 'dashboard' }),
        value: membershipDays === null
          ? t('membership.notActive', { ns: 'dashboard' })
          : t('membership.days', { count: membershipDays, ns: 'dashboard' }),
        label:
          membershipDays === null
            ? t('membership.explorePlan', { ns: 'dashboard' })
            : t('membership.validUntil', { date: formatDate(data.membership?.endDate), ns: 'dashboard' }),
        icon: CreditCard,
        tone: 'bg-emerald-500',
      },
      {
        title: t('water.title', { ns: 'dashboard' }),
        value: `${Math.round(waterProgress)}%`,
        label: t('water.mlLogged', { total: data.water?.totalMl ?? 0, target: data.water?.targetMl ?? user?.clientProfile?.dailyWaterTarget ?? 0, ns: 'dashboard' }),
        icon: Droplets,
        tone: 'bg-sky-500',
      },
      {
        title: t('workoutPlan.title', { ns: 'dashboard' }),
        value: activeAssignment
          ? `${Math.round(activeAssignment.completionPercentage ?? 0)}%`
          : t('workoutPlan.none', { ns: 'dashboard' }),
        label: activeAssignment?.workoutPlan.name ?? t('workoutPlan.noActive', { ns: 'dashboard' }),
        icon: Dumbbell,
        tone: 'bg-violet-500',
      },
      {
        title: t('goals.title', { ns: 'dashboard' }),
        value: data.goals.length.toString(),
        label:
          data.goals[0]?.title ?? t('goals.noGoals', { ns: 'dashboard' }),
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
      t,
    ],
  )

  if (!isClient) {
    return (
      <div className="space-y-6">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('trainer.badge', { ns: 'dashboard' })}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {t('trainer.welcome', { name: user?.trainerProfile?.firstname ?? user?.email?.split('@')[0] ?? t('fallbacks.there'), ns: 'dashboard' })}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            {t('trainer.overview', { ns: 'dashboard' })}
          </p>
        </div>

        <div className={`grid gap-4 md:grid-cols-2 ${isTrainer ? 'xl:grid-cols-3' : 'xl:grid-cols-4'}`}>
          {isLoading
            ? Array.from({ length: isTrainer ? 3 : 4 }).map((_, i) => (
                <div key={i} className="h-36 animate-pulse rounded-2xl bg-muted" />
              ))
            : isTrainer ? (
              <>
                <MetricCard
                  icon={Users2}
                  title={t('trainer.quickAccess', { ns: 'dashboard' })}
                  value={trainerAnalytics?.totalClients?.toString() ?? '0'}
                  tone="bg-blue-500"
                />
                <MetricCard
                  icon={Dumbbell}
                  title={t('trainer.workouts', { ns: 'dashboard' })}
                  value={trainerAnalytics?.totalSessions?.toString() ?? '0'}
                  tone="bg-violet-500"
                />
                <MetricCard
                  icon={CheckCircle2}
                  title={t('trainer.sessions', { ns: 'dashboard' })}
                  value={trainerAnalytics?.attendanceRate != null ? `${Math.round(trainerAnalytics.attendanceRate)}%` : '0%'}
                  tone="bg-emerald-500"
                />
              </>
            ) : (
              <>
                <MetricCard
                  icon={Users2}
                  title={t('admin.activeClients', { ns: 'dashboard' })}
                  value={adminAnalytics?.activeClients?.toString() ?? '0'}
                  tone="bg-blue-500"
                />
                <MetricCard
                  icon={CreditCard}
                  title={t('admin.activeMemberships', { ns: 'dashboard' })}
                  value={adminAnalytics?.activeMemberships?.toString() ?? '0'}
                  tone="bg-violet-500"
                />
                <MetricCard
                  icon={DollarSign}
                  title={t('admin.revenue', { ns: 'dashboard' })}
                  value={formatCurrency(adminAnalytics?.revenue)}
                  tone="bg-emerald-500"
                />
                <MetricCard
                  icon={Activity}
                  title={t('admin.todayCheckIns', { ns: 'dashboard' })}
                  value={adminAnalytics?.todayCheckIns?.toString() ?? '0'}
                  tone="bg-amber-500"
                />
              </>
            )}
        </div>

        <Card>
          <CardContent className="flex min-h-48 flex-col items-center justify-center p-8 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10">
              <Activity className="h-6 w-6 text-primary" />
            </div>
            <h2 className="mt-4 text-lg font-bold text-foreground">{t('trainer.title', { ns: 'dashboard' })}</h2>
            <p className="mt-2 max-w-md text-sm text-muted-foreground">
              {t('trainer.desc', { ns: 'dashboard' })}
            </p>
            <div className="mt-5 flex flex-wrap justify-center gap-2">
              <ActionLink to="/trainer-workouts" label={t('trainer.managePlans', { ns: 'dashboard' })} icon={Dumbbell} />
              <ActionLink to="/trainer-sessions" label={t('trainer.viewSessions', { ns: 'dashboard' })} icon={CalendarDays} />
              <ActionLink to="/analytics" label={t('trainer.analytics', { ns: 'dashboard' })} icon={BarChart3} />
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
        <Card>
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
  errors[key] = 'loadFailed'
  return fallback
}

const getNextUpcomingSession = (sessions: TrainingSessionResponse[]) => {
  const now = Date.now()
  return (
    sessions
      .filter((session) => getAppDateTimeMs(session.startTime) >= now)
      .sort(
        (a, b) =>
          getAppDateTimeMs(a.startTime) - getAppDateTimeMs(b.startTime),
      )[0] ?? null
  )
}

const getDaysRemaining = (endDate: string) => {
  const diff = getAppDateTimeMs(endDate) - Date.now()
  return Math.max(0, Math.ceil(diff / 86_400_000))
}

const formatTime = (value: string) =>
  new Intl.DateTimeFormat(undefined, {
    hour: 'numeric',
    minute: '2-digit',
  }).format(parseAppDate(value) ?? new Date(value))

const WorkoutPlanPanel = ({
  assignment,
}: {
  assignment: ClientWorkoutPlanResponse | null
}) => {
  const { t } = useTranslation(['dashboard', 'common'])

  if (!assignment) {
    return (
      <EmptyState
        icon={Dumbbell}
        title={t('workoutPlan.noActive')}
        description={t('workoutPlan.noActiveDesc')}
        actionLabel={t('workoutPlan.browseWorkouts')}
        to="/workouts"
      />
    )
  }

  const progress = clampPercentage(assignment.completionPercentage ?? 0)

  return (
    <div className="rounded-2xl border border-border bg-muted/40 p-4">
      <div className="flex items-center justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('workoutPlan.activeWorkoutPlan')}
          </p>
          <h3 className="mt-2 text-lg font-semibold text-foreground">
            {assignment.workoutPlan.name}
          </h3>
          <p className="mt-1 text-sm text-muted-foreground">
            {t('common:enums.difficultyLevel.' + assignment.workoutPlan.difficultyLevel)} ·{' '}
            {t('workoutPlan.sessionsPerWeek', { count: assignment.workoutPlan.sessionsPerWeek })}
          </p>
        </div>
        <StatusBadge
          status={assignment.status}
          colors={assignmentStatusColors}
          label={t('common:enums.assignmentStatus.' + assignment.status)}
        />
      </div>

      <ProgressBar value={progress} className="mt-5" />
      <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
        <span>{Math.round(progress)}{t('workoutPlan.complete')}</span>
        <span>
          {t('workoutPlan.logged', { completed: assignment.completedWorkouts ?? 0, total: assignment.totalWorkouts ?? 0 })}
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
  const { t } = useTranslation(['dashboard', 'common'])

  if (!session) {
    return (
      <EmptyState
        icon={CalendarDays}
        title={t('nextSession.noUpcoming')}
        description={t('nextSession.noUpcomingDesc')}
        actionLabel={t('nextSession.viewSessions')}
        to="/sessions"
      />
    )
  }

  return (
    <div className="rounded-2xl border border-border bg-muted/40 p-4">
      <div className="flex items-center justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('nextSession.title')}
          </p>
          <h3 className="mt-2 text-lg font-semibold text-foreground">
            {t('common:enums.trainingType.' + session.type)} {t('nextSession.training')}
          </h3>
          <p className="mt-1 text-sm text-muted-foreground">
            {t('nextSession.withTrainer', { name: `${session.trainer.firstname} ${session.trainer.lastname}` })}
          </p>
        </div>
        <CalendarDays className="h-5 w-5 text-primary" />
      </div>

      <div className="mt-5 grid gap-3 text-sm sm:grid-cols-2">
        <InfoTile
          icon={Clock3}
          label={t('nextSession.starts')}
          value={`${formatDate(session.startTime)} · ${formatTime(session.startTime)}`}
        />
        <InfoTile
          icon={Users2}
          label={t('nextSession.capacity')}
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
  const { t } = useTranslation('dashboard')
  const target = water?.targetMl ?? fallbackTarget ?? 0
  const total = water?.totalMl ?? 0
  const progress = target > 0 ? clampPercentage((total / target) * 100) : 0

  if (!water && !fallbackTarget) {
    return (
      <EmptyState
        icon={Droplets}
        title={t('water.noTarget')}
        description={t('water.noTargetDesc')}
        actionLabel={t('water.updateProfile')}
        to="/profile"
      />
    )
  }

  return (
    <div>
      <div className="flex items-end justify-between">
        <div>
          <p className="text-3xl font-bold text-foreground">{t('water.mlLogged', { total, target })}</p>
          <p className="mt-1 text-sm text-muted-foreground">
            {t('water.ofTarget', { target })}
          </p>
        </div>
        <div className="rounded-full bg-sky-500/10 px-3 py-1 text-sm font-semibold text-sky-600 dark:text-sky-300">
          {Math.round(progress)}%
        </div>
      </div>
      <ProgressBar value={progress} className="mt-5" />
      {water?.intakes.length ? (
        <p className="mt-3 text-xs text-muted-foreground">
          {t('water.intakesLogged', { count: water.intakes.length })}
        </p>
      ) : (
        <p className="mt-3 text-xs text-muted-foreground">
          {t('water.noIntakes')}
        </p>
      )}
    </div>
  )
}

const MembershipPanel = ({
  membership,
  daysRemaining,
}: {
  membership: MembershipResponse | null
  daysRemaining: number | null
}) => {
  const { t } = useTranslation(['dashboard', 'common'])

  if (!membership) {
    return (
      <EmptyState
        icon={CreditCard}
        title={t('membership.noActive')}
        description={t('membership.noActiveDesc')}
        actionLabel={t('membership.explorePlans')}
        to="/memberships"
      />
    )
  }

  const membershipLabel = t('common:enums.membershipType.' + membership.type)

  return (
    <div className="space-y-4">
      <div className="rounded-2xl bg-primary p-5 text-primary-foreground">
        <p className="text-sm opacity-80">{t('membership.activePlan')}</p>
        <div className="mt-3 flex items-end justify-between gap-4">
          <div>
            <p className="text-2xl font-bold">{membershipLabel}</p>
            <p className="mt-1 text-sm opacity-80">
              {t('membership.daysRemaining', { count: daysRemaining ?? undefined })}
            </p>
          </div>
          <CheckCircle2 className="h-8 w-8 opacity-90" />
        </div>
      </div>
      <div className="grid gap-3 text-sm sm:grid-cols-2">
        <InfoTile
          icon={CalendarDays}
          label={t('membership.validUntilDate')}
          value={formatDate(membership.endDate)}
        />
        <InfoTile
          icon={Trophy}
          label={t('membership.visitsLeft')}
          value={membership.visitsLeft === null ? t('membership.unlimited') : String(membership.visitsLeft)}
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
}) => {
  const { t } = useTranslation(['dashboard', 'common'])

  return (
    <div className="grid gap-4 lg:grid-cols-2">
      <div className="space-y-3">
        {goals.length ? (
          goals.map((goal) => (
            <div key={goal.id} className="rounded-2xl border border-border p-4">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-foreground">
                    {goal.title}
                  </p>
                  <p className="mt-1 text-xs text-muted-foreground">
                    {t('common:enums.goalType.' + goal.goalType)} · {goal.currentValue}/
                    {goal.targetValue} {goal.unit.toLowerCase()}
                  </p>
                </div>
                <StatusBadge
                  status=""
                  label={`${Math.round(goal.progressPercentage)}%`}
                />
              </div>
              <ProgressBar value={goal.progressPercentage} className="mt-4" />
            </div>
          ))
        ) : (
          <EmptyState
            icon={Target}
            title={t('goals.noActiveGoals')}
            description={t('goals.noActiveGoalsDesc')}
            actionLabel={t('goals.openProgress')}
            to="/progress"
          />
        )}
      </div>

      {latestMeasurement ? (
        <div className="rounded-2xl border border-border p-4">
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('measurements.latest')}
          </p>
          <p className="mt-2 text-sm text-muted-foreground">
            {formatDate(latestMeasurement.measurementDate)}
          </p>
          <div className="mt-4 grid grid-cols-2 gap-3">
            <MeasurementTile label={t('measurements.weight')} value={latestMeasurement.weight} unit="kg" />
            <MeasurementTile
              label={t('measurements.bodyFat')}
              value={latestMeasurement.bodyFatPercentage}
              unit="%"
            />
            <MeasurementTile
              label={t('measurements.muscle')}
              value={latestMeasurement.muscleMass}
              unit="kg"
            />
            <MeasurementTile label={t('measurements.bmi')} value={latestMeasurement.bmi} unit="" />
          </div>
        </div>
      ) : (
        <EmptyState
          icon={HeartPulse}
          title={t('goals.noMeasurements')}
          description={t('goals.noMeasurementsDesc')}
          actionLabel={t('goals.addMeasurement')}
          to="/progress"
        />
      )}
    </div>
  )
}

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

export default Dashboard
