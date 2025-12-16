import { ref, computed } from 'vue'

interface User {
  fullName: string
  email: string
  group: string
  room: string
  contract: string
}

const TOKEN_KEY = 'auth_token'
const USER_KEY = 'auth_user'

// Глобальное реактивное состояние
const user = ref<User | null>(null)
const token = ref<string | null>(null)

// Инициализация из cookies при загрузке
const initFromCookies = () => {
  const cookies = document.cookie.split(';').reduce((acc, cookie) => {
    const [key, value] = cookie.trim().split('=')
    acc[key] = value
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

  const login = (email: string, _password: string) => {
    // Фейковый логин - просто создаём пользователя
    const fakeUser: User = {
      fullName: 'Иванов Иван Иванович',
      email: email,
      group: '23212',
      room: '107М',
      contract: '123456789'
    }

    const fakeToken = generateFakeToken()
    
    token.value = fakeToken
    user.value = fakeUser
    
    setCookie(TOKEN_KEY, fakeToken)
    localStorage.setItem(USER_KEY, JSON.stringify(fakeUser))

    return { success: true }
  }

  const register = (userData: {
    email: string
    fullName: string
    group: string
    room: string
    contract: string
    password: string
  }) => {
    const newUser: User = {
      fullName: userData.fullName,
      email: userData.email,
      group: userData.group,
      room: userData.room,
      contract: userData.contract
    }

    const fakeToken = generateFakeToken()
    
    token.value = fakeToken
    user.value = newUser
    
    setCookie(TOKEN_KEY, fakeToken)
    localStorage.setItem(USER_KEY, JSON.stringify(newUser))

    return { success: true }
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
    login,
    register,
    logout,
    deleteAccount
  }
}
