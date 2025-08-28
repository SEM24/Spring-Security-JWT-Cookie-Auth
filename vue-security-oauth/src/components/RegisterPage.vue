<template>
  <div class="register-page">
    <div class="auth-container">
      <div class="auth-header">
        <h1>Create Account</h1>
        <p>Join us today! Please fill in the information below.</p>
      </div>
      <form class="auth-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <my-input v-model="form.email" type="text" placeholder="Email" :error="errors.email"
                    required/>
          <div class="form-group">
            <my-input
              v-model="form.username"
              type="text"
              placeholder="Username"
              :error="errors.username"
              required
            />
          </div>

          <div class="form-group">
            <my-input
              v-model="form.password"
              type="password"
              placeholder="Password"
              :error="errors.password"
              required
            />
          </div>

          <div class="form-group">
            <my-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="Confirm Password"
              :error="errors.confirmPassword"
              required
            />
          </div>
          <div class="password-requirements" v-if="form.password">
            <p class="requirements-title">Password must contain:</p>
            <ul>
              <li :class="{ valid: hasMinLength }">At least 8 characters</li>
              <li :class="{ valid: hasLetter }">At least one letter</li>
              <li :class="{ valid: hasNumber }">At least one number</li>
            </ul>
          </div>
          <my-button
            type="submit"
            :disabled="loading"
            class="auth-btn"
          >
            {{ loading ? 'Creating Account...' : 'Create Account' }}
          </my-button>
        </div>
      </form>
      <div v-if="message" :class="messageClass" class="message">
        {{ message }}
      </div>

      <div class="divider">
        <span>OR</span>
      </div>

      <div class="oauth-section">
        <a
          href="http://localhost:8082/api/oauth2/authorization/google"
          class="oauth-btn google-btn"
        >
          <svg class="oauth-icon" viewBox="0 0 24 24">
            <path fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
            <path fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          Sign up with Google
        </a>

        <my-button
          @click="loginWithGitHub"
          class="oauth-btn github-btn"
          :disabled="loading"
          type="button"
        >
          <svg class="oauth-icon" viewBox="0 0 24 24">
            <path fill="currentColor"
                  d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
          </svg>
          Sign up with GitHub
        </my-button>
      </div>
      <div class="auth-footer">
        <p>Already have an account?
          <router-link to="/signin" class="auth-link">Sign in here</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import MyInput from "@/components/UI/MyInput.vue";
import MyButton from "@/components/UI/MyButton.vue";
import authService from "@/services/authService.js";
import formHelpers from "@/mixins/formHelpers.js";

export default {
  components: {MyButton, MyInput},
  mixins: [formHelpers],
  name: "RegisterPage",
  data() {
    return {
      form: {
        email: "",
        username: "",
        password: "",
        confirmPassword: "",
      },
      loading: false,
    }
  },
  hasMinLength() {
    return this.form.password.length >= 8
  },

  hasLetter() {
    return /[a-zA-Z]/.test(this.form.password)
  },

  hasNumber() {
    return /\d/.test(this.form.password)
  },
  methods: {
    async handleRegister() {
      this.clearErrors()
      this.clearMessage()
      this.loading = true;
      try {
        const result = await authService.register(this.form);
        if (result.success) {
          this.showMessage('Account created successfully! Redirecting to main page.', 'success')
          setTimeout(() => {
            this.$router.push("/");
          }, 2000)
        } else {
          this.showMessage(result.error, 'error')
        }
      } catch (error) {
        this.showMessage('An error occurred. Please try again.', 'error')
      } finally {
        this.loading = false
      }
    },
  },

}
</script>

<style scoped>

.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  width: 100vw;
}

.auth-container {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h1 {
  color: #333;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.auth-header p {
  color: #666;
  margin: 0;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.auth-btn {
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 0.5rem;
}

.message {
  padding: 12px;
  border-radius: 6px;
  margin: 1rem 0;
  text-align: center;
}

.message-success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message-error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.divider {
  text-align: center;
  margin: 1.5rem 0;
  position: relative;
  color: #666;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #ddd;
}

.divider span {
  background: white;
  padding: 0 15px;
  position: relative;
}

.oauth-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.oauth-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 12px 20px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  font-size: 14px;
}

.oauth-icon {
  width: 18px;
  height: 18px;
}

.google-btn {
  background: #fff;
  color: #333;
  border: 1px solid #dadce0;
}

.google-btn:hover:not(:disabled) {
  background: #f8f9fa;
  box-shadow: 0 1px 2px 0 rgba(60, 64, 67, .3);
}

.github-btn {
  background: #333;
  color: white;
}

.github-btn:hover:not(:disabled) {
  background: #24292e;
}

.oauth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.auth-footer p {
  margin: 0;
  color: #666;
}

.auth-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
