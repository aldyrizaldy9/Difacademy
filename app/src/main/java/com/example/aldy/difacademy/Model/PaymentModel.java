package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class PaymentModel implements Parcelable {
    private String userId, namaUser, email, noWa, blendedCourseId, namaKelas, namaBank, paymentId;
    private long dateCreated;
    private boolean isSeen, isPaid;

    public PaymentModel() {
    }

    public PaymentModel(String userId,
                        String namaUser,
                        String email,
                        String noWa,
                        String blendedCourseId,
                        String namaKelas,
                        String namaBank,
                        long dateCreated,
                        boolean isSeen,
                        boolean isPaid) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.email = email;
        this.noWa = noWa;
        this.blendedCourseId = blendedCourseId;
        this.namaKelas = namaKelas;
        this.namaBank = namaBank;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
        this.isPaid = isPaid;
    }

    protected PaymentModel(Parcel in) {
        userId = in.readString();
        namaUser = in.readString();
        email = in.readString();
        noWa = in.readString();
        blendedCourseId = in.readString();
        namaKelas = in.readString();
        namaBank = in.readString();
        paymentId = in.readString();
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
        dest.writeString(blendedCourseId);
        dest.writeString(namaKelas);
        dest.writeString(namaBank);
        dest.writeString(paymentId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
        dest.writeByte((byte) (isPaid ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentModel> CREATOR = new Creator<PaymentModel>() {
        @Override
        public PaymentModel createFromParcel(Parcel in) {
            return new PaymentModel(in);
        }

        @Override
        public PaymentModel[] newArray(int size) {
            return new PaymentModel[size];
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

    public String getNamaBank() {
        return namaBank;
    }

    @Exclude
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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
