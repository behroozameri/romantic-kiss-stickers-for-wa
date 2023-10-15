package com.bpzone.romantickissstickers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.bpzone.romantickissstickers.Sql.DatabaseHelper;
import com.bpzone.romantickissstickers.Sql.SQL_AdsClick;
import com.bpzone.romantickissstickers.Sql.Sql_AdsInfo;
import com.bpzone.romantickissstickers.Sql.Sql_CreateDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EntryActivity extends BaseActivity {
    private static final String TAG = "EntryActivity";
    private View progressBar;
    private LoadListAsyncTask loadListAsyncTask;
    public static InterstitialAd mInterstitialAdExit;
    public static int mAppCountClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        try {

            new GetPermission(this);
            StickerPackListActivity.sAdActivity = 0;

            DatabaseHelper.GetInstance(this);
            new Sql_CreateDatabase();

            initUseApp();

            try {
                if (new AdsStatus().getAdsStatus()) {

                    MobileAds.initialize(this, initializationStatus -> {
                    });

                    AdRequest adRequestExit = new AdRequest.Builder().build();
                    InterstitialAd.load(this, getString(R.string.I_AD_UNIT_ID_Exit), adRequestExit, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAdExit = interstitialAd;
                            mInterstitialAdExit.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAdExit = null;
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mInterstitialAdExit = null;
                        }
                    });
                }
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "onCreate-MobileAds", e.getMessage(), e);
            }

            overridePendingTransition(0, 0);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            progressBar = findViewById(R.id.entry_activity_progress);
            loadListAsyncTask = new LoadListAsyncTask(this);
            loadListAsyncTask.execute();

            FrameLayout frameLayout = findViewById(R.id.layout);
            AnimationDrawable animationDrawable = (AnimationDrawable) frameLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(2000);
            animationDrawable.start();

            FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onCreate", e.getMessage(), e);
        }
    }

    private void initUseApp() {
        try {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                int appUse = pref.getInt("AppUse", 0);
                if (appUse == 0) {
                    for (int i = 0; i < ((Integer)new Sql_AdsInfo().getCountAdsInfo() / 2 ); i++)
                        new SQL_AdsClick().addCountAdsClick();
                }
                appUse++;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putInt("AppUse", appUse);
                editor.apply();
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "initUseApp-AppUse", e.getMessage(), e);
            }

            try {
                String date = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
                int iDate = Integer.parseInt(date);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                int lastAppUse = pref.getInt("LastAppUse", 0);
                if (lastAppUse != iDate) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putInt("LastAppUse", iDate);
                    editor.putInt("AppCountClose", 0);
                    editor.apply();
                }
                mAppCountClose = pref.getInt("AppCountClose", 0);

            } catch (Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "initUseApp-LastAppUse", e.getMessage(), e);
            }

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "initUseApp", e.getMessage(), e);
        }
    }

    private void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        try {
            progressBar.setVisibility(View.GONE);
            if (stickerPackList.size() > 1) {
                if (mInterstitialAdExit != null) {
                    mInterstitialAdExit.show(this);
                    new SQL_AdsClick().addCountAdsClick();
                }
                final Intent intent = new Intent(this, StickerPackListActivity.class);
                intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
                startActivity(intent);
            }
            finish();
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "showStickerPack", e.getMessage(), e);
        }
    }

    private void showErrorMessage(String errorMessage) {
        try {
            progressBar.setVisibility(View.GONE);
            Log.e("EntryActivity", "error fetching sticker packs, " + errorMessage);
            final TextView errorMessageTV = findViewById(R.id.error_message);
            errorMessageTV.setText(getString(R.string.error_message, errorMessage));
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "showErrorMessage", e.getMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (loadListAsyncTask != null && !loadListAsyncTask.isCancelled()) {
                loadListAsyncTask.cancel(true);
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onDestroy", e.getMessage(), e);
        }
    }

    static class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<EntryActivity> contextWeakReference;

        LoadListAsyncTask(EntryActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Pair<String, ArrayList<StickerPack>> doInBackground(Void... voids) {

            ArrayList<StickerPack> stickerPackList;
            try {
                final Context context = contextWeakReference.get();
                if (context != null) {
                    stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList.size() == 0) {
                        return new Pair<>("could not find any packs", null);
                    }
                    for (StickerPack stickerPack : stickerPackList) {
                        StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
                    }
                    return new Pair<>(null, stickerPackList);
                } else {
                    return new Pair<>("could not fetch sticker packs", null);
                }
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "LoadListAsyncTask-doInBackground", e.getMessage(), e);
                return new Pair<>(e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(Pair<String, ArrayList<StickerPack>> stringListPair) {
            try {
                final EntryActivity entryActivity = contextWeakReference.get();
                if (entryActivity != null) {
                    if (stringListPair.first != null) {
                        entryActivity.showErrorMessage(stringListPair.first);
                    } else {
                        entryActivity.showStickerPack(stringListPair.second);
                    }
                }
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "LoadListAsyncTask-onPostExecute", e.getMessage(), e);
            }
        }
    }
}
