package com.example.aldy.difacademy.Model;

import java.util.ArrayList;

public class UserModel {
    private String userId, nama, email, noTelp;

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

}
