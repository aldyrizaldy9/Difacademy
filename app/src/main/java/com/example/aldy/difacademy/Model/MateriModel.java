package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class MateriModel implements Parcelable {

    String documentId, title, thumbnailUrl;
    long dateCreated;

    public MateriModel() {
    }

    public MateriModel(String title, String thumbnailUrl, long dateCreated) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.dateCreated = dateCreated;
    }

    protected MateriModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        thumbnailUrl = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(thumbnailUrl);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MateriModel> CREATOR = new Creator<MateriModel>() {
        @Override
        public MateriModel createFromParcel(Parcel in) {
            return new MateriModel(in);
        }

        @Override
        public MateriModel[] newArray(int size) {
            return new MateriModel[size];
        }
    };

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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
