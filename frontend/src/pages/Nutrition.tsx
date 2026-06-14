import { useEffect, useMemo, useState, type FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import {
  CalendarDays,
  Droplets,
  Flame,
  Leaf,
  Plus,
  Target,
  Utensils,
} from 'lucide-react'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import { Input } from '../components/ui/input'
import {
  createMealPlan,
  getMyMealPlans,
  getTodayMealPlan,
  getTodayWaterIntake,
  getWeeklyWaterIntake,
  logWaterIntake,
  type DailyWaterIntakeResponse,
  type MealPlanResponse,
} from '../services/nutrition.service'
import { useAuthStore } from '../store/useAuthStore'
import { clampPercentage, type IconType } from '../lib/utils'
import { ProgressBar } from '../components/ui/progress-bar'
import { EmptyState } from '../components/ui/empty-state'
import { SkeletonCard, SkeletonBlock, SkeletonLine } from '../components/ui/skeleton'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

type NutritionState = {
  todayWater: DailyWaterIntakeResponse | null
  weeklyWater: DailyWaterIntakeResponse[]
  todayMealPlan: MealPlanResponse | null
  mealPlans: MealPlanResponse[]
}

type NutritionErrors = Partial<Record<keyof NutritionState, string>>

const todayIso = () => new Date().toISOString().slice(0, 10)

const quickWaterAmounts = [250, 400, 500, 750]

const Nutrition = () => {
  const { t } = useTranslation('nutrition')
  const profile = useAuthStore((state) => state.user?.clientProfile)
  const [state, setState] = useState<NutritionState>({
    todayWater: null,
    weeklyWater: [],
    todayMealPlan: null,
    mealPlans: [],
  })
  const [errors, setErrors] = useState<NutritionErrors>({})
  const [isLoading, setIsLoading] = useState(true)
  const [isLoggingWater, setIsLoggingWater] = useState(false)
  const [customWaterAmount, setCustomWaterAmount] = useState('')
  const [targetCalories, setTargetCalories] = useState('')
  const [isCreatingMealPlan, setIsCreatingMealPlan] = useState(false)

  const loadNutrition = async () => {
    setIsLoading(true)
    setErrors({})

    const [waterResult, weeklyWaterResult, todayPlanResult, plansResult] =
      await Promise.allSettled([
        getTodayWaterIntake(),
        getWeeklyWaterIntake(),
        getTodayMealPlan(todayIso()),
        getMyMealPlans(0, 5),
      ])

    const nextErrors: NutritionErrors = {}
    const todayWater = unwrapResult(waterResult, 'todayWater', nextErrors, null)
    const weeklyWater = unwrapResult(
      weeklyWaterResult,
      'weeklyWater',
      nextErrors,
      [],
    )
    const todayMealPlan = unwrapResult(
      todayPlanResult,
      'todayMealPlan',
      nextErrors,
      null,
    )
    const mealPlansPage = unwrapResult(plansResult, 'mealPlans', nextErrors, {
      content: [],
      totalElements: 0,
      totalPages: 0,
      number: 0,
      size: 0,
    })

    setState({
      todayWater,
      weeklyWater,
      todayMealPlan,
      mealPlans: mealPlansPage.content,
    })
    setErrors(nextErrors)
    setIsLoading(false)
  }

  useEffect(() => {
    void loadNutrition()
  }, [])

  const waterTarget = state.todayWater?.targetMl ?? profile?.dailyWaterTarget ?? 0
  const waterTotal = state.todayWater?.totalMl ?? 0
  const waterProgress = waterTarget > 0 ? clampPercentage((waterTotal / waterTarget) * 100) : 0
  const mealCompletion = state.todayMealPlan
    ? getMealCompletion(state.todayMealPlan)
    : 0

  const weeklyAverage = useMemo(() => {
    if (!state.weeklyWater.length) return 0
    const total = state.weeklyWater.reduce((sum, day) => sum + day.progress, 0)
    return Math.round(total / state.weeklyWater.length)
  }, [state.weeklyWater])

  const handleLogWater = async (amountMl: number) => {
    if (!Number.isFinite(amountMl) || amountMl <= 0) {
      toast.error(t('common:toast.waterAmountError'))
      return
    }

    setIsLoggingWater(true)

    try {
      await logWaterIntake({ amountMl })
      toast.success(`${amountMl} ml logged.`)
      setCustomWaterAmount('')
      await loadNutrition()
    } catch (error) {
      console.error(error)
      toast.error(getApiErrorMessage(error, 'Unable to log water intake.'))
    } finally {
      setIsLoggingWater(false)
    }
  }

  const handleCustomWaterSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    void handleLogWater(Number(customWaterAmount))
  }

  const handleCreateMealPlan = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const calories = targetCalories.trim() ? Number(targetCalories) : undefined

    if (calories !== undefined && (!Number.isFinite(calories) || calories <= 0)) {
      toast.error(t('common:toast.caloriesError'))
      return
    }

    setIsCreatingMealPlan(true)

    try {
      await createMealPlan({
        planDate: todayIso(),
        targetCalories: calories,
        targetMacros: {
          protein: null,
          carbs: null,
          fats: null,
          fiber: null,
          sugar: null,
        },
      })
      toast.success(t('common:toast.mealPlanCreated'))
      setTargetCalories('')
      await loadNutrition()
    } catch (error) {
      console.error(error)
      toast.error(getApiErrorMessage(error, 'Unable to create meal plan.'))
    } finally {
      setIsCreatingMealPlan(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('title')}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {t('title')}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            {t('subtitle')}
          </p>
        </div>
        <Link
          to="/profile"
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-card px-4 text-sm font-semibold text-foreground shadow-soft transition-all hover:bg-accent"
        >
          <Target className="h-4 w-4" />
          {t('editTargets')}
        </Link>
      </div>

      <section className="grid gap-4 md:grid-cols-3">
        {isLoading ? (
          Array.from({ length: 3 }).map((_, index) => (
            <SkeletonCard key={index} className="h-32" />
          ))
        ) : (
          <>
            <MetricCard
              icon={Droplets}
              label="Water today"
              value={`${Math.round(waterProgress)}%`}
              detail={`${waterTotal} / ${waterTarget} ml`}
            />
            <MetricCard
              icon={Flame}
              label="Calories today"
              value={`${state.todayMealPlan?.totalCalories ?? 0}`}
              detail={
                state.todayMealPlan?.targetCalories
                  ? `of ${state.todayMealPlan.targetCalories} target`
                  : 'No calorie target set'
              }
            />
            <MetricCard
              icon={Leaf}
              label="Weekly hydration"
              value={`${weeklyAverage}%`}
              detail="Average target completion"
            />
          </>
        )}
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,1.1fr),minmax(360px,0.9fr)]">
        <Card>
          <CardHeader>
            <CardTitle>Today&apos;s Water</CardTitle>
            <CardDescription>
              Log small increments throughout the day.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <SkeletonBlock />
            ) : (
              <div className="space-y-5">
                <div>
                  <div className="flex items-end justify-between gap-4">
                    <div>
                      <p className="text-3xl font-bold text-foreground">
                        {waterTotal} ml
                      </p>
                      <p className="mt-1 text-sm text-muted-foreground">
                        of {waterTarget || 'unset'} ml target
                      </p>
                    </div>
                    <span className="rounded-full bg-sky-500/10 px-3 py-1 text-sm font-semibold text-sky-600 dark:text-sky-300">
                      {Math.round(waterProgress)}%
                    </span>
                  </div>
                  <ProgressBar value={waterProgress} className="mt-5" />
                </div>

                <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
                  {quickWaterAmounts.map((amount) => (
                    <button
                      key={amount}
                      type="button"
                      disabled={isLoggingWater}
                      onClick={() => void handleLogWater(amount)}
                      className="inline-flex h-10 items-center justify-center rounded-xl border border-border bg-background text-sm font-semibold text-foreground transition hover:bg-accent disabled:cursor-not-allowed disabled:opacity-60"
                    >
                      +{amount} ml
                    </button>
                  ))}
                </div>

                <form
                  onSubmit={handleCustomWaterSubmit}
                  className="grid gap-2 sm:grid-cols-[minmax(0,1fr),auto]"
                >
                  <Input
                    type="number"
                    min={1}
                    placeholder="Custom amount in ml"
                    value={customWaterAmount}
                    onChange={(event) => setCustomWaterAmount(event.target.value)}
                  />
                  <button
                    type="submit"
                    disabled={isLoggingWater}
                    className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {isLoggingWater && (
                      <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                    )}
                    Log water
                  </button>
                </form>

                {!state.todayWater?.intakes.length && (
                  <p className="rounded-2xl border border-dashed border-border bg-muted/30 px-4 py-3 text-sm text-muted-foreground">
                    No water logged yet today. Start with a small entry and build
                    from there.
                  </p>
                )}
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Weekly Hydration</CardTitle>
            <CardDescription>
              Seven-day water progress at a glance.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-3">
                <SkeletonLine />
                <SkeletonLine />
                <SkeletonLine />
              </div>
            ) : state.weeklyWater.length ? (
              <div className="space-y-3">
                {state.weeklyWater.map((day) => (
                  <WaterDayRow key={day.date} day={day} />
                ))}
              </div>
            ) : (
              <EmptyState
                icon={Droplets}
                title="No weekly water history"
                description="Your hydration week will populate once you start logging water."
                actionLabel="Log water above"
              />
            )}
          </CardContent>
        </Card>
      </section>

      <section className="grid gap-4 xl:grid-cols-[minmax(0,1fr),minmax(0,1fr)]">
        <Card>
          <CardHeader>
            <CardTitle>Today&apos;s Meal Plan</CardTitle>
            <CardDescription>
              Meal plan totals and completion for today.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <SkeletonBlock />
            ) : state.todayMealPlan ? (
              <MealPlanPanel plan={state.todayMealPlan} completion={mealCompletion} />
            ) : (
              <div className="space-y-5">
                <EmptyState
                  icon={Utensils}
                  title="No meal plan for today"
                  description="Create a light plan shell now, then add meals when the full nutrition editor lands."
                  actionLabel="Create below"
                />
                <form
                  onSubmit={handleCreateMealPlan}
                  className="grid gap-2 sm:grid-cols-[minmax(0,1fr),auto]"
                >
                  <Input
                    type="number"
                    min={1}
                    placeholder="Target calories (optional)"
                    value={targetCalories}
                    onChange={(event) => setTargetCalories(event.target.value)}
                  />
                  <button
                    type="submit"
                    disabled={isCreatingMealPlan}
                    className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {isCreatingMealPlan && (
                      <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                    )}
                    <Plus className="h-4 w-4" />
                    Create plan
                  </button>
                </form>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Recent Meal Plans</CardTitle>
            <CardDescription>
              Your latest nutrition planning history.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-3">
                <SkeletonLine />
                <SkeletonLine />
                <SkeletonLine />
              </div>
            ) : state.mealPlans.length ? (
              <div className="space-y-3">
                {state.mealPlans.map((plan) => (
                  <MealPlanRow key={plan.id} plan={plan} />
                ))}
              </div>
            ) : (
              <EmptyState
                icon={CalendarDays}
                title="No meal plans yet"
                description="Meal plans you create will appear here with calorie and macro totals."
                actionLabel="Create today's plan"
              />
            )}
          </CardContent>
        </Card>
      </section>

      {Object.keys(errors).length > 0 && !isLoading && (
        <div className="rounded-2xl border border-amber-500/30 bg-amber-500/10 px-4 py-3 text-sm text-amber-700 dark:text-amber-200">
          Some nutrition sections could not be refreshed. The available sections
          are still shown.
        </div>
      )}
    </div>
  )
}

