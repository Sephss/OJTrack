package com.example.ojtrack.models;

public class NewNoteModel {
    int noteId;
    String title, description, category, isPinned, updatedAt, timestamp;
    public NewNoteModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(String isPinned) {
        this.isPinned = isPinned;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public NewNoteModel(int noteId, String title, String description, String category, String isPinned, String updatedAt, String timestamp) {
       this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.isPinned = isPinned;
        this.updatedAt = updatedAt;
        this.timestamp = timestamp;
    }
}
