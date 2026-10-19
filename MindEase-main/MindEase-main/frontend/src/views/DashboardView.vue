<script setup>
import { ref, onMounted } from 'vue';
import api from '../api'; // Use our new api.js
import BreathingExercise from '../components/BreathingExercise.vue';
import RiddleGame from '../components/RiddleGame.vue';
import JournalEntry from '../components/JournalEntry.vue';
import JournalView from '../components/JournalView.vue';
import { RouterLink } from 'vue-router';

// --- State Variables ---
const userName = ref('there'); // ** DEFAULT NAME **
const selectedMood = ref('');
const moodText = ref('');
const showBreathingExercise = ref(false);
const showRiddleGame = ref(false);
const showJournal = ref(false);
const showJournalView = ref(false);
const selectedJournalEntryIndex = ref(0);
const moodHistory = ref([]);
const journalHistory = ref([]);
const currentTip = ref(null);

// --- Functions ---
const selectMood = (mood) => {
  selectedMood.value = mood;
  currentTip.value = null;
};

const fetchTip = async (moodType) => {
  if (!moodType) return;
  try {
    const response = await api.get(`/tips/${moodType}`);
    currentTip.value = response.data;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      currentTip.value = { tipText: "Keep exploring your feelings. You're doing great!", visualIdentifier: null };
    } else {
      console.error('Failed to fetch tip:', error);
      currentTip.value = null;
    }
  }
};

const submitMood = async () => {
  const moodToSave = selectedMood.value || 'Neutral';
  if (!moodToSave && !moodText.value.trim()) {
    alert('Please select a mood or write something first.');
    return;
  }
  try {
    const response = await api.post('/moods', {
      moodType: moodToSave,
      moodText: moodText.value.trim()
    });
    alert('Mood saved successfully!');
    fetchTip(moodToSave);
    selectedMood.value = '';
    moodText.value = '';
    fetchHistory();
  } catch (error) {
    console.error('Failed to save mood:', error);
    alert('Sorry, there was an error saving your mood.');
  }
};

const fetchHistory = async () => {
  try {
    const moodResponse = await api.get('/moods');
    moodHistory.value = moodResponse.data;
    const journalResponse = await api.get('/journals');
    journalHistory.value = journalResponse.data;
  } catch (error) {
    console.error('Failed to fetch history:', error);
    alert('Could not load history. Please check the connection.');
  }
};

// ... inside <script setup>

onMounted(() => {
  // ** FIX: Read the user's name from localStorage **
  const storedUser = localStorage.getItem('user');
  if (storedUser) {
    const userObject = JSON.parse(storedUser);
    userName.value = userObject.displayName; // Set dynamic name
  }
  fetchHistory();
});
// ...
const formatTimestamp = (timestamp) => {
  if (!timestamp) return '';
  return new Date(timestamp).toLocaleString('en-US', {
    month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit', hour12: true
  });
};

const deleteMood = async (moodId) => {
  if (confirm('Are you sure you want to delete this mood entry?')) {
    try {
      await api.delete(`/moods/${moodId}`);
      alert('Mood entry deleted.');
      fetchHistory();
    } catch (error) {
      console.error('Failed to delete mood:', error);
      alert('Could not delete mood entry.');
    }
  }
};

const deleteJournal = async (journalId) => {
  try {
    await api.delete(`/journals/${journalId}`);
    alert('Journal entry deleted.');
    fetchHistory();
    showJournalView.value = false;
  } catch (error) {
    console.error('Failed to delete journal:', error);
    alert('Could not delete journal entry.');
  }
};

const openJournalView = (journal, index) => {
  selectedJournalEntryIndex.value = index;
  showJournalView.value = true;
};

const handleJournalUpdate = () => {
  // This is called when an entry is successfully updated in JournalView
  // We refresh history to reflect the change
  fetchHistory();
  showJournalView.value = false;
};
</script>

