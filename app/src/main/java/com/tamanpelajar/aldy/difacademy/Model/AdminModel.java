package com.tamanpelajar.aldy.difacademy.Model;

import com.google.firebase.firestore.Exclude;

public class AdminModel {
    private String adminId, nama;

    @Exclude
    public String getAdminId() {
        return adminId;
    }

    public String getNama() {
        return nama;
    }
}
