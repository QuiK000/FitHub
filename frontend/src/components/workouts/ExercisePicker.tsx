import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Search } from 'lucide-react'
import { Input } from '../ui/input'
import {
  getExercises,
  type ExerciseResponse,
  type ExerciseCategory,
  type MuscleGroup,
} from '../../services/workout.service'

type ExercisePickerProps = {
  selectedExerciseId: string
  onSelect: (exercise: ExerciseResponse) => void
}

const exerciseCategories: ExerciseCategory[] = [
  'STRENGTH', 'CARDIO', 'FLEXIBILITY', 'BALANCE', 'PLYOMETRIC',
  'OLYMPIC_LIFTING', 'POWERLIFTING', 'CALISTHENICS', 'STRETCHING', 'YOGA', 'PILATES',
]

const muscleGroups: MuscleGroup[] = [
  'CHEST', 'BACK', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'FOREARMS', 'CORE', 'ABS',
  'OBLIQUES', 'LOWER_BACK', 'QUADS', 'HAMSTRINGS', 'GLUTES', 'CALVES', 'FULL_BODY',
]

export const ExercisePicker = ({
  selectedExerciseId,
  onSelect,
}: ExercisePickerProps) => {
  const { t } = useTranslation(['workouts'])
  const [exercises, setExercises] = useState<ExerciseResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState<string>('')
  const [muscleFilter, setMuscleFilter] = useState<string>('')

  const loadExercises = async () => {
    setIsLoading(true)
    try {
      const result = await getExercises(0, 100)
      setExercises(result.content)
    } catch (err) {
      console.error(err)
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadExercises()
  }, [])

  const filteredExercises = exercises.filter((exercise) => {
    const matchesSearch = !search || 
      exercise.name.toLowerCase().includes(search.toLowerCase())
    const matchesCategory = !categoryFilter || exercise.category === categoryFilter
    const matchesMuscle = !muscleFilter || exercise.primaryMuscleGroup === muscleFilter
    return matchesSearch && matchesCategory && matchesMuscle
  })

  return (
    <div className="space-y-3">
      <div className="flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder={t('exercisePicker.searchPlaceholder')}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9"
          />
        </div>
      </div>

      <div className="flex gap-2">
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="flex h-10 rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
        >
          <option value="">{t('exercisePicker.allCategories')}</option>
          {exerciseCategories.map((cat) => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>

        <select
          value={muscleFilter}
          onChange={(e) => setMuscleFilter(e.target.value)}
          className="flex h-10 rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
        >
          <option value="">{t('exercisePicker.allMuscles')}</option>
          {muscleGroups.map((mg) => (
            <option key={mg} value={mg}>{mg}</option>
          ))}
        </select>
      </div>

      <div className="max-h-60 overflow-y-auto rounded-xl border border-border">
        {isLoading ? (
          <div className="p-4 text-center text-sm text-muted-foreground">
            {t('common:buttons.loading')}
          </div>
        ) : filteredExercises.length ? (
          filteredExercises.map((exercise) => (
            <button
              key={exercise.id}
              type="button"
              onClick={() => onSelect(exercise)}
              className={`flex w-full items-center justify-between border-b border-border p-3 text-left transition hover:bg-accent last:border-b-0 ${
                selectedExerciseId === exercise.id ? 'bg-primary/10' : ''
              }`}
            >
              <div>
                <p className="font-medium text-foreground">{exercise.name}</p>
                <p className="text-xs text-muted-foreground">
                  {exercise.category} · {exercise.primaryMuscleGroup}
                </p>
              </div>
              {selectedExerciseId === exercise.id && (
                <span className="text-xs font-medium text-primary">✓</span>
              )}
            </button>
          ))
        ) : (
          <div className="p-4 text-center text-sm text-muted-foreground">
            {t('exercisePicker.noResults')}
          </div>
        )}
      </div>
    </div>
  )
}
