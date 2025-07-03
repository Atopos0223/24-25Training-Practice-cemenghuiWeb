// src/stores/meeting.ts
import { defineStore } from 'pinia'

interface Meeting {
  id?: number
  title: string
  description?: string
  startTime?: string
  endTime?: string
  location?: string
  [key: string]: any
}

export const useMeetingStore = defineStore('meeting', {
  state: () => ({
    meetings: [] as Meeting[]
  }),
  actions: {
    addMeeting(meeting: Meeting) {
      this.meetings.push(meeting)
    },
    // 你可以根据需要添加更多 actions
  }
})