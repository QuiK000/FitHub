import { useEffect, useState, type ComponentType, type FormEvent, type SVGProps } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import {
  Clock,
  MessageSquare,
  Search,
  Star,
  User2,
  X,
} from 'lucide-react'
import { Input } from '../components/ui/input'
import { useAuthStore } from '../store/useAuthStore'
import {
  getTrainers,
  getTrainerReviews,
  createTrainerReview,
  type TrainerReviewResponse,
  type TrainerProfileResponse,
} from '../services/trainer.service'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

const Trainers = () => {
  const { t } = useTranslation(['trainers', 'common'])
  const roles = useAuthStore((state) => state.roles)
  const isClient = roles.includes('CLIENT')
  const [trainers, setTrainers] = useState<TrainerProfileResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [selectedTrainer, setSelectedTrainer] = useState<TrainerProfileResponse | null>(null)
  const [reviews, setReviews] = useState<TrainerReviewResponse[]>([])
  const [isLoadingReviews, setIsLoadingReviews] = useState(false)

  const loadTrainers = async (query?: string) => {
    setIsLoading(true)
    try {
      const page = await getTrainers(0, 20, query)
      setTrainers(page.content)
    } catch (err) {
      console.error(err)
      toast.error(t('common:toast.trainersLoadFailed'))
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    void loadTrainers()
  }, [])

  const handleSearch = () => {
    void loadTrainers(search.trim() || undefined)
  }

  const handleSelectTrainer = async (trainer: TrainerProfileResponse) => {
    setSelectedTrainer(trainer)
    setIsLoadingReviews(true)
    try {
      const page = await getTrainerReviews(trainer.id, 0, 10)
      setReviews(page.content)
    } catch {
      setReviews([])
    } finally {
      setIsLoadingReviews(false)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            {t('title')}
          </p>
          <h1 className="mt-2 text-2xl font-bold text-foreground md:text-3xl">
            {t('title')}
          </h1>
          <p className="mt-1 max-w-2xl text-sm text-muted-foreground">
            {t('subtitle')}
          </p>
        </div>
      </div>

      <div className="flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            placeholder="Search trainers..."
            className="pl-9"
          />
        </div>
        <button
          type="button"
          onClick={handleSearch}
          className="inline-flex h-10 items-center justify-center rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-soft transition-all hover:bg-primary/90"
        >
          Search
        </button>
      </div>

      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-56 animate-pulse rounded-2xl bg-muted" />
          ))}
        </div>
      ) : trainers.length ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {trainers.map((trainer) => (
            <TrainerCard
              key={trainer.id}
              trainer={trainer}
              onSelect={() => void handleSelectTrainer(trainer)}
            />
          ))}
        </div>
      ) : (
        <EmptyState
          icon={User2}
          title="No trainers found"
          description="Try adjusting your search or check back later."
        />
      )}

      {selectedTrainer && (
        <TrainerDetailModal
          trainer={selectedTrainer}
          reviews={reviews}
          isLoadingReviews={isLoadingReviews}
          isClient={isClient}
          onClose={() => {
            setSelectedTrainer(null)
            setReviews([])
          }}
          onReviewCreated={async () => {
            if (selectedTrainer) {
              const page = await getTrainerReviews(selectedTrainer.id, 0, 10)
              setReviews(page.content)
            }
          }}
        />
      )}
    </div>
  )
}

type IconType = ComponentType<SVGProps<SVGSVGElement>>

const TrainerCard = ({
  trainer,
  onSelect,
}: {
  trainer: TrainerProfileResponse
  onSelect: () => void
}) => {
  const initials = [trainer.firstname, trainer.lastname]
    .filter(Boolean)
    .map((n) => n[0])
    .join('')
    .toUpperCase()

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.25 }}
      className="rounded-2xl border border-border bg-card p-5 shadow-soft transition-shadow hover:shadow-soft-md"
    >
      <div className="flex items-start gap-4">
        <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl bg-primary text-lg font-bold text-primary-foreground">
          {initials || 'T'}
        </div>
        <div className="min-w-0 flex-1">
          <h3 className="text-base font-semibold text-foreground">
            {trainer.firstname} {trainer.lastname}
          </h3>
          <div className="mt-1 flex items-center gap-1.5 text-xs text-muted-foreground">
            <Clock className="h-3.5 w-3.5" />
            {trainer.experienceYears} years experience
          </div>
        </div>
      </div>

      {trainer.specializations.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-1.5">
          {trainer.specializations.map((spec) => (
            <span
              key={spec}
              className="rounded-full bg-primary/10 px-2.5 py-1 text-xs font-medium text-primary"
            >
              {spec}
            </span>
          ))}
        </div>
      )}

      {trainer.description && (
        <p className="mt-3 line-clamp-2 text-sm text-muted-foreground">
          {trainer.description}
        </p>
      )}

      <button
        type="button"
        onClick={onSelect}
        className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-xl border border-border bg-background px-4 py-2 text-sm font-semibold text-foreground transition hover:bg-accent"
      >
        <MessageSquare className="h-4 w-4" />
        View details & reviews
      </button>
    </motion.div>
  )
}

