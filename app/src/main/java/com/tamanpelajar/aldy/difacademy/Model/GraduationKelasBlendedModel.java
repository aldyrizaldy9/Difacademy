package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class GraduationKelasBlendedModel implements Parcelable {
    public static final Creator<GraduationKelasBlendedModel> CREATOR = new Creator<GraduationKelasBlendedModel>() {
        @Override
        public GraduationKelasBlendedModel createFromParcel(Parcel in) {
            return new GraduationKelasBlendedModel(in);
        }

        @Override
        public GraduationKelasBlendedModel[] newArray(int size) {
            return new GraduationKelasBlendedModel[size];
        }
    };
    private String userId, namaUser, email, noWa, kelasId, namaKelas, documentId;
    private long dateCreated;
    private boolean isSeen, isDone;

    public GraduationKelasBlendedModel() {
    }

    public GraduationKelasBlendedModel(String userId,
                                       String namaUser,
                                       String email,
                                       String noWa,
                                       String kelasId,
                                       String namaKelas,
                                       long dateCreated,
                                       boolean isSeen,
                                       boolean isDone) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.kelasId = kelasId;
        this.namaKelas = namaKelas;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
        this.isDone = isDone;
    }

    protected GraduationKelasBlendedModel(Parcel in) {
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        kelasId = in.readString();
        namaKelas = in.readString();
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
        dest.writeString(kelasId);
        dest.writeString(namaKelas);
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

    public String getKelasId() {
        return kelasId;
    }

    public String getNamaKelas() {
        return namaKelas;
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
