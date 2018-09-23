package com.example.photostorage.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class ImageInfo {
    @Id
    private String id;
    private String fileId;
    private String title;
    private String description;
    private String fileName;

    public ImageInfo() {
    }

    public ImageInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public ImageInfo(String fileId, String fileName, String title, String description) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageInfo imageInfo = (ImageInfo) o;
        return Objects.equals(id, imageInfo.id) &&
                Objects.equals(fileId, imageInfo.fileId) &&
                Objects.equals(title, imageInfo.title) &&
                Objects.equals(description, imageInfo.description) &&
                Objects.equals(fileName, imageInfo.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileId, title, description, fileName);
    }
}
