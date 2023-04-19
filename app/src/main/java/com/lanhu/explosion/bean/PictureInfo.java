package com.lanhu.explosion.bean;

public class PictureInfo extends BaseInfo {

    private String path;

    public PictureInfo() {

    }

    public PictureInfo(String path, int status) {
        this.path = path;
        this.uploadStatus = status;
    }

    public PictureInfo(String path, int status, int id) {
        this.path = path;
        this.uploadStatus = status;
        this.dbId = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
