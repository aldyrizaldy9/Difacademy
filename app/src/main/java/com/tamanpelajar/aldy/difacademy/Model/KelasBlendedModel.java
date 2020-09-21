package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class KelasBlendedModel implements Parcelable {
    private String documentId, title, description, thumbnailUrl, harga, tagId, tag;
    private long dateCreated;

    public KelasBlendedModel() {
    }

    public KelasBlendedModel(String title, String description, String thumbnailUrl, String harga, String tagId, String tag, long dateCreated) {
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.harga = harga;
        this.tagId = tagId;
        this.tag = tag;
        this.dateCreated = dateCreated;
    }

    protected KelasBlendedModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        description = in.readString();
        thumbnailUrl = in.readString();
        harga = in.readString();
        tagId = in.readString();
        tag = in.readString();
        dateCreated = in.readLong();
    }

    public static final Creator<KelasBlendedModel> CREATOR = new Creator<KelasBlendedModel>() {
        @Override
        public KelasBlendedModel createFromParcel(Parcel in) {
            return new KelasBlendedModel(in);
        }

        @Override
        public KelasBlendedModel[] newArray(int size) {
            return new KelasBlendedModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(thumbnailUrl);
        dest.writeString(harga);
        dest.writeString(tagId);
        dest.writeString(tag);
        dest.writeLong(dateCreated);
    }

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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
