import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Calendar, Droplets, Phone, User2, Weight } from 'lucide-react'
import { Button } from '../components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
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
        // eslint-disable-next-line no-console
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
          <p className="text-xs uppercase tracking-[0.24em] text-emerald-300">
            Profile
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-slate-50 md:text-3xl">
            {fullName}
          </h1>
          <p className="mt-1 text-sm text-slate-400">
            Manage your personal details and training metrics.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <Button
            variant="outline"
            onClick={() => setIsEditOpen(true)}
            className="rounded-full border-slate-700 bg-slate-900/80 text-xs"
          >
            Edit profile
          </Button>
        </div>
      </div>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,1.4fr),minmax(0,1fr)]">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <Card className="border-slate-800/80 bg-slate-900/80">
            <CardHeader className="flex flex-row items-center justify-between gap-3">
              <div>
                <CardTitle>Account details</CardTitle>
                <CardDescription>
                  Core information used across your FitHub experience.
                </CardDescription>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-tr from-emerald-400 via-cyan-400 to-sky-500 p-[2px]">
                <div className="flex h-full w-full items-center justify-center rounded-2xl bg-slate-950">
                  <User2 className="h-4 w-4 text-slate-50" />
                </div>
              </div>
            </CardHeader>
            <CardContent className="grid gap-4 md:grid-cols-2">
              <ProfileField
                label="Email"
                value={user?.email ?? 'Not available'}
                icon={<MailDot />}
              />
              <ProfileField
                label="Phone"
                value={profile?.phone ?? 'Add a contact number'}
                icon={<Phone className="h-3.5 w-3.5 text-slate-400" />}
              />
              <ProfileField
                label="Account status"
                value={profile?.active ? 'Active' : 'Inactive'}
                badgeClassName={
                  profile?.active
                    ? 'bg-emerald-500/15 text-emerald-300'
                    : 'bg-slate-700/60 text-slate-300'
                }
              />
              <ProfileField
                label="Member since"
                value={formattedCreatedAt ?? '—'}
                icon={<Calendar className="h-3.5 w-3.5 text-slate-400" />}
              />
              <ProfileField
                label="Birthdate"
                value={formattedBirthdate ?? 'Add your birthdate'}
                icon={<Calendar className="h-3.5 w-3.5 text-slate-400" />}
              />
              <ProfileField
                label="Gender"
                value={profile?.gender ?? '—'}
              />
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.35 }}
          className="space-y-4"
        >
          <Card className="border-slate-800/80 bg-slate-900/80">
            <CardHeader>
              <CardTitle>Physical stats</CardTitle>
              <CardDescription>
                Data your trainers use to tailor your sessions.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4 text-sm text-slate-300">
              <div className="flex items-center justify-between rounded-2xl bg-slate-950/60 px-4 py-3">
                <div className="flex items-center gap-3">
                  <Weight className="h-4 w-4 text-slate-400" />
                  <div>
                    <p className="text-xs text-slate-400">Current weight</p>
                    <p className="text-sm font-semibold text-slate-50">
                      {profile?.weight ? `${profile.weight} kg` : 'Add weight'}
                    </p>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between rounded-2xl bg-slate-950/60 px-4 py-3">
                <div className="flex items-center gap-3">
                  <HeightIcon />
                  <div>
                    <p className="text-xs text-slate-400">Height</p>
                    <p className="text-sm font-semibold text-slate-50">
                      {profile?.height ? `${profile.height} cm` : 'Add height'}
                    </p>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between rounded-2xl bg-slate-950/60 px-4 py-3">
                <div className="flex items-center gap-3">
                  <Droplets className="h-4 w-4 text-sky-400" />
                  <div>
                    <p className="text-xs text-slate-400">
                      Daily hydration target
                    </p>
                    <p className="text-sm font-semibold text-slate-50">
                      {profile?.dailyWaterTarget
                        ? `${profile.dailyWaterTarget} ml`
                        : 'Set your target'}
                    </p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </motion.div>
      </div>

      {isLoading && (
        <div className="absolute inset-0 left-0 top-0 z-10 flex items-center justify-center bg-slate-950/60 backdrop-blur-sm">
          <div className="flex items-center gap-3 rounded-2xl border border-slate-800/80 bg-slate-900/90 px-4 py-3 text-sm text-slate-200">
            <span className="inline-flex h-4 w-4 animate-spin rounded-full border-[2px] border-slate-700 border-t-emerald-400" />
            Loading your profile…
          </div>
        </div>
      )}

      {error && !isLoading && (
        <div className="rounded-2xl border border-red-500/40 bg-red-500/10 px-4 py-3 text-sm text-red-100">
          {error}
        </div>
      )}

      {isEditOpen && (
        <div className="fixed inset-0 z-30 flex items-center justify-center bg-slate-950/70 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.98 }}
            className="w-full max-w-lg rounded-3xl border border-slate-800/80 bg-slate-950/90 p-6 shadow-[0_24px_80px_rgba(15,23,42,0.95)]"
          >
            <div className="mb-4 flex items-center justify-between">
              <div>
                <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
                  Edit profile
                </p>
                <p className="mt-1 text-sm text-slate-300">
                  Update your basic information and metrics.
                </p>
              </div>
              <button
                type="button"
                onClick={() => setIsEditOpen(false)}
                className="h-8 w-8 rounded-full border border-slate-800/80 bg-slate-900/80 text-xs text-slate-400 transition hover:border-slate-700 hover:text-slate-100"
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
              <Button
                type="button"
                variant="ghost"
                size="sm"
                className="rounded-full px-4"
                onClick={() => setIsEditOpen(false)}
              >
                Cancel
              </Button>
              <Button
                type="button"
                size="sm"
                className="rounded-full px-4"
              >
                Save changes
              </Button>
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
    <div className="space-y-1.5 text-xs text-slate-400">
      <p>{label}</p>
      <div className="flex items-center gap-2 text-sm text-slate-100">
        {icon}
        {isBadge ? (
          <span
            className={`inline-flex items-center rounded-full px-2 py-0.5 text-[11px] ${badgeClassName}`}
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

const MailDot = () => (
  <div className="relative flex h-3.5 w-3.5 items-center justify-center">
    <span className="absolute inline-flex h-3 w-3 animate-ping rounded-full bg-emerald-400/40" />
    <span className="relative inline-flex h-2 w-2 rounded-full bg-emerald-400" />
  </div>
)

const HeightIcon = () => (
  <div className="flex h-4 w-4 items-center justify-center rounded-full border border-slate-700/80 text-[9px] text-slate-300">
    cm
  </div>
)

type EditFieldProps = {
  label: string
  placeholder?: string | number | null
}

const EditField = ({ label, placeholder }: EditFieldProps) => (
  <label className="space-y-1.5 text-xs text-slate-300">
    <span>{label}</span>
    <input
      className="mt-0.5 w-full rounded-xl border border-slate-800/80 bg-slate-900/80 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-500 focus:border-emerald-500/80 focus:outline-none focus:ring-1 focus:ring-emerald-500/70"
      placeholder={placeholder ? String(placeholder) : ''}
    />
  </label>
)

export default Profile

