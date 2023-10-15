package com.bpzone.romantickissstickers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.INTERNET;


@SuppressLint("Registered")
public class GetPermission extends AppCompatActivity {

    private static final String TAG = "Get_Permission";
    public static final int RequestPermissionCode = 1;
    private Context mContext;

    public GetPermission(Context context) {
        try {
            mContext = context;
            if (!checkPermission()) {
                requestPermission();
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "GetPermission", e.getMessage(), e);
        }
    }

    public boolean checkPermission() {
        try {
            int PermissionResult_INTERNET = ContextCompat.checkSelfPermission(mContext, INTERNET);
            return PermissionResult_INTERNET == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "checkPermission", e.getMessage(), e);
            return false;
        }
    }

    private void requestPermission() {
        try {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]
                    {
                            INTERNET
                    }, RequestPermissionCode);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "requestPermission", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
