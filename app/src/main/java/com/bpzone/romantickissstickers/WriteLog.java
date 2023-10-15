package com.bpzone.romantickissstickers;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class WriteLog {

    private static final String TAG = "WriteLog";

    @SuppressLint("StaticFieldLeak")
    private static WriteLog wl = null;
    private static int counter;
    private final String deviceName;
    private final String logName;

    private String mNewKey;
    private String mMsg;

    private WriteLog() {
        counter = 0;
        deviceName = getDeviceName();
        int appVersion = BuildConfig.VERSION_CODE;
        logName = "Log-" + appVersion;
    }

    public static WriteLog GetInstance() {
        if (wl == null)
            wl = new WriteLog();

        return wl;
    }

    public void addToLog(String _className, String _functionName, String _msg, Exception ex) {
        try {

            MakeMessage(_className, _functionName, _msg);

            if (BuildConfig.DEBUG) {
                ShowLog(_className, _functionName, _msg, ex);
            }

            addToFirebase();

        } catch (Exception e) {
            Log.d(TAG, "addToLog: " + e.getMessage());
        }
    }

    private void MakeMessage(String _className, String _functionName, String _msg) {
        try {
            counter++;
            String time = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd-HH-mm-ss", new java.util.Date()));
            int sdkVersion = Build.VERSION.SDK_INT;
            mNewKey = time + "-" + counter + "-" + sdkVersion + "-" + deviceName;
            mMsg = _className + " # " + _functionName + " # " + _msg;
        } catch (Exception e) {
            Log.d(TAG, "addToFirebase: " + e.getMessage());
        }
    }

    public void addToFirebase() {
        try {
            //FirebaseDatabase.getInstance().getReference().child("Log").child(logName).child(mNewKey).setValue(mMsg);
        } catch (Exception e) {
            Log.d(TAG, "addToFirebase: " + e.getMessage());
        }
    }

    private void ShowLog(String _className, String _functionName, String _msg, Exception ex) {
        try {
            if (_className.length() < 22)
                Log.d(_className, _functionName + " : " + _msg);
            else
                Log.d(_className.substring(0, 22), _functionName + " : " + _msg);
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
}

