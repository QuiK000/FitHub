import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import {
  CheckCircle2,
  Dumbbell,
  Plus,
  Users2,
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
  getMyPlans,
  getAssignedPlans,
  type ClientWorkoutPlanResponse,
  type WorkoutPlanResponse,
} from '../services/workout.service'
import { CreatePlanModal } from '../components/workouts/CreatePlanModal'

const TrainerWorkouts = () => {
  const { t } = useTranslation(['sessions', 'common'])
  const user = useAuthStore((state) => state.user)
  const [plans, setPlans] = useState<WorkoutPlanResponse[]>([])
  const [assignments, setAssignments] = useState<ClientWorkoutPlanResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isCreateOpen, setIsCreateOpen] = useState(false)

  const loadData = async () => {
    setIsLoading(true)
    try {
      const [plansResult, assignmentsResult] = await Promise.allSettled([
        getMyPlans(0, 20),
        getAssignedPlans(0, 20),
      ])
      if (plansResult.status === 'fulfilled') setPlans(plansResult.value.content)
      if (assignmentsResult.status === 'fulfilled') setAssignments(assignmentsResult.value.content)
    } catch (err) {
      console.error(err)
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadData()
  }, [])

  const trainerName = user?.trainerProfile?.firstname ?? 'Trainer'

  const activeAssignments = assignments.filter((a) => a.status === 'IN_PROGRESS' || a.status === 'ASSIGNED')
  const completedAssignments = assignments.filter((a) => a.status === 'COMPLETED')

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('title')}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            Trainer workspace, {trainerName}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            Manage your workout plans, client assignments, and training sessions.
          </p>
        </div>
        <button
          type="button"
          onClick={() => setIsCreateOpen(true)}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
        >
          <Plus className="h-4 w-4" />
          Create plan
        </button>
      </div>

      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-40 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : (
        <>
          <section className="grid gap-4 md:grid-cols-3">
            <SummaryCard
              icon={Dumbbell}
              label="My Plans"
              value={plans.length.toString()}
              detail="Workout plans created"
              tone="bg-violet-500"
            />
            <SummaryCard
              icon={Users2}
              label="Active Assignments"
              value={activeAssignments.length.toString()}
              detail="Plans assigned to clients"
              tone="bg-emerald-500"
            />
            <SummaryCard
              icon={CheckCircle2}
              label="Completed"
              value={completedAssignments.length.toString()}
              detail="Successfully completed"
              tone="bg-blue-500"
            />
          </section>

          <section className="grid gap-4 xl:grid-cols-[minmax(0,1.2fr),minmax(360px,0.8fr)]">
            <Card>
              <CardHeader>
                <CardTitle>My Workout Plans</CardTitle>
                <CardDescription>Plans you've created for your clients.</CardDescription>
              </CardHeader>
              <CardContent>
                {plans.length ? (
                  <div className="grid gap-3 md:grid-cols-2">
                    {plans.slice(0, 6).map((plan) => (
                      <motion.div
                        key={plan.id}
                        initial={{ opacity: 0, y: 8 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="rounded-2xl border border-border bg-background p-4"
                      >
                        <div className="flex items-start justify-between gap-2">
                          <div>
                            <p className="text-sm font-semibold text-foreground">{plan.name}</p>
                            <p className="mt-1 text-xs text-muted-foreground">
                              {formatEnum(plan.difficultyLevel)} · {plan.durationWeeks} weeks
                            </p>
                          </div>
                          <span className={`rounded-full px-2 py-1 text-xs font-semibold ${plan.active ? 'bg-emerald-500/10 text-emerald-600' : 'bg-muted text-muted-foreground'}`}>
                            {plan.active ? 'Active' : 'Inactive'}
                          </span>
                        </div>
                        <p className="mt-2 line-clamp-2 text-xs text-muted-foreground">{plan.description}</p>
                        <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
                          <span>{plan.exercises.length} exercises</span>
                          <span>{plan.sessionsPerWeek} sessions/week</span>
                        </div>
                      </motion.div>
                    ))}
                  </div>
                ) : (
                  <EmptyState
                    icon={Dumbbell}
                    title="No workout plans yet"
                    description="Create your first workout plan to assign to clients."
                  />
                )}
              </CardContent>
            </Card>

            <div className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Recent Assignments</CardTitle>
                  <CardDescription>Latest client plan assignments.</CardDescription>
                </CardHeader>
                <CardContent>
                  {assignments.length ? (
                    <div className="space-y-3">
                      {assignments.slice(0, 5).map((assignment) => (
                        <div key={assignment.id} className="rounded-xl bg-muted px-4 py-3">
                          <div className="flex items-center justify-between">
                            <div>
                              <p className="text-sm font-semibold text-foreground">
                                {assignment.workoutPlan.name}
                              </p>
                              <p className="mt-0.5 text-xs text-muted-foreground">
                                Assigned {formatDate(assignment.assignedDate)}
                              </p>
                            </div>
                            <StatusBadge status={assignment.status} />
                          </div>
                          {assignment.completionPercentage != null && (
                            <div className="mt-2">
                              <div className="h-1.5 overflow-hidden rounded-full bg-background">
                                <div
                                  className="h-full rounded-full bg-primary transition-all"
                                  style={{ width: `${assignment.completionPercentage}%` }}
                                />
                              </div>
                              <p className="mt-1 text-xs text-muted-foreground">
                                {Math.round(assignment.completionPercentage)}% complete
                              </p>
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  ) : (
                    <EmptyState
                      icon={Users2}
                      title="No assignments yet"
                      description="Assign your workout plans to clients to get started."
                    />
                  )}
                </CardContent>
              </Card>
            </div>
          </section>
        </>
      )}

      <CreatePlanModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onCreated={async () => {
          setIsCreateOpen(false)
          await loadData()
        }}
      />
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
  }).format(new Date(value))

const StatusBadge = ({ status }: { status: string }) => {
  const colors: Record<string, string> = {
    ASSIGNED: 'bg-amber-500/10 text-amber-600',
    IN_PROGRESS: 'bg-blue-500/10 text-blue-600',
    COMPLETED: 'bg-emerald-500/10 text-emerald-600',
    CANCELLED: 'bg-red-500/10 text-red-600',
  }
  return (
    <span className={`rounded-full px-2 py-1 text-xs font-semibold ${colors[status] ?? 'bg-muted text-muted-foreground'}`}>
      {formatEnum(status)}
    </span>
  )
}

type IconType = React.ComponentType<React.SVGProps<SVGSVGElement>>

const SummaryCard = ({
  icon: Icon,
  label,
  value,
  detail,
  tone,
}: {
  icon: IconType
  label: string
  value: string
  detail: string
  tone: string
}) => (
  <motion.div
    initial={{ opacity: 0, y: 10 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.25 }}
  >
    <Card className="h-full">
      <CardContent className="flex h-full flex-col justify-between p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <p className="text-xs font-medium text-muted-foreground">{label}</p>
            <p className="mt-2 text-2xl font-bold text-foreground">{value}</p>
            <p className="mt-1 text-xs text-muted-foreground">{detail}</p>
          </div>
          <div className={`flex h-10 w-10 items-center justify-center rounded-xl ${tone}`}>
            <Icon className="h-5 w-5 text-white" />
          </div>
        </div>
      </CardContent>
    </Card>
  </motion.div>
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
  <div className="flex min-h-40 flex-col items-center justify-center rounded-2xl border border-dashed border-border bg-muted/30 p-6 text-center">
    <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-background">
      <Icon className="h-5 w-5 text-muted-foreground" />
    </div>
    <p className="mt-3 text-sm font-semibold text-foreground">{title}</p>
    <p className="mt-1 max-w-xs text-xs text-muted-foreground">{description}</p>
  </div>
)

export default TrainerWorkouts
