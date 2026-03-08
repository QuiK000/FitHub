import {NavLink, Outlet, useNavigate} from 'react-router-dom'
import {Activity, BarChart3, Dumbbell, LogOut, Settings, User2, Users2,} from 'lucide-react'
import {useAuthStore} from "../store/useAuthStore.ts";

const navItems = [
    {to: '/', label: 'Dashboard', icon: BarChart3},
    {to: '/workouts', label: 'Workouts', icon: Dumbbell},
    {to: '/memberships', label: 'Memberships', icon: Users2},
    {to: '/profile', label: 'Profile', icon: User2},
    {to: '/analytics', label: 'Analytics', icon: Activity},
]

const MainLayout = () => {
    const navigate = useNavigate()
    const logout = useAuthStore((state) => state.logout)

    const handleLogout = () => {
        logout()
        navigate('/login', {replace: true})
    }

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 text-slate-50">
            {/* Sidebar */}
            <aside
                className="hidden w-64 flex-col border-r border-slate-800/80 bg-slate-950/70 px-4 py-6 backdrop-blur-xl md:flex">
                <div className="mb-8 flex items-center gap-3 px-2">
                    <div
                        className="flex h-9 w-9 items-center justify-center rounded-2xl bg-gradient-to-tr from-emerald-400 via-cyan-400 to-sky-500 shadow-soft-glow">
                        <Dumbbell className="h-5 w-5 text-slate-950"/>
                    </div>
                    <div>
                        <p className="text-xs uppercase tracking-[0.25em] text-slate-500">
                            FitHub
                        </p>
                        <p className="text-sm font-medium text-slate-100">
                            Studio Command Center
                        </p>
                    </div>
                </div>

                <nav className="space-y-1 text-sm font-medium text-slate-400">
                    {navItems.map((item) => {
                        const Icon = item.icon
                        return (
                            <NavLink
                                key={item.to}
                                to={item.to}
                                className={({isActive}) =>
                                    [
                                        'group flex items-center gap-3 rounded-xl px-3 py-2 transition-colors',
                                        isActive
                                            ? 'bg-emerald-500/10 text-emerald-300'
                                            : 'hover:bg-slate-800/70 hover:text-slate-100',
                                    ].join(' ')
                                }
                            >
                                <Icon className="h-4 w-4 text-slate-500 group-hover:text-emerald-300"/>
                                <span>{item.label}</span>
                            </NavLink>
                        )
                    })}
                </nav>

                <div className="mt-auto space-y-4 border-t border-slate-800/80 pt-4">
                    <div className="flex items-center justify-between rounded-xl bg-slate-900/80 px-3 py-3">
                        <div>
                            <p className="text-xs font-medium text-slate-400">Training Load</p>
                            <p className="text-sm font-semibold text-slate-100">Weekly: 78%</p>
                        </div>
                        <div className="h-10 w-10 rounded-full bg-gradient-to-tr from-emerald-400 to-cyan-400 p-[2px]">
                            <div className="flex h-full w-full items-center justify-center rounded-full bg-slate-950">
                                <Activity className="h-4 w-4 text-emerald-300"/>
                            </div>
                        </div>
                    </div>

                    <button
                        type="button"
                        onClick={handleLogout}
                        className="flex w-full items-center justify-between rounded-xl border border-slate-800/80 bg-slate-900/80 px-3 py-2 text-xs font-medium text-slate-400 transition hover:border-red-500/60 hover:bg-red-500/5 hover:text-red-300"
                    >
            <span className="flex items-center gap-2">
              <LogOut className="h-3.5 w-3.5"/>
              Sign out
            </span>
                        <span className="rounded-full bg-slate-800/80 px-2 py-0.5 text-[10px] uppercase tracking-wide">
              Esc
            </span>
                    </button>
                </div>
            </aside>

            {/* Main content */}
            <div className="flex min-h-screen flex-1 flex-col">
                {/* Top navbar */}
                <header className="sticky top-0 z-20 border-b border-slate-800/80 bg-slate-950/70 backdrop-blur-xl">
                    <div className="flex items-center justify-between px-4 py-3 md:px-8">
                        <div className="flex items-center gap-3">
                            <div
                                className="flex h-9 w-9 items-center justify-center rounded-2xl bg-slate-900/90 md:hidden">
                                <Dumbbell className="h-4 w-4 text-emerald-300"/>
                            </div>
                            <div>
                                <p className="text-[11px] uppercase tracking-[0.24em] text-slate-500">
                                    Today&apos;s Overview
                                </p>
                                <p className="text-sm font-semibold text-slate-100">
                                    Central Performance Hub
                                </p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3">
                            <div
                                className="hidden items-center gap-2 rounded-full border border-slate-800/80 bg-slate-900/80 px-3 py-1.5 text-xs text-slate-400 md:flex">
                                <span
                                    className="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_0_6px_rgba(16,185,129,0.35)]"/>
                                Live members
                                <span className="rounded-full bg-slate-800 px-2 py-0.5 text-[10px] text-slate-200">
                  128 online
                </span>
                            </div>

                            <button
                                className="flex h-9 w-9 items-center justify-center rounded-full border border-slate-800/80 bg-slate-900/90 text-slate-400 transition hover:border-slate-700 hover:text-slate-100">
                                <Settings className="h-4 w-4"/>
                            </button>

                            <div
                                className="flex items-center gap-2 rounded-full border border-slate-800/80 bg-slate-900/90 px-2 py-1">
                                <div
                                    className="h-7 w-7 rounded-full bg-gradient-to-tr from-slate-700 via-slate-500 to-slate-300"/>
                                <div className="hidden text-xs leading-tight text-slate-300 md:block">
                                    <p className="font-medium">Alex Trainer</p>
                                    <p className="text-[11px] text-slate-500">Head Coach</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>

                {/* Routed content */}
                <main className="flex-1 px-4 py-6 md:px-8 md:py-8">
                    <Outlet/>
                </main>
            </div>
        </div>
    )
}

export default MainLayout

