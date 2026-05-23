import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

// Import translation files
import commonEN from '../locales/en/common.json'
import authEN from '../locales/en/auth.json'
import landingEN from '../locales/en/landing.json'
import dashboardEN from '../locales/en/dashboard.json'
import profileEN from '../locales/en/profile.json'
import navigationEN from '../locales/en/navigation.json'
import validationEN from '../locales/en/validation.json'

import commonUK from '../locales/uk/common.json'
import authUK from '../locales/uk/auth.json'
import landingUK from '../locales/uk/landing.json'
import dashboardUK from '../locales/uk/dashboard.json'
import profileUK from '../locales/uk/profile.json'
import navigationUK from '../locales/uk/navigation.json'
import validationUK from '../locales/uk/validation.json'

import commonRU from '../locales/ru/common.json'
import authRU from '../locales/ru/auth.json'
import landingRU from '../locales/ru/landing.json'
import dashboardRU from '../locales/ru/dashboard.json'
import profileRU from '../locales/ru/profile.json'
import navigationRU from '../locales/ru/navigation.json'
import validationRU from '../locales/ru/validation.json'

const resources = {
  en: {
    common: commonEN,
    auth: authEN,
    landing: landingEN,
    dashboard: dashboardEN,
    profile: profileEN,
    navigation: navigationEN,
    validation: validationEN,
  },
  uk: {
    common: commonUK,
    auth: authUK,
    landing: landingUK,
    dashboard: dashboardUK,
    profile: profileUK,
    navigation: navigationUK,
    validation: validationUK,
  },
  ru: {
    common: commonRU,
    auth: authRU,
    landing: landingRU,
    dashboard: dashboardRU,
    profile: profileRU,
    navigation: navigationRU,
    validation: validationRU,
  },
}

void i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: 'en',
    defaultNS: 'common',
    ns: ['common', 'auth', 'landing', 'dashboard', 'profile', 'navigation', 'validation'],

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
