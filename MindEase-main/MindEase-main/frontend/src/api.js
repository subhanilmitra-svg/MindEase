import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Your backend API base URL
});

// Request Interceptor: Automatically add the token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Handles token expiration (401/403)
api.interceptors.response.use(
  (response) => response, // Pass through successful responses
  (error) => {
    // Check for explicit 401 or 403 errors indicating a session issue
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      
      const tokenExists = localStorage.getItem('token');
      const isLoginRoute = window.location.pathname.startsWith('/login');

      // Only redirect if a token exists AND we are not trying to log in (to prevent loops)
      if (tokenExists && !isLoginRoute) {
          // Force logout and redirect
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          // Removed alert, as it pauses execution and is annoying
          window.location.href = '/login'; 
      }
    }
    return Promise.reject(error);
  }
);

export default api;