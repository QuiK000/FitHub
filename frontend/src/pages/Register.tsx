import { FormEvent, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Dumbbell, Mail, Lock } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../components/ui/card'
import { register } from '../services/auth.service'

const Register = () => {
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const validate = () => {
    if (!email || !password || !confirmPassword) {
      setError('Please fill in all required fields.')
      return false
    }

    if (password.length < 8) {
      setError('Password must be at least 8 characters long.')
      return false
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match.')
      return false
    }

    return true
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)

    if (!validate()) return

    setIsSubmitting(true)

    try {
      await register({ email, password, confirmPassword })

      // After successful registration, send them to email verification
      navigate('/verify-email', {
        replace: true,
        state: { email },
      })
    } catch (err) {
      console.error(err)
      setError('Registration failed. Please check your details and try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 px-4 py-8 text-slate-50">
        <motion.div
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.45, ease: 'easeOut' }}
            className="grid w-full max-w-5xl gap-8 rounded-3xl border border-slate-800/80 bg-slate-950/80 p-6 shadow-[0_30px_80px_rgba(15,23,42,0.9)] backdrop-blur-2xl md:grid-cols-[minmax(0,1.1fr),minmax(0,1.2fr)] md:p-10"
        >
          {/* Brand / hero side */}
          <div className="hidden flex-col justify-between md:flex">
            <div>
              <div className="inline-flex items-center gap-3 rounded-full border border-slate-800/80 bg-slate-900/80 px-3 py-1 text-[11px] uppercase tracking-[0.24em] text-slate-400">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 shadow-[0_0_0_5px_rgba(16,185,129,0.25)]" />
                New workspace
              </div>
              <h1 className="mt-6 text-3xl font-semibold leading-tight text-slate-50">
                Create your
                <br />
                <span className="bg-gradient-to-r from-emerald-400 via-cyan-400 to-sky-500 bg-clip-text text-transparent">
                FitHub studio profile.
              </span>
              </h1>
              <p className="mt-3 text-sm text-slate-400">
                One account to manage workouts, nutrition plans, memberships, and
                live performance analytics.
              </p>
            </div>

            <div className="mt-8 space-y-3 text-xs text-slate-400">
              <div className="flex items-center justify-between rounded-2xl border border-slate-800/80 bg-slate-900/80 px-4 py-3">
                <div>
                  <p className="text-[11px] uppercase tracking-[0.24em] text-slate-500">
                    Real-time overview
                  </p>
                  <p className="mt-1 text-sm text-slate-200">
                    Adaptive dashboards for every trainer & manager.
                  </p>
                </div>
                <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-tr from-emerald-400 via-cyan-400 to-sky-500 p-[2px]">
                  <div className="flex h-full w-full items-center justify-center rounded-2xl bg-slate-950">
                    <Dumbbell className="h-4 w-4 text-slate-50" />
                  </div>
                </div>
              </div>
              <p className="text-[11px] text-slate-500">
                Designed for premium fitness studios, high-performance teams, and
                boutique gyms.
              </p>
            </div>
          </div>

          {/* Register card */}
          <Card className="flex flex-col justify-center border-slate-800/80 bg-slate-950/80">
            <CardHeader className="space-y-4">
              <div className="flex items-center gap-3 md:hidden">
                <div className="flex h-9 w-9 items-center justify-center rounded-2xl bg-gradient-to-tr from-emerald-400 via-cyan-400 to-sky-500">
                  <Dumbbell className="h-4 w-4 text-slate-950" />
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

              <div>
                <p className="text-xs uppercase tracking-[0.24em] text-slate-500">
                  Create account
                </p>
                <CardTitle className="mt-2">
                  Set up your operator profile
                </CardTitle>
                <CardDescription>
                  Use a valid studio email. You&apos;ll use this to sign in to the
                  dashboard.
                </CardDescription>
              </div>
            </CardHeader>

            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-1.5 text-sm">
                  <Label htmlFor="email">Work email</Label>
                  <div className="flex items-center gap-2">
                    <Mail className="h-4 w-4 text-slate-500" />
                    <Input
                        id="email"
                        type="email"
                        autoComplete="email"
                        required
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        placeholder="studio.owner@fithub.studio"
                    />
                  </div>
                </div>

                <div className="grid gap-3 md:grid-cols-2">
                  <div className="space-y-1.5 text-sm">
                    <Label htmlFor="password">Password</Label>
                    <div className="flex items-center gap-2">
                      <Lock className="h-4 w-4 text-slate-500" />
                      <Input
                          id="password"
                          type="password"
                          autoComplete="new-password"
                          required
                          value={password}
                          onChange={(event) => setPassword(event.target.value)}
                          placeholder="Create a strong password"
                      />
                    </div>
                  </div>

                  <div className="space-y-1.5 text-sm">
                    <Label htmlFor="confirmPassword">Confirm password</Label>
                    <div className="flex items-center gap-2">
                      <Lock className="h-4 w-4 text-slate-500" />
                      <Input
                          id="confirmPassword"
                          type="password"
                          autoComplete="new-password"
                          required
                          value={confirmPassword}
                          onChange={(event) =>
                              setConfirmPassword(event.target.value)
                          }
                          placeholder="Repeat your password"
                      />
                    </div>
                  </div>
                </div>

                {error && (
                    <div className="rounded-xl border border-red-500/40 bg-red-500/10 px-3 py-2 text-xs text-red-200">
                      {error}
                    </div>
                )}

                <Button
                    type="submit"
                    disabled={isSubmitting}
                    className="mt-1 w-full"
                >
                  {isSubmitting && (
                      <span className="inline-flex h-4 w-4 animate-spin rounded-full border-[2px] border-slate-900 border-t-emerald-400" />
                  )}
                  <span>
                  {isSubmitting ? 'Creating your workspace…' : 'Create account'}
                </span>
                </Button>
              </form>

              <p className="mt-4 text-[11px] text-slate-500">
                Already have an account?{' '}
                <Link
                    to="/login"
                    className="font-medium text-slate-200 underline-offset-2 hover:underline"
                >
                  Sign in
                </Link>
                .
              </p>
            </CardContent>
          </Card>
        </motion.div>
      </div>
  )
}

export default Register