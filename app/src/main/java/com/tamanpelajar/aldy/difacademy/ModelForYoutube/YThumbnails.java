package com.tamanpelajar.aldy.difacademy.ModelForYoutube;

import com.google.gson.annotations.SerializedName;

public class YThumbnails {
    @SerializedName("default")
    private YUrlThumbnails normal;
    private YUrlThumbnails medium;
    private YUrlThumbnails high;
    private YUrlThumbnails standard;
    private YUrlThumbnails maxres;

    public YUrlThumbnails getNormal() {
        return normal;
    }

    public void setNormal(YUrlThumbnails normal) {
        this.normal = normal;
    }

    public YUrlThumbnails getMedium() {
        return medium;
    }

    public void setMedium(YUrlThumbnails medium) {
        this.medium = medium;
    }

    public YUrlThumbnails getHigh() {
        return high;
    }

    public void setHigh(YUrlThumbnails high) {
        this.high = high;
    }

    public YUrlThumbnails getStandard() {
        return standard;
    }

    public void setStandard(YUrlThumbnails standard) {
        this.standard = standard;
    }

    public YUrlThumbnails getMaxres() {
        return maxres;
    }

    public void setMaxres(YUrlThumbnails maxres) {
        this.maxres = maxres;
    }
}
