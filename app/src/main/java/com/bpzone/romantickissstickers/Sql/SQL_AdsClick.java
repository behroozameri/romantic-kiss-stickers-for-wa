package com.bpzone.romantickissstickers.Sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bpzone.romantickissstickers.WriteLog;

import java.util.ArrayList;
import java.util.List;

public class SQL_AdsClick {

    private static final String TAG = "SQL_AdsClick";

    private SQLiteDatabase db;
    private int mDate;

    public SQL_AdsClick() {
        try {
            db = DatabaseHelper.GetInstance();
            String date = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
            mDate = Integer.parseInt(date);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "SQL_AdsClick", e.getMessage(), e);
        }
    }

    private void insertAdsClick() {
        try {
            ContentValues initialValues = new ContentValues();
            initialValues.put("_date", mDate);
            initialValues.put("count", 0);
            db.insert("AdsClick", null, initialValues);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "insertAdsClick", e.getMessage(), e);
        }
    }

    public int getCountAdsClick() {
        try {
            @SuppressLint("Recycle") Cursor versionRow = db.rawQuery("SELECT count FROM AdsClick WHERE _date = " + mDate, null);
            if (versionRow.moveToFirst()) {
                return versionRow.getInt(0);
            } else {
                insertAdsClick();
                return 0;
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "getCountAdsClick", e.getMessage(), e);
            return 0;
        }
    }

    public void addCountAdsClick() {
        try {
            int count = getCountAdsClick();
            count++;
            ContentValues initialValues = new ContentValues();
            initialValues.put("count", count);
            db.update("AdsClick", initialValues, "_date = " + mDate, null);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "addCountAdsClick", e.getMessage(), e);
        }
    }

    public List<Integer> getClickHistory() {
        try {
            List<Integer> history = new ArrayList<>();
            @SuppressLint("Recycle") Cursor allRows = db.rawQuery("SELECT * FROM AdsClick ORDER BY _date DESC ", null);
            if (allRows.moveToFirst()) {
                do {
                    history.add(allRows.getInt(1));
                } while (allRows.moveToNext());
            }
            return history;
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "getClickHistory", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
