package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class GraduationModel implements Parcelable {
    private String userId, namaUser, email, noWa, blendedCourseId, namaKelas, graduationId;
    private long dateCreated;
    private boolean isSeen, isDone;

    public GraduationModel() {
    }

    public GraduationModel(String userId,
                           String namaUser,
                           String email,
                           String noWa,
                           String blendedCourseId,
                           String namaKelas,
                           long dateCreated,
                           boolean isSeen,
                           boolean isDone) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.blendedCourseId = blendedCourseId;
        this.namaKelas = namaKelas;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
        this.isDone = isDone;
    }

    protected GraduationModel(Parcel in) {
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        blendedCourseId = in.readString();
        namaKelas = in.readString();
        graduationId = in.readString();
        dateCreated = in.readLong();
        isSeen = in.readByte() != 0;
        isDone = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(namaUser);
        dest.writeString(email);
        dest.writeString(noWa);
        dest.writeString(blendedCourseId);
        dest.writeString(namaKelas);
        dest.writeString(graduationId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isDone ? 1 : 0));
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

    public String getUserId() {
        return userId;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public String getEmail() {
        return email;
    }

    public String getNoWa() {
        return noWa;
    }

    public String getBlendedCourseId() {
        return blendedCourseId;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    @Exclude
    public String getGraduationId() {
        return graduationId;
    }

    public void setGraduationId(String graduationId) {
        this.graduationId = graduationId;
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

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
