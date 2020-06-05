package com.tamanpelajar.aldy.difacademy.Model;

public class OngoingMateriModel {
    private String courseId, materiId;
    private long dateCreated;

    public OngoingMateriModel() {
    }

    public OngoingMateriModel(String courseId, String materiId, long dateCreated) {
        this.courseId = courseId;
        this.materiId = materiId;
        this.dateCreated = dateCreated;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getMateriId() {
        return materiId;
    }

    public long getDateCreated() {
        return dateCreated;
    }
}
