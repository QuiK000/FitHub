import { useEffect, useState, type FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import { Dumbbell, Plus, Search, X } from 'lucide-react'
import { Input } from '../components/ui/input'
import { Button } from '../components/ui/button'
import { Label } from '../components/ui/label'
import { EmptyState } from '../components/ui/empty-state'
import { Pagination } from '../components/ui/pagination'
import {
  getExercises,
  createExercise,
  updateExercise,
  activateExercise,
  deactivateExercise,
  type ExerciseResponse,
  type CreateExerciseRequest,
  type UpdateExerciseRequest,
  type ExerciseCategory,
  type MuscleGroup,
} from '../services/workout.service'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

const exerciseCategories: ExerciseCategory[] = [
  'STRENGTH', 'CARDIO', 'FLEXIBILITY', 'BALANCE', 'PLYOMETRIC',
  'OLYMPIC_LIFTING', 'POWERLIFTING', 'CALISTHENICS', 'STRETCHING', 'YOGA', 'PILATES',
]

const muscleGroups: MuscleGroup[] = [
  'CHEST', 'BACK', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'FOREARMS', 'CORE', 'ABS',
  'OBLIQUES', 'LOWER_BACK', 'QUADS', 'HAMSTRINGS', 'GLUTES', 'CALVES', 'FULL_BODY',
]

const ExerciseManagement = () => {
  const { t } = useTranslation(['admin', 'common'])
  const [exercises, setExercises] = useState<ExerciseResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
  const [editingExercise, setEditingExercise] = useState<ExerciseResponse | null>(null)

  const loadExercises = async (page = 0, searchQuery?: string) => {
    setIsLoading(true)
    try {
      const result = await getExercises(page, 12)
      let filtered = result.content
      if (searchQuery) {
        filtered = filtered.filter(
          (e) =>
            e.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            e.category.toLowerCase().includes(searchQuery.toLowerCase()),
        )
      }
      setExercises(filtered)
      setTotalPages(result.totalPages)
      setCurrentPage(page)
    } catch (err) {
      console.error(err)
      toast.error(t('common:errors.loadFailed'))
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadExercises()
  }, [])

  const handleSearch = () => {
    void loadExercises(0, search.trim() || undefined)
  }

  const handleToggleActive = async (exercise: ExerciseResponse) => {
    try {
      if (exercise.active) {
        await deactivateExercise(exercise.id)
        toast.success(t('common:messages.updated'))
      } else {
        await activateExercise(exercise.id)
        toast.success(t('common:messages.updated'))
      }
      await loadExercises(currentPage, search.trim() || undefined)
    } catch (err) {
      toast.error(getApiErrorMessage(err))
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground">{t('exercises.title')}</h1>
          <p className="text-sm text-muted-foreground">{t('exercises.subtitle')}</p>
        </div>
        <Button onClick={() => setIsCreateModalOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          {t('exercises.create')}
        </Button>
      </div>

      <div className="flex gap-2">
        <Input
          placeholder={t('common:labels.search')}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
        />
        <Button variant="outline" onClick={handleSearch}>
          <Search className="h-4 w-4" />
        </Button>
      </div>

      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-48 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : exercises.length ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {exercises.map((exercise) => (
              <motion.div
                key={exercise.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="rounded-2xl border border-border bg-card p-4 shadow-soft"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
                      <Dumbbell className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                      <h3 className="font-medium text-foreground">{exercise.name}</h3>
                      <p className="text-xs text-muted-foreground">{exercise.category}</p>
                    </div>
                  </div>
                  <span
                    className={`rounded-full px-2 py-1 text-xs font-medium ${
                      exercise.active
                        ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
                        : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
                    }`}
                  >
                    {exercise.active ? t('common:status.active') : t('common:status.inactive')}
                  </span>
                </div>
                <p className="mt-2 text-sm text-muted-foreground">{exercise.primaryMuscleGroup}</p>
                <div className="mt-4 flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setEditingExercise(exercise)}
                  >
                    {t('common:buttons.edit')}
                  </Button>
                  <Button
                    variant={exercise.active ? 'destructive' : 'default'}
                    size="sm"
                    onClick={() => void handleToggleActive(exercise)}
                  >
                    {exercise.active ? t('exercises.deactivate') : t('exercises.activate')}
                  </Button>
                </div>
              </motion.div>
            ))}
          </div>
          <div className="mt-6">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={(page) => void loadExercises(page, search.trim() || undefined)}
            />
          </div>
        </>
      ) : (
        <EmptyState
          icon={Dumbbell}
          title={t('common:messages.noData')}
          description={t('exercises.subtitle')}
        />
      )}

      {isCreateModalOpen && (
        <ExerciseFormModal
          onClose={() => setIsCreateModalOpen(false)}
          onSuccess={() => {
            setIsCreateModalOpen(false)
            void loadExercises(currentPage, search.trim() || undefined)
          }}
        />
      )}

      {editingExercise && (
        <ExerciseFormModal
          exercise={editingExercise}
          onClose={() => setEditingExercise(null)}
          onSuccess={() => {
            setEditingExercise(null)
            void loadExercises(currentPage, search.trim() || undefined)
          }}
        />
      )}
    </div>
  )
}

const ExerciseFormModal = ({
  exercise,
  onClose,
  onSuccess,
}: {
  exercise?: ExerciseResponse
  onClose: () => void
  onSuccess: () => void
}) => {
  const { t } = useTranslation(['admin', 'common'])
  const [form, setForm] = useState<CreateExerciseRequest>({
    name: exercise?.name ?? '',
    description: exercise?.description ?? '',
    category: exercise?.category ?? 'STRENGTH',
    primaryMuscleGroup: exercise?.primaryMuscleGroup ?? 'CHEST',
    secondaryMuscleGroups: exercise?.secondaryMuscleGroups ?? [],
    instructions: exercise?.instructions ?? '',
  })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    try {
      if (exercise) {
        await updateExercise(exercise.id, form)
      } else {
        await createExercise(form)
      }
      toast.success(t('common:messages.saved'))
      onSuccess()
    } catch (err) {
      toast.error(getApiErrorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        className="max-h-[calc(100vh-3rem)] w-full max-w-lg overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
      >
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-foreground">
            {exercise ? t('exercises.edit') : t('exercises.create')}
          </h2>
          <button
            type="button"
            onClick={onClose}
            className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-background text-muted-foreground hover:bg-accent"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-1.5">
            <Label>{t('exercises.name')}</Label>
            <Input
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
            />
          </div>

          <div className="space-y-1.5">
            <Label>{t('exercises.description')}</Label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              required
              rows={3}
              className="flex w-full rounded-xl border border-border bg-background px-3 py-2 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-1.5">
              <Label>{t('exercises.category')}</Label>
              <select
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value as ExerciseCategory })}
                className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
              >
                {exerciseCategories.map((cat) => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            <div className="space-y-1.5">
              <Label>{t('exercises.muscleGroup')}</Label>
              <select
                value={form.primaryMuscleGroup}
                onChange={(e) => setForm({ ...form, primaryMuscleGroup: e.target.value as MuscleGroup })}
                className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
              >
                {muscleGroups.map((mg) => (
                  <option key={mg} value={mg}>{mg}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={onClose}>
              {t('common:buttons.cancel')}
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? t('common:buttons.loading') : t('common:buttons.save')}
            </Button>
          </div>
        </form>
      </motion.div>
    </div>
  )
}

export default ExerciseManagement
