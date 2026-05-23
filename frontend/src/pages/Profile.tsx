import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Calendar, Droplets, Mail, Phone, User2, Weight } from 'lucide-react'
import { useAuthStore } from '../store/useAuthStore'
import { getMyClientProfile } from '../services/profile.service'
import type { ClientProfileResponse } from '../services/user.service'

const Profile = () => {
  const { user } = useAuthStore()
  const [profile, setProfile] = useState<ClientProfileResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isEditOpen, setIsEditOpen] = useState(false)

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const data = await getMyClientProfile()
        setProfile(data)
      } catch (err) {
        console.error(err)
        setError('Unable to load your profile right now.')
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

  return (
    <div className="space-y-6">
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

        <div className="flex flex-wrap items-center gap-3">
          <button
            onClick={() => setIsEditOpen(true)}
            className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-background px-4 text-sm font-semibold text-foreground transition-all hover:bg-accent"
          >
            Edit profile
          </button>
        </div>
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
                value={formattedCreatedAt ?? '—'}
                icon={<Calendar className="h-4 w-4 text-muted-foreground" />}
              />
              <ProfileField
                label="Birthdate"
                value={formattedBirthdate ?? 'Add your birthdate'}
                icon={<Calendar className="h-4 w-4 text-muted-foreground" />}
              />
              <ProfileField label="Gender" value={profile?.gender ?? '—'} />
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
              <div className="flex items-center justify-between rounded-xl bg-muted px-4 py-3">
                <div className="flex items-center gap-3">
                  <Weight className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <p className="text-xs text-muted-foreground">Current weight</p>
                    <p className="text-sm font-semibold text-foreground">
                      {profile?.weight ? `${profile.weight} kg` : 'Add weight'}
                    </p>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between rounded-xl bg-muted px-4 py-3">
                <div className="flex items-center gap-3">
                  <HeightIcon />
                  <div>
                    <p className="text-xs text-muted-foreground">Height</p>
                    <p className="text-sm font-semibold text-foreground">
                      {profile?.height ? `${profile.height} cm` : 'Add height'}
                    </p>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between rounded-xl bg-muted px-4 py-3">
                <div className="flex items-center gap-3">
                  <Droplets className="h-4 w-4 text-primary" />
                  <div>
                    <p className="text-xs text-muted-foreground">
                      Daily hydration target
                    </p>
                    <p className="text-sm font-semibold text-foreground">
                      {profile?.dailyWaterTarget
                        ? `${profile.dailyWaterTarget} ml`
                        : 'Set your target'}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </motion.div>
      </div>

      {isLoading && (
        <div className="absolute inset-0 left-0 top-0 z-10 flex items-center justify-center bg-background/60 backdrop-blur-sm">
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
        <div className="fixed inset-0 z-30 flex items-center justify-center bg-background/70 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.98 }}
            className="w-full max-w-lg rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
          >
            <div className="mb-4 flex items-center justify-between">
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
                onClick={() => setIsEditOpen(false)}
                className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-background text-sm text-muted-foreground transition hover:bg-accent hover:text-accent-foreground"
              >
                ✕
              </button>
            </div>

            <div className="grid gap-3 md:grid-cols-2">
              <EditField label="First name" placeholder={profile?.firstname} />
              <EditField label="Last name" placeholder={profile?.lastname} />
              <EditField label="Phone" placeholder={profile?.phone} />
              <EditField label="Height (cm)" placeholder={profile?.height} />
              <EditField label="Weight (kg)" placeholder={profile?.weight} />
              <EditField
                label="Water target (ml)"
                placeholder={profile?.dailyWaterTarget}
              />
            </div>

            <div className="mt-5 flex items-center justify-end gap-2">
              <button
                type="button"
                onClick={() => setIsEditOpen(false)}
                className="inline-flex h-9 items-center justify-center rounded-xl px-4 text-sm font-medium text-foreground transition-all hover:bg-accent"
              >
                Cancel
              </button>
              <button
                type="button"
                className="inline-flex h-9 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
              >
                Save changes
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  )
}

type ProfileFieldProps = {
  label: string
  value: string
  icon?: React.ReactNode
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

const HeightIcon = () => (
  <div className="flex h-4 w-4 items-center justify-center rounded-full border border-border text-[9px] text-muted-foreground">
    cm
  </div>
)

type EditFieldProps = {
  label: string
  placeholder?: string | number | null
}

const EditField = ({ label, placeholder }: EditFieldProps) => (
  <label className="space-y-1.5">
    <span className="text-xs text-foreground">{label}</span>
    <input
      className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
      placeholder={placeholder ? String(placeholder) : ''}
    />
  </label>
)

export default Profile
