package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class MateriModel implements Parcelable {

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
    private String documentId, title, thumbnailUrl, harga, courseId;
    private long dateCreated;

    public MateriModel() {
    }

    public MateriModel(String title, String thumbnailUrl, String harga, String courseId, long dateCreated) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.harga = harga;
        this.courseId = courseId;
        this.dateCreated = dateCreated;
    }

    protected MateriModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        thumbnailUrl = in.readString();
        harga = in.readString();
        courseId = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(thumbnailUrl);
        dest.writeString(harga);
        dest.writeString(courseId);
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getHarga() {
        return harga;
    }

    public String getCourseId() {
        return courseId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
