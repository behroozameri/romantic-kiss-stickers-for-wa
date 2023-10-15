package com.bpzone.romantickissstickers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.bpzone.romantickissstickers.Sql.SQL_AdsClick;

import java.lang.ref.WeakReference;
import java.util.Date;

public class StickerPackDetailsActivity extends AddStickerPackActivity {

    private static final String TAG = "PackDetailsActivity";

    /**
     * Do not change below values of below 3 lines as this is also used by WhatsApp
     */
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";

    public static final String EXTRA_SHOW_UP_BUTTON = "show_up_button";
    public static final String EXTRA_STICKER_PACK_DATA = "sticker_pack";

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private StickerPreviewAdapter stickerPreviewAdapter;
    private int numColumns;
    private View addButton;
    private View alreadyAddedText;
    private StickerPack stickerPack;
    private View divider;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;

    private Handler handler;
    private int exitTime;
    private Date dateChange;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_details);

        if (new AdsStatus().getAdsStatus()) {
            exitTime = getResources().getInteger(R.integer.exitTime);
            dateChange = new Date();
            handler = new Handler();
            handler.postDelayed(runnable, 1000 * 60);
        }

        boolean showUpButton = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        stickerPack = getIntent().getParcelableExtra(EXTRA_STICKER_PACK_DATA);
        TextView packNameTextView = findViewById(R.id.pack_name);
        TextView packPublisherTextView = findViewById(R.id.author);
        ImageView packTrayIcon = findViewById(R.id.tray_image);
        TextView packSizeTextView = findViewById(R.id.pack_size);

        try {
            if (new AdsStatus().getAdsStatus()) {
                AdView adView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
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
            }
        } catch (Exception e) {
            WriteLog.GetInstance().addToLog(TAG, "onCreate-mAdView", e.getMessage(), e);
        }

        addButton = findViewById(R.id.add_to_whatsapp_button);
        alreadyAddedText = findViewById(R.id.already_added_text);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView = findViewById(R.id.sticker_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(pageLayoutListener);
        recyclerView.addOnScrollListener(dividerScrollListener);
        divider = findViewById(R.id.divider);
        if (stickerPreviewAdapter == null) {
            stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), stickerPack);
            recyclerView.setAdapter(stickerPreviewAdapter);
        }
        packNameTextView.setText(stickerPack.name);

        packPublisherTextView.setText(stickerPack.publisher);

        if (stickerPack.getType() == 0)
            packTrayIcon.setImageURI(StickerPackLoader.getStickerAssetUri(stickerPack.identifier, stickerPack.trayImageFile));


        if (stickerPack.getType() == 0)
            packSizeTextView.setText(Formatter.formatShortFileSize(this, stickerPack.getTotalSize()));


        addButton.setOnClickListener(v -> {
            if (stickerPack.getType() == 0)
                addStickerPackToWhatsApp(stickerPack.identifier, stickerPack.name);
            checkAndShowAd(this);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
            getSupportActionBar().setTitle(showUpButton ? getResources().getString(R.string.title_activity_sticker_pack_details_multiple_pack) : getResources().getQuantityString(R.plurals.title_activity_sticker_packs_list, 1));
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Date dateNow = new Date();
                long millis = dateNow.getTime() - dateChange.getTime();
                if (millis > exitTime) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(StickerPackListActivity.mContext);
                    int appCountClose = pref.getInt("AppCountClose", 0);
                    appCountClose++;
                    EntryActivity.mAppCountClose = appCountClose;
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(StickerPackListActivity.mContext).edit();
                    editor.putInt("AppCountClose", appCountClose);
                    editor.apply();
                    finish();
                } else {
                    handler.postDelayed(this, 1000 * 60);
                }
            } catch (Exception e) {
                WriteLog.GetInstance().addToLog(TAG, "Runnable", e.getMessage(), e);
            }
        }
    };

    private void checkAndShowAd(Context context) {
        try {
            if (new AdsStatus().getAdsStatus()) {
                StickerPackListActivity.sAdActivity++;
                if (StickerPackListActivity.sAdActivity % StickerPackListActivity.sAdShowActivity == 0) {
                    if (StickerPackListActivity.mInterstitialAd != null) {
                        StickerPackListActivity.mInterstitialAd.show((Activity) context);
                        new SQL_AdsClick().addCountAdsClick();
                        StickerPackListActivity.sAdShowActivity = StickerPackListActivity.sAdShowActivity + context.getResources().getInteger(R.integer.sAdShowActivityAdd);
                        StickerPackListActivity.sAdActivity = 0;
                        AdRequest adRequest = new AdRequest.Builder().build();
                        InterstitialAd.load(context, context.getString(R.string.I_AD_UNIT_ID), adRequest, new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                StickerPackListActivity.mInterstitialAd = interstitialAd;
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                StickerPackListActivity.mInterstitialAd = null;
                            }
                        });
                    }
                }
            }
        } catch (android.content.ActivityNotFoundException e) {
            WriteLog.GetInstance().addToLog(TAG, "checkAndShowAd", e.getMessage(), e);
        }
    }

    private final ViewTreeObserver.OnGlobalLayoutListener pageLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setNumColumns(recyclerView.getWidth() / recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size));
        }
    };

    private void setNumColumns(int numColumns) {
        if (this.numColumns != numColumns) {
            layoutManager.setSpanCount(numColumns);
            this.numColumns = numColumns;
            if (stickerPreviewAdapter != null) {
                stickerPreviewAdapter.notifyDataSetChanged();
            }
        }
    }

    private final RecyclerView.OnScrollListener dividerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            updateDivider(recyclerView);
        }

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateDivider(recyclerView);
        }

        private void updateDivider(RecyclerView recyclerView) {
            boolean showDivider = recyclerView.computeVerticalScrollOffset() > 0;
            if (divider != null) {
                divider.setVisibility(showDivider ? View.VISIBLE : View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        whiteListCheckAsyncTask.execute(stickerPack);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
        try {
            if (handler != null && runnable != null)
                handler.removeCallbacks(runnable);
        } catch (android.content.ActivityNotFoundException e) {
            WriteLog.GetInstance().addToLog(TAG, "onPause", e.getMessage(), e);
        }
    }

    private void updateAddUI(Boolean isWhitelisted) {
        if (isWhitelisted) {
            addButton.setVisibility(View.GONE);
            alreadyAddedText.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            alreadyAddedText.setVisibility(View.GONE);
        }
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, Boolean> {
        private final WeakReference<StickerPackDetailsActivity> stickerPackDetailsActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackDetailsActivity stickerPackListActivity) {
            this.stickerPackDetailsActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final Boolean doInBackground(StickerPack... stickerPacks) {
            StickerPack stickerPack = stickerPacks[0];
            final StickerPackDetailsActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity == null) {
                return false;
            }
            if (stickerPack.getType() == 0)
                return WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier);
            else
                return false;

        }

        @Override
        protected void onPostExecute(Boolean isWhitelisted) {
            final StickerPackDetailsActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity != null) {
                stickerPackDetailsActivity.updateAddUI(isWhitelisted);
            }
        }
    }
}
