package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class OnlineCourseModel implements Parcelable {
    public static final Creator<OnlineCourseModel> CREATOR = new Creator<OnlineCourseModel>() {
        @Override
        public OnlineCourseModel createFromParcel(Parcel in) {
            return new OnlineCourseModel(in);
        }

        @Override
        public OnlineCourseModel[] newArray(int size) {
            return new OnlineCourseModel[size];
        }
    };
    String documentId, title, description, tagId, tag, thumbnailUrl, gDriveUrl, harga;
    long dateCreated;

    public OnlineCourseModel() {
    }

    public OnlineCourseModel(String title, String description, String tagId, String tag, String thumbnailUrl, String gDriveUrl, String harga, long dateCreated) {
        this.title = title;
        this.description = description;
        this.tagId = tagId;
        this.tag = tag;
        this.thumbnailUrl = thumbnailUrl;
        this.gDriveUrl = gDriveUrl;
        this.harga = harga;
        this.dateCreated = dateCreated;
    }

    protected OnlineCourseModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        description = in.readString();
        tagId = in.readString();
        tag = in.readString();
        thumbnailUrl = in.readString();
        gDriveUrl = in.readString();
        harga = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(tagId);
        dest.writeString(tag);
        dest.writeString(thumbnailUrl);
        dest.writeString(gDriveUrl);
        dest.writeString(harga);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getgDriveUrl() {
        return gDriveUrl;
    }

    public void setgDriveUrl(String gDriveUrl) {
        this.gDriveUrl = gDriveUrl;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public long getDateCreated() {
        return dateCreated;
    }
}
