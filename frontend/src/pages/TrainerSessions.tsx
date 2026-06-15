import { useEffect, useState, type FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import { CalendarDays, Plus, Users2, X } from 'lucide-react'
import { Input } from '../components/ui/input'
import { Button } from '../components/ui/button'
import { Label } from '../components/ui/label'
import { EmptyState } from '../components/ui/empty-state'
import { Pagination } from '../components/ui/pagination'
import { StatusBadge, sessionStatusColors } from '../components/ui/status-badge'
import {
  getTrainingSessions,
  createSession,
  updateSession,
  checkInClient,
  getAttendanceBySession,
  type TrainingSessionResponse,
  type CreateTrainingSessionRequest,
  type UpdateTrainingSessionRequest,
  type AttendanceSessionResponse,
} from '../services/workout.service'
import { getApiErrorMessage } from '../utils/errorHandler'
import { formatDateTime } from '../lib/utils'
import toast from '../utils/toast'

const TrainerSessions = () => {
  const { t } = useTranslation(['sessions', 'common'])
  const [sessions, setSessions] = useState<TrainingSessionResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
  const [editingSession, setEditingSession] = useState<TrainingSessionResponse | null>(null)
  const [selectedSession, setSelectedSession] = useState<TrainingSessionResponse | null>(null)
  const [attendances, setAttendances] = useState<AttendanceSessionResponse[]>([])
  const [isLoadingAttendances, setIsLoadingAttendances] = useState(false)
  const [checkInClientId, setCheckInClientId] = useState('')

  const loadSessions = async (page = 0) => {
    setIsLoading(true)
    try {
      const result = await getTrainingSessions(page, 12)
      setSessions(result.content)
      setTotalPages(result.totalPages)
      setCurrentPage(page)
    } catch (err) {
      console.error(err)
      toast.error(t('errors.loadFailed'))
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadSessions()
  }, [])

  const handleCheckIn = async (sessionId: string) => {
    if (!checkInClientId.trim()) return
    try {
      await checkInClient(sessionId, checkInClientId)
      toast.success(t('common:toast.sessionJoined'))
      setCheckInClientId('')
      await loadAttendances(sessionId)
    } catch (err) {
      toast.error(getApiErrorMessage(err))
    }
  }

  const loadAttendances = async (sessionId: string) => {
    setIsLoadingAttendances(true)
    try {
      const result = await getAttendanceBySession(sessionId)
      setAttendances(result)
    } catch (err) {
      console.error(err)
    } finally {
      setIsLoadingAttendances(false)
    }
  }

  const handleViewAttendance = async (session: TrainingSessionResponse) => {
    setSelectedSession(session)
    await loadAttendances(session.id)
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground">{t('title')}</h1>
          <p className="text-sm text-muted-foreground">{t('subtitle')}</p>
        </div>
        <Button onClick={() => setIsCreateModalOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          {t('common:buttons.create')}
        </Button>
      </div>

      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-2">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-48 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : sessions.length ? (
        <>
          <div className="grid gap-4 md:grid-cols-2">
            {sessions.map((session) => (
              <motion.div
                key={session.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="rounded-2xl border border-border bg-card p-5 shadow-soft"
              >
                <div className="flex items-start justify-between">
                  <div>
                    <div className="flex items-center gap-2">
                      <StatusBadge
                        status={session.status}
                        colorMap={sessionStatusColors}
                      />
                      <span className="text-xs text-muted-foreground">
                        {t(`type.${session.type}`)}
                      </span>
                    </div>
                    <p className="mt-2 text-sm text-muted-foreground">
                      {t('startsAt')}: {formatDateTime(session.startTime)}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      {t('capacity')}: {session.currentParticipants}/{session.maxParticipants}
                    </p>
                  </div>
                </div>
                <div className="mt-4 flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => void handleViewAttendance(session)}
                  >
                    <Users2 className="mr-1 h-4 w-4" />
                    {t('viewAttendance')}
                  </Button>
                </div>
              </motion.div>
            ))}
          </div>
          <div className="mt-6">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={(page) => void loadSessions(page)}
            />
          </div>
        </>
      ) : (
        <EmptyState
          icon={CalendarDays}
          title={t('emptyState.title')}
          description={t('emptyState.description')}
        />
      )}

      {isCreateModalOpen && (
        <SessionFormModal
          onClose={() => setIsCreateModalOpen(false)}
          onSuccess={() => {
            setIsCreateModalOpen(false)
            void loadSessions(currentPage)
          }}
        />
      )}

      {selectedSession && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.96 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            className="max-h-[calc(100vh-3rem)] w-full max-w-lg overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
          >
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-lg font-semibold text-foreground">
                {t('common:buttons.edit')}
              </h2>
              <button
                type="button"
                onClick={() => {
                  setSelectedSession(null)
                  setAttendances([])
                }}
                className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-background text-muted-foreground hover:bg-accent"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            <div className="space-y-4">
              <div className="space-y-1.5">
                <Label>{t('common:labels.search')}</Label>
                <div className="flex gap-2">
                  <Input
                    placeholder="Client ID"
                    value={checkInClientId}
                    onChange={(e) => setCheckInClientId(e.target.value)}
                  />
                  <Button onClick={() => void handleCheckIn(selectedSession.id)}>
                    {t('join')}
                  </Button>
                </div>
              </div>

              <div className="space-y-2">
                <h3 className="font-medium text-foreground">{t('capacity')}</h3>
                {isLoadingAttendances ? (
                  <div className="h-20 animate-pulse rounded-xl bg-muted" />
                ) : attendances.length ? (
                  <div className="space-y-2">
                    {attendances.map((att) => (
                      <div
                        key={att.id}
                        className="flex items-center justify-between rounded-xl border border-border bg-background p-3"
                      >
                        <span className="text-sm text-foreground">
                          {att.client.clientFirstname} {att.client.clientLastname}
                        </span>
                        <span className="text-xs text-muted-foreground">
                          {formatDateTime(att.checkInTime)}
                        </span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">
                    {t('emptyState.description')}
                  </p>
                )}
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  )
}

const SessionFormModal = ({
  onClose,
  onSuccess,
}: {
  onClose: () => void
  onSuccess: () => void
}) => {
  const { t } = useTranslation(['sessions', 'common'])
  const [form, setForm] = useState<CreateTrainingSessionRequest>({
    type: 'GROUP',
    startTime: '',
    endTime: '',
    maxParticipants: 10,
  })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    try {
      await createSession(form)
      toast.success(t('common:messages.created'))
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
            {t('common:buttons.create')}
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
            <Label>{t('form.sessionType')}</Label>
            <select
              value={form.type}
              onChange={(e) => setForm({ ...form, type: e.target.value as 'GROUP' | 'PERSONAL' })}
              className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
            >
              <option value="GROUP">{t('type.GROUP')}</option>
              <option value="PERSONAL">{t('type.PERSONAL')}</option>
            </select>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-1.5">
              <Label>{t('startsAt')}</Label>
              <Input
                type="datetime-local"
                value={form.startTime}
                onChange={(e) => setForm({ ...form, startTime: e.target.value })}
                required
              />
            </div>
            <div className="space-y-1.5">
              <Label>{t('form.endTime')}</Label>
              <Input
                type="datetime-local"
                value={form.endTime}
                onChange={(e) => setForm({ ...form, endTime: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label>{t('capacity')}</Label>
            <Input
              type="number"
              min="1"
              value={form.maxParticipants}
              onChange={(e) => setForm({ ...form, maxParticipants: parseInt(e.target.value) || 10 })}
            />
          </div>

          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={onClose}>
              {t('common:buttons.cancel')}
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? t('common:buttons.loading') : t('common:buttons.create')}
            </Button>
          </div>
        </form>
      </motion.div>
    </div>
  )
}

export default TrainerSessions
