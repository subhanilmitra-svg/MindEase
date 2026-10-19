<script setup>
import { ref, onMounted } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import api from '../api'; // Use our central api.js file

const displayName = ref('');
const user = ref(null);
const router = useRouter(); 

// 1. Load user's current display name when page loads
onMounted(() => {
  const storedUser = localStorage.getItem('user');
  if (storedUser) {
    user.value = JSON.parse(storedUser);
    displayName.value = user.value.displayName;
  }
});

// 2. Connect the "Save Changes" button
const handleSave = async () => {
  if (!displayName.value.trim()) {
    alert('Display Name cannot be empty.');
    return;
  }
  try {
    // Use 'api' which sends the token automatically
    const response = await api.put('/users/me/displayname', { 
      displayName: displayName.value 
    });

    // Update local storage with the new user info
    localStorage.setItem('user', JSON.stringify(response.data));
    alert('Display Name updated successfully!');

  } catch (error) {
    console.error('Failed to update display name:', error);
    alert('Error saving changes.');
  }
};

// 3. Connect the "Delete Account" button
const handleDelete = async () => {
  if (confirm('Are you sure you want to permanently delete your account? This cannot be undone.')) {
    try {
      await api.delete('/users/me');
      alert('Account deleted successfully.');
      // Log user out
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      router.push('/login'); // Navigate to login
    } catch (error) {
      console.error('Failed to delete account:', error);
      alert('Error deleting account.');
    }
  }
};

// ** NEW: Logout Function **
const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    router.push('/login');
};
</script>

<template>
  <div class="settings-layout">
    <nav class="sidebar">
      <div class="sidebar-header">
        <img src="/logo.png" alt="MindEase Logo" class="logo" />
      </div>
      <div class="sidebar-links">
        <RouterLink to="/dashboard" class="sidebar-link">Dashboard</RouterLink>
        <RouterLink to="/dashboard" class="sidebar-link">History</RouterLink>
        <RouterLink to="/dashboard" class="sidebar-link">Journal</RouterLink>
        <RouterLink to="/settings" class="sidebar-link active">Settings</RouterLink>
      </div>
    </nav>

    <main class="main-content">
      <div class="content-wrapper">
        <header class="main-header">
          <h1>Settings</h1>
          <p>Manage your account preferences.</p>
        </header>

        <div class="settings-card">
          <h3>Profile Settings</h3>
          <div class="input-group">
            <label for="displayName">Display Name</label>
            <input type="text" id="displayName" v-model="displayName" />
          </div>
          <button @click="handleSave" class="save-button">Save Changes</button>
        </div>

        <div class="settings-card danger-zone">
          <h3>Account Management</h3>
          <p>Permanently delete your account and all associated data.</p>
          
          <button @click="handleLogout" class="logout-button">Log Out</button>
          
          <button @click="handleDelete" class="delete-account-button">Delete Account</button>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* Styles remain the same */
.settings-layout { display: flex; min-height: 100vh; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
.sidebar { width: 220px; background-color: #ffffff; border-right: 1px solid #e9ecef; display: flex; flex-direction: column; padding: 20px; z-index: 10; }
.sidebar-header { text-align: center; padding: 10px 0; margin-bottom: 30px; }
.logo { width: 80px; }
.sidebar-links { display: flex; flex-direction: column; gap: 8px; }
.sidebar-link { padding: 12px 16px; border-radius: 8px; text-decoration: none; color: #495057; font-weight: 500; transition: background-color 0.2s ease; font-size: 15px; }
.sidebar-link:hover { background-color: #f1f3f5; }
.sidebar-link.active { background-color: #e6fffa; color: #38b2ac; font-weight: 600; }
.main-content { flex: 1; position: relative; overflow: hidden; background: linear-gradient(135deg, #F0F7F7, #E6F0FF); padding: 40px 50px; }
.content-wrapper { position: relative; z-index: 5; max-width: 800px; margin: 0 auto; }
.shape { position: absolute; border-radius: 50%; opacity: 0.5; filter: blur(80px); }
.shape-1 { width: 400px; height: 400px; background-color: #A0D4D4; top: -100px; right: -150px; }
.shape-2 { width: 300px; height: 300px; background-color: #a0c4ff; bottom: -80px; left: -100px; }
.main-header { text-align: center; margin-bottom: 40px; }
.main-header h1 { font-size: 32px; font-weight: 700; color: #212529; margin: 0; }
.main-header p { font-size: 18px; color: #6c757d; margin-top: 4px; }
.settings-card { background-color: rgba(255, 255, 255, 0.8); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.2); border-radius: 16px; padding: 30px; margin-bottom: 25px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); }
.settings-card h3 { margin-top: 0; font-size: 20px; font-weight: 600; color: #343a40; }
.input-group { display: flex; flex-direction: column; gap: 8px; margin-top: 20px; }
.input-group label { font-weight: 500; color: #495057; }
.input-group input { padding: 12px; border: 1px solid #dee2e6; border-radius: 8px; font-size: 16px; }
.save-button { margin-top: 20px; padding: 12px 25px; border: none; border-radius: 10px; background-color: #38b2ac; color: white; font-size: 16px; font-weight: 600; cursor: pointer; transition: background-color 0.3s; }
.save-button:hover { background-color: #2D5E5E; }
.danger-zone { border-color: #f5c6cb; }
.danger-zone h3 { color: #721c24; }
.danger-zone p { color: #721c24; font-size: 14px; }

/* NEW LOGOUT BUTTON STYLE */
.logout-button {
  margin-top: 10px;
  padding: 12px 25px;
  border: none;
  border-radius: 10px;
  background-color: #f0ad4e; /* Orange for Logout */
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right: 15px; /* Spacing between logout and delete */
}
.logout-button:hover {
  background-color: #eb9316;
}

.delete-account-button {
  margin-top: 10px;
  padding: 12px 25px;
  border: none;
  border-radius: 10px;
  background-color: #dc3545;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}
.delete-account-button:hover {
  background-color: #c82333;
}
</style>