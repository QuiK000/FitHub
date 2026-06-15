import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'framer-motion'
import { Check, X } from 'lucide-react'
import { Button } from '../ui/button'
import { createPayment, type CreatePaymentRequest } from '../../services/payment.service'
import { getApiErrorMessage } from '../../utils/errorHandler'
import toast from '../ui/toast'

type PlanType = 'MONTHLY' | 'YEARLY' | 'VISITS'

type BrowsePlansModalProps = {
  isOpen: boolean
  onClose: () => void
  onSuccess: () => void
}

const plans = [
  {
    type: 'MONTHLY' as PlanType,
    price: 29.99,
    features: ['Unlimited gym access', 'Group classes', 'Basic tracking'],
  },
  {
    type: 'YEARLY' as PlanType,
    price: 299.99,
    features: ['Unlimited gym access', 'Group classes', 'Advanced analytics', 'Priority support'],
    savings: 'Save $59.89',
  },
  {
    type: 'VISITS' as PlanType,
    price: 9.99,
    features: ['10 visit passes', 'Group classes', 'Basic tracking'],
  },
]

export const BrowsePlansModal = ({
  isOpen,
  onClose,
  onSuccess,
}: BrowsePlansModalProps) => {
  const { t } = useTranslation(['memberships', 'common'])
  const [selectedPlan, setSelectedPlan] = useState<PlanType | null>(null)
  const [isProcessing, setIsProcessing] = useState(false)

  const handlePurchase = async (plan: typeof plans[0]) => {
    setSelectedPlan(plan.type)
    setIsProcessing(true)
    try {
      const payload: CreatePaymentRequest = {
        membershipId: '',
        amount: plan.price,
        currency: 'USD',
      }
      await createPayment(payload)
      toast.success(t('browse.success'))
      onSuccess()
      onClose()
    } catch (err) {
      toast.error(getApiErrorMessage(err, t('browse.failed')))
    } finally {
      setIsProcessing(false)
      setSelectedPlan(null)
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-background/70 px-4 py-6 backdrop-blur-sm">
      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        className="max-h-[calc(100vh-3rem)] w-full max-w-3xl overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-soft-lg"
      >
        <div className="mb-6 flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-foreground">{t('browse.title')}</h2>
            <p className="text-sm text-muted-foreground">{t('browse.subtitle')}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-background text-muted-foreground hover:bg-accent"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          {plans.map((plan) => (
            <div
              key={plan.type}
              className={`relative rounded-2xl border p-6 transition ${
                selectedPlan === plan.type
                  ? 'border-primary bg-primary/5'
                  : 'border-border bg-background hover:border-primary/50'
              }`}
            >
              {plan.savings && (
                <span className="absolute -top-3 left-1/2 -translate-x-1/2 rounded-full bg-primary px-3 py-1 text-xs font-medium text-primary-foreground">
                  {plan.savings}
                </span>
              )}
              <h3 className="text-lg font-semibold text-foreground">
                {t(`browse.${plan.type.toLowerCase()}.name`, plan.type)}
              </h3>
              <div className="mt-2">
                <span className="text-3xl font-bold text-foreground">${plan.price}</span>
                <span className="text-sm text-muted-foreground">
                  /{plan.type === 'VISITS' ? 'pass' : plan.type === 'MONTHLY' ? 'month' : 'year'}
                </span>
              </div>
              <ul className="mt-4 space-y-2">
                {plan.features.map((feature) => (
                  <li key={feature} className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Check className="h-4 w-4 text-primary" />
                    {feature}
                  </li>
                ))}
              </ul>
              <Button
                className="mt-6 w-full"
                onClick={() => void handlePurchase(plan)}
                disabled={isProcessing}
              >
                {isProcessing && selectedPlan === plan.type
                  ? t('browse.processing')
                  : t('browse.purchaseButton')}
              </Button>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  )
}
