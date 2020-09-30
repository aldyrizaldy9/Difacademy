package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class GraduationMateriBlendedModel implements Parcelable {
    public static final Creator<GraduationMateriBlendedModel> CREATOR = new Creator<GraduationMateriBlendedModel>() {
        @Override
        public GraduationMateriBlendedModel createFromParcel(Parcel in) {
            return new GraduationMateriBlendedModel(in);
        }

        @Override
        public GraduationMateriBlendedModel[] newArray(int size) {
            return new GraduationMateriBlendedModel[size];
        }
    };
    private String userId, namaUser, email, noWa, materiId, namaMateri, documentId;
    private long dateCreated;
    private boolean isSeen, isDone;

    public GraduationMateriBlendedModel() {
    }

    public GraduationMateriBlendedModel(String userId,
                                        String namaUser,
                                        String email,
                                        String noWa,
                                        String materiId,
                                        String namaMateri,
                                        long dateCreated,
                                        boolean isSeen,
                                        boolean isDone) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.materiId = materiId;
        this.namaMateri = namaMateri;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
        this.isDone = isDone;
    }

    protected GraduationMateriBlendedModel(Parcel in) {
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        materiId = in.readString();
        namaMateri = in.readString();
        documentId = in.readString();
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
        dest.writeString(materiId);
        dest.writeString(namaMateri);
        dest.writeString(documentId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isDone ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public String getMateriId() {
        return materiId;
    }

    public String getNamaMateri() {
        return namaMateri;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
