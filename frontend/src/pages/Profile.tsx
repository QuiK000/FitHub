import { useEffect, useMemo, useState, type FormEvent, type ReactNode } from 'react'
import { motion } from 'framer-motion'
import { Calendar, Droplets, Mail, Phone, User2, Weight, X } from 'lucide-react'
import { useAuthStore } from '../store/useAuthStore'
import {
  getMyClientProfile,
  updateMyClientProfile,
} from '../services/profile.service'
import type {
  ClientGender,
  ClientProfileResponse,
  UpdateClientProfileRequest,
} from '../types/user.types'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

type ProfileForm = {
  firstname: string
  lastname: string
  phone: string
  birthdate: string
  height: string
  weight: string
  dailyWaterTarget: string
  gender: ClientGender
}

type ProfileErrors = Partial<Record<keyof ProfileForm, string>>

const phonePattern = /^\+[1-9]\d{1,14}$/

const genderOptions: { label: string; value: ClientGender }[] = [
  { label: 'Male', value: 'MALE' },
  { label: 'Female', value: 'FEMALE' },
  { label: 'Other', value: 'OTHER' },
]

const createFormFromProfile = (
  profile: ClientProfileResponse | null,
): ProfileForm => ({
  firstname: profile?.firstname ?? '',
  lastname: profile?.lastname ?? '',
  phone: profile?.phone ?? '',
  birthdate: profile?.birthdate ?? '',
  height: profile?.height?.toString() ?? '',
  weight: profile?.weight?.toString() ?? '',
  dailyWaterTarget: profile?.dailyWaterTarget?.toString() ?? '',
  gender: profile?.gender ?? 'OTHER',
})

