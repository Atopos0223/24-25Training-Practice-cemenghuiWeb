// src/stores/meeting.ts
import { defineStore } from 'pinia'

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: []
  }),
  actions: {
    addMeeting(meeting) {
      this.meetings.push(meeting)
    },
    // 你可以根据需要添加更多 actions
  }
})