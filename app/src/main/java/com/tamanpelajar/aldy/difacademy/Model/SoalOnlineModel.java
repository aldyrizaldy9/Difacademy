package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SoalOnlineModel implements Parcelable {
    String documentId, soal, jwbA, jwbB, jwbC, jwbD, jwbE, jawabanBenar;
    long dateCreated;

    public SoalOnlineModel() {
    }

    public SoalOnlineModel(String soal, String jwbA, String jwbB, String jwbC, String jwbD, String jwbE, String jawabanBenar, long dateCreated) {
        this.soal = soal;
        this.jwbA = jwbA;
        this.jwbB = jwbB;
        this.jwbC = jwbC;
        this.jwbD = jwbD;
        this.jwbE = jwbE;
        this.jawabanBenar = jawabanBenar;
        this.dateCreated = dateCreated;
    }

    protected SoalOnlineModel(Parcel in) {
        documentId = in.readString();
        soal = in.readString();
        jwbA = in.readString();
        jwbB = in.readString();
        jwbC = in.readString();
        jwbD = in.readString();
        jwbE = in.readString();
        jawabanBenar = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(soal);
        dest.writeString(jwbA);
        dest.writeString(jwbB);
        dest.writeString(jwbC);
        dest.writeString(jwbD);
        dest.writeString(jwbE);
        dest.writeString(jawabanBenar);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SoalOnlineModel> CREATOR = new Creator<SoalOnlineModel>() {
        @Override
        public SoalOnlineModel createFromParcel(Parcel in) {
            return new SoalOnlineModel(in);
        }

        @Override
        public SoalOnlineModel[] newArray(int size) {
            return new SoalOnlineModel[size];
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
