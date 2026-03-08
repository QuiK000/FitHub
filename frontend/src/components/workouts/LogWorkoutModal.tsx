import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Button } from '../ui/button'
import { Input } from '../ui/input'
import type {
  ClientWorkoutPlanResponse,
  WorkoutPlanExerciseResponse,
  LogWorkoutRequest,
} from '../../services/workout.service'
import { logWorkout } from '../../services/workout.service'

type LogWorkoutModalProps = {
  isOpen: boolean
  onClose: () => void
  assignment: ClientWorkoutPlanResponse
  exercises: WorkoutPlanExerciseResponse[]
  onLogged?: () => void
}

export const LogWorkoutModal = ({
  isOpen,
  onClose,
  assignment,
  exercises,
  onLogged,
}: LogWorkoutModalProps) => {
  const [exerciseId, setExerciseId] = useState<string>(
    exercises[0]?.exercise.exerciseId ?? '',
  )
  const [setsCompleted, setSetsCompleted] = useState<number | ''>('')
  const [repsCompleted, setRepsCompleted] = useState<number | ''>('')
  const [weightUsed, setWeightUsed] = useState<number | ''>('')
  const [durationSeconds, setDurationSeconds] = useState<number | ''>('')
  const [difficulty, setDifficulty] = useState<number | ''>(3)
  const [notes, setNotes] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async () => {
    if (!exerciseId) {
      setError('Please select an exercise to log.')
      return
    }

    setError(null)
    setIsSubmitting(true)

    try {
      const payload: LogWorkoutRequest = {
        exerciseId,
        clientWorkoutPlanId: assignment.id,
        setsCompleted: setsCompleted === '' ? undefined : Number(setsCompleted),
        repsCompleted: repsCompleted === '' ? undefined : Number(repsCompleted),
        weightUsed: weightUsed === '' ? undefined : Number(weightUsed),
        durationSeconds:
          durationSeconds === '' ? undefined : Number(durationSeconds),
        difficultRating: difficulty === '' ? undefined : Number(difficulty),
        notes: notes || undefined,
      }

      await logWorkout(payload)
      if (onLogged) onLogged()
      onClose()
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error(err)
      setError('Unable to log your workout. Please try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-950/70 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.98 }}
            className="w-full max-w-xl rounded-3xl border border-slate-800/80 bg-slate-950/95 p-6 shadow-[0_24px_80px_rgba(15,23,42,0.95)]"
          >
            <div className="mb-4 flex items-center justify-between">
              <div>
                <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
                  Log workout
                </p>
                <p className="mt-1 text-sm text-slate-300">
                  {assignment.workoutPlan.name}
                </p>
              </div>
              <button
                type="button"
                onClick={onClose}
                className="h-8 w-8 rounded-full border border-slate-800/80 bg-slate-900/80 text-xs text-slate-400 transition hover:border-slate-700 hover:text-slate-100"
              >
                ✕
              </button>
            </div>

            <div className="space-y-4 text-sm text-slate-200">
              <div className="space-y-1.5">
                <p className="text-xs text-slate-400">Exercise</p>
                <select
                  value={exerciseId}
                  onChange={(event) => setExerciseId(event.target.value)}
                  className="w-full rounded-xl border border-slate-800/80 bg-slate-900/80 px-3 py-2 text-sm text-slate-100 focus:border-emerald-500/80 focus:outline-none focus:ring-1 focus:ring-emerald-500/70"
                >
                  {exercises.map((exercise) => (
                    <option
                      key={exercise.id}
                      value={exercise.exercise.exerciseId}
                    >
                      {exercise.exercise.name} · Day {exercise.dayNumber}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid gap-3 md:grid-cols-2">
                <div className="space-y-1.5">
                  <p className="text-xs text-slate-400">Sets completed</p>
                  <Input
                    type="number"
                    min={1}
                    value={setsCompleted}
                    onChange={(event) =>
                      setSetsCompleted(
                        event.target.value === ''
                          ? ''
                          : Number(event.target.value),
                      )
                    }
                    placeholder="e.g. 3"
                  />
                </div>
                <div className="space-y-1.5">
                  <p className="text-xs text-slate-400">Reps completed</p>
                  <Input
                    type="number"
                    min={1}
                    value={repsCompleted}
                    onChange={(event) =>
                      setRepsCompleted(
                        event.target.value === ''
                          ? ''
                          : Number(event.target.value),
                      )
                    }
                    placeholder="e.g. 10"
                  />
                </div>
              </div>

              <div className="grid gap-3 md:grid-cols-3">
                <div className="space-y-1.5">
                  <p className="text-xs text-slate-400">Weight used (kg)</p>
                  <Input
                    type="number"
                    min={0}
                    step={0.5}
                    value={weightUsed}
                    onChange={(event) =>
                      setWeightUsed(
                        event.target.value === ''
                          ? ''
                          : Number(event.target.value),
                      )
                    }
                    placeholder="Optional"
                  />
                </div>
                <div className="space-y-1.5">
                  <p className="text-xs text-slate-400">Duration (sec)</p>
                  <Input
                    type="number"
                    min={0}
                    value={durationSeconds}
                    onChange={(event) =>
                      setDurationSeconds(
                        event.target.value === ''
                          ? ''
                          : Number(event.target.value),
                      )
                    }
                    placeholder="Optional"
                  />
                </div>
                <div className="space-y-1.5">
                  <p className="text-xs text-slate-400">Difficulty (1–5)</p>
                  <Input
                    type="number"
                    min={1}
                    max={5}
                    value={difficulty}
                    onChange={(event) =>
                      setDifficulty(
                        event.target.value === ''
                          ? ''
                          : Number(event.target.value),
                      )
                    }
                    placeholder="3"
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <p className="text-xs text-slate-400">Notes</p>
                <textarea
                  className="min-h-[72px] w-full rounded-xl border border-slate-800/80 bg-slate-900/80 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-500 focus:border-emerald-500/80 focus:outline-none focus:ring-1 focus:ring-emerald-500/70"
                  placeholder="How did this workout feel?"
                  value={notes}
                  onChange={(event) => setNotes(event.target.value)}
                />
              </div>

              {error && (
                <div className="rounded-xl border border-red-500/40 bg-red-500/10 px-3 py-2 text-xs text-red-200">
                  {error}
                </div>
              )}

              <div className="mt-4 flex items-center justify-end gap-2">
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="rounded-full px-4"
                  onClick={onClose}
                  disabled={isSubmitting}
                >
                  Cancel
                </Button>
                <Button
                  type="button"
                  size="sm"
                  className="rounded-full px-4"
                  disabled={isSubmitting}
                  onClick={handleSubmit}
                >
                  {isSubmitting && (
                    <span className="inline-flex h-4 w-4 animate-spin rounded-full border-[2px] border-slate-900 border-t-emerald-400" />
                  )}
                  <span className="ml-1">
                    {isSubmitting ? 'Logging…' : 'Log workout'}
                  </span>
                </Button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  )
}

