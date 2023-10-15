package com.bpzone.romantickissstickers.Sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bpzone.romantickissstickers.WriteLog;

public class Sql_AdsInfo {

    private static final String TAG = "Sql_AdsInfo";

    private SQLiteDatabase db;

    public Sql_AdsInfo() {
        try {
            db = DatabaseHelper.GetInstance();
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "Sql_AdsInfo", e.getMessage(), e);
        }
    }

    public void insertAdsInfo(int status, int count) {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("status", status);
            initialValues.put("count", count);
            db.insert("AdsInfo", null, initialValues);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "insertAdsInfo", e.getMessage(), e);
        }
    }

    public int getStatusAdsInfo() {
        try {
            @SuppressLint("Recycle") Cursor versionRow = db.rawQuery("SELECT status FROM AdsInfo WHERE _id = 1", null);
            if (versionRow.moveToFirst()) {
                return versionRow.getInt(0);
            } else {
                ContentValues initialValues = new ContentValues();
                initialValues.put("status", 1);
                initialValues.put("count", 2);
                db.insert("AdsInfo", null, initialValues);
            }

            return 0;
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "getStatusAdsInfo", e.getMessage(), e);
            return 0;
        }
    }

    public int getCountAdsInfo() {
        try {
            @SuppressLint("Recycle") Cursor versionRow = db.rawQuery("SELECT count FROM AdsInfo WHERE _id = 1", null);
            if (versionRow.moveToFirst()) {
                return versionRow.getInt(0);
            } else {
                ContentValues initialValues = new ContentValues();
                initialValues.put("status", 1);
                initialValues.put("count", 2);
                db.insert("AdsInfo", null, initialValues);
            }

            return 0;
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "getCountAdsInfo", e.getMessage(), e);
            return 0;
        }
    }

    public void updateStatusAdsInfo(int status) {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("status", status);
            db.update("AdsInfo", initialValues, "_id = 1", null);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "updateStatusVersion", e.getMessage(), e);
        }
    }

    public void updateCountAdsInfo(int count) {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("count", count);
            db.update("AdsInfo", initialValues, "_id = 1", null);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "updateCountVersion", e.getMessage(), e);
        }
    }
}
