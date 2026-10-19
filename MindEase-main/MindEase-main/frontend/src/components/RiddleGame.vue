<script setup>
import { ref, onMounted } from 'vue';
import api from '../api'; // Use our api.js file

// Refs to hold the fetched riddle and user's input
const currentRiddle = ref({ question: 'Loading riddle...', answer: '' });
const userAnswer = ref('');
const feedback = ref('');

// Allows the component to send a 'close' message
const emit = defineEmits(['close']);

// Function to fetch a random riddle from the backend
const fetchRiddle = async () => {
  feedback.value = '';
  userAnswer.value = '';
  currentRiddle.value.question = 'Loading...';
  try {
    const response = await api.get('/riddles/random');
    currentRiddle.value = response.data;
  } catch (error) {
    console.error('Failed to fetch riddle:', error);
    currentRiddle.value.question = 'Could not load riddle. Please try again.';
  }
};

// Fetch a riddle as soon as the component is mounted
onMounted(() => {
  fetchRiddle();
});

// --- UPDATED checkAnswer Function ---
const checkAnswer = () => {
  if (!currentRiddle.value.answer) return;

  // Trim BOTH the correct answer and the user's answer
  const correctAnswer = currentRiddle.value.answer.toLowerCase().trim();
  const userInput = userAnswer.value.toLowerCase().trim();

  if (userInput === correctAnswer) {
    feedback.value = 'Correct! ✅';
  } else {
    // Show the correct answer
    feedback.value = `Not quite. The answer was: ${currentRiddle.value.answer}`;
  }
};
// --- END UPDATED Function ---

const nextRiddle = () => {
  fetchRiddle();
};
</script>

<template>
  <div class="riddle-overlay" @click.self="emit('close')">
    <div class="riddle-card">
      <button @click="emit('close')" class="close-btn">×</button>
      <h3>Mind Diversion 🧩</h3>
      <p class="riddle-question">{{ currentRiddle.question }}</p>
      
      <div class="answer-section">
        <input 
          type="text" 
          v-model="userAnswer" 
          placeholder="Your answer..." 
          @keyup.enter="checkAnswer"
          :disabled="!currentRiddle.answer"
          class="answer-input"
        />
        <button 
          @click="checkAnswer" 
          :disabled="!currentRiddle.answer"
          class="submit-button"
        >Submit</button>
      </div>

      <p class="feedback">{{ feedback }}</p>

      <button @click="nextRiddle" class="next-btn">Next Riddle →</button>
    </div>
  </div>
</template>

<style scoped>
/* Styles remain the same */
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600&display=swap');
.riddle-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background-color: rgba(0, 0, 0, 0.6); backdrop-filter: blur(8px);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
  animation: fadeIn 0.3s ease-out;
  font-family: 'Montserrat', sans-serif;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.riddle-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 35px 45px;
  border-radius: 20px;
  width: 90%;
  max-width: 550px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
  text-align: center;
  position: relative;
  transform: scale(0.95);
  animation: scaleIn 0.3s ease-out forwards;
}
@keyframes scaleIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }
.close-btn {
  position: absolute; top: 15px; right: 20px; font-size: 28px;
  color: #999; background: none; border: none; cursor: pointer;
  transition: color 0.2s ease;
}
.close-btn:hover { color: #555; }
h3 {
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin-top: 0;
  margin-bottom: 25px;
}
.riddle-question {
  font-size: 18px;
  color: #444;
  min-height: 70px;
  line-height: 1.6;
}
.answer-section {
  display: flex;
  gap: 12px;
  margin-top: 25px;
}
.answer-input {
  flex-grow: 1; padding: 14px; border: 1px solid #ccc;
  border-radius: 10px; font-size: 16px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}
.answer-input:focus {
  outline: none;
  border-color: #38b2ac;
  box-shadow: 0 0 0 3px rgba(56, 178, 172, 0.2);
}
.answer-input:disabled { background-color: #f5f5f5; }
.submit-button {
  padding: 14px 25px; border: none; border-radius: 10px;
  background-color: #38b2ac;
  color: white; font-size: 16px; font-weight: 500; cursor: pointer;
  transition: background-color 0.3s ease;
}
.submit-button:hover { background-color: #2D5E5E; }
.submit-button:disabled { background-color: #ccc; }
.feedback {
  margin-top: 20px; font-size: 16px; font-weight: 500;
  min-height: 24px; color: #38b2ac;
}
.next-btn {
  margin-top: 15px; background: none; border: none;
  color: #777; cursor: pointer; font-size: 15px;
  transition: color 0.2s ease;
}
.next-btn:hover { color: #333; }
</style>