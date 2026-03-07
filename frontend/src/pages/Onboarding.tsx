import {FormEvent, useState} from 'react'
import {useNavigate} from 'react-router-dom'
import {motion} from 'framer-motion'
import {Activity, Droplets, Ruler, UserRound, Weight} from 'lucide-react'
import {Button} from '../components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '../components/ui/card'
import {Input} from '../components/ui/input'
import {Label} from '../components/ui/label'
import {type ClientGender, createClientProfile, type CreateClientProfileRequest,} from '../services/profile.service'

const genderOptions: { label: string; value: ClientGender }[] = [
    {label: 'Male', value: 'MALE'},
    {label: 'Female', value: 'FEMALE'},
    {label: 'Other', value: 'OTHER'},
]

const Onboarding = () => {
    const navigate = useNavigate()
    const [form, setForm] = useState<CreateClientProfileRequest>({
        firstname: '',
        lastname: '',
        phone: '',
        birthdate: null,
        height: 170,
        weight: 70,
        dailyWaterTarget: 2500,
        gender: 'OTHER',
    })
    const [fitnessGoal, setFitnessGoal] = useState('')
    const [error, setError] = useState<string | null>(null)
    const [isSubmitting, setIsSubmitting] = useState(false)

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        setError(null)
        setIsSubmitting(true)

        try {
            await createClientProfile({
                ...form,
                birthdate: form.birthdate?.trim() ? form.birthdate : null,
            })
            navigate('/', {replace: true})
        } catch (err) {
            console.error(err)
            setError('Unable to create profile right now. Please check your details and retry.')
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div
            className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 px-4 py-10 text-slate-50">
            <motion.div
                initial={{opacity: 0, y: 18}}
                animate={{opacity: 1, y: 0}}
                transition={{duration: 0.45, ease: 'easeOut'}}
                className="mx-auto w-full max-w-3xl"
            >
                <Card
                    className="border-slate-800/80 bg-slate-950/80 shadow-[0_30px_80px_rgba(15,23,42,0.9)] backdrop-blur-2xl">
                    <CardHeader className="space-y-4">
                        <div
                            className="inline-flex w-fit items-center gap-2 rounded-full border border-slate-800 bg-slate-900/80 px-3 py-1 text-[11px] uppercase tracking-[0.2em] text-slate-400">
                            <Activity className="h-3.5 w-3.5 text-emerald-400"/>
                            Client onboarding
                        </div>
                        <div>
                            <CardTitle>Complete your profile</CardTitle>
                            <CardDescription>
                                Add your details to personalize your training dashboard and daily targets.
                            </CardDescription>
                        </div>
                    </CardHeader>

                    <CardContent>
                        <form onSubmit={handleSubmit} className="space-y-5">
                            <div className="grid gap-4 md:grid-cols-2">
                                <div className="space-y-1.5">
                                    <Label htmlFor="firstname">First name</Label>
                                    <Input
                                        id="firstname"
                                        required
                                        value={form.firstname}
                                        onChange={(event) =>
                                            setForm((prev) => ({...prev, firstname: event.target.value}))
                                        }
                                        placeholder="John"
                                    />
                                </div>

                                <div className="space-y-1.5">
                                    <Label htmlFor="lastname">Last name</Label>
                                    <Input
                                        id="lastname"
                                        required
                                        value={form.lastname}
                                        onChange={(event) =>
                                            setForm((prev) => ({...prev, lastname: event.target.value}))
                                        }
                                        placeholder="Doe"
                                    />
                                </div>
                            </div>

                            <div className="grid gap-4 md:grid-cols-2">
                                <div className="space-y-1.5">
                                    <Label htmlFor="phone">Phone (E.164)</Label>
                                    <Input
                                        id="phone"
                                        required
                                        value={form.phone}
                                        onChange={(event) =>
                                            setForm((prev) => ({...prev, phone: event.target.value}))
                                        }
                                        placeholder="+12025550123"
                                    />
                                </div>

                                <div className="space-y-1.5">
                                    <Label htmlFor="birthdate">Birthdate</Label>
                                    <Input
                                        id="birthdate"
                                        type="date"
                                        value={form.birthdate ?? ''}
                                        onChange={(event) =>
                                            setForm((prev) => ({
                                                ...prev,
                                                birthdate: event.target.value || null,
                                            }))
                                        }
                                    />
                                </div>
                            </div>

                            <div className="grid gap-4 md:grid-cols-3">
                                <div className="space-y-1.5">
                                    <Label htmlFor="height">Height (cm)</Label>
                                    <div className="relative">
                                        <Ruler
                                            className="pointer-events-none absolute left-3 top-3.5 h-4 w-4 text-slate-500"/>
                                        <Input
                                            id="height"
                                            type="number"
                                            min={1}
                                            required
                                            className="pl-9"
                                            value={form.height}
                                            onChange={(event) =>
                                                setForm((prev) => ({...prev, height: Number(event.target.value)}))
                                            }
                                        />
                                    </div>
                                </div>

                                <div className="space-y-1.5">
                                    <Label htmlFor="weight">Weight (kg)</Label>
                                    <div className="relative">
                                        <Weight
                                            className="pointer-events-none absolute left-3 top-3.5 h-4 w-4 text-slate-500"/>
                                        <Input
                                            id="weight"
                                            type="number"
                                            min={1}
                                            step="0.1"
                                            required
                                            className="pl-9"
                                            value={form.weight}
                                            onChange={(event) =>
                                                setForm((prev) => ({...prev, weight: Number(event.target.value)}))
                                            }
                                        />
                                    </div>
                                </div>

                                <div className="space-y-1.5">
                                    <Label htmlFor="dailyWaterTarget">Water target (ml)</Label>
                                    <div className="relative">
                                        <Droplets
                                            className="pointer-events-none absolute left-3 top-3.5 h-4 w-4 text-slate-500"/>
                                        <Input
                                            id="dailyWaterTarget"
                                            type="number"
                                            min={1}
                                            required
                                            className="pl-9"
                                            value={form.dailyWaterTarget}
                                            onChange={(event) =>
                                                setForm((prev) => ({
                                                    ...prev,
                                                    dailyWaterTarget: Number(event.target.value),
                                                }))
                                            }
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="grid gap-4 md:grid-cols-2">
                                <div className="space-y-1.5">
                                    <Label htmlFor="gender">Gender</Label>
                                    <div className="relative">
                                        <UserRound
                                            className="pointer-events-none absolute left-3 top-3.5 h-4 w-4 text-slate-500"/>
                                        <select
                                            id="gender"
                                            value={form.gender}
                                            onChange={(event) =>
                                                setForm((prev) => ({
                                                    ...prev,
                                                    gender: event.target.value as ClientGender
                                                }))
                                            }
                                            className="flex h-11 w-full rounded-md border border-slate-800 bg-slate-900 px-9 py-2 text-sm text-slate-100 focus:border-emerald-500 focus:outline-none focus:ring-1 focus:ring-emerald-500"
                                        >
                                            {genderOptions.map((option) => (
                                                <option key={option.value} value={option.value}>
                                                    {option.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                <div className="space-y-1.5">
                                    <Label htmlFor="fitnessGoal">Fitness goal (optional)</Label>
                                    <Input
                                        id="fitnessGoal"
                                        value={fitnessGoal}
                                        onChange={(event) => setFitnessGoal(event.target.value)}
                                        placeholder="Build muscle, improve endurance..."
                                    />
                                    <p className="text-[11px] text-slate-500">
                                        Stored locally for now. Add this field to your backend DTO when ready.
                                    </p>
                                </div>
                            </div>

                            {error && (
                                <div
                                    className="rounded-xl border border-red-500/40 bg-red-500/10 px-3 py-2 text-xs text-red-200">
                                    {error}
                                </div>
                            )}

                            <Button type="submit" className="w-full" disabled={isSubmitting}>
                                {isSubmitting ? 'Creating profile…' : 'Finish onboarding'}
                            </Button>
                        </form>
                    </CardContent>
                </Card>
            </motion.div>
        </div>
    )
}

export default Onboarding