const Profile = () => {
  const { user, fetchCurrentUser } = useAuthStore()
  const [profile, setProfile] = useState<ClientProfileResponse | null>(null)
  const [form, setForm] = useState<ProfileForm>(() => createFormFromProfile(null))
  const [formErrors, setFormErrors] = useState<ProfileErrors>({})
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [isEditOpen, setIsEditOpen] = useState(false)

  useEffect(() => {
    const loadProfile = async () => {
      setIsLoading(true)
      setError(null)

      try {
        const data = await getMyClientProfile()
        setProfile(data)
        setForm(createFormFromProfile(data))
      } catch (err) {
        console.error(err)
        const message = getApiErrorMessage(
          err,
          'Unable to load your profile right now.',
        )
        setError(message)
        toast.error(message)
      } finally {
        setIsLoading(false)
      }
    }

    void loadProfile()
  }, [])

  const fullName =
    profile && profile.firstname && profile.lastname
      ? `${profile.firstname} ${profile.lastname}`
      : user?.email ?? 'Client'

  const formattedBirthdate =
    profile?.birthdate &&
    new Date(profile.birthdate).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })

  const formattedCreatedAt =
    profile?.createdAt &&
    new Date(profile.createdAt).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })

  const canSave = useMemo(
    () => Object.keys(validateProfileForm(form)).length === 0,
    [form],
  )

  const updateField = (field: keyof ProfileForm, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }))
    setFormErrors((prev) => {
      const next = { ...prev }
      delete next[field]
      return next
    })
  }

  const handleOpenEdit = () => {
    setForm(createFormFromProfile(profile))
    setFormErrors({})
    setIsEditOpen(true)
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const errors = validateProfileForm(form)
    setFormErrors(errors)

    if (Object.keys(errors).length > 0) {
      toast.error('Please fix the highlighted profile fields.')
      return
    }

    const payload: UpdateClientProfileRequest = {
      firstname: form.firstname.trim(),
      lastname: form.lastname.trim(),
      phone: form.phone.trim(),
      birthdate: form.birthdate || null,
      height: Number(form.height),
      weight: Number(form.weight),
      dailyWaterTarget: Number(form.dailyWaterTarget),
      gender: form.gender,
    }

    setIsSaving(true)

    try {
      const response = await updateMyClientProfile(payload)
      const updatedProfile = await getMyClientProfile()
      setProfile(updatedProfile)
      setForm(createFormFromProfile(updatedProfile))
      await fetchCurrentUser()
      setIsEditOpen(false)
      toast.success(response.message || 'Profile updated successfully.')
    } catch (err) {
      console.error(err)
      toast.error(getApiErrorMessage(err, 'Unable to save profile changes.'))
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div className="relative space-y-6">
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Profile
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {fullName}
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Manage your personal details and training metrics.
          </p>
        </div>

        <button
          type="button"
          onClick={handleOpenEdit}
          disabled={isLoading || Boolean(error)}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-background px-4 text-sm font-semibold text-foreground transition-all hover:bg-accent disabled:cursor-not-allowed disabled:opacity-60"
        >
          Edit profile
        </button>
      </div>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,1.4fr),minmax(0,1fr)]">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <div className="rounded-2xl border border-border bg-card p-6 shadow-soft">
            <div className="mb-6 flex items-center justify-between gap-3">
              <div>
                <h2 className="text-lg font-semibold text-card-foreground">
                  Account details
                </h2>
                <p className="mt-1 text-sm text-muted-foreground">
                  Core information used across your FitHub experience.
                </p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
                <User2 className="h-5 w-5 text-primary" />
              </div>
            </div>
            <div className="grid gap-4 md:grid-cols-2">
              <ProfileField
                label="Email"
                value={user?.email ?? 'Not available'}
                icon={<Mail className="h-4 w-4 text-muted-foreground" />}
              />
              <ProfileField
                label="Phone"
                value={profile?.phone ?? 'Add a contact number'}
                icon={<Phone className="h-4 w-4 text-muted-foreground" />}
              />
              <ProfileField
                label="Account status"
                value={profile?.active ? 'Active' : 'Inactive'}
                badgeClassName={
                  profile?.active
                    ? 'bg-success/10 text-success'
                    : 'bg-muted text-muted-foreground'
                }
              />
              <ProfileField
                label="Member since"
                value={formattedCreatedAt ?? '-'}
                icon={<Calendar className="h-4 w-4 text-muted-foreground" />}
              />
              <ProfileField
                label="Birthdate"
                value={formattedBirthdate ?? 'Add your birthdate'}
                icon={<Calendar className="h-4 w-4 text-muted-foreground" />}
              />
              <ProfileField label="Gender" value={profile?.gender ?? '-'} />
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.35 }}
          className="space-y-4"
        >
          <div className="rounded-2xl border border-border bg-card p-6 shadow-soft">
            <h2 className="text-lg font-semibold text-card-foreground">
              Physical stats
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">
              Data your trainers use to tailor your sessions.
            </p>
            <div className="mt-6 space-y-3">
              <StatRow
                icon={<Weight className="h-4 w-4 text-muted-foreground" />}
                label="Current weight"
                value={profile?.weight ? `${profile.weight} kg` : 'Add weight'}
              />
              <StatRow
                icon={<HeightIcon />}
                label="Height"
                value={profile?.height ? `${profile.height} cm` : 'Add height'}
              />
              <StatRow
                icon={<Droplets className="h-4 w-4 text-primary" />}
                label="Daily hydration target"
                value={
                  profile?.dailyWaterTarget
                    ? `${profile.dailyWaterTarget} ml`
                    : 'Set your target'
                }
              />
            </div>
          </div>
        </motion.div>
      </div>

      {isLoading && (
        <div className="absolute inset-0 z-10 flex items-center justify-center bg-background/60 backdrop-blur-sm">
          <div className="flex items-center gap-3 rounded-2xl border border-border bg-card px-4 py-3 text-sm text-foreground shadow-soft-lg">
            <span className="inline-flex h-4 w-4 animate-spin rounded-full border-2 border-border border-t-primary" />
            Loading your profile...
          </div>
        </div>
      )}

      {error && !isLoading && (
        <div className="rounded-2xl border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
          {error}
        </div>
      )}

      {isEditOpen && (
        <div className="fixed inset-0 z-30 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.98 }}
            className="max-h-[calc(100vh-3rem)] w-full max-w-2xl overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
          >
            <div className="mb-4 flex items-center justify-between gap-4">
              <div>
                <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  Edit profile
                </p>
                <p className="mt-1 text-sm text-muted-foreground">
                  Update your basic information and metrics.
                </p>
              </div>
              <button
                type="button"
                aria-label="Close profile editor"
                onClick={() => setIsEditOpen(false)}
                disabled={isSaving}
                className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-border bg-background text-muted-foreground transition hover:bg-accent hover:text-accent-foreground disabled:cursor-not-allowed disabled:opacity-60"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="grid gap-3 md:grid-cols-2">
                <EditField
                  label="First name"
                  value={form.firstname}
                  error={formErrors.firstname}
                  onChange={(value) => updateField('firstname', value)}
                />
                <EditField
                  label="Last name"
                  value={form.lastname}
                  error={formErrors.lastname}
                  onChange={(value) => updateField('lastname', value)}
                />
                <EditField
                  label="Phone"
                  value={form.phone}
                  error={formErrors.phone}
                  onChange={(value) => updateField('phone', value)}
                  placeholder="+12025550123"
                />
                <EditField
                  label="Birthdate"
                  type="date"
                  value={form.birthdate}
                  error={formErrors.birthdate}
                  onChange={(value) => updateField('birthdate', value)}
                />
                <EditField
                  label="Height (cm)"
                  type="number"
                  value={form.height}
                  error={formErrors.height}
                  onChange={(value) => updateField('height', value)}
                />
                <EditField
                  label="Weight (kg)"
                  type="number"
                  step="0.1"
                  value={form.weight}
                  error={formErrors.weight}
                  onChange={(value) => updateField('weight', value)}
                />
                <EditField
                  label="Water target (ml)"
                  type="number"
                  value={form.dailyWaterTarget}
                  error={formErrors.dailyWaterTarget}
                  onChange={(value) => updateField('dailyWaterTarget', value)}
                />
                <label className="space-y-1.5">
                  <span className="text-xs text-foreground">Gender</span>
                  <select
                    value={form.gender}
                    onChange={(event) =>
                      updateField('gender', event.target.value as ClientGender)
                    }
                    className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft transition-all focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                  >
                    {genderOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                  {formErrors.gender && (
                    <span className="text-xs text-destructive">
                      {formErrors.gender}
                    </span>
                  )}
                </label>
              </div>

              <div className="flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsEditOpen(false)}
                  disabled={isSaving}
                  className="inline-flex h-9 items-center justify-center rounded-xl px-4 text-sm font-medium text-foreground transition-all hover:bg-accent disabled:cursor-not-allowed disabled:opacity-60"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSaving || !canSave}
                  className="inline-flex h-9 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {isSaving && (
                    <span className="inline-flex h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                  )}
                  {isSaving ? 'Saving...' : 'Save changes'}
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </div>
  )
}

