package com.bpzone.romantickissstickers;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StickerPackInfoActivity extends BaseActivity {

    private static final String TAG = "StickerPackInfoActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_info);
        try {
            final TextView tv_2_AppVersion = findViewById(R.id.tv_2_AppVersion);
            final LinearLayout LL_AppVersion = findViewById(R.id.LL_AppVersion);
            tv_2_AppVersion.setText("your version code is : " + BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")");
            LL_AppVersion.setOnClickListener(v -> RateAPP());

            final String email = "bpzone.developer@gmail.com";
            final LinearLayout send_email = findViewById(R.id.LL_send_email);
            if (TextUtils.isEmpty(email)) {
                send_email.setVisibility(View.GONE);
            } else {
                send_email.setOnClickListener(v -> launchEmailClient());
            }

            final LinearLayout shareApp = findViewById(R.id.LL_share);
            shareApp.setOnClickListener(v -> shareAPP());

            final LinearLayout rateAPP = findViewById(R.id.LL_rate);
            rateAPP.setOnClickListener(v -> RateAPP());

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onCreate", e.getMessage(), e);
        }
    }


    private void launchEmailClient() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"bpzone.developer@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Romantic Kiss");
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.info_send_email_to_prompt)));
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "launchEmailClient", e.getMessage(), e);
        }
    }

    private void shareAPP() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "Invite you to install this app: \n\n";
            shareMessage = shareMessage + "GooglePlay\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_app)));
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "shareAPP", e.getMessage(), e);
        }
    }

    private void RateAPP() {
        try {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "RateAPP", e.getMessage(), e);
        }
    }
}
