package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GraduateModel implements Parcelable {
    String documentId, userId, kelasId;
    long dateCreated;
    boolean isSeen;

    public GraduateModel(){}

    public GraduateModel(String userId, String kelasId, long dateCreated, boolean isSeen) {
        this.userId = userId;
        this.kelasId = kelasId;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
    }

    protected GraduateModel(Parcel in) {
        documentId = in.readString();
        userId = in.readString();
        kelasId = in.readString();
        dateCreated = in.readLong();
        isSeen = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(userId);
        dest.writeString(kelasId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GraduateModel> CREATOR = new Creator<GraduateModel>() {
        @Override
        public GraduateModel createFromParcel(Parcel in) {
            return new GraduateModel(in);
        }

        @Override
        public GraduateModel[] newArray(int size) {
            return new GraduateModel[size];
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKelasId() {
        return kelasId;
    }

    public void setKelasId(String kelasId) {
        this.kelasId = kelasId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
