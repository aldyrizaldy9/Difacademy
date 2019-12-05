package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class NewsModel implements Parcelable {
    public static final Creator<NewsModel> CREATOR = new Creator<NewsModel>() {
        @Override
        public NewsModel createFromParcel(Parcel in) {
            return new NewsModel(in);
        }

        @Override
        public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };
    private String newsId, judul, isi, linkfoto;
    private long dateCreated;

    public NewsModel() {
    }


    public NewsModel(String judul, String isi, String linkfoto, long dateCreated) {
        this.judul = judul;
        this.isi = isi;
        this.linkfoto = linkfoto;
        this.dateCreated = dateCreated;
    }

    protected NewsModel(Parcel in) {
        newsId = in.readString();
        judul = in.readString();
        isi = in.readString();
        linkfoto = in.readString();
        dateCreated = in.readLong();
    }

    @Exclude
    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() {
        return isi;
    }

    public String getLinkfoto() {
        return linkfoto;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsId);
        dest.writeString(judul);
        dest.writeString(isi);
        dest.writeString(linkfoto);
        dest.writeLong(dateCreated);
    }
}
