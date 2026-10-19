<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const displayName = ref('')
const username = ref('')
const password = ref('')
const confirmPassword = ref('')

const router = useRouter()

// Make sure the 'async' keyword is here
const handleSignup = async () => {
  if (password.value !== confirmPassword.value) {
    alert('Passwords do not match!')
    return
  }

  try {
    // The 'await' will now be correct
    const response = await axios.post('http://localhost:8080/api/auth/signup', {
      displayName: displayName.value,
      username: username.value,
      password: password.value,
    })

    alert('User registered successfully! Please log in.')
    router.push('/login')
  } catch (error) {
    console.error('Signup failed:', error)
    alert('Signup failed: ' + (error.response?.data || error.message))
  }
}
</script>

<template>
  <div class="signup-container">
    <div class="signup-card">
      <h1>Create Your Account</h1>
      <p class="subtitle">Join MindEase to start your journey.</p>

      <form @submit.prevent="handleSignup">
        <div class="input-group">
          <label for="displayName">Display Name</label>
          <input type="text" id="displayName" v-model="displayName" required />
        </div>
        <div class="input-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <div class="input-group">
          <label for="confirmPassword">Confirm Password</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" required />
        </div>
        <button type="submit" class="signup-button">Create Account</button>
      </form>

      <p class="login-link">
        Already have an account? <RouterLink to="/login">Sign In</RouterLink>
      </p>
    </div>
  </div>
</template>

<style scoped>
/* All the styles from before remain the same */
.signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f7f7;
}
.signup-card {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
  animation: float 6s ease-in-out infinite;
}
@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0px);
  }
}
h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2d3748;
}
.subtitle {
  color: #6b7a99;
  margin-bottom: 30px;
}
.input-group {
  text-align: left;
  margin-bottom: 20px;
}
.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
}
.input-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #cbd5e0;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}
.signup-button {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 8px;
  background-color: #4fd1c5;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.signup-button:hover {
  background-color: #38b2ac;
}
.login-link {
  margin-top: 25px;
  font-size: 14px;
  color: #6b7a99;
}
.login-link a {
  color: #4fd1c5;
  font-weight: 600;
  text-decoration: none;
}
</style>
