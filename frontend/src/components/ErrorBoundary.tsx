import { Component, type ErrorInfo, type ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { AlertTriangle, RefreshCw } from 'lucide-react'

type Props = {
  children: ReactNode
}

type State = {
  hasError: boolean
  error: Error | null
}

function ErrorFallback({ onReload }: { onReload: () => void }) {
  const { t } = useTranslation('common')

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background px-4 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-destructive/10">
        <AlertTriangle className="h-8 w-8 text-destructive" />
      </div>
      <h1 className="mt-6 text-2xl font-bold text-foreground">
        {t('errorBoundary.title')}
      </h1>
      <p className="mt-2 max-w-md text-sm text-muted-foreground">
        {t('errorBoundary.description')}
      </p>
      <button
        type="button"
        onClick={onReload}
        className="mt-6 inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
      >
        <RefreshCw className="h-4 w-4" />
        {t('errorBoundary.reload')}
      </button>
    </div>
  )
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo)
  }

  render() {
    if (this.state.hasError) {
      return <ErrorFallback onReload={() => window.location.reload()} />
    }

    return this.props.children
  }
}
