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
      <button @click="refreshUserInfo" class="btn btn-secondary" :disabled="refreshing">
        {{ refreshing ? 'Refreshing...' : 'Refresh Info' }}
      </button>
      <button @click="logout" class="btn btn-danger">
        Logout
      </button>
    </div>
  </div>
</template>

<script>
import authService from "@/services/authService.js";

export default {
  name: "UserPage",
  data() {
    return {
      user: null,
      loading: true,
      error: null,
      refreshing: false
    }
  },
  async mounted() {
    await this.fetchUserInfo()
  },
  methods: {
    async fetchUserInfo() {
      this.loading = true
      this.error = null
      try {
        const userInfo = await authService.getUserInfo()
        this.user = {
          username: userInfo
        }
        console.log('User info loaded:', this.user)
      } catch (error) {
        console.error('Failed to fetch user info:', error)
        this.error = error.message || 'Failed to load user information'

        if (error.message === 'Unauthorized') {
          this.$router.push('/signin')
        }
      } finally {
        this.loading = false
      }
    },
    async refreshUserInfo() {
      this.refreshing = true
      try {
        const userInfo = await authService.getUserInfo()
        this.user = {
          ...this.user,
          username: userInfo
        }
      } catch (error) {
        this.error = error.message || 'Failed to refresh user information'
      } finally {
        this.refreshing = false
      }
    },
    async logout() {
      try {
        await authService.logout()
        this.$router.push("/signin")
      } catch (error) {
        console.error("Logout error", error)
        setTimeout(() => {
          this.$router.push("signin")
        }, 2000)
      }
    }
  }
}
</script>

<style scoped>
.user-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  min-height: 100vh;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;

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
    color: #666;
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

  .avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    overflow: hidden;
    border: 4px solid rgba(255, 255, 255, 0.3);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &-placeholder {
      width: 100%;
      height: 100%;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      font-weight: bold;
    }
  }

  .user-info {
    flex: 1;

    h1 {
      margin: 0 0 0.5rem 0;
      font-size: 2rem;
      font-weight: 600;
    }

    .user-email {
      opacity: 0.9;
      margin: 0 0 0.5rem 0;
      font-size: 1.1rem;
    }

    .user-roles {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin: 0;

      .role-label {
        opacity: 0.8;
      }

      .role-badge {
        background: rgba(255, 255, 255, 0.2);
        padding: 0.25rem 0.75rem;
        border-radius: 20px;
        font-size: 0.875rem;
        font-weight: 500;
      }
    }
  }
}

.user-details-card {
  padding: 2rem;

  h2 {
    margin: 0 0 1.5rem 0;
    color: #333;
    font-size: 1.5rem;
  }

  .details-grid {
    display: grid;
    gap: 1rem;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }

  .detail-item {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;

    label {
      font-weight: 600;
      color: #555;
      font-size: 0.875rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    span {
      color: #333;
      font-size: 1rem;
    }
  }
}

.user-actions {
  padding: 1.5rem 2rem;
  background: #f8f9fa;
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  border-top: 1px solid #dee2e6;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &-secondary {
    background: #6c757d;
    color: white;

    &:hover:not(:disabled) {
      background: #545b62;
    }
  }

  &-danger {
    background: #dc3545;
    color: white;

    &:hover:not(:disabled) {
      background: #c82333;
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .user-page {
    padding: 1rem;
  }

  .user-header {
    flex-direction: column;
    text-align: center;

    .user-info h1 {
      font-size: 1.5rem;
    }
  }

  .user-actions {
    flex-direction: column;

    .btn {
      width: 100%;
    }
  }

  .details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
