import { useEffect, useState, type FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import {
  BarChart3,
  CreditCard,
  Bell,
  Eye,
  EyeOff,
  PauseCircle,
  PlayCircle,
  Plus,
  Search,
  ShieldCheck,
  Send,
  Star,
  User2,
  X,
  XCircle,
} from 'lucide-react'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import { Input } from '../components/ui/input'
import { searchClients, type ClientProfileResponse } from '../services/user.service'
import {
  createMembership,
  activateMembership,
  freezeMembership,
  unfreezeMembership,
  cancelMembership,
  getMembershipByClientId,
  type MembershipResponse,
} from '../services/membership.service'
import {
  getDashboardAnalytics,
  type DashboardAnalyticsResponse,
} from '../services/dashboard.service'
import {
  getAllReviews,
  updateReviewVisibility,
  type TrainerReviewResponse,
} from '../services/review.service'
import { formatEnum, formatDate } from '../lib/utils'
import api from '../services/api'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

type AdminTab = 'overview' | 'memberships' | 'broadcast' | 'reviews'

const getAdminTabs = (t: (key: string) => string): { key: AdminTab; label: string; icon: React.ComponentType<React.SVGProps<SVGSVGElement>> }[] => [
  { key: 'overview', label: t('tabs.overview'), icon: BarChart3 },
  { key: 'memberships', label: t('tabs.memberships'), icon: CreditCard },
  { key: 'reviews', label: t('tabs.reviews'), icon: Star },
  { key: 'broadcast', label: t('tabs.broadcast'), icon: Send },
]

const Admin = () => {
  const { t } = useTranslation(['admin', 'common'])
  const [activeTab, setActiveTab] = useState<AdminTab>('overview')

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

      <div className="flex gap-1 overflow-x-auto rounded-xl border border-border bg-muted p-1">
        {getAdminTabs(t).map((tab) => (
          <button
            key={tab.key}
            type="button"
            onClick={() => setActiveTab(tab.key)}
            className={`inline-flex items-center gap-2 whitespace-nowrap rounded-lg px-4 py-2 text-sm font-medium transition ${
              activeTab === tab.key
                ? 'bg-background text-foreground shadow-soft'
                : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            <tab.icon className="h-4 w-4" />
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'overview' && <OverviewTab />}
      {activeTab === 'memberships' && <MembershipTab />}
      {activeTab === 'reviews' && <ReviewsTab />}
      {activeTab === 'broadcast' && <BroadcastTab />}
    </div>
  )
}

const OverviewTab = () => {
  const { t } = useTranslation(['admin', 'common'])
  const [analytics, setAnalytics] = useState<DashboardAnalyticsResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      setIsLoading(true)
      try {
        const data = await getDashboardAnalytics()
        setAnalytics(data)
      } catch {
        // silent
      } finally {
        setIsLoading(false)
      }
    }
    void load()
  }, [])

  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="h-36 animate-pulse rounded-2xl bg-muted" />
        ))}
      </div>
    )
  }

  return (
    <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      <MetricCard
        icon={ShieldCheck}
        label={t('overview.activeClients')}
        value={analytics?.activeClients?.toString() ?? '0'}
        tone="bg-blue-500"
      />
      <MetricCard
        icon={CreditCard}
        label={t('overview.activeMemberships')}
        value={analytics?.activeMemberships?.toString() ?? '0'}
        tone="bg-emerald-500"
      />
      <MetricCard
        icon={BarChart3}
        label={t('overview.revenue')}
        value={`$${analytics?.revenue?.toLocaleString() ?? '0'}`}
        tone="bg-violet-500"
      />
      <MetricCard
        icon={Bell}
        label={t('overview.todayCheckIns')}
        value={analytics?.todayCheckIns?.toString() ?? '0'}
        tone="bg-amber-500"
      />
    </section>
  )
}

const MembershipTab = () => {
  const { t } = useTranslation(['admin', 'common'])
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<ClientProfileResponse[]>([])
  const [isSearching, setIsSearching] = useState(false)
  const [selectedClient, setSelectedClient] = useState<ClientProfileResponse | null>(null)
  const [memberships, setMemberships] = useState<MembershipResponse[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [isCreateOpen, setIsCreateOpen] = useState(false)
  const [showDropdown, setShowDropdown] = useState(false)

  const handleSearchClients = async (query: string) => {
    setSearchQuery(query)
    if (query.trim().length < 2) {
      setSearchResults([])
      setShowDropdown(false)
      return
    }

    setIsSearching(true)
    try {
      const page = await searchClients(query.trim(), 0, 10)
      setSearchResults(page.content)
      setShowDropdown(true)
    } catch {
      setSearchResults([])
    } finally {
      setIsSearching(false)
    }
  }

  const handleSelectClient = async (client: ClientProfileResponse) => {
    setSelectedClient(client)
    setSearchQuery(`${client.firstname} ${client.lastname}`)
    setShowDropdown(false)
    if (!client.id) {
      toast.error('Client ID not available. Please try again.')
      return
    }
    setIsLoading(true)
    try {
      const page = await getMembershipByClientId(client.id, 0, 20)
      setMemberships(page.content)
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Unable to load memberships.'))
      setMemberships([])
    } finally {
      setIsLoading(false)
    }
  }

  const handleClearSelection = () => {
    setSelectedClient(null)
    setSearchQuery('')
    setMemberships([])
    setSearchResults([])
  }

  const handleAction = async (
    action: () => Promise<MembershipResponse>,
    label: string,
  ) => {
    try {
      await action()
      toast.success(`${label} successfully.`)
      if (selectedClient?.id) {
        const page = await getMembershipByClientId(selectedClient.id, 0, 20)
        setMemberships(page.content)
      }
    } catch (err) {
      toast.error(getApiErrorMessage(err, `Failed to ${label.toLowerCase()}.`))
    }
  }

  return (
    <div className="space-y-6">
      <div className="rounded-2xl border border-border bg-card p-5">
        <h3 className="text-sm font-semibold text-foreground mb-3">Find Client</h3>
        <div className="relative">
          <div className="flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                value={searchQuery}
                onChange={(e) => void handleSearchClients(e.target.value)}
                onFocus={() => searchResults.length > 0 && setShowDropdown(true)}
                placeholder="Search by name or email..."
                className="pl-9"
              />
              {isSearching && (
                <span className="absolute right-3 top-1/2 -translate-y-1/2">
                  <span className="h-4 w-4 animate-spin rounded-full border-2 border-border border-t-primary" />
                </span>
              )}
            </div>
            {selectedClient && (
              <button
                type="button"
                onClick={handleClearSelection}
                className="inline-flex h-10 items-center justify-center rounded-xl border border-border bg-background px-3 text-sm text-muted-foreground transition hover:bg-accent"
              >
                <X className="h-4 w-4" />
              </button>
            )}
          </div>

          {showDropdown && searchResults.length > 0 && (
            <div className="absolute left-0 right-0 top-full z-50 mt-1 max-h-60 overflow-y-auto rounded-xl border border-border bg-card shadow-soft-lg">
              {searchResults.map((client) => (
                <button
                  key={client.id}
                  type="button"
                  onClick={() => void handleSelectClient(client)}
                  className="flex w-full items-center gap-3 px-4 py-3 text-left transition hover:bg-accent"
                >
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-primary/10 text-xs font-bold text-primary">
                    {client.firstname?.[0]}{client.lastname?.[0]}
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-semibold text-foreground truncate">
                      {client.firstname} {client.lastname}
                    </p>
                    <p className="text-xs text-muted-foreground truncate">
                      {client.phone || 'No phone'} · {client.gender || '-'}
                    </p>
                  </div>
                  <span className={`shrink-0 rounded-full px-2 py-0.5 text-xs font-medium ${client.active ? 'bg-emerald-500/10 text-emerald-600' : 'bg-muted text-muted-foreground'}`}>
                    {client.active ? 'Active' : 'Inactive'}
                  </span>
                </button>
              ))}
            </div>
          )}

          {showDropdown && searchResults.length === 0 && !isSearching && searchQuery.length >= 2 && (
            <div className="absolute left-0 right-0 top-full z-50 mt-1 rounded-xl border border-border bg-card p-4 shadow-soft-lg">
              <p className="text-sm text-muted-foreground text-center">No clients found</p>
            </div>
          )}
        </div>
        <p className="mt-2 text-xs text-muted-foreground">
          Start typing a client's name or email to search
        </p>
      </div>

      {selectedClient && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="rounded-2xl border border-primary/30 bg-primary/5 p-4"
        >
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-primary text-sm font-bold text-primary-foreground">
              {selectedClient.firstname?.[0]}{selectedClient.lastname?.[0]}
            </div>
            <div className="min-w-0 flex-1">
              <p className="font-semibold text-foreground">
                {selectedClient.firstname} {selectedClient.lastname}
              </p>
              <p className="text-xs text-muted-foreground">
                {selectedClient.phone || 'No phone'} · Member since {new Date(selectedClient.createdAt).toLocaleDateString()}
              </p>
            </div>
            {selectedClient.id && (
              <code className="shrink-0 rounded bg-background px-2 py-0.5 text-xs font-mono text-muted-foreground">
                {selectedClient.id.slice(0, 8)}...
              </code>
            )}
          </div>
        </motion.div>
      )}

      <div className="flex justify-end">
        <button
          type="button"
          onClick={() => setIsCreateOpen(true)}
          disabled={!selectedClient}
          className="inline-flex h-10 items-center justify-center gap-2 rounded-xl border border-border bg-background px-4 text-sm font-semibold text-foreground transition-all hover:bg-accent disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Plus className="h-4 w-4" />
          {t('memberships.createButton')}
        </button>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="h-24 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : memberships.length > 0 ? (
        <div className="divide-y divide-border overflow-hidden rounded-2xl border border-border">
          {memberships.map((m) => (
            <div key={m.id} className="grid gap-3 bg-card p-4 md:grid-cols-[minmax(0,1fr),auto]">
              <div>
                <p className="font-semibold text-foreground">{formatEnum(m.type)} plan</p>
                <p className="mt-1 text-xs text-muted-foreground">
                  {formatDate(m.startDate)} — {formatDate(m.endDate)}
                </p>
                <div className="mt-2 flex items-center gap-2">
                  <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${statusColors[m.status] ?? ''}`}>
                    {formatEnum(m.status)}
                  </span>
                  {m.visitsLeft !== null && (
                    <span className="text-xs text-muted-foreground">
                      {m.visitsLeft} visits left
                    </span>
                  )}
                </div>
              </div>
              <div className="flex flex-wrap items-center gap-2">
                {m.status === 'CREATED' && (
                  <ActionBtn icon={PlayCircle} label="Activate" onClick={() => void handleAction(() => activateMembership(m.id), 'Activated')} />
                )}
                {m.status === 'ACTIVE' && (
                  <ActionBtn icon={PauseCircle} label="Freeze" onClick={() => void handleAction(() => freezeMembership(m.id), 'Frozen')} />
                )}
                {m.status === 'FROZEN' && (
                  <ActionBtn icon={PlayCircle} label="Unfreeze" onClick={() => void handleAction(() => unfreezeMembership(m.id), 'Unfrozen')} />
                )}
                {(m.status === 'ACTIVE' || m.status === 'FROZEN') && (
                  <ActionBtn icon={XCircle} label="Cancel" onClick={() => void handleAction(() => cancelMembership(m.id), 'Cancelled')} danger />
                )}
              </div>
            </div>
          ))}
        </div>
      ) : selectedClient ? (
        <div className="flex min-h-32 flex-col items-center justify-center rounded-2xl border border-dashed border-border bg-muted/30 p-8 text-center">
          <CreditCard className="h-8 w-8 text-muted-foreground/40" />
          <p className="mt-3 text-sm font-semibold text-foreground">{t('memberships.noResults')}</p>
          <p className="mt-1 text-xs text-muted-foreground">This client doesn't have any memberships yet.</p>
        </div>
      ) : (
        <div className="flex min-h-32 flex-col items-center justify-center rounded-2xl border border-dashed border-border bg-muted/30 p-8 text-center">
          <User2 className="h-8 w-8 text-muted-foreground/40" />
          <p className="mt-3 text-sm font-semibold text-foreground">Select a client first</p>
          <p className="mt-1 text-xs text-muted-foreground">Search for a client above to manage their memberships.</p>
        </div>
      )}

      {isCreateOpen && selectedClient?.id && (
        <CreateMembershipModal
          clientId={selectedClient.id}
          onClose={() => setIsCreateOpen(false)}
          onCreated={async () => {
            setIsCreateOpen(false)
            toast.success(t('common:toast.membershipCreated'))
            const page = await getMembershipByClientId(selectedClient.id!, 0, 20)
            setMemberships(page.content)
          }}
        />
      )}
    </div>
  )
}