const TrainerDetailModal = ({
  trainer,
  reviews,
  isLoadingReviews,
  isClient,
  onClose,
  onReviewCreated,
}: {
  trainer: TrainerProfileResponse
  reviews: TrainerReviewResponse[]
  isLoadingReviews: boolean
  isClient: boolean
  onClose: () => void
  onReviewCreated: () => Promise<void>
}) => {
  const [showReviewForm, setShowReviewForm] = useState(false)

  return (
    <div className="fixed inset-0 z-30 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        className="max-h-[calc(100vh-3rem)] w-full max-w-2xl overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
      >
        <div className="mb-5 flex items-start justify-between gap-4">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              Trainer profile
            </p>
            <h2 className="mt-1 text-xl font-bold text-foreground">
              {trainer.firstname} {trainer.lastname}
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">
              {trainer.experienceYears} years experience
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-border bg-background text-muted-foreground transition hover:bg-accent"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        {trainer.specializations.length > 0 && (
          <div className="mb-5 flex flex-wrap gap-1.5">
            {trainer.specializations.map((spec) => (
              <span
                key={spec}
                className="rounded-full bg-primary/10 px-2.5 py-1 text-xs font-medium text-primary"
              >
                {spec}
              </span>
            ))}
          </div>
        )}

        {trainer.description && (
          <p className="mb-5 text-sm text-muted-foreground">{trainer.description}</p>
        )}

        <div className="mb-5 flex items-center justify-between">
          <h3 className="text-sm font-semibold text-foreground">Reviews</h3>
          {isClient && (
            <button
              type="button"
              onClick={() => setShowReviewForm(!showReviewForm)}
              className="inline-flex h-8 items-center justify-center gap-1.5 rounded-xl bg-primary px-3 text-xs font-semibold text-primary-foreground transition hover:bg-primary/90"
            >
              <Star className="h-3 w-3" />
              Write review
            </button>
          )}
        </div>

        {showReviewForm && isClient && (
          <ReviewForm
            trainerId={trainer.id}
            onCreated={async () => {
              setShowReviewForm(false)
              await onReviewCreated()
            }}
            onCancel={() => setShowReviewForm(false)}
          />
        )}

        {isLoadingReviews ? (
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="h-20 animate-pulse rounded-xl bg-muted" />
            ))}
          </div>
        ) : reviews.length ? (
          <div className="space-y-3">
            {reviews.map((review) => (
              <ReviewRow key={review.id} review={review} />
            ))}
          </div>
        ) : (
          <p className="rounded-xl border border-dashed border-border bg-muted/30 px-4 py-6 text-center text-sm text-muted-foreground">
            No reviews yet. Be the first to leave feedback.
          </p>
        )}
      </motion.div>
    </div>
  )
}

const ReviewForm = ({
  trainerId,
  onCreated,
  onCancel,
}: {
  trainerId: string
  onCreated: () => Promise<void>
  onCancel: () => void
}) => {
  const { t } = useTranslation(['trainers', 'common'])
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    try {
      await createTrainerReview(trainerId, {
        rating,
        comment: comment.trim() || undefined,
      })
      toast.success(t('common:toast.reviewSubmitted'))
      await onCreated()
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Failed to submit review.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mb-5 space-y-4 rounded-2xl border border-border bg-muted/40 p-4">
      <div className="space-y-1.5">
        <span className="text-xs text-foreground">Rating</span>
        <div className="flex gap-1">
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              type="button"
              onClick={() => setRating(star)}
              className="transition hover:scale-110"
            >
              <Star
                className={`h-6 w-6 ${
                  star <= rating
                    ? 'fill-amber-400 text-amber-400'
                    : 'text-muted-foreground'
                }`}
              />
            </button>
          ))}
        </div>
      </div>
      <label className="space-y-1.5">
        <span className="text-xs text-foreground">Comment (optional)</span>
        <textarea
          className="min-h-[80px] w-full rounded-xl border border-border bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring"
          placeholder="Share your experience..."
          value={comment}
          onChange={(e) => setComment(e.target.value)}
        />
      </label>
      <div className="flex justify-end gap-2">
        <button type="button" onClick={onCancel} disabled={isSubmitting} className="inline-flex h-9 items-center justify-center rounded-xl px-4 text-sm font-medium text-foreground transition hover:bg-accent disabled:opacity-60">
          Cancel
        </button>
        <button type="submit" disabled={isSubmitting} className="inline-flex h-9 items-center justify-center gap-2 rounded-xl bg-primary px-4 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:opacity-60">
          {isSubmitting && <span className="h-4 w-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />}
          Submit
        </button>
      </div>
    </form>
  )
}

const ReviewRow = ({ review }: { review: TrainerReviewResponse }) => (
  <div className="rounded-xl border border-border bg-background p-3">
    <div className="flex items-start justify-between gap-3">
      <div>
        <p className="text-sm font-semibold text-foreground">
          {review.reviewer.clientFirstname} {review.reviewer.clientLastname}
        </p>
        <div className="mt-1 flex items-center gap-1">
          {Array.from({ length: 5 }).map((_, i) => (
            <Star
              key={i}
              className={`h-3.5 w-3.5 ${
                i < review.rating
                  ? 'fill-amber-400 text-amber-400'
                  : 'text-muted-foreground'
              }`}
            />
          ))}
        </div>
      </div>
      <span className="text-xs text-muted-foreground">
        {new Date(review.createdAt).toLocaleDateString()}
      </span>
    </div>
    {review.comment && (
      <p className="mt-2 text-sm text-muted-foreground">{review.comment}</p>
    )}
  </div>
)

const EmptyState = ({
  icon: Icon,
  title,
  description,
}: {
  icon: IconType
  title: string
  description: string
}) => (
  <div className="flex min-h-48 flex-col items-center justify-center rounded-2xl border border-dashed border-border bg-muted/30 p-8 text-center">
    <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-background">
      <Icon className="h-6 w-6 text-muted-foreground" />
    </div>
    <p className="mt-4 text-sm font-semibold text-foreground">{title}</p>
    <p className="mt-1 max-w-sm text-sm text-muted-foreground">{description}</p>
  </div>
)

export default Trainers
