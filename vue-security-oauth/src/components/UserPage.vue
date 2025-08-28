<template>
  <div class="user-page">
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Loading user information...</p>
    </div>
    <div v-else-if="error" class="error-container">
      <div class="error-icon">⚠️</div>
      <h2>Error Loading User Info</h2>
      <p>{{ error }}</p>
      <button @click="fetchUserInfo" class="retry-btn">Try Again</button>
    </div>
    <div v-else-if="user" class="user-content">
      <div class="user-header">
        <div class="user-info">
          <h1>Hello, {{ user.username || user.email }}</h1>
        </div>
      </div>
    </div>
    <div class="user-actions">
      <my-button @click="refreshUserInfo" class="btn btn-secondary" :disabled="refreshing">
        {{ refreshing ? 'Refreshing...' : 'Refresh Info' }}
      </my-button>
      <my-button @click="logout" class="btn btn-danger">
        Logout
      </my-button>
    </div>
  </div>
</template>

<script>
import MyButton from "@/components/UI/MyButton.vue";
import {onMounted, ref} from "vue";
import authService from "@/services/authService.js";
import router from "@/router/index.js";

export default {
  name: "UserPage",
  components: {MyButton},

  setup(props) {
    const user = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const refreshing = ref(false)
    const fetchUserInfo = async () => {
      loading.value = true
      error.value = null
      try {
        const userInfo = await authService.getUserInfo()
        user.value = {
          username: userInfo
        }
        console.log('User info loaded:', user.value)
      } catch (err) {
        console.error("Failed to fetch user info:", err)
        error.value = err.message || "Failed to load user information"

        if (error.message === 'Unauthorized') {
          router.push('/signin')
        }
      } finally {
        loading.value = false
      }
    }
    const refreshUserInfo = async () => {
      refreshing.value = true
      try {
        const userInfo = await authService.getUserInfo()
        user.value = {
          ...user.value,
          username: userInfo
        }
      } catch (err) {
        error.value = err.message || 'Failed to refresh user information'
      } finally {
        refreshing.value = false
      }
    }
    const logout = async () => {
      try {
        await authService.logout()
        router.push("/signin")
      } catch (error) {
        console.error("Logout error", error)
        setTimeout(() => {
          router.push("signin")
        }, 2000)
      }
    }
    onMounted(() => {
      fetchUserInfo()
    })
    return {user, loading, error, refreshing, fetchUserInfo, logout, refreshUserInfo}
  }
}
</script>

<style scoped>
.user-page {
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 2rem;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.loading-container,
.error-container,
.user-content {
  width: 100%;
  max-width: 600px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  .spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
  }

  p {
    color: #fff;
    font-size: 1.1rem;
  }
}

.error-container {
  text-align: center;
  padding: 3rem;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  .error-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
  }

  h2 {
    color: #dc3545;
    margin-bottom: 1rem;
  }

  p {
    color: #666;
    margin-bottom: 2rem;
    font-size: 1.1rem;
  }

  .retry-btn {
    background: #007bff;
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 6px;
    cursor: pointer;
    font-size: 1rem;
    transition: background-color 0.3s;

    &:hover {
      background: #0056b3;
    }
  }
}

.user-content {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  color: white;
  display: flex;
  align-items: center;
  gap: 1.5rem;

  .user-info {
    flex: 1;

    h1 {
      margin: 0 0 0.5rem 0;
      font-size: 2rem;
      font-weight: 600;
    }
  }
}

.user-actions {
  padding: 1.5rem 6rem;
  background: #f8f9fa;
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  border-top: 1px solid #dee2e6;
}
</style>
