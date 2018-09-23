package com.example.photostorage.entity;

import org.springframework.http.codec.multipart.FilePart;

public class FileForm {
    private FilePart file;
    private String title;
    private String description;

    public FileForm(FilePart file, String title, String description) {
        this.file = file;
        this.title = title;
        this.description = description;
    }

    public FileForm() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FilePart getFile() {
        return file;
    }

    public void setFile(FilePart file) {
        this.file = file;
    }
}
