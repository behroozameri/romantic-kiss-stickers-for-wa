package com.bpzone.romantickissstickers;

import com.bpzone.romantickissstickers.Sql.SQL_AdsClick;
import com.bpzone.romantickissstickers.Sql.Sql_AdsInfo;

import java.util.List;

public class AdsStatus {

    private static final String TAG = "AdsStatus";
    private int maxClick = 10;
    private int todayClick = 0;

    public AdsStatus() {
        try {
            maxClick = new Sql_AdsInfo().getCountAdsInfo();
            todayClick = new SQL_AdsClick().getCountAdsClick();
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "AdsStatus", e.getMessage(), e);
        }
    }

    public boolean getAdsStatus() {
        try {

            if (BuildConfig.ShowAdMob != 1)
                return false;

            if (new Sql_AdsInfo().getStatusAdsInfo() != 1)
                return false;

            if (maxClick <= todayClick)
                return false;

            if (maxClick < EntryActivity.mAppCountClose)
                return false;

            List<Integer> history = new SQL_AdsClick().getClickHistory();
            if (history.size() >= 3) {
                if (history.get(1) >= new Sql_AdsInfo().getCountAdsInfo() && history.get(2) >= new Sql_AdsInfo().getCountAdsInfo())
                    return false;
            }

            return true;

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "getAdsStatus", e.getMessage(), e);
            return false;
        }
    }
}
