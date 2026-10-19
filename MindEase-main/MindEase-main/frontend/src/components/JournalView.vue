<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import api from '../api'; // ** CORRECTED: Use api **

// --- Props & Emits ---
const props = defineProps({
  allJournalEntries: { type: Array, default: () => [] },
  startIndex: { type: Number, default: 0 }
});
const emit = defineEmits(['close', 'entryUpdated', 'deleteEntry']);

// --- State Variables ---
const entryText = ref('');
const currentEntryIndex = ref(0);
const isEditing = ref(false);
const addedStickers = ref([]);
let stickerCounter = 0;

// --- Computed Properties ---
const currentEntry = computed(() => {
  if (props.allJournalEntries && props.allJournalEntries.length > 0 && currentEntryIndex.value >= 0 && currentEntryIndex.value < props.allJournalEntries.length) {
    return props.allJournalEntries[currentEntryIndex.value];
  }
  return null;
});

const formattedDate = computed(() => {
  if (!currentEntry.value || !currentEntry.value.timestamp) return 'Invalid Date';
  return new Date(currentEntry.value.timestamp).toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
  });
});

// --- Lifecycle Hook ---
onMounted(() => {
  currentEntryIndex.value = props.startIndex;
});

// --- Watcher ---
watch(currentEntry, (newEntry) => {
  if (newEntry) {
    entryText.value = newEntry.entryText || '';
    try {
      // Load stickers from the entry
      addedStickers.value = newEntry.stickers ? JSON.parse(newEntry.stickers) : [];
      stickerCounter = addedStickers.value.reduce((max, s) => Math.max(max, s.id), 0) + 1;
    } catch (e) {
      addedStickers.value = [];
    }
    isEditing.value = false;
  } else {
    entryText.value = '';
    addedStickers.value = [];
    isEditing.value = false;
  }
}, { immediate: true });

// --- Functions ---
const handleSaveChanges = async () => {
  if (!entryText.value.trim() && addedStickers.value.length === 0) {
    alert("Cannot save an empty entry."); return;
  }
  if (!currentEntry.value || !currentEntry.value.id) return;

  try {
    // ** FIX: Use api.put which sends the token **
    const response = await api.put(`/journals/${currentEntry.value.id}`, {
      entryText: entryText.value,
      stickers: JSON.stringify(addedStickers.value)
    });
    console.log('Journal entry updated:', response.data);
    alert('Changes saved successfully!');
    emit('entryUpdated');
    isEditing.value = false;
  } catch (error) {
    console.error('Failed to update journal entry:', error);
    alert('Sorry, there was an error saving your changes.');
  }
};

const handleDelete = () => {
  if (!currentEntry.value || !currentEntry.value.id) return;
  if (confirm('Are you sure you want to delete this journal entry permanently?')) {
    emit('deleteEntry', currentEntry.value.id);
  }
};

const nextPage = () => {
  if (currentEntryIndex.value < props.allJournalEntries.length - 1) {
    currentEntryIndex.value++;
  }
};

const prevPage = () => {
  if (currentEntryIndex.value > 0) {
    currentEntryIndex.value--;
  }
};

// --- Sticker Logic (Simplified for brevity) ---
const addSticker = (emoji) => { /* ... */ };
const activeSticker = ref(null);
const offset = ref({ x: 0, y: 0 });
const startDrag = (sticker, event) => { /* ... */ };
const drag = (event) => { /* ... */ };
const stopDrag = () => { /* ... */ };
</script>

<template>
  <div class="journal-overlay" @click.self="emit('close')">
    <div class="journal-modal">
      <button @click="emit('close')" class="close-btn">×</button>
      <div class="journal-header">
        <h2>Journal Entry</h2>
        <p class="date">{{ formattedDate }}</p>
      </div>
      <div class="journal-body">
        <div class="journal-page">
          <textarea
            v-model="entryText"
            class="lined-textarea"
            placeholder="Start writing..."
            :readonly="!isEditing"
            rows="12"
          ></textarea>
           <div
            v-for="sticker in addedStickers"
            :key="sticker.id"
            class="placed-sticker"
            :class="{ 'is-editing': isEditing }"
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
          <button @click="prevPage" v-if="currentEntryIndex > 0" class="page-nav-btn prev">←</button>
          <div v-else class="nav-placeholder"></div>
          <div class="center-buttons">
              <button v-if="!isEditing && currentEntry && currentEntry.id" @click="isEditing = true" class="edit-btn">Edit</button>
              <button v-else-if="isEditing && currentEntry && currentEntry.id" @click="handleSaveChanges" class="save-btn">Save Changes</button>
              <button v-if="currentEntry && currentEntry.id" @click="handleDelete" class="delete-btn-footer">Delete</button>
          </div>
          <button @click="nextPage" v-if="currentEntryIndex < allJournalEntries.length - 1" class="page-nav-btn next">→</button>
          <div v-else class="nav-placeholder"></div>
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
.lined-textarea[readonly] { background-color: #fffef5; cursor: default; }
.sticker-panel { width: 80px; background-color: #f8f9fa; border-left: 1px solid #e9ecef; padding: 20px 10px; display: flex; flex-direction: column; align-items: center; gap: 15px; }
.sticker { font-size: 36px; cursor: pointer; transition: transform 0.2s ease; }
.sticker:hover { transform: scale(1.2); }
.placed-sticker { position: absolute; font-size: 48px; user-select: none; z-index: 10; cursor: default; }
.placed-sticker.is-editing { cursor: grab; }
.placed-sticker.is-editing:active { cursor: grabbing; }
.journal-footer { padding: 15px 30px; border-top: 1px solid #eee; background-color: #ffffff; display: flex; justify-content: space-between; align-items: center; }
.center-buttons { display: flex; gap: 15px; }
.save-btn, .edit-btn { padding: 8px 20px; border: none; border-radius: 8px; background-color: #38b2ac; color: white; font-size: 15px; font-weight: 500; cursor: pointer; transition: background-color 0.2s ease; }
.save-btn:hover, .edit-btn:hover { background-color: #2D5E5E; }
.page-nav-btn { background: none; border: none; color: #38b2ac; font-size: 24px; cursor: pointer; font-weight: bold; padding: 10px; width: 40px; flex-shrink: 0; }
.nav-placeholder { width: 40px; flex-shrink: 0; }
.delete-btn-footer { padding: 8px 15px; border: none; border-radius: 8px; background-color: transparent; color: #dc3545; font-size: 15px; font-weight: 500; cursor: pointer; transition: background-color 0.2s ease, color 0.2s ease; }
.delete-btn-footer:hover { background-color: #f8d7da; }
</style>