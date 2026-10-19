<script setup>
import { ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import axios from 'axios'; // We use normal axios *only* for login/signup

const username = ref('');
const password = ref('');
const router = useRouter();

const handleLogin = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value,
    });

    // ** 1. SAVE THE TOKEN & USER DATA **
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data.user));

    console.log('Login successful, token saved.');
    router.push('/dashboard'); // 2. Redirect

  } catch (error) {
    console.error('Login failed:', error);
    alert('Login failed: ' + (error.response?.data || 'Invalid credentials'));
  }
};
</script>

<template>
  <div class="auth-page-container">
    <div class="auth-card">
      <div class="auth-sidebar">
        <img src="/logo.png" alt="MindEase Logo" class="sidebar-logo" />
        <div class="app-name">MindEase</div>
        <p class="sidebar-tagline">Relax, Reflect, Refresh</p>
      </div>
      <div class="auth-main-content">
        <h2>Welcome Back</h2>
        <form @submit.prevent="handleLogin">
          <div class="input-group">
            <label for="username">Username</label>
            <input type="text" id="username" v-model="username" required />
          </div>
          <div class="input-group">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="password" required />
          </div>
          <button type="submit" class="auth-button">Login</button>
        </form>
        <p class="auth-link">
          Don't have an account? <RouterLink to="/signup">Sign Up</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* All styles remain exactly the same as before */
.auth-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f7f7;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
.auth-card {
  display: flex;
  background: white;
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  width: 100%;
  max-width: 900px;
  min-height: 580px;
  border: 1px solid #e9ecef;
}
.auth-sidebar {
  width: 340px;
  background: #e6f0ff;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  border-right: 1px solid #e9ecef;
}
.sidebar-logo {
  width: 140px;
  height: auto;
  margin-bottom: 15px;
}
.app-name {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #2d5e5e;
}
.sidebar-tagline {
  font-size: 18px;
  color: #6c757d;
}
.auth-main-content {
  flex-grow: 1;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
h2 {
  font-size: 34px;
  color: #333;
  margin-bottom: 35px;
  font-weight: 700;
}
.input-group {
  margin-bottom: 25px;
  width: 100%;
  max-width: 320px;
  text-align: left;
}
.input-group label {
  display: block;
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
  font-weight: 500;
}
.input-group input {
  width: 100%;
  padding: 14px 18px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 16px;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}
.input-group input:focus {
  outline: none;
  border-color: #4fd1c5;
  box-shadow: 0 0 0 3px rgba(79, 209, 197, 0.2);
}
.auth-button {
  width: 100%;
  max-width: 320px;
  padding: 16px;
  border: none;
  border-radius: 10px;
  background: #2d5e5e;
  color: white;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
}
.auth-button:hover {
  transform: translateY(-2px);
  background: #38b2ac;
}
.auth-link {
  margin-top: 30px;
  font-size: 15px;
  color: #777;
}
.auth-link a {
  color: #38b2ac;
  text-decoration: none;
  font-weight: 600;
}
</style>
