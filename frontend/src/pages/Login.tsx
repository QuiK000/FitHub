import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Dumbbell, Lock, Mail } from 'lucide-react'
import axios from 'axios'
import { login } from '../services/auth.service'
import { getMyClientProfile } from '../services/profile.service'
import { getCurrentUser } from '../services/user.service'
import { normalizeRoles, useAuthStore } from '../store/useAuthStore'
import ThemeToggle from '../components/ThemeToggle'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

const Login = () => {
  const { t } = useTranslation('auth')
  const navigate = useNavigate()
  const { setAuth, token, isAuthenticated } = useAuthStore()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (isAuthenticated || token) {
      navigate('/dashboard', { replace: true })
    }
  }, [isAuthenticated, navigate, token])

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)
    setIsSubmitting(true)

    try {
      const auth = await login({ email, password })

      try {
        localStorage.setItem('access_token', auth.accessToken)
        if (auth.refreshToken) {
          localStorage.setItem('refresh_token', auth.refreshToken)
        }
      } catch {
        // ignore persistence errors
      }

      const user = await getCurrentUser()
      setAuth(auth.accessToken, auth.refreshToken, user)

      const roles = normalizeRoles(user.roles)

      if (!roles.includes('CLIENT')) {
        navigate('/dashboard', { replace: true })
        return
      }

      try {
        await getMyClientProfile()
        navigate('/dashboard', { replace: true })
      } catch (profileErr) {
        if (
          axios.isAxiosError(profileErr) &&
          (profileErr.response?.status === 404 ||
            profileErr.response?.status === 500)
        ) {
          navigate('/onboarding', { replace: true })
        } else {
          console.error('Unexpected profile lookup error', profileErr)
          setError(t('login.errors.profileError'))
        }
      }
    } catch (err) {
      console.error(err)
      const message = getApiErrorMessage(
        err,
        t('login.errors.invalidCredentials'),
      )
      setError(message)
      toast.error(message)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 py-8">
      <div className="absolute right-4 top-4">
        <ThemeToggle />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md"
      >
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary">
            <Dumbbell className="h-6 w-6 text-primary-foreground" />
          </div>
          <h1 className="text-2xl font-bold text-foreground">
            {t('login.title')}
          </h1>
          <p className="mt-2 text-sm text-muted-foreground">
            {t('login.subtitle')}
          </p>
        </div>

        <div className="rounded-2xl border border-border bg-card p-6 shadow-soft-lg">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label htmlFor="email" className="text-sm font-medium text-foreground">
                {t('login.emailLabel')}
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <input
                  id="email"
                  type="email"
                  autoComplete="email"
                  required
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  className="flex h-10 w-full rounded-xl border border-border bg-background pl-10 pr-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                  placeholder={t('login.emailPlaceholder')}
                />
              </div>
            </div>

            <div className="space-y-2">
              <label htmlFor="password" className="text-sm font-medium text-foreground">
                {t('login.passwordLabel')}
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <input
                  id="password"
                  type="password"
                  autoComplete="current-password"
                  required
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  className="flex h-10 w-full rounded-xl border border-border bg-background pl-10 pr-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                  placeholder={t('login.passwordPlaceholder')}
                />
              </div>
            </div>

            {error && (
              <div className="rounded-xl border border-destructive/40 bg-destructive/10 px-3 py-2 text-sm text-destructive">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={isSubmitting}
              className="inline-flex h-10 w-full items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isSubmitting && (
                <span className="inline-flex h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
              )}
              <span>{isSubmitting ? t('login.submittingButton') : t('login.submitButton')}</span>
            </button>
          </form>

          <div className="mt-6 text-center text-sm text-muted-foreground">
            <Link
              to="/forgot-password"
              className="mb-3 block font-medium text-primary hover:underline"
            >
              {t('login.forgotPassword')}
            </Link>
            {t('login.noAccount')}{' '}
            <Link
              to="/register"
              className="font-medium text-primary hover:underline"
            >
              {t('login.createAccount')}
            </Link>
          </div>
        </div>

        <p className="mt-6 text-center text-xs text-muted-foreground">
          {t('login.termsPrefix')}{' '}
          <span className="cursor-pointer font-medium text-foreground hover:underline">
            {t('login.termsLink')}
          </span>{' '}
          {t('login.and')}{' '}
          <span className="cursor-pointer font-medium text-foreground hover:underline">
            {t('login.privacyLink')}
          </span>
          .
        </p>
      </motion.div>
    </div>
  )
}

export default Login
