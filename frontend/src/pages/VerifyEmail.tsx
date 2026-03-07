import {FormEvent, useMemo, useState} from 'react'
import {Link, useNavigate, useSearchParams} from 'react-router-dom'
import {CheckCircle2, Mail, RotateCcw, ShieldCheck} from 'lucide-react'
import {motion} from 'framer-motion'
import {resendVerification, verifyEmail} from '../services/auth.service'
import {Button} from '../components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '../components/ui/card'
import {Input} from '../components/ui/input'
import {Label} from '../components/ui/label'

const VerifyEmail = () => {
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
            const response = await verifyEmail({token: token.trim()})
            setSuccess(response.message || 'Email verified successfully. Redirecting...')
            window.setTimeout(() => {
                navigate('/login', {replace: true})
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
            const response = await resendVerification({email: email.trim()})
            setResendSuccess(response.message || 'Verification code resent successfully.')
        } catch (err) {
            console.error(err)
            setError('Could not resend verification code. Please try again shortly.')
        } finally {
            setIsResending(false)
        }
    }

    return (
        <div
            className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 px-4 py-10 text-slate-50">
            <motion.div
                initial={{opacity: 0, y: 20}}
                animate={{opacity: 1, y: 0}}
                transition={{duration: 0.45, ease: 'easeOut'}}
                className="w-full max-w-lg"
            >
                <Card
                    className="border-slate-800/80 bg-slate-950/80 shadow-[0_30px_80px_rgba(15,23,42,0.9)] backdrop-blur-2xl">
                    <CardHeader className="space-y-4">
                        <div
                            className="inline-flex w-fit items-center gap-2 rounded-full border border-slate-800 bg-slate-900/80 px-3 py-1 text-[11px] uppercase tracking-[0.2em] text-slate-400">
                            <ShieldCheck className="h-3.5 w-3.5 text-emerald-400"/>
                            Secure verification
                        </div>
                        <div>
                            <CardTitle>Verify your email address</CardTitle>
                            <CardDescription>
                                Enter the verification token sent to your inbox to activate your account.
                            </CardDescription>
                        </div>
                    </CardHeader>

                    <CardContent className="space-y-5">
                        <form onSubmit={handleVerify} className="space-y-4">
                            <div className="space-y-1.5">
                                <Label htmlFor="token">Verification token</Label>
                                <Input
                                    id="token"
                                    value={token}
                                    onChange={(event) => setToken(event.target.value)}
                                    placeholder="Paste your token"
                                    required
                                />
                                <p className="text-xs text-slate-500">Token preview: {tokenPreview}</p>
                            </div>

                            <Button type="submit" className="w-full" disabled={isSubmitting || !token.trim()}>
                                {isSubmitting ? 'Verifying…' : 'Verify email'}
                            </Button>
                        </form>

                        <div className="rounded-xl border border-slate-800 bg-slate-900/70 p-4">
                            <div className="mb-3 flex items-center gap-2 text-sm text-slate-200">
                                <Mail className="h-4 w-4 text-cyan-400"/>
                                Didn&apos;t receive a code?
                            </div>

                            <div className="space-y-3">
                                <div className="space-y-1.5">
                                    <Label htmlFor="email">Account email</Label>
                                    <Input
                                        id="email"
                                        type="email"
                                        value={email}
                                        onChange={(event) => setEmail(event.target.value)}
                                        placeholder="you@example.com"
                                    />
                                </div>
                                <Button
                                    type="button"
                                    variant="outline"
                                    className="w-full"
                                    disabled={isResending}
                                    onClick={handleResend}
                                >
                                    <RotateCcw className="h-4 w-4"/>
                                    {isResending ? 'Sending…' : 'Resend code'}
                                </Button>
                            </div>
                        </div>

                        {error && (
                            <div
                                className="rounded-xl border border-red-500/40 bg-red-500/10 px-3 py-2 text-xs text-red-200">
                                {error}
                            </div>
                        )}

                        {success && (
                            <div
                                className="flex items-center gap-2 rounded-xl border border-emerald-500/40 bg-emerald-500/10 px-3 py-2 text-xs text-emerald-200">
                                <CheckCircle2 className="h-4 w-4"/>
                                {success}
                            </div>
                        )}

                        {resendSuccess && (
                            <div
                                className="rounded-xl border border-cyan-500/40 bg-cyan-500/10 px-3 py-2 text-xs text-cyan-100">
                                {resendSuccess}
                            </div>
                        )}

                        <p className="text-center text-xs text-slate-500">
                            Already verified?{' '}
                            <Link to="/login" className="text-slate-200 underline-offset-2 hover:underline">
                                Continue to login
                            </Link>
                        </p>
                    </CardContent>
                </Card>
            </motion.div>
        </div>
    )
}

export default VerifyEmail