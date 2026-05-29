import { useEffect, useMemo, useState, type ComponentType, type SVGProps } from 'react'
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
import toast from '../utils/toast'

const WorkoutDetail = () => {
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
      setError('Unable to load this workout plan.')
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
      'Trainer')

  const handleLogged = async () => {
    toast.success('Workout logged successfully.')
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
        <CardContent className="flex min-h-72 flex-col items-center justify-center p-8 text-center">
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-destructive/10">
            <Dumbbell className="h-6 w-6 text-destructive" />
          </div>
          <h1 className="mt-5 text-2xl font-bold text-foreground">
            Workout plan not found
          </h1>
          <p className="mt-2 max-w-xl text-sm text-muted-foreground">
            {error ?? 'This assignment may no longer be available.'}
          </p>
          <Link
            to="/workouts"
            className="mt-5 inline-flex h-10 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground"
          >
            Back to workouts
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
            Back to plans
          </Link>
          <p className="mt-5 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Workout plan
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {plan.name}
          </h1>
          <p className="mt-2 max-w-3xl text-sm text-muted-foreground">
            {plan.description}
          </p>
          <div className="mt-4 flex flex-wrap gap-2">
            <StatusPill label={formatEnum(plan.difficultyLevel)} />
            <StatusPill label={formatEnum(assignment.status)} />
            <StatusPill label={`${Math.round(progress)}% complete`} />
          </div>
        </div>

        <button
          type="button"
          onClick={() => setIsLogOpen(true)}
          disabled={!plan.exercises.length}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
        >
          <Dumbbell className="h-4 w-4" />
          Log workout
        </button>
      </div>

      <section className="grid gap-4 md:grid-cols-3">
        <MetricCard
          icon={CalendarDays}
          label="Program length"
          value={`${plan.durationWeeks} weeks`}
          detail={`${plan.sessionsPerWeek} sessions per week`}
        />
        <MetricCard
          icon={ListChecks}
          label="Logged workouts"
          value={`${assignment.completedWorkouts ?? 0}/${assignment.totalWorkouts ?? 0}`}
          detail="Completed from this assignment"
        />
        <MetricCard
          icon={User2}
          label="Trainer"
          value={trainerName ?? 'Trainer'}
          detail="Assigned to your plan"
        />
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,1.25fr),minmax(360px,0.75fr)]">
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between gap-4">
              <div>
                <CardTitle>Exercise Schedule</CardTitle>
                <CardDescription>
                  Follow the plan day by day and log each completed exercise.
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
                title="No exercises in this plan"
                description="Your trainer has not added exercises to this plan yet."
              />
            )}
          </CardContent>
        </Card>

        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Progress</CardTitle>
              <CardDescription>
                Completion updates after workouts are logged.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex items-end justify-between">
                <div>
                  <p className="text-3xl font-bold text-foreground">
                    {Math.round(progress)}%
                  </p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    Assignment completion
                  </p>
                </div>
                <CheckCircle2 className="h-8 w-8 text-primary" />
              </div>
              <ProgressBar value={progress} className="mt-5" />
              <div className="mt-4 grid gap-3 text-sm">
                <InfoRow label="Start date" value={formatDate(assignment.startDate)} />
                <InfoRow label="End date" value={formatDate(assignment.endDate)} />
                <InfoRow label="Assigned" value={formatDate(assignment.assignedDate)} />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Activity</CardTitle>
              <CardDescription>
                Latest logs across your workout history.
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
                  title="No workout logs yet"
                  description="Log your first workout from this plan to start building history."
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

type IconType = ComponentType<SVGProps<SVGSVGElement>>

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

const formatEnum = (value: string) =>
  value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')

const formatDate = (value?: string | null) => {
  if (!value) return 'Not set'
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(value))
}

const clampPercentage = (value: number) =>
  Math.max(0, Math.min(100, Number.isFinite(value) ? value : 0))

