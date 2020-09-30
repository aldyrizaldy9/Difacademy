package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class PaymentMateriOnlineModel implements Parcelable {

    public static final Creator<PaymentMateriOnlineModel> CREATOR = new Creator<PaymentMateriOnlineModel>() {
        @Override
        public PaymentMateriOnlineModel createFromParcel(Parcel in) {
            return new PaymentMateriOnlineModel(in);
        }

        @Override
        public PaymentMateriOnlineModel[] newArray(int size) {
            return new PaymentMateriOnlineModel[size];
        }
    };
    private String userId, namaUser, email, noWa, kelasId, materiId, namaMateri, hargaMateri, namaBank, documentId;
    private long dateCreated;
    private boolean isSeen, isPaid;

    public PaymentMateriOnlineModel() {
    }

    public PaymentMateriOnlineModel(String userId,
                                    String namaUser,
                                    String email,
                                    String noWa,
                                    String kelasId,
                                    String materiId,
                                    String namaMateri,
                                    String hargaMateri,
                                    String namaBank,
                                    long dateCreated,
                                    boolean isSeen,
                                    boolean isPaid) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.kelasId = kelasId;
        this.materiId = materiId;
        this.namaMateri = namaMateri;
        this.hargaMateri = hargaMateri;
        this.namaBank = namaBank;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
        this.isPaid = isPaid;
    }

    protected PaymentMateriOnlineModel(Parcel in) {
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        kelasId = in.readString();
        materiId = in.readString();
        namaMateri = in.readString();
        hargaMateri = in.readString();
        namaBank = in.readString();
        documentId = in.readString();
        dateCreated = in.readLong();
        isSeen = in.readByte() != 0;
        isPaid = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(namaUser);
        dest.writeString(email);
        dest.writeString(noWa);
        dest.writeString(kelasId);
        dest.writeString(materiId);
        dest.writeString(namaMateri);
        dest.writeString(hargaMateri);
        dest.writeString(namaBank);
        dest.writeString(documentId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isPaid ? 1 : 0));
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

    public String getMateriId() {
        return materiId;
    }

    public String getNamaMateri() {
        return namaMateri;
    }

    public String getHargaMateri() {
        return hargaMateri;
    }

    public String getNamaBank() {
        return namaBank;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
