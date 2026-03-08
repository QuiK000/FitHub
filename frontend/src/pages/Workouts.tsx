import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { ArrowRight, Flame, Timer, User2 } from 'lucide-react'
import { useAuthStore } from '../store/useAuthStore'
import {
  type ClientWorkoutPlanResponse,
  getMyActiveAssignments,
} from '../services/workout.service'

const Workouts = () => {
  const user = useAuthStore((state) => state.user)
  const [plans, setPlans] = useState<ClientWorkoutPlanResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      setIsLoading(true)
      setError(null)
      try {
        const data = await getMyActiveAssignments()
        setPlans(data)
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err)
        setError('Unable to load your workout plans.')
      } finally {
        setIsLoading(false)
      }
    }

    void load()
  }, [])

  const titleName =
    user?.clientProfile?.firstname ?? user?.clientProfile?.lastname ?? 'Athlete'

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-emerald-300">
            Workouts
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-slate-50 md:text-3xl">
            Your active plans, {titleName}
          </h1>
          <p className="mt-1 text-sm text-slate-400">
            Follow structured programs designed by your trainer to hit your next
            performance milestone.
          </p>
        </div>
      </div>

      {error && (
        <div className="rounded-2xl border border-red-500/40 bg-red-500/10 px-4 py-3 text-sm text-red-100">
          {error}
        </div>
      )}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {isLoading
          ? Array.from({ length: 3 }).map((_, index) => (
              // eslint-disable-next-line react/no-array-index-key
              <div
                key={index}
                className="h-40 rounded-2xl border border-slate-800/80 bg-slate-900/70 shadow-sm shadow-slate-950/40"
              >
                <div className="h-full w-full animate-pulse rounded-2xl bg-gradient-to-br from-slate-900/80 via-slate-800/60 to-slate-900/80" />
              </div>
            ))
          : plans.map((assignment) => (
              <WorkoutCard key={assignment.id} assignment={assignment} />
            ))}
      </div>

      {!isLoading && !error && plans.length === 0 && (
        <div className="rounded-2xl border border-slate-800/80 bg-slate-900/70 px-4 py-6 text-sm text-slate-300 shadow-sm shadow-slate-950/40">
          You don&apos;t have any active workout plans yet. Your coach can
          assign one from the FitHub studio dashboard.
        </div>
      )}
    </div>
  )
}

type WorkoutCardProps = {
  assignment: ClientWorkoutPlanResponse
}

const WorkoutCard = ({ assignment }: WorkoutCardProps) => {
  const plan = assignment.workoutPlan
  const difficultyLabel = plan.difficultyLevel

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      whileHover={{ y: -3, scale: 1.01 }}
      className="group relative overflow-hidden rounded-2xl border border-slate-800/80 bg-slate-900/80 p-4 shadow-sm shadow-slate-950/40"
    >
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-emerald-300">
            Plan
          </p>
          <h2 className="mt-1 text-sm font-semibold text-slate-50 line-clamp-2">
            {plan.name}
          </h2>
          <p className="mt-1 text-xs text-slate-400">
            {plan.sessionsPerWeek} sessions · {plan.durationWeeks} weeks
          </p>
        </div>
        <span className="inline-flex items-center rounded-full bg-slate-900/90 px-2 py-1 text-[10px] uppercase tracking-wide text-slate-300">
          {difficultyLabel}
        </span>
      </div>

      <div className="mt-4 flex items-center justify-between text-[11px] text-slate-400">
        <div className="flex items-center gap-2">
          <Timer className="h-3.5 w-3.5 text-slate-500" />
          <span>
            {assignment.completedWorkouts ?? 0}/{assignment.totalWorkouts ?? 0}{' '}
            sessions logged
          </span>
        </div>
        <div className="flex items-center gap-1 text-emerald-300">
          <Flame className="h-3.5 w-3.5" />
          <span>
            {Math.round(assignment.completionPercentage ?? 0)}
            % complete
          </span>
        </div>
      </div>

      <div className="mt-4 flex items-center justify-between">
        <div className="inline-flex items-center gap-2 rounded-full bg-slate-900/90 px-3 py-1 text-[11px] text-slate-300">
          <User2 className="h-3.5 w-3.5 text-slate-500" />
          <span>{plan.trainer.fullName}</span>
        </div>
        <Link
          to={`/workouts/${assignment.id}`}
          className="inline-flex items-center gap-1 text-[11px] font-medium text-emerald-300 underline-offset-2 hover:underline"
        >
          View details
          <ArrowRight className="h-3.5 w-3.5" />
        </Link>
      </div>
    </motion.div>
  )
}

export default Workouts