<template>
  <div class="dashboard-layout">
    <nav class="sidebar">
      <div class="sidebar-header">
        <img src="/logo.png" alt="MindEase Logo" class="logo" />
      </div>
      <div class="sidebar-links">
        <RouterLink to="/dashboard" class="sidebar-link active">Dashboard</RouterLink>
        <div class="history-section">
          <h4>Mood History</h4>
          <ul>
            <li v-for="mood in moodHistory" :key="mood.id" class="history-item">
              <div>
                {{ mood.moodType || 'Entry' }} - <small>{{ formatTimestamp(mood.timestamp) }}</small>
                <p v-if="mood.moodText">{{ mood.moodText }}</p>
              </div>
              <button @click="deleteMood(mood.id)" class="delete-btn">🗑️</button>
            </li>
            <li v-if="!moodHistory || moodHistory.length === 0"><small>No moods recorded yet.</small></li>
          </ul>
        </div>
        <div class="history-section">
          <h4>Journal History</h4>
          <ul>
            <li v-for="(journal, index) in journalHistory" :key="journal.id" class="history-item clickable" @click="openJournalView(journal, index)">
              <div>
                Entry - <small>{{ formatTimestamp(journal.timestamp) }}</small>
                <p>{{ journal.entryText ? journal.entryText.substring(0, 30) + '...' : 'Empty Entry' }}</p>
              </div>
            </li>
             <li v-if="!journalHistory || journalHistory.length === 0"><small>No journal entries yet.</small></li>
          </ul>
        </div>
        <RouterLink to="/settings" class="sidebar-link">Settings</RouterLink>
      </div>
    </nav>

    <main class="main-content">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="content-wrapper">
        <header class="main-header">
          <h1>Hello, {{ userName }}</h1>
          <p>How are you feeling today?</p>
        </header>
        <div class="mood-card">
          <div class="mood-selector">
            <div @click="selectMood('Happy')" :class="['mood-emoji', { selected: selectedMood === 'Happy' }]">😊</div>
            <div @click="selectMood('Sad')" :class="['mood-emoji', { selected: selectedMood === 'Sad' }]">😔</div>
            <div @click="selectMood('Angry')" :class="['mood-emoji', { selected: selectedMood === 'Angry' }]">😠</div>
            <div @click="selectMood('Stressed')" :class="['mood-emoji', { selected: selectedMood === 'Stressed' }]">😰</div>
            <div @click="selectMood('Excited')" :class="['mood-emoji', { selected: selectedMood === 'Excited' }]">🤩</div>
          </div>
          <textarea
            v-model="moodText"
            class="mood-textarea"
            placeholder="Care to share more about your feelings? (Optional)"
          ></textarea>
          <button @click="submitMood" class="save-mood-button">Save Mood</button>
        </div>

        <div v-if="currentTip" class="tip-card">
          <span class="tip-icon">💡</span>
          <p>{{ currentTip.tipText }}</p>
        </div>

        <div class="features-grid">
          <div @click="showBreathingExercise = true" class="feature-card">💨<span>Breathing Exercise</span></div>
          <div @click="showRiddleGame = true" class="feature-card">🧩<span>Play a Riddle</span></div>
          <div @click="showJournal = true" class="feature-card">📝<span>Open Journal</span></div>
        </div>
      </div>
    </main>

    <BreathingExercise v-if="showBreathingExercise" @close="showBreathingExercise = false" />
    <RiddleGame v-if="showRiddleGame" @close="showRiddleGame = false" />
    <JournalEntry v-if="showJournal" @close="showJournal = false" @entrySaved="fetchHistory" />
    <JournalView
      v-if="showJournalView"
      :allJournalEntries="journalHistory"
      :startIndex="selectedJournalEntryIndex"
      @close="showJournalView = false"
      @entryUpdated="handleJournalUpdate"
      @deleteEntry="deleteJournal"
    />
  </div>
</template>

