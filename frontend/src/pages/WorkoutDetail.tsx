import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  Activity,
  ArrowLeft,
  CalendarDays,
  CheckCircle2,
  Clock,
  Dumbbell,
  FileText,
  ListChecks,
  PauseCircle,
  Target,
  User2,
} from 'lucide-react'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import {
  type ClientWorkoutPlanResponse,
  type WorkoutLogResponse,
  type WorkoutPlanExerciseResponse,
  type WorkoutPlanResponse,
  getMyAssignmentById,
  getMyWorkoutLogs,
  getWorkoutPlanById,
} from '../services/workout.service'
import { LogWorkoutModal } from '../components/workouts/LogWorkoutModal'
import { formatDate, clampPercentage } from '../lib/utils'
import { ProgressBar } from '../components/ui/progress-bar'
import { EmptyState } from '../components/ui/empty-state'
import { SkeletonBlock } from '../components/ui/skeleton'
import { StatusBadge } from '../components/ui/status-badge'
import { MetricCard } from '../components/ui/metric-card'
import toast from '../utils/toast'

const workoutStatusColors: Record<string, string> = {
  ASSIGNED: 'bg-amber-500/10 text-amber-600',
  NOT_STARTED: 'bg-muted text-muted-foreground',
  IN_PROGRESS: 'bg-blue-500/10 text-blue-600',
  COMPLETED: 'bg-emerald-500/10 text-emerald-600',
  CANCELLED: 'bg-red-500/10 text-red-600',
}

