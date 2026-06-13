import { useEffect, useMemo, useState, type ComponentType, type SVGProps } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  ArrowRight,
  CalendarDays,
  CheckCircle2,
  Dumbbell,
  History,
  ListChecks,
  Play,
  Trophy,
  User2,
} from 'lucide-react'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import { useAuthStore } from '../store/useAuthStore'
import {
  type ClientWorkoutPlanResponse,
  type WorkoutLogResponse,
  type WorkoutPlanExerciseResponse,
  getMyActiveAssignments,
  getMyAssignments,
  getMyWorkoutLogs,
  getWorkoutPlanById,
} from '../services/workout.service'
import { LogWorkoutModal } from '../components/workouts/LogWorkoutModal'

type WorkoutsState = {
  activeAssignments: ClientWorkoutPlanResponse[]
  allAssignments: ClientWorkoutPlanResponse[]
  recentLogs: WorkoutLogResponse[]
}

const Workouts = () => {
  const user = useAuthStore((state) => state.user)
  const roles = useAuthStore((state) => state.roles)
  const isClient = roles.includes('CLIENT')
  const [state, setState] = useState<WorkoutsState>({
    activeAssignments: [],
    allAssignments: [],
    recentLogs: [],
  })
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedAssignment, setSelectedAssignment] = useState<ClientWorkoutPlanResponse | null>(null)
  const [selectedExercises, setSelectedExercises] = useState<WorkoutPlanExerciseResponse[]>([])
  const [isLogOpen, setIsLogOpen] = useState(false)

  const handleStartWorkout = async (assignment: ClientWorkoutPlanResponse) => {
    setSelectedAssignment(assignment)
    try {
      const plan = await getWorkoutPlanById(assignment.workoutPlan.id)
      setSelectedExercises(plan.exercises)
    } catch {
      setSelectedExercises([])
    }
    setIsLogOpen(true)
  }

  const loadData = async () => {
    if (!isClient) {
      setIsLoading(false)
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      const [activeAssignments, allAssignments, recentLogsPage] =
        await Promise.all([
          getMyActiveAssignments(),
          getMyAssignments(),
          getMyWorkoutLogs(0, 5),
        ])

      setState({
        activeAssignments,
        allAssignments,
        recentLogs: recentLogsPage.content,
      })
    } catch (err) {
      console.error(err)
      setError('Unable to load your workout module right now.')
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadData()
  }, [isClient])

  useEffect(() => {
    void loadData()
  }, [isClient])

  const titleName =
    user?.clientProfile?.firstname ?? user?.clientProfile?.lastname ?? 'Athlete'

  const totalCompleted = useMemo(
    () =>
      state.allAssignments.reduce(
        (sum, assignment) => sum + (assignment.completedWorkouts ?? 0),
        0,
      ),
    [state.allAssignments],
  )

  const averageCompletion = useMemo(() => {
    if (!state.allAssignments.length) return 0
    const total = state.allAssignments.reduce(
      (sum, assignment) => sum + (assignment.completionPercentage ?? 0),
      0,
    )
    return Math.round(total / state.allAssignments.length)
  }, [state.allAssignments])

  const handleLogged = async () => {
    setIsLogOpen(false)
    setSelectedAssignment(null)
    setSelectedExercises([])
    await loadData()
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Workouts
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            Your training workspace, {titleName}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            Follow assigned plans, review recent logs, and keep your next session
            easy to find.
          </p>
        </div>

        <Link
          to="/sessions"
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-card px-4 text-sm font-semibold text-foreground shadow-soft transition-all hover:bg-accent"
        >
          <CalendarDays className="h-4 w-4" />
          View sessions
        </Link>
      </div>

      {error && !isLoading && (
        <div className="rounded-2xl border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-3">
        {isLoading ? (
          Array.from({ length: 3 }).map((_, index) => (
            <SkeletonCard key={index} className="h-32" />
          ))
        ) : (
          <>
            <SummaryCard
              icon={Dumbbell}
              label="Active plans"
              value={state.activeAssignments.length.toString()}
              detail="Currently assigned by your trainer"
            />
            <SummaryCard
              icon={ListChecks}
              label="Logged workouts"
              value={totalCompleted.toString()}
              detail="Completed across all assignments"
            />
            <SummaryCard
              icon={Trophy}
              label="Average completion"
              value={`${averageCompletion}%`}
              detail="Across your plan history"
            />
          </>
        )}
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,1.3fr),minmax(360px,0.7fr)]">
        <Card>
          <CardHeader>
            <CardTitle>Active Workout Plans</CardTitle>
            <CardDescription>
              Start here when you are ready to train.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="grid gap-4 md:grid-cols-2">
                <SkeletonBlock />
                <SkeletonBlock />
              </div>
            ) : state.activeAssignments.length ? (
              <div className="grid gap-4 md:grid-cols-2">
                {state.activeAssignments.map((assignment) => (
                  <WorkoutCard
                    key={assignment.id}
                    assignment={assignment}
                    onStart={() => handleStartWorkout(assignment)}
                  />
                ))}
              </div>
            ) : (
              <EmptyState
                icon={Dumbbell}
                title="No active workout plans"
                description="Your trainer can assign a workout plan when your next training block is ready."
                actionLabel="Explore trainers"
                to="/trainers"
              />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Recent Logs</CardTitle>
            <CardDescription>
              Your latest completed exercises.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-3">
                <SkeletonLine />
                <SkeletonLine />
                <SkeletonLine />
              </div>
            ) : state.recentLogs.length ? (
              <div className="space-y-3">
                {state.recentLogs.map((log) => (
                  <LogRow key={log.id} log={log} />
                ))}
              </div>
            ) : (
              <EmptyState
                icon={History}
                title="No workouts logged yet"
                description="Open an assigned plan and log your first workout when you finish a set."
                actionLabel="View active plans"
                to="/workouts"
              />
            )}
          </CardContent>
        </Card>
      </section>

      <Card>
        <CardHeader>
          <CardTitle>Plan History</CardTitle>
          <CardDescription>
            Completed, cancelled, and in-progress assignments stay visible here.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-3">
              <SkeletonLine />
              <SkeletonLine />
            </div>
          ) : state.allAssignments.length ? (
            <div className="divide-y divide-border overflow-hidden rounded-2xl border border-border">
              {state.allAssignments.map((assignment) => (
                <HistoryRow key={assignment.id} assignment={assignment} />
              ))}
            </div>
          ) : (
            <EmptyState
              icon={CheckCircle2}
              title="No assignment history yet"
              description="Assigned plans will appear here once your trainer starts building your program."
              actionLabel="Back to dashboard"
              to="/dashboard"
            />
          )}
        </CardContent>
      </Card>

      {selectedAssignment && (
        <LogWorkoutModal
          isOpen={isLogOpen}
          onClose={() => {
            setIsLogOpen(false)
            setSelectedAssignment(null)
            setSelectedExercises([])
          }}
          assignment={selectedAssignment}
          exercises={selectedExercises}
          onLogged={handleLogged}
        />
      )}
    </div>
  )
}

