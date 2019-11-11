package com.example.aldy.difacademy.Model;

public class VideoFreeModel {
    private String thumbnailUrl, videoYoutubeId, title, description, tagId;

    public VideoFreeModel(){}

    public VideoFreeModel(String thumbnailUrl, String videoYoutubeId, String title, String description, String tagId) {
        this.thumbnailUrl = thumbnailUrl;
        this.videoYoutubeId = videoYoutubeId;
        this.title = title;
        this.description = description;
        this.tagId = tagId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoYoutubeId() {
        return videoYoutubeId;
    }

    public void setVideoYoutubeId(String videoYoutubeId) {
        this.videoYoutubeId = videoYoutubeId;
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

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