const WorkoutDetail = () => {
  const { t } = useTranslation(['workouts', 'common'])
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [assignment, setAssignment] = useState<ClientWorkoutPlanResponse | null>(
    null,
  )
  const [plan, setPlan] = useState<WorkoutPlanResponse | null>(null)
  const [recentLogs, setRecentLogs] = useState<WorkoutLogResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isLogOpen, setIsLogOpen] = useState(false)

  const load = async (assignmentId: string) => {
    setIsLoading(true)
    setError(null)

    try {
      const assignmentData = await getMyAssignmentById(assignmentId)
      const [planData, logsPage] = await Promise.all([
        getWorkoutPlanById(assignmentData.workoutPlan.id),
        getMyWorkoutLogs(0, 8),
      ])

      setAssignment(assignmentData)
      setPlan(planData)
      setRecentLogs(logsPage.content)
    } catch (err) {
      console.error(err)
      setError(t('detail.notAvailable'))
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    if (!id) {
      navigate('/workouts', { replace: true })
      return
    }

    void load(id)
  }, [id, navigate])

  const exercisesByDay = useMemo(() => groupExercisesByDay(plan?.exercises ?? []), [plan])
  const progress = clampPercentage(assignment?.completionPercentage ?? 0)
  const trainerName =
    plan &&
    ([plan.trainer.firstname, plan.trainer.lastname].filter(Boolean).join(' ') ||
      t('detail.trainer'))

  const handleLogged = async () => {
    toast.success(t('common:toast.workoutLogged'))
    setIsLogOpen(false)
    if (id) {
      await load(id)
    }
  }

  if (isLoading) {
    return (
      <div className="space-y-5">
        <SkeletonBlock className="h-44" />
        <div className="grid gap-4 md:grid-cols-3">
          <SkeletonBlock className="h-28" />
          <SkeletonBlock className="h-28" />
          <SkeletonBlock className="h-28" />
        </div>
        <SkeletonBlock className="h-72" />
      </div>
    )
  }

  if (!plan || !assignment) {
    return (
      <Card>
        <CardContent className="flex min-h-48 flex-col items-center justify-center p-8 text-center">
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-destructive/10">
            <Dumbbell className="h-6 w-6 text-destructive" />
          </div>
          <h1 className="mt-5 text-2xl font-bold text-foreground">
            {t('detail.notFound')}
          </h1>
          <p className="mt-2 max-w-xl text-sm text-muted-foreground">
            {error ?? t('detail.notAvailable')}
          </p>
          <Link
            to="/workouts"
            className="mt-5 inline-flex h-10 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground"
          >
            {t('detail.backToWorkouts')}
          </Link>
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-start">
        <div>
          <Link
            to="/workouts"
            className="inline-flex items-center gap-2 text-sm font-semibold text-primary hover:underline"
          >
            <ArrowLeft className="h-4 w-4" />
            {t('detail.backToPlans')}
          </Link>
          <p className="mt-5 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('detail.workoutPlan')}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {plan.name}
          </h1>
          <p className="mt-2 max-w-3xl text-sm text-muted-foreground">
            {plan.description}
          </p>
          <div className="mt-4 flex flex-wrap gap-2">
            <StatusBadge 
              status={plan.difficultyLevel} 
              label={t(`difficulty.${plan.difficultyLevel}`)} 
            />
            <StatusBadge 
              status={assignment.status} 
              colors={workoutStatusColors}
              label={t(`status.${assignment.status}`)} 
            />
            <StatusBadge 
              status="COMPLETED" 
              label={`${Math.round(progress)}${t('detail.complete')}`} 
            />
          </div>
        </div>

        <button
          type="button"
          onClick={() => setIsLogOpen(true)}
          disabled={!plan.exercises.length}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
        >
          <Dumbbell className="h-4 w-4" />
          {t('detail.logWorkout')}
        </button>
      </div>

      <section className="grid gap-4 md:grid-cols-3">
        <MetricCard
          icon={CalendarDays}
          title={t('detail.programLength')}
          value={`${plan.durationWeeks} ${t('detail.weeks')}`}
          label={`${plan.sessionsPerWeek} ${t('detail.sessionsPerWeek')}`}
          tone="bg-primary/10"
        />
        <MetricCard
          icon={ListChecks}
          title={t('detail.loggedWorkouts')}
          value={`${assignment.completedWorkouts ?? 0}/${assignment.totalWorkouts ?? 0}`}
          label={t('detail.completedFrom')}
          tone="bg-primary/10"
        />
        <MetricCard
          icon={User2}
          title={t('detail.trainer')}
          value={trainerName ?? t('detail.trainer')}
          label={t('detail.assignedTo')}
          tone="bg-primary/10"
        />
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,1.25fr),minmax(360px,0.75fr)]">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between gap-4">
              <div>
                <CardTitle>{t('detail.exerciseSchedule')}</CardTitle>
                <CardDescription>
                  {t('detail.exerciseScheduleDesc')}
                </CardDescription>
              </div>
              <PauseCircle className="h-5 w-5 text-muted-foreground" />
            </div>
          </CardHeader>
          <CardContent>
            {plan.exercises.length ? (
              <div className="space-y-5">
                {exercisesByDay.map(([day, exercises]) => (
                  <DayBlock key={day} day={day} exercises={exercises} />
                ))}
              </div>
            ) : (
              <EmptyState
                icon={FileText}
                title={t('detail.noExercises')}
                description={t('detail.noTrainerExercises')}
              />
            )}
          </CardContent>
        </Card>

        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>{t('detail.progress')}</CardTitle>
              <CardDescription>
                {t('detail.completionUpdates')}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex items-end justify-between">
                <div>
                  <p className="text-3xl font-bold text-foreground">
                    {Math.round(progress)}%
                  </p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    {t('detail.assignmentCompletion')}
                  </p>
                </div>
                <CheckCircle2 className="h-8 w-8 text-primary" />
              </div>
              <ProgressBar value={progress} className="mt-5" />
              <div className="mt-4 grid gap-3 text-sm">
                <InfoRow label={t('detail.startDate')} value={formatDate(assignment.startDate)} />
                <InfoRow label={t('detail.endDate')} value={formatDate(assignment.endDate)} />
                <InfoRow label={t('detail.assigned')} value={formatDate(assignment.assignedDate)} />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>{t('detail.recentActivity')}</CardTitle>
              <CardDescription>
                {t('detail.recentActivityDesc')}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {recentLogs.length ? (
                <div className="space-y-3">
                  {recentLogs.slice(0, 4).map((log) => (
                    <RecentLog key={log.id} log={log} />
                  ))}
                </div>
              ) : (
                <EmptyState
                  icon={Target}
                  title={t('detail.noLogs')}
                  description={t('detail.noLogsDesc')}
                />
              )}
            </CardContent>
          </Card>
        </div>
      </section>

      <LogWorkoutModal
        isOpen={isLogOpen}
        onClose={() => setIsLogOpen(false)}
        assignment={assignment}
        exercises={plan.exercises}
        onLogged={handleLogged}
      />
    </div>
  )
}

