import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import { Home } from 'lucide-react'

const NotFound = () => {
  const { t } = useTranslation('common')

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background px-4 text-center">
      <p className="text-6xl font-bold text-foreground">404</p>
      <h1 className="mt-4 text-xl font-semibold text-foreground">{t('notFound.title')}</h1>
      <p className="mt-2 max-w-md text-sm text-muted-foreground">
        {t('notFound.description')}
      </p>
      <Link
        to="/"
        className="mt-6 inline-flex h-10 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
      >
        <Home className="h-4 w-4" />
        {t('notFound.backToHome')}
      </Link>
    </div>
  )
}

export default NotFound
