<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const text = ref('Get Ready...');
let timeoutId = null; // To keep track of our timer

const emit = defineEmits(['close']);

// 1. Create a new Audio object for our sound
const breathSound = new Audio('/breathing-sound.mp3');
breathSound.loop = true; // Make the sound loop

onMounted(() => {
  // 2. Start playing the sound when the component opens
  breathSound.play().catch(e => console.error("Sound playback failed:", e));

  // Define the breathing cycle
  const breathTexts = ['Inhale...', 'Hold...', 'Exhale...'];
  const timings = [4000, 4000, 6000]; // 4s Inhale, 4s Hold, 6s Exhale = 14s cycle
  let currentIndex = 0;

  // This function will run on a loop
  const cycleText = () => {
    // This logic ensures only one text is visible at a time
    text.value = breathTexts[currentIndex]; 
    const duration = timings[currentIndex];
    currentIndex = (currentIndex + 1) % breathTexts.length;
    // Set a timer to call itself again after the current phase duration
    timeoutId = setTimeout(cycleText, duration);
  };

  // Start the first cycle after a short delay
  setTimeout(cycleText, 1500);
});

// 3. This is important to stop the timer AND sound when you close the component
onUnmounted(() => {
  clearTimeout(timeoutId);
  breathSound.pause(); // Stop the sound
  breathSound.currentTime = 0; // Rewind to the start
});
</script>

<template>
  <div class="breathing-overlay">
    <button @click="emit('close')" class="close-btn">×</button>
    <div class="circle-container">
      <div class="circle"></div>
    </div>
    <!-- 
      FIX: Back to a single <p> tag.
      The JavaScript 'cycleText' function will update this one <p> tag,
      which makes it impossible for text to overlap.
    -->
    <p class="instruction-text">{{ text }}</p>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400&display=swap');

/* The animation for the circle is still perfectly synced */
@keyframes synced-breathe {
  0%,
  100% {
    transform: scale(0.8);
  }
  28.57% {
    transform: scale(1.2);
  } /* 4s / 14s */
  57.14% {
    transform: scale(1.2);
  } /* 8s / 14s */
}

.breathing-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #1a3a3a;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  z-index: 1000;
  animation: fadeIn 1s ease-in-out;
  font-family: 'Montserrat', sans-serif;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.circle-container {
  filter: drop-shadow(0 0 40px rgba(160, 212, 212, 0.6));
}

.circle {
  width: 250px;
  height: 250px;
  background: linear-gradient(135deg, #a0d4d4, #4fd1c5);
  border-radius: 50%;
  animation: synced-breathe 14s ease-in-out infinite;
}

.instruction-text {
  margin-top: 50px;
  font-size: 28px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 300;
  letter-spacing: 3px;
  text-transform: uppercase;
  /* Add a small transition for when the text content changes */
  transition: opacity 0.3s ease-in-out;
}

.close-btn {
  position: absolute;
  top: 30px;
  right: 40px;
  font-size: 40px;
  color: rgba(255, 255, 255, 0.5);
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.3s ease;
}

.close-btn:hover {
  color: rgba(255, 255, 255, 1);
}
</style>