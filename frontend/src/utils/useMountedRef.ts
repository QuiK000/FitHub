import { useEffect, useRef } from 'react'

export const useMountedRef = () => {
  const mounted = useRef(true)

  useEffect(() => {
    return () => {
      mounted.current = false
    }
  }, [])

  return mounted
}
