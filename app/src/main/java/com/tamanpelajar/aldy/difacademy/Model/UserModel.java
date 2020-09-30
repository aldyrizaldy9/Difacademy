package com.tamanpelajar.aldy.difacademy.Model;

public class UserModel {
    private String documentId, userId, nama, email, noTelp;

    public UserModel() {
    }

    public UserModel(String userId, String nama, String email, String noTelp) {
        this.userId = userId;
        this.nama = nama;
        this.email = email;
        this.noTelp = noTelp;
    }

    public String getUserId() {
        return userId;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
