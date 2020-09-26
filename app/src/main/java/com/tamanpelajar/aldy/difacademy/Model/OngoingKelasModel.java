package com.tamanpelajar.aldy.difacademy.Model;

public class OngoingKelasModel {
    private String kelasId;
    private long dateCreated;

    public OngoingKelasModel() {
    }

    public OngoingKelasModel(String kelasId, long dateCreated) {
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
