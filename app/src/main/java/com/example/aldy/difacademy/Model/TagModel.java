package com.example.aldy.difacademy.Model;

public class TagModel {
    private String tag, tagid;

    public TagModel(){
    }

    public TagModel(String tag) {
        this.tag = tag;
    }

    public TagModel(String tag, String tagid) {
        this.tag = tag;
        this.tagid = tagid;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTag() {
        return tag;
    }
}
