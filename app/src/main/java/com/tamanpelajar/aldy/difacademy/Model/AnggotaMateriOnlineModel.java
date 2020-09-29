package com.tamanpelajar.aldy.difacademy.Model;

public class AnggotaMateriOnlineModel {
    String documentId, name, userId, kelasId, materiId;
    long dateCreated;

    public AnggotaMateriOnlineModel() {
    }

    public AnggotaMateriOnlineModel(String name, String userId, String kelasId, String materiId, long dateCreated) {
        this.name = name;
        this.userId = userId;
        this.kelasId = kelasId;
        this.materiId = materiId;
        this.dateCreated = dateCreated;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
