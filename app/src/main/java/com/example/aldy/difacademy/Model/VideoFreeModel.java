package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class VideoFreeModel implements Parcelable {
    private String thumbnailUrl, videoYoutubeId, title, description, tagId, documentId, tag;
    private long dateCreated;

    public VideoFreeModel() {
    }

    public VideoFreeModel(String thumbnailUrl, String videoYoutubeId, String title, String description, String tagId, String tag, long dateCreated) {
        this.thumbnailUrl = thumbnailUrl;
        this.videoYoutubeId = videoYoutubeId;
        this.title = title;
        this.description = description;
        this.tagId = tagId;
        this.tag = tag;
        this.dateCreated = dateCreated;
    }


    protected VideoFreeModel(Parcel in) {
        thumbnailUrl = in.readString();
        videoYoutubeId = in.readString();
        title = in.readString();
        description = in.readString();
        tagId = in.readString();
        documentId = in.readString();
        tag = in.readString();
        dateCreated = in.readLong();
    }

    public static final Creator<VideoFreeModel> CREATOR = new Creator<VideoFreeModel>() {
        @Override
        public VideoFreeModel createFromParcel(Parcel in) {
            return new VideoFreeModel(in);
        }

        @Override
        public VideoFreeModel[] newArray(int size) {
            return new VideoFreeModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(thumbnailUrl);
        parcel.writeString(videoYoutubeId);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(tagId);
        parcel.writeString(documentId);
        parcel.writeString(tag);
        parcel.writeLong(dateCreated);
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