const ReviewsTab = () => {
  const { t } = useTranslation(['admin', 'common'])
  const [reviews, setReviews] = useState<TrainerReviewResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [filterVisible, setFilterVisible] = useState<boolean | undefined>(undefined)

  const loadReviews = async (isVisible?: boolean) => {
    setIsLoading(true)
    try {
      const page = await getAllReviews(0, 50, { isVisible })
      setReviews(page.content)
    } catch (err) {
      console.error(err)
      toast.error('Failed to load reviews.')
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadReviews(filterVisible)
  }, [filterVisible])

  const handleToggleVisibility = async (reviewId: string, currentVisibility: boolean) => {
    try {
      await updateReviewVisibility(reviewId, { visible: !currentVisibility })
      toast.success(t('common:toast.reviewVisibilityUpdated'))
      setReviews((prev) =>
        prev.map((r) =>
          r.id === reviewId ? { ...r, visible: !currentVisibility } : r,
        ),
      )
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Failed to update review visibility.'))
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-foreground">Review Moderation</h3>
        <div className="flex gap-1 rounded-xl border border-border bg-muted p-1">
          {[
            { label: 'All', value: undefined },
            { label: 'Visible', value: true },
            { label: 'Hidden', value: false },
          ].map((option) => (
            <button
              key={option.label}
              type="button"
              onClick={() => setFilterVisible(option.value)}
              className={`rounded-lg px-3 py-1 text-xs font-medium transition ${
                filterVisible === option.value
                  ? 'bg-background text-foreground shadow-soft'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-24 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : reviews.length > 0 ? (
        <div className="space-y-3">
          {reviews.map((review) => (
            <motion.div
              key={review.id}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              className="rounded-2xl border border-border bg-card p-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    <p className="text-sm font-semibold text-foreground">
                      {review.reviewer.clientFirstname} {review.reviewer.clientLastname}
                    </p>
                    <span className="text-xs text-muted-foreground">→</span>
                    <p className="text-sm text-muted-foreground">
                      Trainer review
                    </p>
                  </div>
                  <div className="mt-1 flex items-center gap-1">
                    {Array.from({ length: 5 }).map((_, i) => (
                      <Star
                        key={i}
                        className={`h-3.5 w-3.5 ${
                          i < review.rating
                            ? 'fill-amber-400 text-amber-400'
                            : 'text-muted-foreground'
                        }`}
                      />
                    ))}
                    <span className="ml-2 text-xs text-muted-foreground">
                      {review.rating}/5
                    </span>
                  </div>
                  {review.comment && (
                    <p className="mt-2 text-sm text-muted-foreground line-clamp-2">
                      {review.comment}
                    </p>
                  )}
                  <p className="mt-1 text-xs text-muted-foreground">
                    {formatDate(review.createdAt)}
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => void handleToggleVisibility(review.id, review.visible)}
                  className={`inline-flex shrink-0 items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-semibold transition ${
                    review.visible
                      ? 'border border-emerald-500/40 text-emerald-600 hover:bg-emerald-500/5'
                      : 'border border-border text-muted-foreground hover:bg-accent'
                  }`}
                >
                  {review.visible ? (
                    <>
                      <Eye className="h-3.5 w-3.5" />
                      Visible
                    </>
                  ) : (
                    <>
                      <EyeOff className="h-3.5 w-3.5" />
                      Hidden
                    </>
                  )}
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      ) : (
        <div className="flex min-h-40 flex-col items-center justify-center rounded-2xl border border-dashed border-border bg-muted/30 p-8 text-center">
          <Star className="h-8 w-8 text-muted-foreground/40" />
          <p className="mt-3 text-sm font-semibold text-foreground">No reviews found</p>
          <p className="mt-1 text-xs text-muted-foreground">
            Reviews from clients will appear here.
          </p>
        </div>
      )}
    </div>
  )
}

const BroadcastTab = () => {
  const { t } = useTranslation(['admin', 'common'])
  const [form, setForm] = useState({
    title: '',
    message: '',
    priority: 'NORMAL' as string,
    type: 'GENERAL_ANNOUNCEMENT' as string,
  })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!form.title.trim() || !form.message.trim()) return
    setIsSubmitting(true)
    try {
      await api.post('/notifications/broadcast', {
        title: form.title.trim(),
        message: form.message.trim(),
        priority: form.priority,
        type: form.type,
      })
      toast.success(t('common:toast.broadcastSent'))
      setForm({ title: '', message: '', priority: 'NORMAL', type: 'GENERAL_ANNOUNCEMENT' })
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Failed to send broadcast.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Broadcast notification</CardTitle>
        <CardDescription>Send a notification to all users.</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <label className="space-y-1.5">
              <span className="text-xs text-foreground">Title</span>
              <Input
                value={form.title}
                onChange={(e) => setForm((p) => ({ ...p, title: e.target.value }))}
                placeholder="Notification title"
              />
            </label>
            <label className="space-y-1.5">
              <span className="text-xs text-foreground">Priority</span>
              <select
                value={form.priority}
                onChange={(e) => setForm((p) => ({ ...p, priority: e.target.value }))}
                className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
              >
                <option value="LOW">Low</option>
                <option value="NORMAL">Normal</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent</option>
              </select>
            </label>
          </div>
          <label className="space-y-1.5">
            <span className="text-xs text-foreground">Message</span>
            <textarea
              className="min-h-[100px] w-full rounded-xl border border-border bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
              placeholder="Notification message..."
              value={form.message}
              onChange={(e) => setForm((p) => ({ ...p, message: e.target.value }))}
            />
          </label>
          <div className="flex justify-end">
            <button
              type="submit"
              disabled={isSubmitting || !form.title.trim() || !form.message.trim()}
              className="inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:opacity-60"
            >
              {isSubmitting && <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />}
              <Send className="h-4 w-4" />
              Send broadcast
            </button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

const CreateMembershipModal = ({
  clientId: initialClientId,
  onClose,
  onCreated,
}: {
  clientId?: string
  onClose: () => void
  onCreated: () => Promise<void>
}) => {
  const [form, setForm] = useState({
    clientId: initialClientId ?? '',
    type: 'MONTHLY' as string,
    durationMonths: '1',
    visitsLimit: '',
  })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!form.clientId.trim()) return
    setIsSubmitting(true)
    try {
      await createMembership({
        clientId: form.clientId.trim(),
        type: form.type as 'MONTHLY' | 'YEARLY' | 'VISITS',
        durationMonths: form.type !== 'VISITS' ? Number(form.durationMonths) : undefined,
        visitsLimit: form.type === 'VISITS' && form.visitsLimit ? Number(form.visitsLimit) : undefined,
      })
      await onCreated()
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Failed to create membership.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-30 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        className="max-h-[calc(100vh-3rem)] w-full max-w-lg overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
      >
        <div className="mb-5 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">New membership</p>
            <h2 className="mt-1 text-xl font-bold text-foreground">Create membership</h2>
          </div>
          <button type="button" onClick={onClose} disabled={isSubmitting} className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-background text-muted-foreground transition hover:bg-accent">
            <X className="h-4 w-4" />
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          {initialClientId && (
            <div className="rounded-xl bg-muted px-3 py-2 text-xs text-muted-foreground">
              Client ID: <code className="font-mono">{initialClientId.slice(0, 12)}...</code>
            </div>
          )}
          <label className="space-y-1.5">
            <span className="text-xs text-foreground">Plan type</span>
            <select
              value={form.type}
              onChange={(e) => setForm((p) => ({ ...p, type: e.target.value }))}
              className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
            >
              <option value="MONTHLY">Monthly</option>
              <option value="YEARLY">Yearly</option>
              <option value="VISITS">Visits</option>
            </select>
          </label>
          {form.type !== 'VISITS' && (
            <label className="space-y-1.5">
              <span className="text-xs text-foreground">Duration (months)</span>
              <Input type="number" min={1} value={form.durationMonths} onChange={(e) => setForm((p) => ({ ...p, durationMonths: e.target.value }))} />
            </label>
          )}
          {form.type === 'VISITS' && (
            <label className="space-y-1.5">
              <span className="text-xs text-foreground">Visits limit</span>
              <Input type="number" min={1} value={form.visitsLimit} onChange={(e) => setForm((p) => ({ ...p, visitsLimit: e.target.value }))} placeholder="e.g. 10" />
            </label>
          )}
          <div className="flex justify-end gap-2">
            <button type="button" onClick={onClose} disabled={isSubmitting} className="inline-flex h-9 items-center justify-center rounded-xl px-4 text-sm font-medium text-foreground transition hover:bg-accent disabled:opacity-60">
              Cancel
            </button>
            <button type="submit" disabled={isSubmitting || !form.clientId.trim()} className="inline-flex h-9 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:opacity-60">
              {isSubmitting && <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />}
              Create
            </button>
          </div>
        </form>
      </motion.div>
    </div>
  )
}

const ActionBtn = ({
  icon: Icon,
  label,
  onClick,
  danger = false,
}: {
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>
  label: string
  onClick: () => void
  danger?: boolean
}) => (
  <button
    type="button"
    onClick={onClick}
    className={`inline-flex h-8 items-center justify-center gap-1.5 rounded-lg px-3 text-xs font-semibold transition ${
      danger
        ? 'border border-destructive/40 text-destructive hover:bg-destructive/5'
        : 'border border-border text-foreground hover:bg-accent'
    }`}
  >
    <Icon className="h-3.5 w-3.5" />
    {label}
  </button>
)

const MetricCard = ({
  icon: Icon,
  label,
  value,
  tone,
}: {
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>
  label: string
  value: string
  tone: string
}) => (
  <motion.div
    initial={{ opacity: 0, y: 10 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.25 }}
  >
    <Card className="h-full">
      <CardContent className="flex h-full flex-col justify-between p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <p className="text-xs font-medium text-muted-foreground">{label}</p>
            <p className="mt-2 text-2xl font-bold text-foreground">{value}</p>
          </div>
          <div className={`flex h-10 w-10 items-center justify-center rounded-xl ${tone}`}>
            <Icon className="h-5 w-5 text-white" />
          </div>
        </div>
      </CardContent>
    </Card>
  </motion.div>
)

const statusColors: Record<string, string> = {
  ACTIVE: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
  EXPIRED: 'bg-muted text-muted-foreground',
  FROZEN: 'bg-blue-500/10 text-blue-600 dark:text-blue-400',
  CANCELLED: 'bg-red-500/10 text-red-600 dark:text-red-400',
  CREATED: 'bg-amber-500/10 text-amber-600 dark:text-amber-400',
}



export default Admin
