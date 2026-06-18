import { useState } from 'react'
import type { FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Dumbbell, Mail, Lock } from 'lucide-react'
import { register } from '../services/auth.service'
import ThemeToggle from '../components/ThemeToggle'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

const Register = () => {
  const { t } = useTranslation('auth')
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const validate = () => {
    if (!email || !password || !confirmPassword) {
      setError(t('register.errors.allFieldsRequired'))
      return false
    }

    if (password.length < 8) {
      setError(t('register.errors.passwordTooShort'))
      return false
    }

    if (password !== confirmPassword) {
      setError(t('register.errors.passwordMismatch'))
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

      navigate('/verify-email', {
        replace: true,
        state: { email },
      })
    } catch (err) {
      const message = getApiErrorMessage(err, t('register.errors.registrationFailed'))
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
            {t('register.title')}
          </h1>
          <p className="mt-2 text-sm text-muted-foreground">
            {t('register.subtitle')}
          </p>
        </div>

        <div className="rounded-2xl border border-border bg-card p-6 shadow-soft-lg">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label htmlFor="email" className="text-sm font-medium text-foreground">
                {t('register.emailLabel')}
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
                  placeholder={t('register.emailPlaceholder')}
                />
              </div>
            </div>

            <div className="space-y-2">
              <label htmlFor="password" className="text-sm font-medium text-foreground">
                {t('register.passwordLabel')}
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <input
                  id="password"
                  type="password"
                  autoComplete="new-password"
                  required
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  className="flex h-10 w-full rounded-xl border border-border bg-background pl-10 pr-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                  placeholder={t('register.passwordPlaceholder')}
                />
              </div>
            </div>

            <div className="space-y-2">
              <label
                htmlFor="confirmPassword"
                className="text-sm font-medium text-foreground"
              >
                {t('register.confirmPasswordLabel')}
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <input
                  id="confirmPassword"
                  type="password"
                  autoComplete="new-password"
                  required
                  value={confirmPassword}
                  onChange={(event) => setConfirmPassword(event.target.value)}
                  className="flex h-10 w-full rounded-xl border border-border bg-background pl-10 pr-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                  placeholder={t('register.confirmPasswordPlaceholder')}
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
              <span>
                {isSubmitting ? t('register.submittingButton') : t('register.submitButton')}
              </span>
            </button>
          </form>

          <div className="mt-6 text-center text-sm text-muted-foreground">
            {t('register.haveAccount')}{' '}
            <Link to="/login" className="font-medium text-primary hover:underline">
              {t('register.signIn')}
            </Link>
          </div>
        </div>

        <p className="mt-6 text-center text-xs text-muted-foreground">
          {t('register.termsPrefix')}{' '}
          <span className="cursor-pointer text-foreground hover:underline">
            {t('register.termsLink')}
          </span>{' '}
          {t('register.and')}{' '}
          <span className="cursor-pointer text-foreground hover:underline">
            {t('register.privacyLink')}
          </span>
          .
        </p>
      </motion.div>
    </div>
  )
}

export default Register
