package com.tamanpelajar.aldy.difacademy.Model;

import com.google.firebase.firestore.Exclude;

public class UserModel {
    private String userId, nama, email, noTelp, userDocId;

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

    @Exclude
    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }
}
