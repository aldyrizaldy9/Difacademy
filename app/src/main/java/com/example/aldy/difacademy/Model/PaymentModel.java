package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class PaymentModel implements Parcelable {
    private String userId, blendedCourseId, bankName, paymentId;
    private long dateCreated;
    private boolean isSeen;

    public PaymentModel() {
    }

    public PaymentModel(String userId, String blendedCourseId, String bankName, long dateCreated, boolean isSeen) {
        this.userId = userId;
        this.blendedCourseId = blendedCourseId;
        this.bankName = bankName;
        this.dateCreated = dateCreated;
        this.isSeen = isSeen;
    }


    protected PaymentModel(Parcel in) {
        userId = in.readString();
        blendedCourseId = in.readString();
        bankName = in.readString();
        paymentId = in.readString();
        dateCreated = in.readLong();
        isSeen = in.readByte() != 0;
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

    public String getBlendedCourseId() {
        return blendedCourseId;
    }

    public String getBankName() {
        return bankName;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(blendedCourseId);
        dest.writeString(bankName);
        dest.writeString(paymentId);
        dest.writeLong(dateCreated);
        dest.writeByte((byte) (isSeen ? 1 : 0));
    }
}
