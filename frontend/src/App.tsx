import { useEffect } from 'react'
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
import Progress from './pages/Progress'
import Sessions from './pages/Sessions'
import Trainers from './pages/Trainers'
import Notifications from './pages/Notifications'
import Memberships from './pages/Memberships'
import Analytics from './pages/Analytics'
import Admin from './pages/Admin'
import VerifyEmail from './pages/VerifyEmail'
import Onboarding from './pages/Onboarding'
import ForgotPassword from './pages/ForgotPassword'
import ResetPassword from './pages/ResetPassword'
import TrainerWorkouts from './pages/TrainerWorkouts'
import TrainerProfile from './pages/TrainerProfile'
import TrainerSessions from './pages/TrainerSessions'
import ExerciseManagement from './pages/ExerciseManagement'

import ProtectedRoute from './components/ProtectedRoute'
import ProfileOnboardingGate from './components/ProfileOnboardingGate'

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
                <Route element={<ProfileOnboardingGate />}>
                <Route element={<MainLayout />}>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                        <Route path="/profile" element={<Profile />} />
                        <Route path="/onboarding" element={<Onboarding />} />
                    </Route>
                    <Route path="/trainers" element={<Trainers />} />
                    <Route path="/notifications" element={<Notifications />} />
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                        <Route path="/workouts" element={<Workouts />} />
                        <Route path="/workouts/:id" element={<WorkoutDetail />} />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT', 'TRAINER']} />}>
                        <Route path="/sessions" element={<Sessions />} />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['TRAINER']} />}>
                        <Route path="/trainer-workouts" element={<TrainerWorkouts />} />
                        <Route path="/trainer-profile" element={<TrainerProfile />} />
                        <Route path="/trainer-sessions" element={<TrainerSessions />} />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                        <Route path="/nutrition" element={<Nutrition />} />
                        <Route path="/progress" element={<Progress />} />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['CLIENT']} />}>
                        <Route path="/memberships" element={<Memberships />} />
                    </Route>
                    <Route element={<ProtectedRoute allowedRoles={['TRAINER', 'ADMIN']} />}>
                        <Route path="/analytics" element={<Analytics />} />
                    </Route>
                </Route>
                </Route>
            </Route>

            <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                <Route element={<MainLayout />}>
                    <Route path="/admin" element={<Admin />} />
                    <Route path="/admin/exercises" element={<ExerciseManagement />} />
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
