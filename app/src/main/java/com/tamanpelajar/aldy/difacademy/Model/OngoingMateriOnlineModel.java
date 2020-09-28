package com.tamanpelajar.aldy.difacademy.Model;

public class OngoingMateriOnlineModel {
    private String materiId;
    private long dateCreated;

    public OngoingMateriOnlineModel() {
    }

    public OngoingMateriOnlineModel(String materiId, long dateCreated) {
        this.materiId = materiId;
        this.dateCreated = dateCreated;
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
