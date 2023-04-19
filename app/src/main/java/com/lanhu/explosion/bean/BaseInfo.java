package com.lanhu.explosion.bean;

public class BaseInfo {

    public static final int STATUS_UPLOAD_NO = 0;
    public static final int STATUS_UPLOAD_ING = 1;
    public static final int STATUS_UPLOAD_OK = 2;

    protected int uploadStatus;
    protected int dbId;

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
