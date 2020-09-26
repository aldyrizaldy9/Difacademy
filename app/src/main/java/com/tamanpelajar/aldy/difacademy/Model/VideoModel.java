package com.tamanpelajar.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoModel implements Parcelable {
    String documentId, title, description, videoUrl, kelasId, materiId;
    long dateCreated;

    public VideoModel() {
    }

    public VideoModel(String title, String description, String videoUrl, String kelasId, String materiId, long dateCreated) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.kelasId = kelasId;
        this.materiId = materiId;
        this.dateCreated = dateCreated;
    }

    protected VideoModel(Parcel in) {
        documentId = in.readString();
        title = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        kelasId = in.readString();
        materiId = in.readString();
        dateCreated = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(videoUrl);
        dest.writeString(kelasId);
        dest.writeString(materiId);
        dest.writeLong(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getKelasId() {
        return kelasId;
    }

    public void setKelasId(String kelasId) {
        this.kelasId = kelasId;
    }

    public String getMateriId() {
        return materiId;
    }

    public void setMateriId(String materiId) {
        this.materiId = materiId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
