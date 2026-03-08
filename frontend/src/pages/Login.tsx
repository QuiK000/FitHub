import type {FormEvent} from 'react'
import {useEffect, useState} from 'react'
import {Link, useNavigate} from 'react-router-dom'
import {motion} from 'framer-motion'
import {Dumbbell, Lock, Mail} from 'lucide-react'
import axios from 'axios'
import {login} from '../services/auth.service'
import {getMyClientProfile} from '../services/profile.service'
import {useAuthStore} from '../store/useAuthStore'

const Login = () => {
    const navigate = useNavigate()
    const {setAuth, token, isAuthenticated} = useAuthStore()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (isAuthenticated || token) {
            navigate('/', {replace: true})
        }
    }, [isAuthenticated, navigate, token])

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault()
        setError(null)
        setIsSubmitting(true)

        try {
            const auth = await login({email, password})
            setAuth(auth.accessToken, null)

            // After login, determine where to send the user
            // by checking if the client profile exists.
            try {
                await getMyClientProfile()
                // Profile exists -> go to dashboard
                navigate('/', {replace: true})
            } catch (profileErr) {
                if (
                    axios.isAxiosError(profileErr) &&
                    (profileErr.response?.status === 404 ||
                        profileErr.response?.status === 500)
                ) {
                    // Profile missing -> onboarding flow
                    navigate('/onboarding', {replace: true})
                } else {
                    // Other errors: keep user on login and show a generic error
                    console.error('Unexpected profile lookup error', profileErr)
                    setError('Unable to determine profile status. Please try again.')
                }
            }
        } catch (err) {
            console.error(err)
            setError('Invalid credentials or server unavailable.')
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div
            className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 px-4 py-8 text-slate-50">
            <motion.div
                initial={{opacity: 0, y: 18}}
                animate={{opacity: 1, y: 0}}
                transition={{duration: 0.45, ease: 'easeOut'}}
                className="grid w-full max-w-5xl gap-8 rounded-3xl border border-slate-800/80 bg-slate-950/80 p-6 shadow-[0_30px_80px_rgba(15,23,42,0.9)] backdrop-blur-2xl md:grid-cols-[minmax(0,1.1fr),minmax(0,1.2fr)] md:p-10"
            >
                {/* Brand / hero side */}
                <div className="hidden flex-col justify-between md:flex">
                    <div>
                        <div
                            className="inline-flex items-center gap-3 rounded-full border border-slate-800/80 bg-slate-900/80 px-3 py-1 text-[11px] uppercase tracking-[0.24em] text-slate-400">
                            <span
                                className="h-1.5 w-1.5 rounded-full bg-emerald-400 shadow-[0_0_0_5px_rgba(16,185,129,0.25)]"/>
                            Studio control
                        </div>
                        <h1 className="mt-6 text-3xl font-semibold leading-tight text-slate-50">
                            Welcome back to your
                            <br/>
                            <span
                                className="bg-gradient-to-r from-emerald-400 via-cyan-400 to-sky-500 bg-clip-text text-transparent">
                training command center.
              </span>
                        </h1>
                        <p className="mt-3 text-sm text-slate-400">
                            Sign in to orchestrate workouts, nutrition plans, and memberships in
                            one focused dashboard.
                        </p>
                    </div>

                    <div className="mt-8 space-y-3 text-xs text-slate-400">
                        <div
                            className="flex items-center justify-between rounded-2xl border border-slate-800/80 bg-slate-900/80 px-4 py-3">
                            <div>
                                <p className="text-[11px] uppercase tracking-[0.24em] text-slate-500">
                                    Today&apos;s capacity
                                </p>
                                <p className="mt-1 text-sm text-slate-200">
                                    214 members scheduled • 7 trainers active
                                </p>
                            </div>
                            <div
                                className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-tr from-emerald-400 via-cyan-400 to-sky-500 p-[2px]">
                                <div
                                    className="flex h-full w-full items-center justify-center rounded-2xl bg-slate-950">
                                    <Dumbbell className="h-4 w-4 text-slate-50"/>
                                </div>
                            </div>
                        </div>
                        <p className="text-[11px] text-slate-500">
                            Secure SSO, role-based access, and live analytics included in every
                            workspace.
                        </p>
                    </div>
                </div>

                {/* Login form */}
                <div
                    className="flex flex-col justify-center rounded-2xl border border-slate-800/80 bg-slate-950/80 p-5 md:p-8">
                    <div className="mb-6 flex items-center gap-3 md:hidden">
                        <div
                            className="flex h-9 w-9 items-center justify-center rounded-2xl bg-gradient-to-tr from-emerald-400 via-cyan-400 to-sky-500">
                            <Dumbbell className="h-4 w-4 text-slate-950"/>
                        </div>
                        <div>
                            <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
                                FitHub
                            </p>
                            <p className="text-sm font-medium text-slate-100">
                                Studio Command Center
                            </p>
                        </div>
                    </div>

                    <div className="mb-6">
                        <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
                            Sign in
                        </p>
                        <h2 className="mt-2 text-xl font-semibold text-slate-50">
                            Enter your operator credentials
                        </h2>
                        <p className="mt-1 text-xs text-slate-500">
                            Use your studio email to authenticate with the FitHub backend.
                        </p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-1.5 text-sm">
                            <label htmlFor="email" className="text-xs text-slate-300">
                                Work email
                            </label>
                            <div
                                className="flex items-center gap-2 rounded-xl border border-slate-800/80 bg-slate-900/80 px-3 py-2.5 text-sm text-slate-100 focus-within:border-emerald-500/80 focus-within:bg-slate-900 focus-within:ring-1 focus-within:ring-emerald-500/70">
                                <Mail className="h-4 w-4 text-slate-500"/>
                                <input
                                    id="email"
                                    type="email"
                                    autoComplete="email"
                                    required
                                    value={email}
                                    onChange={(event) => setEmail(event.target.value)}
                                    className="flex-1 bg-transparent text-sm outline-none placeholder:text-slate-500"
                                    placeholder="coach@fithub.studio"
                                />
                            </div>
                        </div>

                        <div className="space-y-1.5 text-sm">
                            <label htmlFor="password" className="text-xs text-slate-300">
                                Password
                            </label>
                            <div
                                className="flex items-center gap-2 rounded-xl border border-slate-800/80 bg-slate-900/80 px-3 py-2.5 text-sm text-slate-100 focus-within:border-emerald-500/80 focus-within:bg-slate-900 focus-within:ring-1 focus-within:ring-emerald-500/70">
                                <Lock className="h-4 w-4 text-slate-500"/>
                                <input
                                    id="password"
                                    type="password"
                                    autoComplete="current-password"
                                    required
                                    value={password}
                                    onChange={(event) => setPassword(event.target.value)}
                                    className="flex-1 bg-transparent text-sm outline-none placeholder:text-slate-500"
                                    placeholder="••••••••"
                                />
                            </div>
                        </div>

                        {error && (
                            <div
                                className="rounded-xl border border-red-500/40 bg-red-500/10 px-3 py-2 text-xs text-red-200">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="mt-2 inline-flex w-full items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-emerald-400 via-cyan-400 to-sky-500 px-4 py-2.5 text-sm font-semibold text-slate-950 shadow-soft-glow transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-70"
                        >
                            {isSubmitting && (
                                <span
                                    className="inline-flex h-4 w-4 animate-spin rounded-full border-[2px] border-slate-900 border-t-emerald-400"/>
                            )}
                            <span>
                {isSubmitting ? 'Signing you in…' : 'Continue to dashboard'}
              </span>
                        </button>
                    </form>

                    <div className="mt-4 space-y-1 text-[11px] text-slate-500">
                        <p>
                            By continuing you agree to the FitHub{' '}
                            <span className="cursor-pointer text-slate-300 underline-offset-2 hover:underline">
                Terms
              </span>{' '}
                            and{' '}
                            <span className="cursor-pointer text-slate-300 underline-offset-2 hover:underline">
                Privacy Policy
              </span>
                            .
                        </p>
                        <p>
                            Need an account?{' '}
                            <Link
                                to="/register"
                                className="font-medium text-slate-200 underline-offset-2 hover:underline"
                            >
                                Create one
                            </Link>
                            .
                        </p>
                    </div>
                </div>
            </motion.div>
        </div>
    )
}

export default Login