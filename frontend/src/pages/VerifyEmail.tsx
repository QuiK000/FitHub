import { useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { CheckCircle2, Mail, RotateCcw, ShieldCheck } from 'lucide-react'
import { motion } from 'framer-motion'
import { resendVerification, verifyEmail } from '../services/auth.service'
import ThemeToggle from '../components/ThemeToggle'

const VerifyEmail = () => {
  const { t } = useTranslation('auth')
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()

  const [token, setToken] = useState(searchParams.get('token') ?? '')
  const [email, setEmail] = useState(searchParams.get('email') ?? '')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isResending, setIsResending] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [resendSuccess, setResendSuccess] = useState<string | null>(null)

  const tokenPreview = useMemo(() => {
    if (!token) return '—'
    if (token.length <= 8) return token
    return `${token.slice(0, 4)}••••${token.slice(-4)}`
  }, [token])

  const handleVerify = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError(null)
    setSuccess(null)
    setIsSubmitting(true)

    try {
      const response = await verifyEmail({ token: token.trim() })
      setSuccess(response.message || 'Email verified successfully. Redirecting...')
      window.setTimeout(() => {
        navigate('/login', { replace: true })
      }, 1000)
    } catch (err) {
      console.error(err)
      setError('Verification failed. Please check the code and try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleResend = async () => {
    if (!email.trim()) {
      setError('Please enter your account email to resend your verification code.')
      return
    }

    setError(null)
    setResendSuccess(null)
    setIsResending(true)

    try {
      const response = await resendVerification({ email: email.trim() })
      setResendSuccess(response.message || 'Verification code resent successfully.')
    } catch (err) {
      console.error(err)
      setError('Could not resend verification code. Please try again shortly.')
    } finally {
      setIsResending(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 py-10">
      <div className="absolute right-4 top-4">
        <ThemeToggle />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-lg"
      >
        <div className="rounded-2xl border border-border bg-card p-6 shadow-soft-lg">
          <div className="mb-6">
            <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-border bg-muted px-3 py-1 text-xs text-muted-foreground">
              <ShieldCheck className="h-3.5 w-3.5 text-success" />
              {t('verifyEmail.badge')}
            </div>
            <h1 className="text-2xl font-bold text-card-foreground">
              {t('verifyEmail.title')}
            </h1>
            <p className="mt-2 text-sm text-muted-foreground">
              {t('verifyEmail.subtitle')}
            </p>
          </div>

          <div className="space-y-5">
            <form onSubmit={handleVerify} className="space-y-4">
              <div className="space-y-2">
                <label htmlFor="token" className="text-sm font-medium text-foreground">
                  {t('verifyEmail.tokenLabel')}
                </label>
                <input
                  id="token"
                  value={token}
                  onChange={(event) => setToken(event.target.value)}
                  placeholder={t('verifyEmail.tokenPlaceholder')}
                  required
                  className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                />
                <p className="text-xs text-muted-foreground">
                  {t('verifyEmail.tokenPreview', { preview: tokenPreview })}
                </p>
              </div>

              <button
                type="submit"
                disabled={isSubmitting || !token.trim()}
                className="inline-flex h-10 w-full items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isSubmitting ? t('verifyEmail.verifyingButton') : t('verifyEmail.verifyButton')}
              </button>
            </form>

            <div className="rounded-xl border border-border bg-muted p-4">
              <div className="mb-3 flex items-center gap-2 text-sm text-foreground">
                <Mail className="h-4 w-4 text-primary" />
                {t('verifyEmail.resendSection.title')}
              </div>

              <div className="space-y-3">
                <div className="space-y-2">
                  <label htmlFor="email" className="text-sm font-medium text-foreground">
                    {t('verifyEmail.resendSection.emailLabel')}
                  </label>
                  <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    placeholder={t('verifyEmail.resendSection.emailPlaceholder')}
                    className="flex h-10 w-full rounded-xl border border-border bg-background px-3 text-sm text-foreground shadow-soft transition-all placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
                  />
                </div>
                <button
                  type="button"
                  disabled={isResending}
                  onClick={handleResend}
                  className="inline-flex h-10 w-full items-center justify-center gap-2 rounded-xl border border-border bg-background px-4 text-sm font-semibold text-foreground transition-all hover:bg-accent disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <RotateCcw className="h-4 w-4" />
                  {isResending ? t('verifyEmail.resendSection.resendingButton') : t('verifyEmail.resendSection.resendButton')}
                </button>
              </div>
            </div>

            {error && (
              <div className="rounded-xl border border-destructive/40 bg-destructive/10 px-3 py-2 text-sm text-destructive">
                {error}
              </div>
            )}

            {success && (
              <div className="flex items-center gap-2 rounded-xl border border-success/40 bg-success/10 px-3 py-2 text-sm text-success">
                <CheckCircle2 className="h-4 w-4" />
                {success}
              </div>
            )}

            {resendSuccess && (
              <div className="rounded-xl border border-primary/40 bg-primary/10 px-3 py-2 text-sm text-primary">
                {resendSuccess}
              </div>
            )}

            <p className="text-center text-sm text-muted-foreground">
              {t('verifyEmail.alreadyVerified')}{' '}
              <Link to="/login" className="font-medium text-primary hover:underline">
                {t('verifyEmail.continueToLogin')}
              </Link>
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  )
}

export default VerifyEmail