const validateProfileForm = (form: ProfileForm): ProfileErrors => {
  const errors: ProfileErrors = {}

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

type ProfileFieldProps = {
  label: string
  value: string
  icon?: ReactNode
  badgeClassName?: string
}

const ProfileField = ({
  label,
  value,
  icon,
  badgeClassName,
}: ProfileFieldProps) => {
  const isBadge = Boolean(badgeClassName)
  return (
    <div className="space-y-1.5">
      <p className="text-xs text-muted-foreground">{label}</p>
      <div className="flex items-center gap-2 text-sm text-foreground">
        {icon}
        {isBadge ? (
          <span
            className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${badgeClassName}`}
          >
            {value}
          </span>
        ) : (
          <span>{value}</span>
        )}
      </div>
    </div>
  )
}

type StatRowProps = {
  icon: ReactNode
  label: string
  value: string
}

const StatRow = ({ icon, label, value }: StatRowProps) => (
  <div className="flex items-center justify-between rounded-xl bg-muted px-4 py-3">
    <div className="flex items-center gap-3">
      {icon}
      <div>
        <p className="text-xs text-muted-foreground">{label}</p>
        <p className="text-sm font-semibold text-foreground">{value}</p>
      </div>
    </div>
  </div>
)

const HeightIcon = () => (
  <div className="flex h-4 w-4 items-center justify-center rounded-full border border-border text-[9px] text-muted-foreground">
    cm
  </div>
)

type EditFieldProps = {
  label: string
  value: string
  onChange: (value: string) => void
  error?: string
  type?: string
  step?: string
  placeholder?: string
}

const EditField = ({
  label,
  value,
  onChange,
  error,
  type = 'text',
  step,
  placeholder,
}: EditFieldProps) => (
  <label className="space-y-1.5">
    <span className="text-xs text-foreground">{label}</span>
    <input
      type={type}
      step={step}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      className={`flex h-10 w-full rounded-xl border bg-background px-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring ${
        error ? 'border-destructive' : 'border-border'
      }`}
      placeholder={placeholder}
    />
    {error && <span className="text-xs text-destructive">{error}</span>}
  </label>
)

export default Profile
