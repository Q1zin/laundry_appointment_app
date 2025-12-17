import { ref, computed } from 'vue'

interface User {
  id: string
  name: string
  email: string
  role: string
  blocked: boolean
}

const TOKEN_KEY = 'auth_token'
const USER_KEY = 'auth_user'
const API_BASE = '/api'

// Глобальное реактивное состояние
const user = ref<User | null>(null)
const token = ref<string | null>(null)

// Инициализация из cookies при загрузке
const initFromCookies = () => {
  const cookies = document.cookie.split(';').reduce((acc, cookie) => {
    const [key, value] = cookie.trim().split('=')
    if (key) {
      acc[key] = value || ''
    }
    return acc
  }, {} as Record<string, string>)

  if (cookies[TOKEN_KEY]) {
    token.value = cookies[TOKEN_KEY]
    try {
      const userData = localStorage.getItem(USER_KEY)
      if (userData) {
        user.value = JSON.parse(userData)
      }
    } catch {
      // ignore parse errors
    }
  }
}

// Установка cookie
const setCookie = (name: string, value: string, days: number = 7) => {
  const expires = new Date()
  expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000)
  document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/`
}

// Удаление cookie
const deleteCookie = (name: string) => {
  document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`
}

// Генерация фейкового токена
const generateFakeToken = () => {
  return 'fake_token_' + Math.random().toString(36).substring(2) + Date.now().toString(36)
}

export function useAuth() {
  // Инициализация при первом использовании
  if (token.value === null && user.value === null) {
    initFromCookies()
  }

  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'admin')

  const login = async (name: string, password: string) => {
    try {
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: name, password })
      })

      const data = await response.json()

      if (!response.ok || !data.success) {
        return { 
          success: false, 
          message: data.message || 'Login failed' 
        }
      }

      // Сохраняем токен
      token.value = data.token
      setCookie(TOKEN_KEY, data.token)

      // Получаем полные данные пользователя
      try {
        const userResponse = await fetch(`${API_BASE}/auth/user/${name}`)
        if (userResponse.ok) {
          const fullUserData = await userResponse.json()
          
          const userData: User = {
            id: fullUserData.id, // реальный ID из базы (например "user-1")
            name: fullUserData.name,
            email: `${fullUserData.name}@laundry.local`,
            role: fullUserData.role,
            blocked: fullUserData.isBlocked || false
          }

          user.value = userData
          localStorage.setItem(USER_KEY, JSON.stringify(userData))
        }
      } catch (err) {
        console.error('Failed to fetch user data:', err)
        // Если не удалось получить данные, используем данные из login
        const userData: User = {
          id: name,
          name: name,
          email: `${name}@laundry.local`,
          role: data.role,
          blocked: false
        }
        user.value = userData
        localStorage.setItem(USER_KEY, JSON.stringify(userData))
      }

      return { success: true, message: data.message }
    } catch (error) {
      console.error('Login error:', error)
      return { 
        success: false, 
        message: 'Network error. Please try again.' 
      }
    }
  }

  const register = async (userData: {
    username: string
    password: string
    email: string
    fullName: string
    room: string
    contract: string
  }) => {
    try {
      const response = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData)
      })

      const data = await response.json()

      if (!response.ok || !data.success) {
        return { 
          success: false, 
          message: data.message || 'Registration failed' 
        }
      }

      // Автоматический вход после регистрации
      token.value = data.token
      setCookie(TOKEN_KEY, data.token)

      // Получаем полные данные пользователя
      try {
        const userResponse = await fetch(`${API_BASE}/auth/user/${userData.username}`)
        if (userResponse.ok) {
          const fullUserData = await userResponse.json()
          
          const newUser: User = {
            id: fullUserData.id,
            name: fullUserData.name,
            email: fullUserData.email || userData.email,
            role: fullUserData.role,
            blocked: fullUserData.isBlocked || false
          }

          user.value = newUser
          localStorage.setItem(USER_KEY, JSON.stringify(newUser))
        }
      } catch (err) {
        console.error('Failed to fetch user data:', err)
      }

      return { success: true, message: data.message }
    } catch (error) {
      console.error('Registration error:', error)
      return { 
        success: false, 
        message: 'Ошибка сети. Попробуйте снова.' 
      }
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    deleteCookie(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  const deleteAccount = () => {
    logout()
    // В реальном приложении здесь был бы запрос на сервер
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    deleteAccount
  }
}