<style scoped>
/* All styles remain the same */
.dashboard-layout { display: flex; min-height: 100vh; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
.sidebar { width: 220px; background-color: #ffffff; border-right: 1px solid #e9ecef; display: flex; flex-direction: column; padding: 20px; z-index: 10; overflow-y: auto;}
.sidebar-header { text-align: center; padding: 10px 0; margin-bottom: 20px; }
.logo { width: 80px; }
.sidebar-links { display: flex; flex-direction: column; gap: 8px; }
.sidebar-link { padding: 10px 12px; border-radius: 8px; text-decoration: none; color: #495057; font-weight: 500; transition: background-color 0.2s ease; font-size: 15px; }
.sidebar-link:hover { background-color: #f1f3f5; }
.sidebar-link.active { background-color: #e6fffa; color: #38b2ac; font-weight: 600; }
.history-section { margin-top: 20px; padding-top: 15px; border-top: 1px solid #e9ecef; }
.history-section h4 { font-size: 14px; color: #6c757d; margin-bottom: 10px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
.history-section ul { list-style: none; padding: 0; margin: 0; }
.history-item { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; padding: 5px; border-radius: 4px; }
.history-item.clickable { cursor: pointer; transition: background-color 0.2s ease; }
.history-item.clickable:hover { background-color: #f8f9fa; }
.history-item > div { flex-grow: 1; margin-right: 10px; overflow: hidden; }
.history-item small { color: #adb5bd; display: block; margin-top: 2px; font-size: 11px; }
.history-item p { font-size: 12px; color: #6c757d; margin: 4px 0 0 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.delete-btn { background: none; border: none; color: #adb5bd; cursor: pointer; font-size: 16px; padding: 0 5px; opacity: 0.6; transition: opacity 0.2s ease, color 0.2s ease; flex-shrink: 0; }
.delete-btn:hover { opacity: 1; color: #dc3545; }
.main-content { flex: 1; position: relative; overflow: hidden; background: linear-gradient(135deg, #F0F7F7, #E6F0FF); padding: 40px 50px; }
.content-wrapper { position: relative; z-index: 5; }
.shape { position: absolute; border-radius: 50%; opacity: 0.5; filter: blur(80px); }
.shape-1 { width: 400px; height: 400px; background-color: #A0D4D4; top: -100px; right: -150px; }
.shape-2 { width: 300px; height: 300px; background-color: #a0c4ff; bottom: -80px; left: -100px; }
.main-header { text-align: center; }
.main-header h1 { font-size: 32px; font-weight: 700; color: #212529; margin: 0; }
.main-header p { font-size: 18px; color: #6c757d; margin-top: 4px; }
.mood-card { background-color: rgba(255, 255, 255, 0.8); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.2); border-radius: 16px; padding: 25px; margin-top: 30px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); }
.mood-selector { display: flex; justify-content: center; gap: 25px; margin-bottom: 20px; }
.mood-emoji { font-size: 40px; cursor: pointer; padding: 10px; border-radius: 50%; transition: transform 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease; }
.mood-emoji:hover { transform: scale(1.15); background-color: rgba(230, 255, 250, 0.7); }
.mood-emoji.selected { background-color: #e6fffa; box-shadow: 0 0 0 3px #38b2ac; transform: scale(1.1); }
.mood-textarea { width: 100%; height: 80px; border: 1px solid #dee2e6; border-radius: 8px; padding: 12px; font-size: 16px; resize: vertical; box-sizing: border-box; background-color: #f8f9fa; margin-bottom: 20px; }
.save-mood-button { width: 100%; padding: 14px; border: none; border-radius: 10px; background-color: #38b2ac; color: white; font-size: 16px; font-weight: 600; cursor: pointer; transition: background-color 0.3s; }
.save-mood-button:hover { background-color: #2D5E5E; }
.tip-card { margin-top: 25px; padding: 15px 20px; background-color: rgba(255, 255, 255, 0.9); border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); display: flex; align-items: center; gap: 15px; animation: slideUp 0.5s ease-out; }
.tip-icon { font-size: 24px; flex-shrink: 0; }
.tip-card p { margin: 0; font-size: 16px; color: #343a40; }
@keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
.features-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 25px; margin-top: 40px; }
.feature-card { background-color: rgba(255, 255, 255, 0.8); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.2); padding: 25px; border-radius: 16px; display: flex; align-items: center; gap: 15px; font-size: 16px; font-weight: 500; color: #343a40; cursor: pointer; transition: transform 0.2s ease, box-shadow 0.2s ease; }
.feature-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1); }
.feature-card span { font-size: 18px; }
</style>