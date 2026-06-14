import { useEffect, useState, type FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { CheckCircle2 } from 'lucide-react'
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
  getMyTrainerProfile,
  createTrainerProfile,
  updateMyTrainerProfile,
  type TrainerProfileResponse,
} from '../services/profile.service'
import { getSpecializations, type SpecializationResponse } from '../services/specialization.service'
import { useAuthStore } from '../store/useAuthStore'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

type TrainerForm = {
  firstname: string
  lastname: string
  specializationIds: string[]
  experienceYears: string
  description: string
}

type TrainerFormErrors = Partial<Record<keyof TrainerForm, string>>

const createFormFromProfile = (profile: TrainerProfileResponse | null): TrainerForm => ({
  firstname: profile?.firstname ?? '',
  lastname: profile?.lastname ?? '',
  specializationIds: profile?.specializations?.map(() => '') ?? [],
  experienceYears: profile?.experienceYears?.toString() ?? '1',
  description: profile?.description ?? '',
})

const TrainerProfile = () => {
  const { t } = useTranslation('profile')
  const navigate = useNavigate()
  const fetchCurrentUser = useAuthStore((state) => state.fetchCurrentUser)
  const [profile, setProfile] = useState<TrainerProfileResponse | null>(null)
  const [form, setForm] = useState<TrainerForm>(() => createFormFromProfile(null))
  const [formErrors, setFormErrors] = useState<TrainerFormErrors>({})
  const [specializations, setSpecializations] = useState<SpecializationResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      setIsLoading(true)
      setError(null)
      try {
        const [profileData, specsData] = await Promise.allSettled([
          getMyTrainerProfile(),
          getSpecializations(0, 50),
        ])
        if (profileData.status === 'fulfilled') {
          setProfile(profileData.value)
          setForm(createFormFromProfile(profileData.value))
        }
        if (specsData.status === 'fulfilled') {
          setSpecializations(specsData.value.content)
        }
      } catch (err) {
        console.error(err)
        setError(getApiErrorMessage(err, 'Unable to load trainer profile.'))
      } finally {
        setIsLoading(false)
      }
    }
    void load()
  }, [])

  const updateField = (field: keyof TrainerForm, value: string | string[]) => {
    setForm((prev) => ({ ...prev, [field]: value }))
    setFormErrors((prev) => {
      const next = { ...prev }
      delete next[field]
      return next
    })
  }

  const toggleSpecialization = (specId: string) => {
    setForm((prev) => {
      const ids = prev.specializationIds.includes(specId)
        ? prev.specializationIds.filter((id) => id !== specId)
        : [...prev.specializationIds, specId]
      return { ...prev, specializationIds: ids }
    })
    setFormErrors((prev) => {
      const next = { ...prev }
      delete next.specializationIds
      return next
    })
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const errors: TrainerFormErrors = {}
    if (!form.firstname.trim()) errors.firstname = 'First name is required.'
    if (!form.lastname.trim()) errors.lastname = 'Last name is required.'
    if (!form.experienceYears || Number(form.experienceYears) < 1) errors.experienceYears = 'Experience must be at least 1 year.'
    if (!form.description.trim()) errors.description = 'Description is required.'

    setFormErrors(errors)
    if (Object.keys(errors).length > 0) {
      toast.error(t('errors.fixFields', { ns: 'common' }))
      return
    }

    setIsSaving(true)
    try {
      if (profile) {
        await updateMyTrainerProfile({
          firstname: form.firstname.trim(),
          lastname: form.lastname.trim(),
          specializationIds: form.specializationIds,
          experienceYears: Number(form.experienceYears),
          description: form.description.trim(),
        })
        toast.success('Profile updated successfully.')
      } else {
        await createTrainerProfile({
          firstname: form.firstname.trim(),
          lastname: form.lastname.trim(),
          specializationIds: form.specializationIds,
          experienceYears: Number(form.experienceYears),
          description: form.description.trim(),
        })
        toast.success('Profile created successfully.')
      }
      await fetchCurrentUser()
      navigate('/dashboard', { replace: true })
    } catch (err) {
      console.error(err)
      toast.error(getApiErrorMessage(err, 'Unable to save trainer profile.'))
    } finally {
      setIsSaving(false)
    }
  }

  const fullName = profile
    ? `${profile.firstname} ${profile.lastname}`
    : 'New Trainer'

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {profile ? t('badge') : 'Trainer Setup'}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {fullName}
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">
            {profile ? t('subtitle') : 'Complete your trainer profile to get started.'}
          </p>
        </div>
      </div>

      {isLoading ? (
        <div className="h-96 animate-pulse rounded-2xl bg-muted" />
      ) : error ? (
        <div className="rounded-2xl border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
          {error}
        </div>
      ) : (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <Card className="border-border bg-card shadow-soft">
            <CardHeader>
              <CardTitle>{profile ? 'Edit Profile' : 'Create Profile'}</CardTitle>
              <CardDescription>
                {profile ? 'Update your trainer information.' : 'Fill in your details to create your trainer profile.'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-5">
                <div className="grid gap-4 md:grid-cols-2">
                  <FormField
                    label="First name"
                    value={form.firstname}
                    error={formErrors.firstname}
                    onChange={(v) => updateField('firstname', v)}
                    placeholder="John"
                  />
                  <FormField
                    label="Last name"
                    value={form.lastname}
                    error={formErrors.lastname}
                    onChange={(v) => updateField('lastname', v)}
                    placeholder="Doe"
                  />
                </div>

                <FormField
                  label="Years of experience"
                  type="number"
                  value={form.experienceYears}
                  error={formErrors.experienceYears}
                  onChange={(v) => updateField('experienceYears', v)}
                  placeholder="5"
                />

                <div className="space-y-1.5">
                  <Label>Specializations</Label>
                  <div className="flex flex-wrap gap-2">
                    {specializations.map((spec) => (
                      <button
                        key={spec.id}
                        type="button"
                        onClick={() => toggleSpecialization(spec.id)}
                        className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-medium transition ${
                          form.specializationIds.includes(spec.id)
                            ? 'bg-primary text-primary-foreground'
                            : 'border border-border bg-background text-foreground hover:bg-accent'
                        }`}
                      >
                        {form.specializationIds.includes(spec.id) && (
                          <CheckCircle2 className="h-3 w-3" />
                        )}
                        {spec.name}
                      </button>
                    ))}
                    {specializations.length === 0 && (
                      <p className="text-xs text-muted-foreground">No specializations available.</p>
                    )}
                  </div>
                  {formErrors.specializationIds && (
                    <p className="text-xs text-destructive">{formErrors.specializationIds}</p>
                  )}
                </div>

                <div className="space-y-1.5">
                  <Label htmlFor="description">Bio / Description</Label>
                  <textarea
                    id="description"
                    value={form.description}
                    onChange={(e) => updateField('description', e.target.value)}
                    className={`min-h-[100px] w-full rounded-xl border bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring ${
                      formErrors.description ? 'border-destructive' : 'border-border'
                    }`}
                    placeholder="Tell clients about your experience, specialties, and training philosophy..."
                  />
                  {formErrors.description && (
                    <p className="text-xs text-destructive">{formErrors.description}</p>
                  )}
                </div>

                <div className="flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => navigate('/dashboard', { replace: true })}
                    disabled={isSaving}
                    className="inline-flex h-9 items-center justify-center rounded-xl px-4 text-sm font-medium text-foreground transition hover:bg-accent disabled:opacity-60"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={isSaving}
                    className="inline-flex h-9 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:opacity-60"
                  >
                    {isSaving && (
                      <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                    )}
                    {isSaving ? 'Saving...' : profile ? 'Save changes' : 'Create profile'}
                  </button>
                </div>
              </form>
            </CardContent>
          </Card>
        </motion.div>
      )}
    </div>
  )
}

const FormField = ({
  label,
  value,
  onChange,
  error,
  type = 'text',
  placeholder,
}: {
  label: string
  value: string
  onChange: (value: string) => void
  error?: string
  type?: string
  placeholder?: string
}) => (
  <div className="space-y-1.5">
    <Label>{label}</Label>
    <Input
      type={type}
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
      className={error ? 'border-destructive' : undefined}
    />
    {error && <p className="text-xs text-destructive">{error}</p>}
  </div>
)

export default TrainerProfile
