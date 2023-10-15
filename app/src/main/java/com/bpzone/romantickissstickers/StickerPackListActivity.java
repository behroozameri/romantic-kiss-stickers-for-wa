package com.bpzone.romantickissstickers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.bpzone.romantickissstickers.Sql.SQL_AdsClick;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StickerPackListActivity extends AddStickerPackActivity {
    private final String TAG = "StickerPackListActivity";
    public static final String EXTRA_STICKER_PACK_LIST_DATA = "sticker_pack_list";
    private static final int STICKER_PREVIEW_DISPLAY_LIMIT = 5;
    private LinearLayoutManager packLayoutManager;
    private RecyclerView packRecyclerView;
    private StickerPackListAdapter allStickerPacksListAdapter;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private ArrayList<StickerPack> stickerPackList;

    private static boolean mExit = false;
    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    public static int sAdActivity = 0;
    public static int sAdShowActivity;
    public static InterstitialAd mInterstitialAd;

    private Handler handler;
    private int exitTime;
    private Date dateChange;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_list);
        try {

            mContext = this;

            if (new AdsStatus().getAdsStatus()) {
                exitTime = getResources().getInteger(R.integer.exitTime);
                handler = new Handler();
                handler.postDelayed(runnable, 1000 * 60);
            }

            packRecyclerView = findViewById(R.id.sticker_pack_list);
            stickerPackList = getIntent().getParcelableArrayListExtra(EXTRA_STICKER_PACK_LIST_DATA);

            showStickerPackList(stickerPackList);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getResources().getQuantityString(R.plurals.title_activity_sticker_packs_list, stickerPackList.size()));
            }

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onCreate", e.getMessage(), e);
        }
    }

    private void initAdMob() {
        try {
            if (new AdsStatus().getAdsStatus()) {

                sAdShowActivity = getResources().getInteger(R.integer.sAdShowActivity);
                AdView adView = findViewById(R.id.adView);
                AdRequest adRequest_Banner = new AdRequest.Builder().build();
                adView.loadAd(adRequest_Banner);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    }

                    @Override
                    public void onAdOpened() {
                        new SQL_AdsClick().addCountAdsClick();
                    }

                    @Override
                    public void onAdClicked() {
                    }

                    @Override
                    public void onAdClosed() {
                    }
                });

                AdRequest adRequest_Interstitial = new AdRequest.Builder().build();
                InterstitialAd.load(this, getString(R.string.I_AD_UNIT_ID), adRequest_Interstitial, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });

            } else {
                LinearLayout LL_adView = findViewById(R.id.LL_adView);
                AdView adView = findViewById(R.id.adView);
                LL_adView.removeView(adView);
            }

        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "initAdMob", e.getMessage(), e);
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Date dateNow = new Date();
                long millis = dateNow.getTime() - dateChange.getTime();
                if (millis > exitTime) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                    int appCountClose = pref.getInt("AppCountClose", 0);
                    appCountClose++;
                    EntryActivity.mAppCountClose = appCountClose;
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                    editor.putInt("AppCountClose", appCountClose);
                    editor.apply();
                    finish();
                } else {
                    handler.postDelayed(this, 1000 * 60);
                }
            } catch (
                    Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "Runnable", e.getMessage(), e);
            }
        }
    };

    private void initAppReview() {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            int appUse = pref.getInt("AppUse", 0);
            if (appUse == 2 || appUse == 5 || appUse == 20 || appUse == 50) {
                ReviewManager reviewManager = ReviewManagerFactory.create(this);
                Task<ReviewInfo> request = reviewManager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ReviewInfo reviewInfo = task.getResult();
                        Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                        flow.addOnCompleteListener(task1 -> {
                        });
                    }
                });
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "initAppReview", e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mExit) {
                super.onBackPressed();
            } else {
                initAppReview();
                mExit = true;
                Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> mExit = false, 2000);
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onBackPressed", e.getMessage(), e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_info) {
                launchInfoActivity();
                return true;
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onOptionsItemSelected", e.getMessage(), e);
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchInfoActivity() {
        try {
            Intent intent = new Intent(StickerPackListActivity.this, StickerPackInfoActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "launchInfoActivity", e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initAdMob();
            dateChange = new Date();
            whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
            whiteListCheckAsyncTask.execute(stickerPackList.toArray(new StickerPack[0]));
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onResume", e.getMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();

            if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
                whiteListCheckAsyncTask.cancel(true);
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onPause", e.getMessage(), e);
        }
    }

    private void showStickerPackList(List<StickerPack> stickerPackList) {
        try {
            allStickerPacksListAdapter = new StickerPackListAdapter(stickerPackList, onAddButtonClickedListener);
            packRecyclerView.setAdapter(allStickerPacksListAdapter);
            packRecyclerView.setHasFixedSize(false);
            packLayoutManager = new LinearLayoutManager(this);
            packLayoutManager.setOrientation(RecyclerView.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                    packRecyclerView.getContext(),
                    packLayoutManager.getOrientation()
            );
            packRecyclerView.addItemDecoration(dividerItemDecoration);
            packRecyclerView.setLayoutManager(packLayoutManager);
            packRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::recalculateColumnCount);
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "showStickerPackList", e.getMessage(), e);
        }
    }


    private final StickerPackListAdapter.OnAddButtonClickedListener onAddButtonClickedListener = pack -> addStickerPackToWhatsApp(pack.identifier, pack.name);


    private void recalculateColumnCount() {
        try {
            final int previewSize = getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size);
            int firstVisibleItemPosition = packLayoutManager.findFirstVisibleItemPosition();
            StickerPackListItemViewHolder viewHolder = (StickerPackListItemViewHolder) packRecyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);
            if (viewHolder != null) {
                final int widthOfImageRow = viewHolder.imageRowView.getMeasuredWidth();
//final int max = Math.max(widthOfImageRow / previewSize, 1);
                final int max = Math.max(widthOfImageRow / previewSize, 2);
                int maxNumberOfImagesInARow = Math.min(STICKER_PREVIEW_DISPLAY_LIMIT, max);
                int minMarginBetweenImages = (widthOfImageRow - maxNumberOfImagesInARow * previewSize) / (maxNumberOfImagesInARow - 1);
                allStickerPacksListAdapter.setImageRowSpec(maxNumberOfImagesInARow, minMarginBetweenImages);
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "recalculateColumnCount", e.getMessage(), e);
        }
    }


    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivity> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivity stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final List<StickerPack> doInBackground(StickerPack... stickerPackArray) {
            try {
                final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
                if (stickerPackListActivity == null) {
                    return Arrays.asList(stickerPackArray);
                }

                for (StickerPack stickerPack : stickerPackArray) {
                    stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
                }
                return Arrays.asList(stickerPackArray);
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog("StickerPackListActivity", "WhiteListCheckAsyncTask-doInBackground", e.getMessage(), e);
                return Arrays.asList(stickerPackArray);
            }
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            try {
                final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
                if (stickerPackListActivity != null) {
                    if (stickerPackList != null) {
                        if (stickerPackList.size() > 0) {
                            if (stickerPackList.get(0).getType() == 0) {
                                Collections.sort(stickerPackList, (lhs, rhs) -> {
                                    boolean b1 = lhs.getIsWhitelisted();
                                    boolean b2 = rhs.getIsWhitelisted();
                                    if (b1 && !b2) {
                                        return +1;
                                    }
                                    if (!b1 && b2) {
                                        return -1;
                                    }
                                    return 0;
                                });
                                stickerPackListActivity.allStickerPacksListAdapter.setStickerPackList(stickerPackList);
                                stickerPackListActivity.allStickerPacksListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog("StickerPackListActivity", "WhiteListCheckAsyncTask-onPostExecute", e.getMessage(), e);
            }
        }
    }
}
