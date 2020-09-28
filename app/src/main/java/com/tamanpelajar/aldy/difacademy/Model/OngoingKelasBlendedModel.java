package com.tamanpelajar.aldy.difacademy.Model;

public class OngoingKelasBlendedModel {
    private String kelasId;
    private long dateCreated;

    public OngoingKelasBlendedModel() {
    }

    public OngoingKelasBlendedModel(String kelasId, long dateCreated) {
        this.kelasId = kelasId;
        this.dateCreated = dateCreated;
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
