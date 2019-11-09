package com.example.aldy.difacademy.Model;

public class VideoFreeModel {
    private String videoFreeId, link, judul, tagId;

    public VideoFreeModel(String link, String judul, String tagId) {
        this.link = link;
        this.judul = judul;
        this.tagId = tagId;
    }

    public void setVideoFreeId(String videoFreeId) {
        this.videoFreeId = videoFreeId;
    }

    public String getVideoFreeId() {
        return videoFreeId;
    }

    public String getLink() {
        return link;
    }

    public String getJudul() {
        return judul;
    }

    public String getTagId() {
        return tagId;
    }
}
