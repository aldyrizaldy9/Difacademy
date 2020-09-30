package com.tamanpelajar.aldy.difacademy.Model;

public class OngoingMateriOnlineModel {
    private String kelasId, materiId;
    private long dateCreated;

    public OngoingMateriOnlineModel() {
    }

    public OngoingMateriOnlineModel(String kelasId, String materiId, long dateCreated) {
        this.kelasId = kelasId;
        this.materiId = materiId;
        this.dateCreated = dateCreated;
    }

    public String getMateriId() {
        return materiId;
    }

    public String getKelasId() {
        return kelasId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

}
