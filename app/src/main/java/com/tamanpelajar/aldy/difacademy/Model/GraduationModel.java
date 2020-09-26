package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class GraduationModel implements Parcelable {
    private String documentId, userId, namaUser, email, noWa, materiId, namaMateri;
    private boolean isSeen, isDone;
    private long dateCreated;

    public GraduationModel() {
    }

    public GraduationModel(String userId, String namaUser, String email, String noWa, String materiId, String namaMateri, boolean isSeen, boolean isDone, long dateCreated) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.materiId = materiId;
        this.namaMateri = namaMateri;
        this.isSeen = isSeen;
        this.isDone = isDone;
        this.dateCreated = dateCreated;
    }

    protected GraduationModel(Parcel in) {
        documentId = in.readString();
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        materiId = in.readString();
        namaMateri = in.readString();
        isSeen = in.readByte() != 0;
        isDone = in.readByte() != 0;
        dateCreated = in.readLong();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(userId);
        dest.writeString(namaUser);
        dest.writeString(email);
        dest.writeString(noWa);
        dest.writeString(materiId);
        dest.writeString(namaMateri);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeLong(dateCreated);
    }

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

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoWa() {
        return noWa;
    }

    public void setNoWa(String noWa) {
        this.noWa = noWa;
    }

    public String getMateriId() {
        return materiId;
    }

    public void setMateriId(String materiId) {
        this.materiId = materiId;
    }

    public String getNamaMateri() {
        return namaMateri;
    }

    public void setNamaMateri(String namaMateri) {
        this.namaMateri = namaMateri;
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

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
