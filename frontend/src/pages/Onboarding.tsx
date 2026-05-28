import { useMemo, useState, type FormEvent, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Activity, Droplets, Ruler, UserRound, Weight } from 'lucide-react'
import { Button } from '../components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import {
  type ClientGender,
  createClientProfile,
  type CreateClientProfileRequest,
} from '../services/profile.service'
import { useAuthStore } from '../store/useAuthStore'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

type OnboardingForm = {
  firstname: string
  lastname: string
  phone: string
  birthdate: string
  height: string
  weight: string
  dailyWaterTarget: string
  gender: ClientGender
}

type OnboardingErrors = Partial<Record<keyof OnboardingForm, string>>

const phonePattern = /^\+[1-9]\d{1,14}$/

const genderOptions: { label: string; value: ClientGender }[] = [
  { label: 'Male', value: 'MALE' },
  { label: 'Female', value: 'FEMALE' },
  { label: 'Other', value: 'OTHER' },
]

const Onboarding = () => {
  const navigate = useNavigate()
  const fetchCurrentUser = useAuthStore((state) => state.fetchCurrentUser)
  const [form, setForm] = useState<OnboardingForm>({
    firstname: '',
    lastname: '',
    phone: '',
    birthdate: '',
    height: '170',
    weight: '70',
    dailyWaterTarget: '2500',
    gender: 'OTHER',
  })
  const [errors, setErrors] = useState<OnboardingErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)

  const canSubmit = useMemo(
    () => Object.keys(validateOnboardingForm(form)).length === 0,
    [form],
  )

  const updateField = (field: keyof OnboardingForm, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }))
    setErrors((prev) => {
      const next = { ...prev }
      delete next[field]
      return next
    })
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const nextErrors = validateOnboardingForm(form)
    setErrors(nextErrors)

    if (Object.keys(nextErrors).length > 0) {
      toast.error('Please complete the highlighted onboarding fields.')
      return
    }

    const payload: CreateClientProfileRequest = {
      firstname: form.firstname.trim(),
      lastname: form.lastname.trim(),
      phone: form.phone.trim(),
      birthdate: form.birthdate || null,
      height: Number(form.height),
      weight: Number(form.weight),
      dailyWaterTarget: Number(form.dailyWaterTarget),
      gender: form.gender,
    }

    setIsSubmitting(true)

    try {
      await createClientProfile(payload)
      await fetchCurrentUser()
      toast.success('Profile created. Welcome to FitHub.')
      navigate('/dashboard', { replace: true })
    } catch (err) {
      console.error(err)
      toast.error(
        getApiErrorMessage(
          err,
          'Unable to create profile right now. Please check your details and retry.',
        ),
      )
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="min-h-screen bg-background px-4 py-10 text-foreground">
      <motion.div
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45, ease: 'easeOut' }}
        className="mx-auto w-full max-w-3xl"
      >
        <Card className="border-border bg-card shadow-soft-lg">
          <CardHeader className="space-y-4">
            <div className="inline-flex w-fit items-center gap-2 rounded-full border border-border bg-muted px-3 py-1 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground">
              <Activity className="h-3.5 w-3.5 text-primary" />
              Client onboarding
            </div>
            <div>
              <CardTitle>Complete your profile</CardTitle>
              <CardDescription>
                Add your details to personalize your training dashboard and daily targets.
              </CardDescription>
            </div>
          </CardHeader>

          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="grid gap-4 md:grid-cols-2">
                <FormField
                  id="firstname"
                  label="First name"
                  value={form.firstname}
                  error={errors.firstname}
                  onChange={(value) => updateField('firstname', value)}
                  placeholder="John"
                />
                <FormField
                  id="lastname"
                  label="Last name"
                  value={form.lastname}
                  error={errors.lastname}
                  onChange={(value) => updateField('lastname', value)}
                  placeholder="Doe"
                />
              </div>

              <div className="grid gap-4 md:grid-cols-2">
                <FormField
                  id="phone"
                  label="Phone (E.164)"
                  value={form.phone}
                  error={errors.phone}
                  onChange={(value) => updateField('phone', value)}
                  placeholder="+12025550123"
                />
                <FormField
                  id="birthdate"
                  label="Birthdate"
                  type="date"
                  value={form.birthdate}
                  error={errors.birthdate}
                  onChange={(value) => updateField('birthdate', value)}
                />
              </div>

              <div className="grid gap-4 md:grid-cols-3">
                <IconField
                  id="height"
                  label="Height (cm)"
                  icon={<Ruler className="h-4 w-4 text-muted-foreground" />}
                  type="number"
                  value={form.height}
                  error={errors.height}
                  onChange={(value) => updateField('height', value)}
                />
                <IconField
                  id="weight"
                  label="Weight (kg)"
                  icon={<Weight className="h-4 w-4 text-muted-foreground" />}
                  type="number"
                  step="0.1"
                  value={form.weight}
                  error={errors.weight}
                  onChange={(value) => updateField('weight', value)}
                />
                <IconField
                  id="dailyWaterTarget"
                  label="Water target (ml)"
                  icon={<Droplets className="h-4 w-4 text-muted-foreground" />}
                  type="number"
                  value={form.dailyWaterTarget}
                  error={errors.dailyWaterTarget}
                  onChange={(value) => updateField('dailyWaterTarget', value)}
                />
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="gender">Gender</Label>
                <div className="relative">
                  <UserRound className="pointer-events-none absolute left-3 top-3.5 h-4 w-4 text-muted-foreground" />
                  <select
                    id="gender"
                    value={form.gender}
                    onChange={(event) =>
                      updateField('gender', event.target.value as ClientGender)
                    }
                    className={`flex h-11 w-full rounded-md border bg-background px-9 py-2 text-sm text-foreground focus:border-ring focus:outline-none focus:ring-1 focus:ring-ring ${
                      errors.gender ? 'border-destructive' : 'border-border'
                    }`}
                  >
                    {genderOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </div>
                {errors.gender && (
                  <p className="text-xs text-destructive">{errors.gender}</p>
                )}
              </div>

              <Button
                type="submit"
                className="w-full"
                disabled={isSubmitting || !canSubmit}
              >
                {isSubmitting && (
                  <span className="mr-2 inline-flex h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                )}
                {isSubmitting ? 'Creating profile...' : 'Finish onboarding'}
              </Button>
            </form>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  )
}

const validateOnboardingForm = (form: OnboardingForm): OnboardingErrors => {
  const errors: OnboardingErrors = {}

  if (!form.firstname.trim()) errors.firstname = 'First name is required.'
  if (!form.lastname.trim()) errors.lastname = 'Last name is required.'
  if (!phonePattern.test(form.phone.trim())) {
    errors.phone = 'Use E.164 format, for example +12025550123.'
  }
  if (!form.gender) errors.gender = 'Gender is required.'
  if (!isPositiveNumber(form.height)) errors.height = 'Height must be positive.'
  if (!isPositiveNumber(form.weight)) errors.weight = 'Weight must be positive.'
  if (!isPositiveInteger(form.dailyWaterTarget)) {
    errors.dailyWaterTarget = 'Water target must be a positive whole number.'
  }
  if (form.birthdate && Number.isNaN(Date.parse(form.birthdate))) {
    errors.birthdate = 'Enter a valid birthdate.'
  }

  return errors
}

const isPositiveNumber = (value: string) => {
  const numericValue = Number(value)
  return Number.isFinite(numericValue) && numericValue > 0
}

const isPositiveInteger = (value: string) => {
  const numericValue = Number(value)
  return Number.isInteger(numericValue) && numericValue > 0
}

type FormFieldProps = {
  id: string
  label: string
  value: string
  onChange: (value: string) => void
  error?: string
  type?: string
  step?: string
  placeholder?: string
}

const FormField = ({
  id,
  label,
  value,
  onChange,
  error,
  type = 'text',
  step,
  placeholder,
}: FormFieldProps) => (
  <div className="space-y-1.5">
    <Label htmlFor={id}>{label}</Label>
    <Input
      id={id}
      type={type}
      step={step}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      placeholder={placeholder}
      className={error ? 'border-destructive' : undefined}
    />
    {error && <p className="text-xs text-destructive">{error}</p>}
  </div>
)

type IconFieldProps = FormFieldProps & {
  icon: ReactNode
}

const IconField = ({ icon, ...props }: IconFieldProps) => (
  <div className="space-y-1.5">
    <Label htmlFor={props.id}>{props.label}</Label>
    <div className="relative">
      <span className="pointer-events-none absolute left-3 top-3.5">{icon}</span>
      <Input
        id={props.id}
        type={props.type}
        step={props.step}
        value={props.value}
        onChange={(event) => props.onChange(event.target.value)}
        className={`pl-9 ${props.error ? 'border-destructive' : ''}`}
      />
    </div>
    {props.error && <p className="text-xs text-destructive">{props.error}</p>}
  </div>
)

export default Onboarding
