<script setup>
import { ref, computed } from 'vue';
import api from '../api'; // Use 'api' instead of 'axios'

const entryText = ref('');
const emit = defineEmits(['close', 'entrySaved']);

// --- Sticker Logic ---
const addedStickers = ref([]);
let stickerCounter = 0;
const addSticker = (emoji) => {
  addedStickers.value.push({ id: stickerCounter++, content: emoji, x: 50, y: 50 });
};
const activeSticker = ref(null);
const offset = ref({ x: 0, y: 0 });
const startDrag = (sticker, event) => {
  activeSticker.value = sticker;
  const stickerElement = event.target;
  const rect = stickerElement.getBoundingClientRect();
  offset.value.x = event.clientX - rect.left;
  offset.value.y = event.clientY - rect.top;
  window.addEventListener('mousemove', drag);
  window.addEventListener('mouseup', stopDrag, { once: true });
};
const drag = (event) => {
  if (activeSticker.value) {
    const journalPage = document.querySelector('.journal-page');
    if (!journalPage) return;
    const pageRect = journalPage.getBoundingClientRect();
    let newX = event.clientX - pageRect.left - offset.value.x;
    let newY = event.clientY - pageRect.top - offset.value.y;
    newX = Math.max(0, Math.min(newX, pageRect.width - 50));
    newY = Math.max(0, Math.min(newY, pageRect.height - 50));
    activeSticker.value.x = newX;
    activeSticker.value.y = newY;
  }
};
const stopDrag = () => {
  window.removeEventListener('mousemove', drag);
  activeSticker.value = null;
};
// --- End Sticker Logic ---

const currentDate = computed(() => {
  return new Date().toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
  });
});

const saveEntry = async () => {
  if (!entryText.value.trim() && addedStickers.value.length === 0) {
    alert("Please write something before saving.");
    return;
  }
  try {
    await api.post('/journals', {
  entryText: entryText.value,
  stickers: JSON.stringify(addedStickers.value)
  });
  alert('Entry saved successfully!');
  emit('entrySaved');
  emit('close');

  } catch (error) {
    console.error('Failed to save journal entry:', error);
    alert('Sorry, there was an error saving your entry.');
  }
};
</script>

<template>
  <div class="journal-overlay" @click.self="emit('close')">
    <div class="journal-modal">
      <button @click="emit('close')" class="close-btn">×</button>
      <div class="journal-header">
        <h2>Today's Thoughts</h2>
        <p class="date">{{ currentDate }}</p>
      </div>
      <div class="journal-body">
        <div class="journal-page">
          <textarea v-model="entryText" class="lined-textarea" placeholder="Start writing..." rows="12"></textarea>
          <div
            v-for="sticker in addedStickers"
            :key="sticker.id"
            class="placed-sticker"
            :style="{ left: sticker.x + 'px', top: sticker.y + 'px' }"
            @mousedown.prevent="startDrag(sticker, $event)"
          >
            {{ sticker.content }}
          </div>
        </div>
        <div class="sticker-panel">
          <div class="sticker" @click="addSticker('😊')">😊</div>
          <div class="sticker" @click="addSticker('⭐')">⭐</div>
          <div class="sticker" @click="addSticker('💡')">💡</div>
          <div class="sticker" @click="addSticker('❤️')">❤️</div>
          <div class="sticker" @click="addSticker('🎉')">🎉</div>
        </div>
      </div>
      <div class="journal-footer">
        <button @click="saveEntry" class="save-btn">Save Entry</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Styles remain the same */
@import url('https://fonts.googleapis.com/css2?family=Caveat&display=swap');
.journal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.journal-modal { display: flex; flex-direction: column; width: 90%; max-width: 800px; height: 85vh; background: #fff; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); position: relative; }
.close-btn { position: absolute; top: 15px; right: 15px; font-size: 28px; color: #aaa; background: none; border: none; cursor: pointer; z-index: 999; }
.journal-header { padding: 20px 30px; border-bottom: 1px solid #eee; }
.journal-header h2 { margin: 0; font-size: 22px; font-weight: 600; color: #333; }
.journal-header .date { margin: 5px 0 0 0; font-size: 14px; color: #888; }
.journal-body { display: flex; flex-grow: 1; overflow: hidden; }
.journal-page { flex-grow: 1; position: relative; }
.lined-textarea { width: 100%; height: 100%; border: none; padding: 8px 20px; font-family: 'Caveat', cursive; font-size: 24px; line-height: 48px; background-image: linear-gradient(#eee 1px, transparent 1px); background-size: 100% 48px; background-color: #fffaf0; resize: none; box-sizing: border-box; }
.lined-textarea:focus { outline: none; }
.sticker-panel { width: 80px; background-color: #f8f9fa; border-left: 1px solid #e9ecef; padding: 20px 10px; display: flex; flex-direction: column; align-items: center; gap: 15px; }
.journal-footer { padding: 15px 30px; border-top: 1px solid #eee; background-color: #ffffff; display: flex; justify-content: center; align-items: center; position: relative; }
.save-btn { padding: 10px 25px; border: none; border-radius: 8px; background-color: #38b2ac; color: white; font-size: 16px; font-weight: 500; cursor: pointer; transition: background-color 0.2s ease; }
.save-btn:hover { background-color: #2D5E5E; }
.sticker { font-size: 36px; cursor: pointer; transition: transform 0.2s ease; }
.sticker:hover { transform: scale(1.2); }
.placed-sticker { position: absolute; font-size: 48px; cursor: grab; user-select: none; z-index: 10; }
.placed-sticker:active { cursor: grabbing; }
</style>