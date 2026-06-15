import { useState, type FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import { X } from 'lucide-react'
import { Input } from '../ui/input'
import { Button } from '../ui/button'
import { Label } from '../ui/label'
import {
  updateMealPlan,
  type UpdateMealPlanRequest,
  type MealPlanResponse,
} from '../../services/nutrition.service'
import { getApiErrorMessage } from '../../utils/errorHandler'
import toast from '../ui/toast'

type EditMealPlanModalProps = {
  mealPlan: MealPlanResponse
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
}

export const EditMealPlanModal = ({
  mealPlan,
  isOpen,
  onClose,
  onSuccess,
}: EditMealPlanModalProps) => {
  const { t } = useTranslation(['nutrition', 'common'])
  const [form, setForm] = useState<UpdateMealPlanRequest>({
    targetCalories: mealPlan.targetCalories ?? undefined,
    targetMacros: mealPlan.targetMacros ?? undefined,
    notes: mealPlan.notes ?? undefined,
  })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    try {
      await updateMealPlan(mealPlan.id, form)
      toast.success(t('mealPlan.editSuccess'))
      onSuccess()
    } catch (err) {
      toast.error(getApiErrorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        className="max-h-[calc(100vh-3rem)] w-full max-w-lg overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
      >
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-foreground">
            {t('mealPlan.editTitle')}
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
            <Label>{t('mealPlan.targetCalories')}</Label>
            <Input
              type="number"
              min="0"
              value={form.targetCalories ?? ''}
              onChange={(e) =>
                setForm({
                  ...form,
                  targetCalories: e.target.value ? parseInt(e.target.value) : undefined,
                })
              }
            />
          </div>

          <div className="grid gap-4 md:grid-cols-3">
            <div className="space-y-1.5">
              <Label>{t('mealPlan.targetProtein')}</Label>
              <Input
                type="number"
                min="0"
                value={form.targetMacros?.protein ?? ''}
                onChange={(e) =>
                  setForm({
                    ...form,
                    targetMacros: {
                      ...form.targetMacros,
                      protein: e.target.value ? parseFloat(e.target.value) : undefined,
                      carbs: form.targetMacros?.carbs,
                      fats: form.targetMacros?.fats,
                      fiber: form.targetMacros?.fiber,
                      sugar: form.targetMacros?.sugar,
                    },
                  })
                }
              />
            </div>
            <div className="space-y-1.5">
              <Label>{t('mealPlan.targetCarbs')}</Label>
              <Input
                type="number"
                min="0"
                value={form.targetMacros?.carbs ?? ''}
                onChange={(e) =>
                  setForm({
                    ...form,
                    targetMacros: {
                      ...form.targetMacros,
                      protein: form.targetMacros?.protein,
                      carbs: e.target.value ? parseFloat(e.target.value) : undefined,
                      fats: form.targetMacros?.fats,
                      fiber: form.targetMacros?.fiber,
                      sugar: form.targetMacros?.sugar,
                    },
                  })
                }
              />
            </div>
            <div className="space-y-1.5">
              <Label>{t('mealPlan.targetFat')}</Label>
              <Input
                type="number"
                min="0"
                value={form.targetMacros?.fats ?? ''}
                onChange={(e) =>
                  setForm({
                    ...form,
                    targetMacros: {
                      ...form.targetMacros,
                      protein: form.targetMacros?.protein,
                      carbs: form.targetMacros?.carbs,
                      fats: e.target.value ? parseFloat(e.target.value) : undefined,
                      fiber: form.targetMacros?.fiber,
                      sugar: form.targetMacros?.sugar,
                    },
                  })
                }
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label>{t('common:labels.notes')}</Label>
            <textarea
              value={form.notes ?? ''}
              onChange={(e) => setForm({ ...form, notes: e.target.value || undefined })}
              rows={3}
              className="flex w-full rounded-xl border border-border bg-background px-3 py-2 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
            />
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
