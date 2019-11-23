package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class GraduationModel implements Parcelable {
    private String graduationId, userId, kelasId;
    private long dateCreated;
    private boolean isSeen;

    public GraduationModel() {
    }

    public GraduationModel(String userId, String kelasId, long dateCreated, boolean isSeen) {
        this.userId = userId;
        this.kelasId = kelasId;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
    }

    protected GraduationModel(Parcel in) {
        graduationId = in.readString();
        userId = in.readString();
        kelasId = in.readString();
        dateCreated = in.readLong();
        isSeen = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(graduationId);
        dest.writeString(userId);
        dest.writeString(kelasId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GraduationModel> CREATOR = new Creator<GraduationModel>() {
        @Override
        public GraduationModel createFromParcel(Parcel in) {
            return new GraduationModel(in);
        }

        @Override
        public GraduationModel[] newArray(int size) {
            return new GraduationModel[size];
        }
    };

    @Exclude
    public String getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(String graduationId) {
        this.graduationId = graduationId;
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

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
