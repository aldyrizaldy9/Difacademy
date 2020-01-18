package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoModel implements Parcelable {

    String title, description, videoUrl, documentId;
    long dateCreated;

    public VideoModel(){}

    public VideoModel(String title, String description, String videoUrl, long dateCreated) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.dateCreated = dateCreated;
    }

    protected VideoModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        documentId = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(videoUrl);
        dest.writeString(documentId);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
