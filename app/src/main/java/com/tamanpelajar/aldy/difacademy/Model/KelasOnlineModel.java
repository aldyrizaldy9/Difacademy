package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class KelasOnlineModel implements Parcelable {
    public static final Creator<KelasOnlineModel> CREATOR = new Creator<KelasOnlineModel>() {
        @Override
        public KelasOnlineModel createFromParcel(Parcel in) {
            return new KelasOnlineModel(in);
        }

        @Override
        public KelasOnlineModel[] newArray(int size) {
            return new KelasOnlineModel[size];
        }
    };
    private String documentId, title, description, thumbnailUrl, googleDrive, tagId, tag;
    private long dateCreated;

    public KelasOnlineModel() {
    }

    public KelasOnlineModel(String title, String description, String thumbnailUrl, String googleDrive, String tagId, String tag, long dateCreated) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.googleDrive = googleDrive;
        this.tagId = tagId;
        this.tag = tag;
        this.dateCreated = dateCreated;
    }

    protected KelasOnlineModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        description = in.readString();
        thumbnailUrl = in.readString();
        googleDrive = in.readString();
        tagId = in.readString();
        tag = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(thumbnailUrl);
        dest.writeString(googleDrive);
        dest.writeString(tagId);
        dest.writeString(tag);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getGoogleDrive() {
        return googleDrive;
    }

    public void setGoogleDrive(String googleDrive) {
        this.googleDrive = googleDrive;
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
