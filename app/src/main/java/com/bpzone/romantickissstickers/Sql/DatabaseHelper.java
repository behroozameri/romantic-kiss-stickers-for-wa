package com.bpzone.romantickissstickers.Sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bpzone.romantickissstickers.WriteLog;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "RomanticKissStickersDB";
    private static final int DATABASE_VERSION = 4;

    private static DatabaseHelper dh = null;
    private static SQLiteDatabase db = null;

    private static final String T_AdsClick = "CREATE TABLE AdsClick ( _date int primary key, count int)";
    private static final String T_AdsInfo = "CREATE TABLE AdsInfo ( _id integer primary key autoincrement, status int, count int)";


    static public SQLiteDatabase GetInstance(Context context) {
        try {
            if (dh == null) {
                dh = new DatabaseHelper(context);
                db = dh.getWritableDatabase();
            }
            return db;

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "GetInstance()", e.getMessage(), e);
            return null;
        }
    }

    static public SQLiteDatabase GetInstance() {
        try {
            return db;
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "GetInstance", e.getMessage(), e);
            return null;
        }
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(T_AdsClick);
            db.execSQL(T_AdsInfo);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onCreate", e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS AdsClick");
            db.execSQL("DROP TABLE IF EXISTS AdsInfo");
            onCreate(db);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onUpgrade", e.getMessage(), e);
        }
    }
}