const unwrapResult = <T,>(
  result: PromiseSettledResult<T>,
  key: keyof NutritionState,
  errors: NutritionErrors,
  fallback: T,
): T => {
  if (result.status === 'fulfilled') return result.value
  console.error(`Failed to load nutrition ${String(key)}`, result.reason)
  errors[key] = 'Failed to load'
  return fallback
}

const formatDate = (value: string) =>
  new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
  }).format(new Date(value))

const getMealCompletion = (plan: MealPlanResponse) => {
  if (!plan.meals.length) return 0
  const completedMeals = plan.meals.filter((meal) => meal.completed).length
  return clampPercentage((completedMeals / plan.meals.length) * 100)
}

const formatMealType = (value: string) =>
  value
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ')

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
        <p className="mt-2 text-2xl font-bold text-foreground">{value}</p>
        <p className="mt-1 text-xs text-muted-foreground">{detail}</p>
      </div>
      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
        <Icon className="h-5 w-5 text-primary" />
      </div>
    </CardContent>
  </Card>
)

const MealPlanPanel = ({
  plan,
  completion,
}: {
  plan: MealPlanResponse
  completion: number
}) => (
  <div className="space-y-5">
    <div>
      <div className="flex items-end justify-between gap-4">
        <div>
          <p className="text-3xl font-bold text-foreground">
            {plan.totalCalories}
          </p>
          <p className="mt-1 text-sm text-muted-foreground">
            {plan.targetCalories
              ? `of ${plan.targetCalories} calories`
              : 'calories logged today'}
          </p>
        </div>
        <span className="rounded-full bg-primary/10 px-3 py-1 text-sm font-semibold text-primary">
          {Math.round(completion)}%
        </span>
      </div>
      <ProgressBar value={plan.caloriesPercentage ?? completion} className="mt-5" />
    </div>

    {plan.meals.length ? (
      <div className="space-y-3">
        {plan.meals.map((meal) => (
          <div
            key={meal.id}
            className="rounded-2xl border border-border bg-background p-3"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-sm font-semibold text-foreground">
                  {meal.name || formatMealType(meal.mealType)}
                </p>
                <p className="mt-1 text-xs text-muted-foreground">
                  {meal.calories} calories · {meal.foods.length} food item
                  {meal.foods.length === 1 ? '' : 's'}
                </p>
              </div>
              <span className="rounded-full bg-muted px-2 py-1 text-xs text-muted-foreground">
                {meal.completed ? 'Done' : 'Planned'}
              </span>
            </div>
          </div>
        ))}
      </div>
    ) : (
      <p className="rounded-2xl border border-dashed border-border bg-muted/30 px-4 py-3 text-sm text-muted-foreground">
        Meal plan created. Meal editing is coming in the next nutrition pass.
      </p>
    )}
  </div>
)

