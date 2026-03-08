import { useEffect, useMemo, useState } from 'react'
import { motion } from 'framer-motion'
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
} from 'recharts'
import { Activity, Flame, HeartPulse, Users2 } from 'lucide-react'
import {
  type AttendanceStatsResponse,
  type DashboardAnalyticsResponse,
  getAttendanceStats,
  getDashboardAnalytics,
} from '../services/dashboard.service'
import { useAuthStore } from '../store/useAuthStore'

const Dashboard = () => {
  const user = useAuthStore((state) => state.user)
  const [analytics, setAnalytics] = useState<DashboardAnalyticsResponse | null>(
    null,
  )
  const [attendance, setAttendance] = useState<AttendanceStatsResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      setIsLoading(true)
      setError(null)
      try {
        const now = new Date()
        const from = new Date()
        from.setDate(now.getDate() - 6)

        const format = (date: Date) => date.toISOString().slice(0, 10)
        const [dashboardData, attendanceData] = await Promise.all([
          getDashboardAnalytics(),
          getAttendanceStats(format(from), format(now)),
        ])

        setAnalytics(dashboardData)
        setAttendance(attendanceData)
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err)
        setError('Unable to load dashboard analytics.')
      } finally {
        setIsLoading(false)
      }
    }

    void load()
  }, [])

  const chartData = useMemo(
    () =>
      attendance.map((entry) => {
        const date = new Date(entry.date)
        const label = date.toLocaleDateString(undefined, {
          weekday: 'short',
        })
        return {
          day: label,
          sessions: entry.checkIns,
        }
      }),
    [attendance],
  )

  const greetingName =
    user?.clientProfile?.firstname ??
    user?.clientProfile?.lastname ??
    user?.email ??
    'Operator'

  return (
    <div className="relative space-y-6">
      {/* Header row */}
      <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
        <div>
          <p className="text-xs uppercase tracking-[0.24em] text-emerald-300">
            Dashboard
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-slate-50 md:text-3xl">
            Performance overview, {greetingName}
          </h1>
          <p className="mt-1 text-sm text-slate-400">
            Monitor members, workloads, and energy in a single command surface.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <button className="inline-flex items-center gap-2 rounded-full border border-slate-800/80 bg-slate-900/80 px-4 py-2 text-xs font-medium text-slate-200 shadow-sm shadow-slate-950/40 transition hover:border-emerald-500/80 hover:bg-emerald-500/10">
            <span className="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_0_6px_rgba(16,185,129,0.35)]" />
            Today&apos;s check-ins:{' '}
            <span className="font-semibold">
              {analytics ? analytics.todayCheckIns : '—'}
            </span>
          </button>
          <button className="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-emerald-400 via-cyan-400 to-sky-500 px-4 py-2 text-xs font-semibold text-slate-950 shadow-soft-glow">
            Start new session
          </button>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {isLoading ? (
          Array.from({ length: 4 }).map((_, index) => (
            // eslint-disable-next-line react/no-array-index-key
            <div
              key={index}
              className="h-28 rounded-2xl border border-slate-800/80 bg-slate-900/60 shadow-sm shadow-slate-950/40"
            >
              <div className="h-full w-full animate-pulse rounded-2xl bg-gradient-to-br from-slate-900/80 via-slate-800/60 to-slate-900/80" />
            </div>
          ))
        ) : (
          <>
            <StatCard
              icon={Users2}
              label="Active clients"
              value={analytics?.activeClients?.toString() ?? '0'}
              delta="Currently engaged memberships"
              accent="emerald"
            />
            <StatCard
              icon={Activity}
              label="Active memberships"
              value={analytics?.activeMemberships?.toString() ?? '0'}
              delta="Running contracts in this period"
              accent="cyan"
            />
            <StatCard
              icon={Flame}
              label="Today’s check-ins"
              value={analytics?.todayCheckIns?.toString() ?? '0'}
              delta="Member arrivals logged at reception"
              accent="orange"
            />
            <StatCard
              icon={HeartPulse}
              label="Monthly revenue"
              value={
                analytics
                  ? new Intl.NumberFormat(undefined, {
                      style: 'currency',
                      currency: 'USD',
                      maximumFractionDigits: 0,
                    }).format(analytics.revenue)
                  : '$0'
              }
              delta="From all active memberships"
              accent="rose"
            />
          </>
        )}
      </div>

      {/* Chart + secondary cards */}
      <div className="grid gap-4 lg:grid-cols-3">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="lg:col-span-2 rounded-2xl border border-slate-800/80 bg-slate-900/70 p-4 shadow-sm shadow-slate-950/40"
        >
          <div className="flex items-center justify-between gap-4 pb-4">
            <div>
              <p className="text-xs font-medium uppercase tracking-[0.24em] text-slate-500">
                Weekly load
              </p>
              <p className="mt-1 text-sm text-slate-300">
                Session density and flow across the week.
              </p>
            </div>
            <div className="flex items-center gap-2 rounded-full bg-slate-800/80 px-3 py-1 text-[11px] text-slate-300">
              <span className="h-2 w-2 rounded-full bg-cyan-400" />
              Sessions / day
            </div>
          </div>

          <div className="w-full">
            {isLoading ? (
              <div className="h-full w-full animate-pulse rounded-2xl bg-gradient-to-br from-slate-900/80 via-slate-800/60 to-slate-900/80" />
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <AreaChart data={chartData} margin={{ left: -20, right: 0 }}>
                  <defs>
                    <linearGradient id="colorSessions" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#22c55e" stopOpacity={0.7} />
                      <stop offset="95%" stopColor="#22c55e" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid
                    strokeDasharray="3 3"
                    stroke="#1e293b"
                    vertical={false}
                  />
                  <XAxis
                    dataKey="day"
                    tickLine={false}
                    axisLine={false}
                    tick={{ fill: '#64748b', fontSize: 12 }}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#020617',
                      borderRadius: 12,
                      border: '1px solid #1e293b',
                      padding: '8px 10px',
                    }}
                    labelStyle={{ color: '#e2e8f0', fontSize: 12 }}
                    itemStyle={{ color: '#a5b4fc', fontSize: 12 }}
                  />
                  <Area
                    type="monotone"
                    dataKey="sessions"
                    stroke="#22c55e"
                    strokeWidth={2}
                    fill="url(#colorSessions)"
                  />
                </AreaChart>
              </ResponsiveContainer>
            )}
          </div>
        </motion.div>

        <div className="space-y-4">
          <motion.div
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="rounded-2xl border border-slate-800/80 bg-slate-900/70 p-4 text-sm text-slate-300 shadow-sm shadow-slate-950/40"
          >
            <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
              Capacity
            </p>
            <div className="mt-3 flex items-end justify-between">
              <div>
                <p className="text-2xl font-semibold text-slate-50">86%</p>
                <p className="mt-1 text-xs text-slate-400">
                  Peak expected in <span className="text-emerald-300">42 min</span>
                </p>
              </div>
              <div className="inline-flex items-center gap-1 rounded-full bg-emerald-500/15 px-3 py-1 text-[11px] text-emerald-300">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
                Stable load
              </div>
            </div>

            <div className="mt-4 space-y-2 text-xs text-slate-400">
              <div className="flex items-center justify-between">
                <span>Top session</span>
                <span className="text-slate-200">
                  {analytics?.popularSessions?.[0]?.trainerName ??
                    'Data not available'}
                </span>
              </div>
              <div className="h-1.5 overflow-hidden rounded-full bg-slate-800">
                <div className="h-full w-full rounded-full bg-gradient-to-r from-emerald-400 via-lime-400 to-yellow-300" />
              </div>
              <div className="flex items-center justify-between pt-1">
                <span>Attendance</span>
                <span className="text-slate-200">
                  {analytics?.popularSessions?.[0]?.attendanceCount ?? 0} check-ins
                </span>
              </div>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.18 }}
            className="rounded-2xl border border-slate-800/80 bg-slate-900/70 p-4 text-xs text-slate-300 shadow-sm shadow-slate-950/40"
          >
            <p className="text-[11px] uppercase tracking-[0.24em] text-slate-500">
              Highlights
            </p>
            <ul className="mt-3 space-y-2.5">
              <li className="flex items-center justify-between">
                <span>Active clients</span>
                <span className="rounded-full bg-slate-800 px-2 py-0.5 text-[11px] text-slate-100">
                  {analytics?.activeClients ?? 0}
                </span>
              </li>
              <li className="flex items-center justify-between">
                <span>Today&apos;s check-ins</span>
                <span className="text-emerald-300">
                  {analytics?.todayCheckIns ?? 0}
                </span>
              </li>
              <li className="flex items-center justify-between">
                <span>Popular sessions tracked</span>
                <span className="text-sky-300">
                  {analytics?.popularSessions?.length ?? 0}
                </span>
              </li>
            </ul>
          </motion.div>
        </div>
      </div>
      {error && !isLoading && (
        <div className="rounded-2xl border border-red-500/40 bg-red-500/10 px-4 py-3 text-sm text-red-100">
          {error}
        </div>
      )}
    </div>
  )
}