type IconType = ComponentType<SVGProps<SVGSVGElement>>

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

const SummaryCard = ({
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
        <p className="mt-2 text-2xl font-bold text-foreground">{value}</p>
        <p className="mt-1 text-xs text-muted-foreground">{detail}</p>
      </div>
      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
        <Icon className="h-5 w-5 text-primary" />
      </div>
    </CardContent>
  </Card>
)

const WorkoutCard = ({
  assignment,
  onStart,
}: {
  assignment: ClientWorkoutPlanResponse
  onStart: () => void
}) => {
  const plan = assignment.workoutPlan
  const progress = clampPercentage(assignment.completionPercentage ?? 0)
  const trainerName =
    [plan.trainer.firstname, plan.trainer.lastname].filter(Boolean).join(' ') ||
    'Trainer'

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.25 }}
      className="rounded-2xl border border-border bg-background p-4 shadow-soft"
    >
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {formatEnum(assignment.status)}
          </p>
          <h2 className="mt-2 text-base font-semibold text-foreground">
            {plan.name}
          </h2>
          <p className="mt-1 text-sm text-muted-foreground">
            {formatEnum(plan.difficultyLevel)} · {plan.sessionsPerWeek} sessions/week
          </p>
        </div>
        <span className="rounded-full bg-primary/10 px-2.5 py-1 text-xs font-semibold text-primary">
          {plan.durationWeeks} weeks
        </span>
      </div>

      <ProgressBar value={progress} className="mt-5" />
      <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
        <span>{Math.round(progress)}% complete</span>
        <span>
          {assignment.completedWorkouts ?? 0}/{assignment.totalWorkouts ?? 0} logged
        </span>
      </div>

      <div className="mt-5 flex items-center justify-between gap-3">
        <div className="inline-flex min-w-0 items-center gap-2 rounded-full bg-muted px-3 py-1 text-xs text-muted-foreground">
          <User2 className="h-3.5 w-3.5 shrink-0" />
          <span className="truncate">{trainerName}</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onStart}
            className="inline-flex shrink-0 items-center gap-1.5 rounded-xl bg-primary px-3 py-1.5 text-xs font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
          >
            <Play className="h-3.5 w-3.5" />
            Start
          </button>
          <Link
            to={`/workouts/${assignment.id}`}
            className="inline-flex shrink-0 items-center gap-1 text-sm font-semibold text-primary hover:underline"
          >
            Details
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </div>
    </motion.div>
  )
}

