package com.bpzone.romantickissstickers.Sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bpzone.romantickissstickers.WriteLog;

public class Sql_CreateDatabase {

    private static final String TAG = "Sql_CreateDatabase";
    private SQLiteDatabase db;

    public Sql_CreateDatabase() {
        try {
            db = DatabaseHelper.GetInstance();
            InitialDB();
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "Sql_CreateDatabase", e.getMessage(), e);
        }
    }

    private void InitialDB() {
        try {

            //---- AdsInfo ------------------------------------------------------------------------

            Cursor allRows = db.rawQuery("SELECT count(*) FROM AdsInfo ", null);
            allRows.moveToFirst();
            int Count = allRows.getInt(0);

            if (Count == 0) {
                new Sql_AdsInfo().insertAdsInfo(1,10);
            }

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "InitialDB", e.getMessage(), e);
        }
    }
}
