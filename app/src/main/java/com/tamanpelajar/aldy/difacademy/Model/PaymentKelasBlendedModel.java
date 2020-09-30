package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentKelasBlendedModel implements Parcelable {

    public static final Creator<PaymentKelasBlendedModel> CREATOR = new Creator<PaymentKelasBlendedModel>() {
        @Override
        public PaymentKelasBlendedModel createFromParcel(Parcel in) {
            return new PaymentKelasBlendedModel(in);
        }

        @Override
        public PaymentKelasBlendedModel[] newArray(int size) {
            return new PaymentKelasBlendedModel[size];
        }
    };
    private String documentId, userId, namaUser, email, noWa, kelasId, namaKelas, hargaKelas, namaBank;
    private long dateCreated;
    private boolean isSeen, isPaid;

    public PaymentKelasBlendedModel() {
    }

    public PaymentKelasBlendedModel(String userId, String namaUser, String email, String noWa, String kelasId, String namaKelas, String hargaKelas, String namaBank, long dateCreated, boolean isSeen, boolean isPaid) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.kelasId = kelasId;
        this.namaKelas = namaKelas;
        this.hargaKelas = hargaKelas;
        this.namaBank = namaBank;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
        this.isPaid = isPaid;
    }

    protected PaymentKelasBlendedModel(Parcel in) {
        documentId = in.readString();
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        kelasId = in.readString();
        namaKelas = in.readString();
        hargaKelas = in.readString();
        namaBank = in.readString();
        dateCreated = in.readLong();
        isSeen = in.readByte() != 0;
        isPaid = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(userId);
        dest.writeString(namaUser);
        dest.writeString(email);
        dest.writeString(noWa);
        dest.writeString(kelasId);
        dest.writeString(namaKelas);
        dest.writeString(hargaKelas);
        dest.writeString(namaBank);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isPaid ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getKelasId() {
        return kelasId;
    }

    public void setKelasId(String kelasId) {
        this.kelasId = kelasId;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getHargaKelas() {
        return hargaKelas;
    }

    public void setHargaKelas(String hargaKelas) {
        this.hargaKelas = hargaKelas;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(String namaBank) {
        this.namaBank = namaBank;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
