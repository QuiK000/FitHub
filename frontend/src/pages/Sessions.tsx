import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import {
  CalendarDays,
  CheckCircle,
  Clock,
  Dumbbell,
  Users2,
  UserCheck,
} from 'lucide-react'
import { useAuthStore } from '../store/useAuthStore'
import {
  getTrainingSessions,
  getMyAttendance,
  joinSession,
  type TrainingSessionResponse,
} from '../services/workout.service'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'
import { formatEnum } from '../lib/utils'
import { EmptyState } from '../components/ui/empty-state'
import { InfoTile } from '../components/ui/info-tile'

type SessionFilter = 'all' | 'upcoming' | 'past'

const Sessions = () => {
  const { t } = useTranslation(['sessions', 'common'])
  const roles = useAuthStore((state) => state.roles)
  const isClient = roles.includes('CLIENT')
  const [sessions, setSessions] = useState<TrainingSessionResponse[]>([])
  const [joinedSessionIds, setJoinedSessionIds] = useState<Set<string>>(new Set())
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [joiningId, setJoiningId] = useState<string | null>(null)
  const [activeFilter, setActiveFilter] = useState<SessionFilter>('all')

  const loadSessions = async () => {
    setIsLoading(true)
    setError(null)
    try {
      const [sessionsPage, attendance] = await Promise.all([
        getTrainingSessions(0, 50),
        isClient ? getMyAttendance() : Promise.resolve([]),
      ])
      setSessions(sessionsPage.content)
      setJoinedSessionIds(new Set(attendance.map((a) => a.session.sessionId)))
    } catch (err) {
      console.error(err)
      setError('Unable to load training sessions.')
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadSessions()
  }, [isClient])

  const handleJoin = async (sessionId: string) => {
    setJoiningId(sessionId)
    try {
      await joinSession(sessionId)
      toast.success(t('common:toast.joinedSession'))
      await loadSessions()
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Unable to join session.'))
    } finally {
      setJoiningId(null)
    }
  }

  const now = Date.now()
  const upcoming = sessions.filter(
    (s) => new Date(s.startTime).getTime() >= now && s.status === 'SCHEDULED',
  )
  const past = sessions.filter(
    (s) => new Date(s.startTime).getTime() < now || s.status !== 'SCHEDULED',
  )

  const filteredSessions = activeFilter === 'upcoming'
    ? upcoming
    : activeFilter === 'past'
      ? past
      : sessions

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
      </div>

      {error && !isLoading && (
        <div className="rounded-2xl border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
          {error}
        </div>
      )}

      <div className="flex gap-1 overflow-x-auto rounded-xl border border-border bg-muted p-1">
        {(['all', 'upcoming', 'past'] as const).map((filter) => (
          <button
            key={filter}
            type="button"
            onClick={() => setActiveFilter(filter)}
            className={`inline-flex items-center gap-2 whitespace-nowrap rounded-lg px-4 py-2 text-sm font-medium transition ${
              activeFilter === filter
                ? 'bg-background text-foreground shadow-soft'
                : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            {filter === 'all' && t('filter.all')}
            {filter === 'upcoming' && t('upcoming')}
            {filter === 'past' && t('past')}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-2">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-48 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : filteredSessions.length ? (
        <div className="grid gap-4 md:grid-cols-2">
          {filteredSessions.map((session) => {
            const isPast = new Date(session.startTime).getTime() < now || session.status !== 'SCHEDULED'
            return (
              <SessionCard
                key={session.id}
                session={session}
                isClient={isClient}
                isJoining={joiningId === session.id}
                isJoined={joinedSessionIds.has(session.id)}
                onJoin={() => void handleJoin(session.id)}
                isPast={isPast}
              />
            )
          })}
        </div>
      ) : (
        <EmptyState
          icon={CalendarDays}
          title={t('emptyState.title')}
          description={t('emptyState.description')}
        />
      )}
    </div>
  )
}

const SessionCard = ({
  session,
  isClient,
  isJoining,
  isJoined,
  onJoin,
  isPast = false,
}: {
  session: TrainingSessionResponse
  isClient: boolean
  isJoining: boolean
  isJoined: boolean
  onJoin: () => void
  isPast?: boolean
}) => {
  const { t } = useTranslation('sessions')
  const spotsLeft = session.maxParticipants - session.currentParticipants
  const isFull = spotsLeft <= 0
  const trainerName =
    [session.trainer.firstname, session.trainer.lastname]
      .filter(Boolean)
      .join(' ') || 'Trainer'

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.25 }}
      className={`rounded-2xl border border-border bg-card p-5 shadow-soft ${isPast ? 'opacity-70' : ''}`}
    >
      <div className="flex items-start justify-between gap-3">
        <div>
          <div className="flex items-center gap-2">
            <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
              session.type === 'GROUP'
                ? 'bg-blue-500/10 text-blue-600 dark:text-blue-400'
                : 'bg-violet-500/10 text-violet-600 dark:text-violet-400'
            }`}>
              {session.type}
            </span>
            <StatusBadge status={session.status} />
            {isJoined && !isPast && (
              <span className="inline-flex items-center gap-1 rounded-full bg-emerald-500/10 px-2 py-1 text-xs font-semibold text-emerald-600 dark:text-emerald-400">
                <CheckCircle className="h-3 w-3" />
                {t('joined')}
              </span>
            )}
          </div>
          <h3 className="mt-3 text-base font-semibold text-foreground">
            {formatEnum(session.type)} training
          </h3>
          <p className="mt-1 text-sm text-muted-foreground">
            With {trainerName}
          </p>
        </div>
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
          <Dumbbell className="h-5 w-5 text-primary" />
        </div>
      </div>

      <div className="mt-5 grid gap-3 text-sm sm:grid-cols-2">
        <InfoTile
          icon={Clock}
          label="Starts"
          value={formatDateTime(session.startTime)}
        />
        <InfoTile
          icon={Users2}
          label="Capacity"
          value={`${session.currentParticipants}/${session.maxParticipants}`}
        />
      </div>

      {isClient && !isPast && session.status === 'SCHEDULED' && (
        <div className="mt-4">
          {isJoined ? (
            <p className="inline-flex items-center gap-1.5 text-xs font-medium text-emerald-600 dark:text-emerald-400">
              <CheckCircle className="h-3.5 w-3.5" />
              {t('alreadyJoined')}
            </p>
          ) : isFull ? (
            <p className="text-xs text-muted-foreground">{t('sessionFull')}</p>
          ) : (
            <button
              type="button"
              disabled={isJoining}
              onClick={onJoin}
              className="inline-flex h-9 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isJoining ? (
                <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
              ) : (
                <UserCheck className="h-4 w-4" />
              )}
              {isJoining ? t('joining') : t('join', { spots: spotsLeft })}
            </button>
          )}
        </div>
      )}
    </motion.div>
  )
}

const StatusBadge = ({ status }: { status: string }) => {
  const colors: Record<string, string> = {
    SCHEDULED: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
    COMPLETED: 'bg-muted text-muted-foreground',
    CANCELLED: 'bg-red-500/10 text-red-600 dark:text-red-400',
  }
  return (
    <span className={`rounded-full px-2 py-1 text-xs font-semibold ${colors[status] ?? 'bg-muted text-muted-foreground'}`}>
      {status}
    </span>
  )
}

const formatDateTime = (value: string) => {
  const date = new Date(value)
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(date)
}

export default Sessions