const groupExercisesByDay = (exercises: WorkoutPlanExerciseResponse[]) => {
  const grouped = exercises.reduce<Record<number, WorkoutPlanExerciseResponse[]>>(
    (acc, exercise) => {
      acc[exercise.dayNumber] = [...(acc[exercise.dayNumber] ?? []), exercise]
      return acc
    },
    {},
  )

  return Object.entries(grouped)
    .map(([day, dayExercises]) => [
      Number(day),
      dayExercises.sort((a, b) => a.orderIndex - b.orderIndex),
    ] as const)
    .sort(([dayA], [dayB]) => dayA - dayB)
}

const DayBlock = ({
  day,
  exercises,
}: {
  day: number
  exercises: WorkoutPlanExerciseResponse[]
}) => {
  const { t } = useTranslation(['workouts', 'common'])

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2 }}
      className="rounded-2xl border border-border bg-background p-4"
    >
      <div className="mb-4 flex items-center justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('detail.day', { n: day })}
          </p>
          <p className="mt-1 text-sm text-muted-foreground">
            {t('detail.exercises', { count: exercises.length })}
          </p>
        </div>
        <Activity className="h-5 w-5 text-primary" />
      </div>
      <div className="space-y-3">
        {exercises.map((exercise) => (
          <ExerciseRow key={exercise.id} exercise={exercise} />
        ))}
      </div>
    </motion.div>
  )
}

const ExerciseRow = ({ exercise }: { exercise: WorkoutPlanExerciseResponse }) => {
  const { t } = useTranslation(['workouts', 'common'])

  return (
    <div className="grid gap-3 rounded-xl bg-muted px-3 py-3 md:grid-cols-[minmax(0,1fr),auto]">
      <div className="flex min-w-0 items-start gap-3">
        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-background">
          <Dumbbell className="h-4 w-4 text-primary" />
        </div>
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-foreground">
            {exercise.exercise.name}
          </p>
          <p className="mt-1 text-xs text-muted-foreground">
            {t('common:enums.exerciseCategory.' + exercise.exercise.category)} ·{' '}
            {t('common:enums.muscleGroup.' + exercise.exercise.primaryMuscleGroup)}
          </p>
          {exercise.notes && (
            <p className="mt-2 text-xs text-muted-foreground">{exercise.notes}</p>
          )}
        </div>
      </div>
      <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground md:justify-end">
        {exercise.sets && exercise.reps && (
          <span className="rounded-full bg-background px-2 py-1">
            {t('detail.setsReps', { sets: exercise.sets, reps: exercise.reps })}
          </span>
        )}
        {exercise.durationSeconds && (
          <span className="rounded-full bg-background px-2 py-1">
            {t('detail.restTime', { n: exercise.durationSeconds })}
          </span>
        )}
        {exercise.restSeconds && (
          <span className="inline-flex items-center gap-1 rounded-full bg-background px-2 py-1">
            <Clock className="h-3 w-3" />
            {t('detail.restTime', { n: exercise.restSeconds })}
          </span>
        )}
      </div>
    </div>
  )
}

const RecentLog = ({ log }: { log: WorkoutLogResponse }) => {
  const { t } = useTranslation(['workouts', 'common'])

  return (
    <div className="rounded-2xl border border-border bg-background p-3">
      <p className="text-sm font-semibold text-foreground">{log.exercise.name}</p>
      <p className="mt-1 text-xs text-muted-foreground">
        {formatDate(log.workoutDate)} · {t('common:enums.muscleGroup.' + log.exercise.primaryMuscleGroup)}
      </p>
      <p className="mt-3 text-xs text-muted-foreground">
        {[
          log.setsCompleted ? `${log.setsCompleted} ${t('planCard.sets')}` : null,
          log.repsCompleted ? `${log.repsCompleted} ${t('planCard.reps')}` : null,
          log.weightUsed ? `${log.weightUsed} ${t('planCard.kg')}` : null,
        ]
          .filter(Boolean)
          .join(' · ') || t('planCard.workoutLogged')}
      </p>
    </div>
  )
}

const InfoRow = ({ label, value }: { label: string; value: string }) => (
  <div className="flex items-center justify-between gap-4 rounded-xl bg-muted px-3 py-2">
    <span className="text-muted-foreground">{label}</span>
    <span className="font-semibold text-foreground">{value}</span>
  </div>
)

export default WorkoutDetail
