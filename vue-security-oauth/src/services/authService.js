import axios from "axios";

const API_BASE_URL = 'http://localhost:8082'
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
})

class AuthService {
  constructor() {
    this.isAuthenticated = false
    this.user = null
    this.refreshTimer = null
  }

  async login(credentials) {
    try {
      const response = await apiClient.post("/api/v1/auth/login", credentials)
      this.isAuthenticated = true
      this.user = response.data.user
      this.startTokenRefreshTimer()
      return {
        success: true,
        data: response.data,
        message: "Login successful"
      }
    } catch (e) {
      return {
        success: false,
        error: e.response?.data?.message || 'Login failed',
        status: e.response?.status
      }
    }
  }

  async register(userData) {
    try {
      const response = await apiClient.post("/api/v1/auth/register", userData)
      return {
        success: true,
        data: response.data,
        message: "Registration successful"
      }
    } catch (e) {
      return {
        success: false,
        error: e.response?.data?.message || 'Registration failed',
        status: e.response?.status
      }
    }
  }

  async refreshToken() {
    try {
      const response = await apiClient.post("/api/v1/auth/refresh")
      this.isAuthenticated = true
      this.user = response.data.user
      this.startTokenRefreshTimer()
      return {
        success: true,
        data: response.data,
      }
    } catch (e) {
      console.error("Token refresh error:", e.response?.data?.message || e.message)
      this.isAuthenticated = false
      this.user = null
      return {success: false}
    }
  }

  async getUserInfo() {
    try {
      const response = await apiClient.get("/api/v1/auth/user/me")
      return response.data
    } catch (e) {
      if (e.response?.status === 401) {
        const refreshRes = await this.refreshToken()
        if (refreshRes.success) {
          return await this.getUserInfo()
        }
        throw new Error("Unauthorized")
      } else {
        console.error("Get user info error:", e.response?.data?.message || e.message)
        throw new Error("Failed to fetch user info")
      }
    }
  }

  async logout() {
    try {
      await apiClient.post("/api/v1/auth/logout")
    } catch (e) {
      console.error("Logout error:", e.response?.data?.message || e.message)
    } finally {
      this.isAuthenticated = false
      this.user = null
      this.clearTokenRefreshTimer()
    }
  }

  startTokenRefreshTimer() {
    this.clearTokenRefreshTimer()
    // Set timer for 14 minutes (assuming 15 min token expiry)
    this.refreshTime = setTimeout(() => {
      this.refreshToken()
    }, 14 * 60 * 1000)
  }

  clearTokenRefreshTimer() {
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer)
      this.refreshTimer = null
    }
  }

  get isLoggedIn() {
    return this.isAuthenticated
  }

  get currentUser() {
    return this.user
  }
}

export const authService = new AuthService()
export default authService
