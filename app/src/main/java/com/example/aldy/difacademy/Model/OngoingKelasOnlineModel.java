package com.example.aldy.difacademy.Model;

public class OngoingKelasOnlineModel {
    private String kelasOnlineId;
    private long dateCreated;

    public OngoingKelasOnlineModel() {
    }

    public OngoingKelasOnlineModel(String kelasOnlineId, long dateCreated) {
        this.kelasOnlineId = kelasOnlineId;
        this.dateCreated = dateCreated;
    }

    public String getKelasOnlineId() {
        return kelasOnlineId;
    }

    public long getDateCreated() {
        return dateCreated;
    }
}