const MealPlanRow = ({ plan }: { plan: MealPlanResponse }) => (
  <div className="rounded-2xl border border-border bg-background p-4">
    <div className="flex items-start justify-between gap-4">
      <div>
        <p className="text-sm font-semibold text-foreground">
          {formatDate(plan.planDate)}
        </p>
        <p className="mt-1 text-xs text-muted-foreground">
          {plan.meals.length} meal{plan.meals.length === 1 ? '' : 's'} ·{' '}
          {plan.totalCalories} calories
        </p>
      </div>
      <span className="rounded-full bg-muted px-2 py-1 text-xs text-muted-foreground">
        {plan.completed ? 'Completed' : 'Open'}
      </span>
    </div>
    {plan.targetCalories && (
      <ProgressBar
        value={plan.caloriesPercentage ?? 0}
        className="mt-4"
      />
    )}
  </div>
)

const WaterDayRow = ({ day }: { day: DailyWaterIntakeResponse }) => (
  <div className="rounded-2xl border border-border bg-background p-4">
    <div className="flex items-center justify-between gap-4">
      <div>
        <p className="text-sm font-semibold text-foreground">
          {formatDate(day.date)}
        </p>
        <p className="mt-1 text-xs text-muted-foreground">
          {day.totalMl} / {day.targetMl} ml
        </p>
      </div>
      <span className="text-sm font-semibold text-primary">
        {Math.round(day.progress)}%
      </span>
    </div>
    <ProgressBar value={day.progress} className="mt-3" />
  </div>
)

export default Nutrition