type StatCardProps = {
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>
  label: string
  value: string
  delta: string
  accent: 'emerald' | 'cyan' | 'orange' | 'rose'
}

const accentRing: Record<StatCardProps['accent'], string> = {
  emerald: 'from-emerald-400 via-emerald-300 to-lime-300',
  cyan: 'from-sky-400 via-cyan-400 to-emerald-300',
  orange: 'from-orange-400 via-amber-300 to-yellow-300',
  rose: 'from-rose-400 via-pink-400 to-fuchsia-400',
}

const StatCard = ({ icon: Icon, label, value, delta, accent }: StatCardProps) => (
  <motion.div
    initial={{ opacity: 0, y: 10 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.3 }}
    className="flex items-start justify-between rounded-2xl border border-slate-800/80 bg-slate-900/70 p-4 shadow-sm shadow-slate-950/40"
  >
    <div>
      <p className="text-xs text-slate-400">{label}</p>
      <p className="mt-2 text-xl font-semibold text-slate-50">{value}</p>
      <p className="mt-1 text-xs text-slate-500">{delta}</p>
    </div>
    <div
      className={`ml-3 flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-tr ${accentRing[accent]} p-[1px]`}
    >
      <div className="flex h-full w-full items-center justify-center rounded-2xl bg-slate-950">
        <Icon className="h-4 w-4 text-slate-50" />
      </div>
    </div>
  </motion.div>
)

export default Dashboard

