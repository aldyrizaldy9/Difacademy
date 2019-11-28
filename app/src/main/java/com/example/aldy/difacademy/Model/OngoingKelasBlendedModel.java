package com.example.aldy.difacademy.Model;

public class OngoingKelasBlendedModel {
    private String kelasBlendedId;
    private long dateCreated;

    public OngoingKelasBlendedModel() {
    }

    public OngoingKelasBlendedModel(String kelasBlendedId, long dateCreated) {
        this.kelasBlendedId = kelasBlendedId;
        this.dateCreated = dateCreated;
    }

    public String getKelasBlendedId() {
        return kelasBlendedId;
    }

    public long getDateCreated() {
        return dateCreated;
    }
}
