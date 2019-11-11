package com.example.aldy.difacademy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class NewsModel implements Parcelable {
    private String newsId, judul, isi, linkfoto;

    public NewsModel() {
    }

    public NewsModel(String judul, String isi, String linkfoto) {
        this.judul = judul;
        this.isi = isi;
        this.linkfoto = linkfoto;
    }

    protected NewsModel(Parcel in) {
        newsId = in.readString();
        judul = in.readString();
        isi = in.readString();
        linkfoto = in.readString();
    }

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
    }
}
