package com.example.todocheck;

import android.net.Uri;

public class Task {
    private int id;
    private String title;
    private String description;
    private boolean completed;
    private int iconResource;
    private Uri imageUri;
    public Task(int id, String title, String description, boolean completed, int iconResource) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.iconResource = iconResource;
        this.imageUri = null;
    }

    public Task(int id, String title, String description, boolean completed, Uri imageUri) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.iconResource = R.drawable.ic_task_default;
        this.imageUri = imageUri;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public boolean hasCustomImage() {
        return imageUri != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}