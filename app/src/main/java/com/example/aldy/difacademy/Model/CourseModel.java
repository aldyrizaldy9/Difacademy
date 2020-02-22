package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class CourseModel implements Parcelable {
    private String documentId, title, description, thumbnailUrl, googleDrive, tagId, tag;
    private long dateCreated;

    public CourseModel() {
    }

    public CourseModel(String title, String description, String thumbnailUrl, String googleDrive, String tagId, String tag, long dateCreated) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.googleDrive = googleDrive;
        this.tagId = tagId;
        this.tag = tag;
        this.dateCreated = dateCreated;
    }

    protected CourseModel(Parcel in) {
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

    public static final Creator<CourseModel> CREATOR = new Creator<CourseModel>() {
        @Override
        public CourseModel createFromParcel(Parcel in) {
            return new CourseModel(in);
        }

        @Override
        public CourseModel[] newArray(int size) {
            return new CourseModel[size];
        }
    };

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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getGoogleDrive() {
        return googleDrive;
    }

    public String getTagId() {
        return tagId;
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
