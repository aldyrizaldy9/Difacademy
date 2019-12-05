package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class BlendedVideoModel implements Parcelable {
    public static final Creator<BlendedVideoModel> CREATOR = new Creator<BlendedVideoModel>() {
        @Override
        public BlendedVideoModel createFromParcel(Parcel in) {
            return new BlendedVideoModel(in);
        }

        @Override
        public BlendedVideoModel[] newArray(int size) {
            return new BlendedVideoModel[size];
        }
    };
    String title, description, videoUrl, documentId;
    long dateCreated;

    public BlendedVideoModel() {
    }

    public BlendedVideoModel(String title, String description, String videoUrl, long dateCreated) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.dateCreated = dateCreated;
    }

    protected BlendedVideoModel(Parcel in) {
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

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

}
