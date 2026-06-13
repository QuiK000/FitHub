import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

import commonEN from '../locales/en/common.json'
import authEN from '../locales/en/auth.json'
import landingEN from '../locales/en/landing.json'
import dashboardEN from '../locales/en/dashboard.json'
import profileEN from '../locales/en/profile.json'
import navigationEN from '../locales/en/navigation.json'
import validationEN from '../locales/en/validation.json'
import progressEN from '../locales/en/progress.json'
import sessionsEN from '../locales/en/sessions.json'
import trainersEN from '../locales/en/trainers.json'
import notificationsEN from '../locales/en/notifications.json'
import membershipsEN from '../locales/en/memberships.json'
import analyticsEN from '../locales/en/analytics.json'
import adminEN from '../locales/en/admin.json'

import commonUK from '../locales/uk/common.json'
import authUK from '../locales/uk/auth.json'
import landingUK from '../locales/uk/landing.json'
import dashboardUK from '../locales/uk/dashboard.json'
import profileUK from '../locales/uk/profile.json'
import navigationUK from '../locales/uk/navigation.json'
import validationUK from '../locales/uk/validation.json'
import progressUK from '../locales/uk/progress.json'
import sessionsUK from '../locales/uk/sessions.json'
import trainersUK from '../locales/uk/trainers.json'
import notificationsUK from '../locales/uk/notifications.json'
import membershipsUK from '../locales/uk/memberships.json'
import analyticsUK from '../locales/uk/analytics.json'
import adminUK from '../locales/uk/admin.json'

import commonRU from '../locales/ru/common.json'
import authRU from '../locales/ru/auth.json'
import landingRU from '../locales/ru/landing.json'
import dashboardRU from '../locales/ru/dashboard.json'
import profileRU from '../locales/ru/profile.json'
import navigationRU from '../locales/ru/navigation.json'
import validationRU from '../locales/ru/validation.json'
import progressRU from '../locales/ru/progress.json'
import sessionsRU from '../locales/ru/sessions.json'
import trainersRU from '../locales/ru/trainers.json'
import notificationsRU from '../locales/ru/notifications.json'
import membershipsRU from '../locales/ru/memberships.json'
import analyticsRU from '../locales/ru/analytics.json'
import adminRU from '../locales/ru/admin.json'
import onboardingEN from '../locales/en/onboarding.json'
import onboardingUK from '../locales/uk/onboarding.json'
import onboardingRU from '../locales/ru/onboarding.json'
import nutritionEN from '../locales/en/nutrition.json'
import nutritionUK from '../locales/uk/nutrition.json'
import nutritionRU from '../locales/ru/nutrition.json'

const allNamespaces = [
  'common', 'auth', 'landing', 'dashboard', 'profile',
  'navigation', 'validation', 'progress', 'sessions',
  'trainers', 'notifications', 'memberships', 'analytics', 'admin', 'onboarding', 'nutrition',
]

const resources = {
  en: {
    common: commonEN, auth: authEN, landing: landingEN,
    dashboard: dashboardEN, profile: profileEN, navigation: navigationEN,
    validation: validationEN, progress: progressEN, sessions: sessionsEN,
    trainers: trainersEN, notifications: notificationsEN,
    memberships: membershipsEN, analytics: analyticsEN, admin: adminEN,
    onboarding: onboardingEN,
    nutrition: nutritionEN,
  },
  uk: {
    common: commonUK, auth: authUK, landing: landingUK,
    dashboard: dashboardUK, profile: profileUK, navigation: navigationUK,
    validation: validationUK, progress: progressUK, sessions: sessionsUK,
    trainers: trainersUK, notifications: notificationsUK,
    memberships: membershipsUK, analytics: analyticsUK, admin: adminUK,
    onboarding: onboardingUK,
    nutrition: nutritionUK,
  },
  ru: {
    common: commonRU, auth: authRU, landing: landingRU,
    dashboard: dashboardRU, profile: profileRU, navigation: navigationRU,
    validation: validationRU, progress: progressRU, sessions: sessionsRU,
    trainers: trainersRU, notifications: notificationsRU,
    memberships: membershipsRU, analytics: analyticsRU, admin: adminRU,
    onboarding: onboardingRU,
    nutrition: nutritionRU,
  },
}

void i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: 'en',
    defaultNS: 'common',
    ns: allNamespaces,

    detection: {
      order: ['localStorage', 'navigator'],
      caches: ['localStorage'],
      lookupLocalStorage: 'i18nextLng',
    },

    interpolation: {
      escapeValue: false,
    },

    react: {
      useSuspense: false,
    },
  })

export default i18n
