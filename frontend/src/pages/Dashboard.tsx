import React, {useEffect, useMemo, useState} from "react"
import {motion} from "framer-motion"
import {Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis,} from "recharts"
import {Activity, Flame, HeartPulse, Users2} from "lucide-react"

import {
    type AttendanceStatsResponse,
    type DashboardAnalyticsResponse,
    getAttendanceStats,
    getDashboardAnalytics,
} from "../services/dashboard.service"

import {useAuthStore} from "../store/useAuthStore"

const Dashboard = () => {
    const user = useAuthStore((state) => state.user)

    const [analytics, setAnalytics] = useState<DashboardAnalyticsResponse | null>(null)

    const [attendance, setAttendance] = useState<
        AttendanceStatsResponse[]
    >([])

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

                const format = (date: Date) =>
                    date.toISOString().slice(0, 10)

                const [dashboardData, attendanceData] =
                    await Promise.all([
                        getDashboardAnalytics(),
                        getAttendanceStats(format(from), format(now)),
                    ])

                setAnalytics(dashboardData)
                setAttendance(attendanceData)
            } catch (err) {
                console.error(err)
                setError("Unable to load dashboard analytics.")
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
                    weekday: "short",
                })

                return {
                    day: label,
                    sessions: entry.checkIns,
                }
            }),
        [attendance]
    )

    const greetingName =
        user?.clientProfile?.firstname ??
        user?.clientProfile?.lastname ??
        user?.email ??
        "User"

    return (
        <div className="relative space-y-6">
            {/* Header */}
            <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
                <div>
                    <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                        Dashboard
                    </p>

                    <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
                        Welcome back, {greetingName}
                    </h1>

                    <p className="mt-1 text-sm text-muted-foreground">
                        Track your progress and stay on top of your fitness goals.
                    </p>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    <button
                        className="inline-flex items-center gap-2 rounded-xl border border-border bg-card px-4 py-2 text-sm font-medium text-foreground shadow-soft transition-all hover:bg-accent">
                        <span className="h-2 w-2 rounded-full bg-success"/>

                        Today's check-ins:
                        <span className="font-semibold">
              {analytics
                  ? analytics.todayCheckIns
                  : "—"}
            </span>
                    </button>

                    <button
                        className="inline-flex items-center gap-2 rounded-xl bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90">
                        Start new session
                    </button>
                </div>
            </div>

            {/* Stats */}
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
                {isLoading ? (
                    Array.from({length: 4}).map((_, index) => (
                        <div
                            key={index}
                            className="h-28 rounded-2xl border border-border bg-card shadow-soft"
                        >
                            <div className="h-full w-full animate-pulse rounded-2xl bg-muted"/>
                        </div>
                    ))
                ) : (
                    <>
                        <StatCard
                            icon={Users2}
                            label="Active clients"
                            value={
                                analytics?.activeClients?.toString() ??
                                "0"
                            }
                            delta="Currently engaged memberships"
                            accent="blue"
                        />

                        <StatCard
                            icon={Activity}
                            label="Active memberships"
                            value={
                                analytics?.activeMemberships?.toString() ??
                                "0"
                            }
                            delta="Running contracts in this period"
                            accent="cyan"
                        />

                        <StatCard
                            icon={Flame}
                            label="Today's check-ins"
                            value={
                                analytics?.todayCheckIns?.toString() ??
                                "0"
                            }
                            delta="Member arrivals logged"
                            accent="orange"
                        />

                        <StatCard
                            icon={HeartPulse}
                            label="Monthly revenue"
                            value={
                                analytics
                                    ? new Intl.NumberFormat(undefined, {
                                        style: "currency",
                                        currency: "USD",
                                        maximumFractionDigits: 0,
                                    }).format(analytics.revenue)
                                    : "$0"
                            }
                            delta="From all active memberships"
                            accent="rose"
                        />
                    </>
                )}
            </div>

            {/* Chart */}
            <div className="grid gap-4 lg:grid-cols-3">
                <motion.div
                    initial={{opacity: 0, y: 12}}
                    animate={{opacity: 1, y: 0}}
                    transition={{duration: 0.4}}
                    className="rounded-2xl border border-border bg-card p-4 shadow-soft lg:col-span-2"
                >
                    <div className="flex items-center justify-between gap-4 pb-4">
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                Weekly Activity
                            </p>

                            <p className="mt-1 text-sm text-muted-foreground">
                                Session density and flow across the week.
                            </p>
                        </div>

                        <div
                            className="flex items-center gap-2 rounded-full bg-muted px-3 py-1 text-xs text-muted-foreground">
                            <span className="h-2 w-2 rounded-full bg-primary"/>
                            Sessions / day
                        </div>
                    </div>

                    <div className="w-full">
                        {isLoading ? (
                            <div className="h-64 w-full animate-pulse rounded-xl bg-muted"/>
                        ) : (
                            <ResponsiveContainer
                                width="100%"
                                height={260}
                            >
                                <AreaChart
                                    data={chartData}
                                    margin={{left: -20, right: 0}}
                                >
                                    <defs>
                                        <linearGradient
                                            id="colorSessions"
                                            x1="0"
                                            y1="0"
                                            x2="0"
                                            y2="1"
                                        >
                                            <stop
                                                offset="5%"
                                                stopColor="hsl(var(--primary))"
                                                stopOpacity={0.3}
                                            />

                                            <stop
                                                offset="95%"
                                                stopColor="hsl(var(--primary))"
                                                stopOpacity={0}
                                            />
                                        </linearGradient>
                                    </defs>

                                    <CartesianGrid
                                        strokeDasharray="3 3"
                                        stroke="hsl(var(--border))"
                                        vertical={false}
                                    />

                                    <XAxis
                                        dataKey="day"
                                        tickLine={false}
                                        axisLine={false}
                                        tick={{
                                            fill:
                                                "hsl(var(--muted-foreground))",
                                            fontSize: 12,
                                        }}
                                    />

                                    <Tooltip
                                        contentStyle={{
                                            backgroundColor:
                                                "hsl(var(--card))",
                                            borderRadius: 12,
                                            border:
                                                "1px solid hsl(var(--border))",
                                            padding: "8px 10px",
                                        }}
                                        labelStyle={{
                                            color:
                                                "hsl(var(--foreground))",
                                            fontSize: 12,
                                        }}
                                        itemStyle={{
                                            color:
                                                "hsl(var(--primary))",
                                            fontSize: 12,
                                        }}
                                    />

                                    <Area
                                        type="monotone"
                                        dataKey="sessions"
                                        stroke="hsl(var(--primary))"
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
                        initial={{opacity: 0, y: 8}}
                        animate={{opacity: 1, y: 0}}
                        transition={{delay: 0.1}}
                        className="rounded-2xl border border-border bg-card p-4 text-sm shadow-soft"
                    >
                        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Capacity
                        </p>

                        <div className="mt-3 flex items-end justify-between">
                            <div>
                                <p className="text-2xl font-bold text-foreground">
                                    86%
                                </p>

                                <p className="mt-1 text-xs text-muted-foreground">
                                    Peak expected in{" "}
                                    <span className="text-primary">
                    42 min
                  </span>
                                </p>
                            </div>

                            <div
                                className="inline-flex items-center gap-1 rounded-full bg-success/10 px-3 py-1 text-xs text-success">
                                <span className="h-1.5 w-1.5 rounded-full bg-success"/>
                                Stable
                            </div>
                        </div>

                        <div className="mt-4 space-y-2 text-xs text-muted-foreground">
                            <div className="flex items-center justify-between">
                                <span>Top session</span>

                                <span className="text-foreground">
                  {analytics?.popularSessions?.[0]
                          ?.trainerName ??
                      "Data not available"}
                </span>
                            </div>

                            <div className="h-1.5 overflow-hidden rounded-full bg-muted">
                                <div className="h-full w-full rounded-full bg-gradient-to-r from-blue-500 to-cyan-500"/>
                            </div>

                            <div className="flex items-center justify-between pt-1">
                                <span>Attendance</span>

                                <span className="text-foreground">
                  {analytics?.popularSessions?.[0]
                      ?.attendanceCount ?? 0}{" "}
                                    check-ins
                </span>
                            </div>
                        </div>
                    </motion.div>

                    <motion.div
                        initial={{opacity: 0, y: 12}}
                        animate={{opacity: 1, y: 0}}
                        transition={{delay: 0.18}}
                        className="rounded-2xl border border-border bg-card p-4 text-xs shadow-soft"
                    >
                        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Highlights
                        </p>

                        <ul className="mt-3 space-y-2.5">
                            <li className="flex items-center justify-between">
                <span className="text-muted-foreground">
                  Active clients
                </span>

                                <span className="rounded-full bg-muted px-2 py-0.5 text-xs font-medium text-foreground">
                  {analytics?.activeClients ?? 0}
                </span>
                            </li>

                            <li className="flex items-center justify-between">
                <span className="text-muted-foreground">
                  Today's check-ins
                </span>

                                <span className="font-medium text-primary">
                  {analytics?.todayCheckIns ?? 0}
                </span>
                            </li>

                            <li className="flex items-center justify-between">
                <span className="text-muted-foreground">
                  Popular sessions tracked
                </span>

                                <span className="font-medium text-primary">
                  {analytics?.popularSessions?.length ??
                      0}
                </span>
                            </li>
                        </ul>
                    </motion.div>
                </div>
            </div>

            {error && !isLoading && (
                <div
                    className="rounded-2xl border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                    {error}
                </div>
            )}
        </div>
    )
}

type StatCardProps = {
    icon: React.ComponentType<
        React.SVGProps<SVGSVGElement>
    >
    label: string
    value: string
    delta: string
    accent: "blue" | "cyan" | "orange" | "rose"
}

const accentColors: Record<
    StatCardProps["accent"],
    string
> = {
    blue: "from-blue-500 to-blue-600",
    cyan: "from-cyan-500 to-cyan-600",
    orange: "from-orange-500 to-orange-600",
    rose: "from-rose-500 to-rose-600",
}

const StatCard = ({
                      icon: Icon,
                      label,
                      value,
                      delta,
                      accent,
                  }: StatCardProps) => (
    <motion.div
        initial={{opacity: 0, y: 10}}
        animate={{opacity: 1, y: 0}}
        transition={{duration: 0.3}}
        className="flex items-start justify-between rounded-2xl border border-border bg-card p-4 shadow-soft transition-shadow hover:shadow-soft-md"
    >
        <div>
            <p className="text-xs text-muted-foreground">
                {label}
            </p>

            <p className="mt-2 text-xl font-bold text-foreground">
                {value}
            </p>

            <p className="mt-1 text-xs text-muted-foreground">
                {delta}
            </p>
        </div>

        <div
            className={`ml-3 flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br ${accentColors[accent]}`}
        >
            <Icon className="h-5 w-5 text-white"/>
        </div>
    </motion.div>
)

export default Dashboard