import { useEffect } from 'react'
import {
    BarChart3,
    Bell,
    CalendarDays,
    CreditCard,
    ShieldCheck,
    Target,
    Users,
} from 'lucide-react'
import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
} from 'react-router-dom'
import { Toaster } from 'sonner'

import MainLayout from './layouts/MainLayout'
import Landing from './pages/Landing'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import Register from './pages/Register'
import Profile from './pages/Profile'
import Workouts from './pages/Workouts'
import WorkoutDetail from './pages/WorkoutDetail'
import Nutrition from './pages/Nutrition'
import VerifyEmail from './pages/VerifyEmail'
import Onboarding from './pages/Onboarding'
import ForgotPassword from './pages/ForgotPassword'
import ResetPassword from './pages/ResetPassword'
import ShellPage from './pages/ShellPage'

import ProtectedRoute from './components/ProtectedRoute'
import ClientOnboardingGate from './components/ClientOnboardingGate'

import { useAuthStore } from './store/useAuthStore'
import { ThemeProvider } from './contexts/ThemeContext'

const AppRoutes = () => {
    const token = useAuthStore((state) => state.token)
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
    const fetchCurrentUser = useAuthStore((state) => state.fetchCurrentUser)

    useEffect(() => {
        if (!token && !isAuthenticated) {
            return
        }

        void fetchCurrentUser()
    }, [fetchCurrentUser, isAuthenticated, token])

    return (
        <Routes>
            <Route path="/" element={<Landing />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/verify-email" element={<VerifyEmail />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            <Route element={<ProtectedRoute />}>
                <Route element={<ClientOnboardingGate />}>
                <Route element={<MainLayout />}>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                        <Route path="/profile" element={<Profile />} />
                        <Route path="/onboarding" element={<Onboarding />} />
                    </Route>
                    <Route
                        path="/trainers"
                        element={
                            <ShellPage
                                title="Trainers"
                                description="Trainer discovery, profiles, reviews, and booking context will be added here."
                                icon={Users}
                            />
                        }
                    />
                    <Route
                        path="/notifications"
                        element={
                            <ShellPage
                                title="Notifications"
                                description="Unread notifications, live updates, and message actions will appear here."
                                icon={Bell}
                            />
                        }
                    />
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT', 'TRAINER']} />}>
                        <Route path="/workouts" element={<Workouts />} />
                        <Route path="/workouts/:id" element={<WorkoutDetail />} />
                        <Route
                            path="/sessions"
                            element={
                                <ShellPage
                                    title="Sessions"
                                    description="Training sessions, joins, waitlists, and check-ins will be managed from this route."
                                    icon={CalendarDays}
                                />
                            }
                        />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                        <Route path="/nutrition" element={<Nutrition />} />
                        <Route
                            path="/progress"
                            element={
                                <ShellPage
                                    title="Progress"
                                    description="Measurements, goals, personal records, and progress photos will be built in this workspace."
                                    icon={Target}
                                />
                            }
                        />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT', 'ADMIN']} />}>
                        <Route
                            path="/memberships"
                            element={
                                <ShellPage
                                    title="Memberships"
                                    description="Active membership details, payment history, and plan status will be available here."
                                    icon={CreditCard}
                                />
                            }
                        />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['TRAINER', 'ADMIN']} />}>
                        <Route
                            path="/analytics"
                            element={
                                <ShellPage
                                    title="Analytics"
                                    description="Role-aware performance, attendance, revenue, and client analytics will land here."
                                    icon={BarChart3}
                                />
                            }
                        />
                    </Route>
                </Route>
                </Route>
            </Route>

            <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                <Route element={<MainLayout />}>
                    <Route
                        path="/admin"
                        element={
                            <ShellPage
                                title="Admin"
                                description="Administrative controls and operations tooling will be exposed from this protected area."
                                icon={ShieldCheck}
                            />
                        }
                    />
                </Route>
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}

const App = () => {
    return (
        <ThemeProvider>
            <BrowserRouter>
                <AppRoutes />
                <Toaster
                    position="bottom-right"
                    expand={false}
                    richColors
                    closeButton
                />
            </BrowserRouter>
        </ThemeProvider>
    )
}

export default App
