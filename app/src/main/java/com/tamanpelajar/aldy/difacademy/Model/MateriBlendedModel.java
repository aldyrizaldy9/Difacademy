package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class MateriBlendedModel implements Parcelable {
    private String documentId, title, description, thumbnailUrl, lampiranUrl, kelasId;
    private long dateCreated;

    public MateriBlendedModel() {}

    public MateriBlendedModel(String title, String description, String thumbnailUrl, String lampiranUrl, String kelasId, long dateCreated) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.lampiranUrl = lampiranUrl;
        this.kelasId = kelasId;
        this.dateCreated = dateCreated;
    }

    protected MateriBlendedModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        description = in.readString();
        thumbnailUrl = in.readString();
        lampiranUrl = in.readString();
        kelasId = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(thumbnailUrl);
        dest.writeString(lampiranUrl);
        dest.writeString(kelasId);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MateriBlendedModel> CREATOR = new Creator<MateriBlendedModel>() {
        @Override
        public MateriBlendedModel createFromParcel(Parcel in) {
            return new MateriBlendedModel(in);
        }

        @Override
        public MateriBlendedModel[] newArray(int size) {
            return new MateriBlendedModel[size];
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLampiranUrl() {
        return lampiranUrl;
    }

    public void setLampiranUrl(String lampiranUrl) {
        this.lampiranUrl = lampiranUrl;
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
