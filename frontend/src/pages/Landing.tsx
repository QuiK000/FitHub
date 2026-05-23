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
  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border bg-background/80 backdrop-blur-xl">
        <div className="container mx-auto flex h-16 items-center justify-between px-4 md:px-6">
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary">
              <Dumbbell className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="text-xl font-bold text-foreground">FitHub</span>
          </div>

          <div className="flex items-center gap-3">
            <LanguageSwitcher />
            <ThemeToggle />
            <Link
              to="/login"
              className="rounded-xl px-4 py-2 text-sm font-medium text-foreground transition-colors hover:bg-accent"
            >
              Sign in
            </Link>
            <Link
              to="/register"
              className="rounded-xl bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
            >
              Get started
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
            Your complete fitness companion
          </div>

          <h1 className="mb-6 text-4xl font-bold leading-tight text-foreground md:text-6xl">
            Transform your fitness journey with{' '}
            <span className="bg-gradient-to-r from-blue-600 to-cyan-600 bg-clip-text text-transparent dark:from-emerald-400 dark:via-cyan-400 dark:to-sky-500">
              intelligent tracking
            </span>
          </h1>

          <p className="mb-10 text-lg text-muted-foreground md:text-xl">
            Track workouts, monitor progress, and achieve your fitness goals with
            our comprehensive platform designed for athletes and fitness enthusiasts.
          </p>

          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Link
              to="/register"
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl bg-primary px-8 text-base font-semibold text-primary-foreground shadow-soft-md transition-all hover:bg-primary/90 hover:shadow-soft-lg"
            >
              Start tracking free
              <TrendingUp className="h-5 w-5" />
            </Link>
            <Link
              to="/login"
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl border border-border bg-background px-8 text-base font-semibold text-foreground transition-all hover:bg-accent"
            >
              Sign in
            </Link>
          </div>

          <p className="mt-6 text-sm text-muted-foreground">
            No credit card required • Free forever • Cancel anytime
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
              value="10K+"
              label="Active users"
              delay={0.3}
            />
            <StatCard
              icon={Activity}
              value="500K+"
              label="Workouts logged"
              delay={0.4}
            />
            <StatCard
              icon={TrendingUp}
              value="95%"
              label="Goal achievement"
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
              Everything you need to succeed
            </h2>
            <p className="text-lg text-muted-foreground">
              Powerful features designed to help you track, analyze, and optimize
              your fitness journey.
            </p>
          </motion.div>

          <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
            <FeatureCard
              icon={Dumbbell}
              title="Workout Logging"
              description="Track every rep, set, and exercise with our intuitive logging system. Build custom routines and monitor your progress over time."
              delay={0.1}
            />
            <FeatureCard
              icon={BarChart3}
              title="Progress Analytics"
              description="Visualize your fitness journey with detailed charts and insights. See your strength gains, volume trends, and performance metrics."
              delay={0.2}
            />
            <FeatureCard
              icon={Heart}
              title="Nutrition Tracking"
              description="Monitor your macros and calories to fuel your workouts. Set nutrition goals and track your daily intake effortlessly."
              delay={0.3}
            />
            <FeatureCard
              icon={Activity}
              title="Performance Metrics"
              description="Track key performance indicators like one-rep max, volume load, and training frequency to optimize your results."
              delay={0.4}
            />
            <FeatureCard
              icon={TrendingUp}
              title="Goal Setting"
              description="Set SMART fitness goals and track your progress. Get motivated with milestone celebrations and achievement badges."
              delay={0.5}
            />
            <FeatureCard
              icon={Users}
              title="Community Support"
              description="Connect with like-minded fitness enthusiasts. Share your progress, get inspired, and stay accountable together."
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
              Ready to start your fitness journey?
            </h2>
            <p className="mb-8 text-lg text-muted-foreground">
              Join thousands of athletes and fitness enthusiasts who trust FitHub
              to track their progress and achieve their goals.
            </p>
            <Link
              to="/register"
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl bg-primary px-8 text-base font-semibold text-primary-foreground shadow-soft-md transition-all hover:bg-primary/90 hover:shadow-soft-lg"
            >
              Create your free account
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
              <span className="font-semibold text-foreground">FitHub</span>
            </div>
            <p className="text-sm text-muted-foreground">
              © 2026 FitHub. All rights reserved.
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
