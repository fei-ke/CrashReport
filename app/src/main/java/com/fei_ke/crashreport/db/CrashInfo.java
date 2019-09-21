package com.fei_ke.crashreport.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 崩溃信息
 */
@DatabaseTable(tableName = "record")
public class CrashInfo {
    //--------表结构
    @DatabaseField(generatedId = true)
    private int _id;
    @DatabaseField
    private String packageName;

    @DatabaseField
    private String crashInfo;

    @DatabaseField
    private String simpleInfo;

    @DatabaseField
    private int stampTime;

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public CrashInfo() {
    }

    public String getCrashInfo() {
        return crashInfo;
    }

    public void setCrashInfo(String crashInfo) {
        this.crashInfo = crashInfo;
    }

    public String getSimpleInfo() {
        return simpleInfo;
    }

    public void setSimpleInfo(String simpleInfo) {
        this.simpleInfo = simpleInfo;
    }

    public int getStampTime() {
        return stampTime;
    }

    public void setStampTime(int stampTime) {
        this.stampTime = stampTime;
    }
}
