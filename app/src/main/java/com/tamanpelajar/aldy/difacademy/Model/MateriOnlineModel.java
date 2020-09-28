package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class MateriOnlineModel implements Parcelable {
    private String documentId, title, thumbnailUrl, harga, kelasId;
    private long dateCreated;

    public MateriOnlineModel() {
    }

    public MateriOnlineModel(String title, String thumbnailUrl, String harga, String kelasId, long dateCreated) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.harga = harga;
        this.kelasId = kelasId;
        this.dateCreated = dateCreated;
    }

    protected MateriOnlineModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        thumbnailUrl = in.readString();
        harga = in.readString();
        kelasId = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(thumbnailUrl);
        dest.writeString(harga);
        dest.writeString(kelasId);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MateriOnlineModel> CREATOR = new Creator<MateriOnlineModel>() {
        @Override
        public MateriOnlineModel createFromParcel(Parcel in) {
            return new MateriOnlineModel(in);
        }

        @Override
        public MateriOnlineModel[] newArray(int size) {
            return new MateriOnlineModel[size];
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKelasId() {
        return kelasId;
    }

    public void setKelasId(String kelasId) {
        this.kelasId = kelasId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
