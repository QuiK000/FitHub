import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  Activity,
  BarChart3,
  Dumbbell,
  Heart,
  TrendingUp,
  Users,
  Zap,
} from 'lucide-react'
import ThemeToggle from '../components/ThemeToggle'
import LanguageSwitcher from '../components/LanguageSwitcher'

const Landing = () => {
  const { t } = useTranslation('landing')
  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border bg-background/80 backdrop-blur-xl">
        <div className="container mx-auto flex h-16 items-center justify-between px-4 md:px-6">
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary">
              <Dumbbell className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="text-xl font-bold text-foreground">{t('header.brand')}</span>
          </div>

          <div className="flex items-center gap-3">
            <LanguageSwitcher />
            <ThemeToggle />
            <Link
              to="/login"
              className="rounded-xl px-4 py-2 text-sm font-medium text-foreground transition-colors hover:bg-accent"
            >
              {t('header.signIn')}
            </Link>
            <Link
              to="/register"
              className="rounded-xl bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
            >
              {t('header.getStarted')}
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="container mx-auto px-4 py-20 md:px-6 md:py-32">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="mx-auto max-w-4xl text-center"
        >
          <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-border bg-muted px-4 py-1.5 text-sm text-muted-foreground">
            <Zap className="h-4 w-4" />
            {t('hero.badge')}
          </div>

          <h1 className="mb-6 text-4xl font-bold leading-tight text-foreground md:text-6xl">
            {t('hero.title')}{' '}
            <span className="bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent dark:from-emerald-400 dark:via-cyan-400 dark:to-sky-500">
              {t('hero.titleHighlight')}
            </span>
          </h1>

          <p className="mb-10 text-lg text-muted-foreground md:text-xl">
            {t('hero.subtitle')}
          </p>

          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Link
              to="/register"
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl bg-primary px-8 text-base font-semibold text-primary-foreground shadow-soft-md transition-all hover:bg-primary/90 hover:shadow-soft-lg"
            >
              {t('hero.ctaPrimary')}
              <TrendingUp className="h-5 w-5" />
            </Link>
            <Link
              to="/login"
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl border border-border bg-background px-8 text-base font-semibold text-foreground transition-all hover:bg-accent"
            >
              {t('hero.ctaSecondary')}
            </Link>
          </div>

          <p className="mt-6 text-sm text-muted-foreground">
            {t('hero.benefits')}
          </p>
        </motion.div>

        {/* Hero Image/Stats */}
        <motion.div
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.2 }}
          className="mx-auto mt-16 max-w-5xl"
        >
          <div className="grid gap-4 md:grid-cols-3">
            <StatCard
              icon={Users}
              value={t('stats.users.value')}
              label={t('stats.users.label')}
              delay={0.3}
            />
            <StatCard
              icon={Activity}
              value={t('stats.workouts.value')}
              label={t('stats.workouts.label')}
              delay={0.4}
            />
            <StatCard
              icon={TrendingUp}
              value={t('stats.achievement.value')}
              label={t('stats.achievement.label')}
              delay={0.5}
            />
          </div>
        </motion.div>
      </section>

      {/* Features Section */}
      <section className="border-t border-border bg-muted/30 py-20 md:py-32">
        <div className="container mx-auto px-4 md:px-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="mx-auto mb-16 max-w-3xl text-center"
          >
            <h2 className="mb-4 text-3xl font-bold text-foreground md:text-4xl">
              {t('features.title')}
            </h2>
            <p className="text-lg text-muted-foreground">
              {t('features.subtitle')}
            </p>
          </motion.div>

          <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
            <FeatureCard
              icon={Dumbbell}
              title={t('features.workoutLogging.title')}
              description={t('features.workoutLogging.description')}
              delay={0.1}
            />
            <FeatureCard
              icon={BarChart3}
              title={t('features.progressAnalytics.title')}
              description={t('features.progressAnalytics.description')}
              delay={0.2}
            />
            <FeatureCard
              icon={Heart}
              title={t('features.nutritionTracking.title')}
              description={t('features.nutritionTracking.description')}
              delay={0.3}
            />
            <FeatureCard
              icon={Activity}
              title={t('features.performanceMetrics.title')}
              description={t('features.performanceMetrics.description')}
              delay={0.4}
            />
            <FeatureCard
              icon={TrendingUp}
              title={t('features.goalSetting.title')}
              description={t('features.goalSetting.description')}
              delay={0.5}
            />
            <FeatureCard
              icon={Users}
              title={t('features.communitySupport.title')}
              description={t('features.communitySupport.description')}
              delay={0.6}
            />
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 md:py-32">
        <div className="container mx-auto px-4 md:px-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="mx-auto max-w-4xl rounded-3xl border border-border bg-card p-8 text-center shadow-soft-lg md:p-16"
          >
            <h2 className="mb-4 text-3xl font-bold text-card-foreground md:text-4xl">
              {t('cta.title')}
            </h2>
            <p className="mb-8 text-lg text-muted-foreground">
              {t('cta.subtitle')}
            </p>
            <Link
              to="/register"
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl bg-primary px-8 text-base font-semibold text-primary-foreground shadow-soft-md transition-all hover:bg-primary/90 hover:shadow-soft-lg"
            >
              {t('cta.button')}
              <TrendingUp className="h-5 w-5" />
            </Link>
          </motion.div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border bg-muted/30 py-12">
        <div className="container mx-auto px-4 md:px-6">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <div className="flex items-center gap-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
                <Dumbbell className="h-4 w-4 text-primary-foreground" />
              </div>
              <span className="font-semibold text-foreground">{t('footer.brand')}</span>
            </div>
            <p className="text-sm text-muted-foreground">
              {t('footer.copyright')}
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}

type StatCardProps = {
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>
  value: string
  label: string
  delay: number
}

const StatCard = ({ icon: Icon, value, label, delay }: StatCardProps) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.6, delay }}
    className="rounded-2xl border border-border bg-card p-6 text-center shadow-soft"
  >
    <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
      <Icon className="h-6 w-6 text-primary" />
    </div>
    <p className="mb-1 text-3xl font-bold text-card-foreground">{value}</p>
    <p className="text-sm text-muted-foreground">{label}</p>
  </motion.div>
)

type FeatureCardProps = {
  icon: React.ComponentType<React.SVGProps<SVGSVGElement>>
  title: string
  description: string
  delay: number
}

const FeatureCard = ({
  icon: Icon,
  title,
  description,
  delay,
}: FeatureCardProps) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    whileInView={{ opacity: 1, y: 0 }}
    viewport={{ once: true }}
    transition={{ duration: 0.6, delay }}
    className="rounded-2xl border border-border bg-card p-6 shadow-soft transition-shadow hover:shadow-soft-md"
  >
    <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
      <Icon className="h-6 w-6 text-primary" />
    </div>
    <h3 className="mb-2 text-xl font-semibold text-card-foreground">{title}</h3>
    <p className="text-sm text-muted-foreground">{description}</p>
  </motion.div>
)

export default Landing
