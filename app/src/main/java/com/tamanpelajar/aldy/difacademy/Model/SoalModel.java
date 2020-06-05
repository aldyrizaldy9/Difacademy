package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class SoalModel implements Parcelable {
    public static final Creator<SoalModel> CREATOR = new Creator<SoalModel>() {
        @Override
        public SoalModel createFromParcel(Parcel in) {
            return new SoalModel(in);
        }

        @Override
        public SoalModel[] newArray(int size) {
            return new SoalModel[size];
        }
    };
    long dateCreated;
    String soal, jwbA, jwbB, jwbC, jwbD, jwbE, jawabanBenar, documentId;

    public SoalModel() {
    }

    public SoalModel(long dateCreated, String soal, String jwbA, String jwbB, String jwbC, String jwbD, String jwbE, String jawabanBenar) {
        this.dateCreated = dateCreated;
        this.soal = soal;
        this.jwbA = jwbA;
        this.jwbB = jwbB;
        this.jwbC = jwbC;
        this.jwbD = jwbD;
        this.jwbE = jwbE;
        this.jawabanBenar = jawabanBenar;
    }

    protected SoalModel(Parcel in) {
        dateCreated = in.readLong();
        soal = in.readString();
        jwbA = in.readString();
        jwbB = in.readString();
        jwbC = in.readString();
        jwbD = in.readString();
        jwbE = in.readString();
        jawabanBenar = in.readString();
        documentId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dateCreated);
        dest.writeString(soal);
        dest.writeString(jwbA);
        dest.writeString(jwbB);
        dest.writeString(jwbC);
        dest.writeString(jwbD);
        dest.writeString(jwbE);
        dest.writeString(jawabanBenar);
        dest.writeString(documentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public String getSoal() {
        return soal;
    }

    public void setSoal(String soal) {
        this.soal = soal;
    }

    public String getJwbA() {
        return jwbA;
    }

    public void setJwbA(String jwbA) {
        this.jwbA = jwbA;
    }

    public String getJwbB() {
        return jwbB;
    }

    public void setJwbB(String jwbB) {
        this.jwbB = jwbB;
    }

    public String getJwbC() {
        return jwbC;
    }

    public void setJwbC(String jwbC) {
        this.jwbC = jwbC;
    }

    public String getJwbD() {
        return jwbD;
    }

    public void setJwbD(String jwbD) {
        this.jwbD = jwbD;
    }

    public String getJwbE() {
        return jwbE;
    }

    public void setJwbE(String jwbE) {
        this.jwbE = jwbE;
    }

    public String getJawabanBenar() {
        return jawabanBenar;
    }

    public void setJawabanBenar(String jawabanBenar) {
        this.jawabanBenar = jawabanBenar;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