const MetricCard = ({
  icon: Icon,
  label,
  value,
  detail,
}: {
  icon: IconType
  label: string
  value: string
  detail: string
}) => (
  <Card>
    <CardContent className="flex items-start justify-between gap-4 p-5">
      <div>
        <p className="text-xs font-medium text-muted-foreground">{label}</p>
        <p className="mt-2 text-xl font-bold text-foreground">{value}</p>
        <p className="mt-1 text-xs text-muted-foreground">{detail}</p>
      </div>
      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
        <Icon className="h-5 w-5 text-primary" />
      </div>
    </CardContent>
  </Card>
)

const DayBlock = ({
  day,
  exercises,
}: {
  day: number
  exercises: WorkoutPlanExerciseResponse[]
}) => (
  <motion.div
    initial={{ opacity: 0, y: 8 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.2 }}
    className="rounded-2xl border border-border bg-background p-4"
  >
    <div className="mb-4 flex items-center justify-between gap-3">
      <div>
        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
          Day {day}
        </p>
        <p className="mt-1 text-sm text-muted-foreground">
          {exercises.length} exercise{exercises.length === 1 ? '' : 's'}
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

const ExerciseRow = ({ exercise }: { exercise: WorkoutPlanExerciseResponse }) => (
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
          {formatEnum(exercise.exercise.category)} ·{' '}
          {formatEnum(exercise.exercise.primaryMuscleGroup)}
        </p>
        {exercise.notes && (
          <p className="mt-2 text-xs text-muted-foreground">{exercise.notes}</p>
        )}
      </div>
    </div>
    <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground md:justify-end">
      {exercise.sets && exercise.reps && (
        <span className="rounded-full bg-background px-2 py-1">
          {exercise.sets} sets x {exercise.reps} reps
        </span>
      )}
      {exercise.durationSeconds && (
        <span className="rounded-full bg-background px-2 py-1">
          {exercise.durationSeconds}s
        </span>
      )}
      {exercise.restSeconds && (
        <span className="inline-flex items-center gap-1 rounded-full bg-background px-2 py-1">
          <Clock className="h-3 w-3" />
          {exercise.restSeconds}s rest
        </span>
      )}
    </div>
  </div>
)

const RecentLog = ({ log }: { log: WorkoutLogResponse }) => (
  <div className="rounded-2xl border border-border bg-background p-3">
    <p className="text-sm font-semibold text-foreground">{log.exercise.name}</p>
    <p className="mt-1 text-xs text-muted-foreground">
      {formatDate(log.workoutDate)} · {formatEnum(log.exercise.primaryMuscleGroup)}
    </p>
    <p className="mt-3 text-xs text-muted-foreground">
      {[
        log.setsCompleted ? `${log.setsCompleted} sets` : null,
        log.repsCompleted ? `${log.repsCompleted} reps` : null,
        log.weightUsed ? `${log.weightUsed} kg` : null,
      ]
        .filter(Boolean)
        .join(' · ') || 'Workout logged'}
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

const InfoRow = ({ label, value }: { label: string; value: string }) => (
  <div className="flex items-center justify-between gap-4 rounded-xl bg-muted px-3 py-2">
    <span className="text-muted-foreground">{label}</span>
    <span className="font-semibold text-foreground">{value}</span>
  </div>
)

const EmptyState = ({
  icon: Icon,
  title,
  description,
}: {
  icon: IconType
  title: string
  description: string
}) => (
  <div className="rounded-2xl border border-dashed border-border bg-muted/30 p-5">
    <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-background">
      <Icon className="h-5 w-5 text-muted-foreground" />
    </div>
    <p className="mt-4 text-sm font-semibold text-foreground">{title}</p>
    <p className="mt-1 text-sm text-muted-foreground">{description}</p>
  </div>
)

const SkeletonBlock = ({ className = '' }: { className?: string }) => (
  <div className={`animate-pulse rounded-2xl border border-border bg-card ${className}`}>
    <div className="h-full rounded-2xl bg-muted/80" />
  </div>
)

export default WorkoutDetail
