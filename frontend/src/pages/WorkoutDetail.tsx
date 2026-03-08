import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Activity, Clock, Dumbbell, PauseCircle } from 'lucide-react'
import {
  type ClientWorkoutPlanResponse,
  type WorkoutPlanResponse,
  getMyAssignmentById,
  getWorkoutPlanById,
} from '../services/workout.service'
import { LogWorkoutModal } from '../components/workouts/LogWorkoutModal'

const WorkoutDetail = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [assignment, setAssignment] = useState<ClientWorkoutPlanResponse | null>(
    null,
  )
  const [plan, setPlan] = useState<WorkoutPlanResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isLogOpen, setIsLogOpen] = useState(false)
  const [toast, setToast] = useState<string | null>(null)

  useEffect(() => {
    if (!id) {
      navigate('/workouts', { replace: true })
      return
    }

    const load = async () => {
      setIsLoading(true)
      setError(null)
      try {
        const assignmentData = await getMyAssignmentById(id)
        setAssignment(assignmentData)
        const planData = await getWorkoutPlanById(assignmentData.workoutPlan.id)
        setPlan(planData)
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err)
        setError('Unable to load this workout plan.')
      } finally {
        setIsLoading(false)
      }
    }

    void load()
  }, [id, navigate])

  const handleLogged = () => {
    setToast('Workout successfully logged!')
    setTimeout(() => setToast(null), 2600)
  }

  if (isLoading) {
    return (
      <div className="space-y-4">
        <div className="h-10 w-40 animate-pulse rounded-full bg-slate-800/70" />
        <div className="h-40 animate-pulse rounded-2xl bg-slate-900/70" />
        <div className="h-40 animate-pulse rounded-2xl bg-slate-900/70" />
      </div>
    )
  }

  if (!plan || !assignment) {
    return (
      <div className="rounded-2xl border border-red-500/40 bg-red-500/10 px-4 py-3 text-sm text-red-100">
        {error ?? 'Workout plan not found.'}
      </div>
    )
  }

  return (
    <div className="relative space-y-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-emerald-300">
            Workout plan
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-slate-50 md:text-3xl">
            {plan.name}
          </h1>
          <p className="mt-1 text-sm text-slate-400 max-w-2xl">
            {plan.description}
          </p>
          <div className="mt-3 flex flex-wrap items-center gap-2 text-[11px] text-slate-400">
            <span className="inline-flex items-center rounded-full bg-slate-900/80 px-2 py-1 uppercase tracking-wide text-slate-200">
              {plan.difficultyLevel}
            </span>
            <span className="inline-flex items-center gap-1 rounded-full bg-slate-900/80 px-2 py-1">
              <Clock className="h-3.5 w-3.5 text-slate-500" />
              {plan.sessionsPerWeek} sessions / week · {plan.durationWeeks} weeks
            </span>
            <span className="inline-flex items-center gap-1 rounded-full bg-slate-900/80 px-2 py-1">
              <Activity className="h-3.5 w-3.5 text-emerald-400" />
              {assignment.completionPercentage
                ? `${Math.round(assignment.completionPercentage)}% complete`
                : 'Just getting started'}
            </span>
          </div>
        </div>

        <div className="flex flex-col items-stretch gap-2 sm:flex-row sm:items-center">
          <button
            type="button"
            className="inline-flex items-center justify-center gap-2 rounded-full border border-slate-800/80 bg-slate-900/80 px-4 py-2 text-xs font-medium text-slate-200 shadow-sm shadow-slate-950/40 transition hover:border-emerald-500/80 hover:bg-emerald-500/10"
            onClick={() => navigate('/workouts')}
          >
            Back to plans
          </button>
          <button
            type="button"
            onClick={() => setIsLogOpen(true)}
            className="inline-flex items-center justify-center gap-2 rounded-full bg-gradient-to-r from-emerald-400 via-cyan-400 to-sky-500 px-4 py-2 text-xs font-semibold text-slate-950 shadow-soft-glow"
          >
            <Dumbbell className="h-3.5 w-3.5" />
            Start workout
          </button>
        </div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="rounded-2xl border border-slate-800/80 bg-slate-900/80 p-4 shadow-sm shadow-slate-950/40"
      >
        <div className="mb-4 flex items-center justify-between">
          <div>
            <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
              Exercises
            </p>
            <p className="mt-1 text-sm text-slate-300">
              Follow the sequence as prescribed by your coach.
            </p>
          </div>
          <div className="inline-flex items-center gap-1 rounded-full bg-slate-950/80 px-3 py-1 text-[11px] text-slate-300">
            <PauseCircle className="h-3.5 w-3.5 text-slate-500" />
            Rest and tempo suggestions included
          </div>
        </div>

        <div className="space-y-3">
          {plan.exercises.map((exercise) => (
            <ExerciseRow key={exercise.id} exercise={exercise} />
          ))}
        </div>
      </motion.div>

      <LogWorkoutModal
        isOpen={isLogOpen}
        onClose={() => setIsLogOpen(false)}
        assignment={assignment}
        exercises={plan.exercises}
        onLogged={handleLogged}
      />

      {toast && (
        <div className="fixed bottom-6 right-6 z-50 rounded-2xl border border-emerald-500/50 bg-slate-900/95 px-4 py-2 text-sm text-emerald-200 shadow-soft-glow">
          {toast}
        </div>
      )}
    </div>
  )
}

type ExerciseRowProps = {
  exercise: WorkoutPlanExerciseResponse
}

const ExerciseRow = ({ exercise }: ExerciseRowProps) => {
  const hasSetsReps = exercise.sets && exercise.reps

  return (
    <div className="flex items-start justify-between gap-3 rounded-2xl border border-slate-800/80 bg-slate-950/80 px-3 py-3">
      <div className="flex items-start gap-3">
        <div className="mt-0.5 flex h-9 w-9 items-center justify-center rounded-2xl bg-slate-900/90">
          <Dumbbell className="h-4 w-4 text-emerald-300" />
        </div>
        <div>
          <p className="text-xs uppercase tracking-[0.18em] text-slate-500">
            Day {exercise.dayNumber} · #{exercise.orderIndex + 1}
          </p>
          <p className="text-sm font-semibold text-slate-50">
            {exercise.exercise.name}
          </p>
          <p className="mt-1 text-[11px] text-slate-500">
            {exercise.exercise.category} · {exercise.exercise.primaryMuscleGroup}
          </p>
        </div>
      </div>
      <div className="flex flex-col items-end gap-1 text-[11px] text-slate-400">
        {hasSetsReps && (
          <span>
            {exercise.sets} sets × {exercise.reps} reps
          </span>
        )}
        {exercise.durationSeconds && (
          <span>Duration: {exercise.durationSeconds}s</span>
        )}
        {exercise.restSeconds && (
          <span className="text-slate-500">
            Rest: {exercise.restSeconds}s between sets
          </span>
        )}
      </div>
    </div>
  )
}

export default WorkoutDetail

