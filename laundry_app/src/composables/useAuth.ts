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

      // Создаём объект пользователя
      const userData: User = {
        id: name, // используем name как id
        name: name,
        email: `${name}@laundry.local`,
        role: data.role,
        blocked: false
      }

      user.value = userData
      localStorage.setItem(USER_KEY, JSON.stringify(userData))

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
    email: string
    name: string
    password: string
  }) => {
    // Backend пока не поддерживает регистрацию
    // В будущем можно добавить POST /api/auth/register
    return { 
      success: false, 
      message: 'Registration not implemented yet' 
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
