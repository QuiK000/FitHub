import { useEffect } from 'react'
import axios from 'axios'
import {
    BrowserRouter,
    Navigate,
    Route,
    Routes,
    useLocation,
    useNavigate,
} from 'react-router-dom'

import MainLayout from './layouts/MainLayout'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import Register from './pages/Register'
import Profile from './pages/Profile'
import Workouts from './pages/Workouts'
import WorkoutDetail from './pages/WorkoutDetail'
import VerifyEmail from './pages/VerifyEmail'
import Onboarding from './pages/Onboarding'

import ProtectedRoute from './components/ProtectedRoute'

import { getMyClientProfile } from './services/profile.service'
import { useAuthStore } from './store/useAuthStore'

const AppRoutes = () => {
    const token = useAuthStore((state) => state.token)
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
    const fetchCurrentUser = useAuthStore((state) => state.fetchCurrentUser)

    const navigate = useNavigate()
    const location = useLocation()

    useEffect(() => {
        if (!token && !isAuthenticated) {
            return
        }

        void fetchCurrentUser()

        if (location.pathname === '/onboarding') {
            return
        }

        void getMyClientProfile().catch((profileErr) => {
            if (
                axios.isAxiosError(profileErr) &&
                (profileErr.response?.status === 404 ||
                    profileErr.response?.status === 500)
            ) {
                navigate('/onboarding', { replace: true })
            } else {
                console.error('Unexpected profile lookup error', profileErr)
            }
        })
    }, [fetchCurrentUser, isAuthenticated, location.pathname, navigate, token])

    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/verify-email" element={<VerifyEmail />} />

            <Route element={<ProtectedRoute />}>
                <Route element={<MainLayout />}>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/profile" element={<Profile />} />
                    <Route path="/workouts" element={<Workouts />} />
                    <Route path="/workouts/:id" element={<WorkoutDetail />} />
                    <Route path="/onboarding" element={<Onboarding />} />
                </Route>
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}

const App = () => {
    return (
        <BrowserRouter>
            <AppRoutes />
        </BrowserRouter>
    )
}

export default App