const LogRow = ({ log }: { log: WorkoutLogResponse }) => (
  <div className="rounded-2xl border border-border bg-background p-3">
    <div className="flex items-start justify-between gap-3">
      <div>
        <p className="text-sm font-semibold text-foreground">
          {log.exercise.name}
        </p>
        <p className="mt-1 text-xs text-muted-foreground">
          {formatDate(log.workoutDate)} · {formatEnum(log.exercise.category)}
        </p>
      </div>
      {log.difficultyRating && (
        <span className="rounded-full bg-muted px-2 py-1 text-xs text-muted-foreground">
          RPE {log.difficultyRating}/5
        </span>
      )}
    </div>
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

const HistoryRow = ({
  assignment,
}: {
  assignment: ClientWorkoutPlanResponse
}) => (
  <Link
    to={`/workouts/${assignment.id}`}
    className="grid gap-3 bg-card p-4 transition hover:bg-accent md:grid-cols-[minmax(0,1fr),auto,auto]"
  >
    <div>
      <p className="font-semibold text-foreground">{assignment.workoutPlan.name}</p>
      <p className="mt-1 text-xs text-muted-foreground">
        Assigned {formatDate(assignment.assignedDate)}
      </p>
    </div>
    <div className="text-sm text-muted-foreground">
      {formatEnum(assignment.status)}
    </div>
    <div className="text-sm font-semibold text-primary">
      {Math.round(assignment.completionPercentage ?? 0)}%
    </div>
  </Link>
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
  <div className="flex min-h-52 flex-col justify-between rounded-2xl border border-dashed border-border bg-muted/30 p-5">
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

const SkeletonCard = ({ className = '' }: { className?: string }) => (
  <div className={`animate-pulse rounded-2xl border border-border bg-card ${className}`}>
    <div className="h-full rounded-2xl bg-muted/80" />
  </div>
)

const SkeletonBlock = () => (
  <div className="h-52 animate-pulse rounded-2xl bg-muted" />
)

const SkeletonLine = () => (
  <div className="h-16 animate-pulse rounded-xl bg-muted" />
)

export default Workouts
