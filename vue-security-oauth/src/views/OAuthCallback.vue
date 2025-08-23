<template>
  <div class="oauth-callback">
    <div v-if="loading" class="loading"><h2>🔄 Processing Google login...</h2>
      <p>Please wait...</p></div>
    <div v-else-if="success" class="success"><h2>✅ Successful login!</h2>
    </div>
    <div v-else class="error">
      <h2>❌ Login error</h2>
      <p>{{ errorMessage }}</p>
      <button @click="goToLogin" class="btn">
        Try again
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "OAuthCallback",
  data() {
    return {
      loading: true,
      success: false,
      errorMessage: ""
    }
  },
  mounted() {
    this.handleCallback()
  },
  methods: {
    handleCallback() {
      const urlParams = new URLSearchParams(window.location.search)
      const token = urlParams.get("token")
      const error = urlParams.get("error")
      if (token) {
        this.handleSuccess()
      } else if (error) {
        this.handleSuccess(error);
      } else {
        this.checkAuthStatus()
      }
    },
    handleSuccess() {
      try {
        this.success = true
        this.loading = false
        this.$router.push("/dashboard")
      } catch (error) {
        console.error('Error handling OAuth success:', error)
        this.handleError('Token processing error')
      }
    },
    handleError(error) {
      this.loading = false
      this.success = false
      this.errorMessage = decodeURIComponent(error) || 'OAuth Unknown Error'

      console.error('OAuth error:', error)
    },
    async checkAuthStatus() {
      try {
        this.success = true
        this.loading = false

        setTimeout(() => {
          this.$router.push('/dashboard')
        }, 2000)

      } catch (error) {
        this.handleError('Token not found')
      }
    },

    goToLogin() {
      this.$router.push('/signin')
    }
  }
}

</script>

<style scoped>
.oauth-callback {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.container {
  text-align: center;
  padding: 40px;
  max-width: 500px;
}

.loading, .success, .error {
  padding: 20px;
  border-radius: 8px;
}

.loading {
  background: #e3f2fd;
  border: 1px solid #90caf9;
  color: #1565c0;
}

.success {
  background: #e8f5e8;
  border: 1px solid #81c784;
  color: #2e7d32;
}

.error {
  background: #ffebee;
  border: 1px solid #e57373;
  color: #c62828;
}

.btn {
  margin-top: 15px;
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn:hover {
  background: #0056b3;
}

h2 {
  margin-bottom: 10px;
}

p {
  margin-bottom: 15px;
}
</style>
