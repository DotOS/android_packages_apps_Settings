package com.android.settings.deviceinfo.aboutphone;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settings.R;
import com.android.settings.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.text.format.Formatter.FLAG_SHORTER;

public class StorageCardView extends AboutBaseCard {

    private long freeBytes = 0;
    private long usedBytes = 0;
    private long totalBytes = 0;
    private int mUsedPercent = -1;
    private WaveView waveView;

    public StorageCardView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public StorageCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StorageCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void init(Context context) {
        setCardBackgroundColor(getResources().getColor(R.color.search_bar_background));
        setLayoutParams(new LayoutParams(getResources().getDimensionPixelSize(R.dimen.storage_card_min_width), getResources().getDimensionPixelSize(R.dimen.storage_card_min_height)));
        layout = new RelativeLayout(context);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.storage_card_min_height)));
        waveView = new WaveView(context);
        layout.setClickable(true);
        TextView storageTitle = new TextView(context);
        storageTitle.setTextSize(18);
        storageTitle.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary));
        storageTitle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        storageTitle.setAlpha((float) 0.9);
        storageTitle.setPadding(defaultPadding,defaultPadding,0,0);
        storageTitle.setText(getResources().getString(R.string.storage_card_title));
        layout.addView(waveView);
        layout.addView(storageTitle);
        waveView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        setupStorageInfo(context);
        addView(layout);
        setRadius(defaultRadius);
    }

    private void setupStorageInfo(Context context) {
        TextView storageInfoUsed = new TextView(context);
        storageInfoUsed.setTextSize(26);
        storageInfoUsed.setId(R.id.storage_info_used_id);
        storageInfoUsed.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        storageInfoUsed.setLayoutParams(params);
        storageInfoUsed.setGravity(Gravity.START|Gravity.BOTTOM);
        storageInfoUsed.setAlpha((float) 0.9);
        storageInfoUsed.setPadding(defaultPadding, 0,0,defaultPadding);
        TextView storageInfoTotal = new TextView(context);
        storageInfoTotal.setTextSize(16);
        storageInfoTotal.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary));
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.END_OF, R.id.storage_info_used_id);
        params2.addRule(RelativeLayout.ALIGN_BASELINE, R.id.storage_info_used_id);
        storageInfoTotal.setLayoutParams(params2);
        storageInfoTotal.setAlpha((float) 0.9);
        TextView storageInfo = new TextView(context);
        storageInfo.setTextSize(14);
        storageInfo.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorSecondary));
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.ABOVE, R.id.storage_info_used_id);
        storageInfo.setLayoutParams(params3);
        storageInfo.setAlpha((float) 0.7);
        storageInfo.setPadding(defaultPadding, 0,0,0);
        storageInfo.setText(getResources().getString(R.string.storage_card_info));
        manageStorageInfo(storageInfoUsed, storageInfoTotal);
        layout.addView(storageInfoUsed);
        layout.addView(storageInfoTotal);
        layout.addView(storageInfo);
    }

    protected void manageStorageInfo(TextView storageInfoUsed, TextView storageInfoTotal) {
        StorageManager storageManager = getContext().getSystemService(StorageManager.class);
        List<VolumeInfo> volumes = storageManager.getVolumes();
        for (VolumeInfo vol : volumes) {
            final File path = vol.getPath();
            if (vol.isMountedReadable()) {
                if (vol.getType() == VolumeInfo.TYPE_PRIVATE) {
                    final StorageStatsManager stats = getContext().getSystemService(StorageStatsManager.class);
                    try {
                        totalBytes = stats.getTotalBytes(vol.getFsUuid());
                        freeBytes = stats.getFreeBytes((vol.getFsUuid()));
                        usedBytes = totalBytes - freeBytes;
                    } catch (IOException e) {
                        Log.w("StorageManager", e);
                    }
                } /*else {
                    if (totalBytes <= 0) {
                        totalBytes = path.getTotalSpace();
                    }
                    freeBytes = path.getFreeSpace();
                    usedBytes = totalBytes - freeBytes;
                }*/
                String used = Formatter.formatFileSize(getContext(), usedBytes, FLAG_SHORTER);
                String total = Formatter.formatFileSize(getContext(), totalBytes, FLAG_SHORTER);
                storageInfoUsed.setText(used);
                storageInfoTotal.setText(String.format("/%s", total));
                if (totalBytes > 0) {
                    mUsedPercent = (int) ((usedBytes * 100) / totalBytes);
                    waveView.setProgress(mUsedPercent);
                }
                waveView.setLowStorage(freeBytes < storageManager.getStorageLowBytes(path));
            }
        }
        ProgressBarAnimation anim = new ProgressBarAnimation(waveView, 100, mUsedPercent);
        anim.setDuration(1250);
        waveView.startAnimation(anim);
    }

    protected class ProgressBarAnimation extends Animation {
        private WaveView waveView;
        private float from;
        private float to;

        ProgressBarAnimation(WaveView waveView, float from, float to) {
            super();
            this.waveView = waveView;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            waveView.setProgress((int) value);
        }

    }

    protected class WaveView extends LinearLayout {
        protected static final int HUGE = 0;
        protected static final int LARGE = 1;
        protected static final int MIDDLE = 2;
        protected static final int LITTLE = 3;
        private int mAboveWaveColor = Utils.getColorAttrDefaultColor(this.getContext(), android.R.attr.colorAccent);
        private int mProgress;
        private int mWaveHeight = 2;
        private int mWaveMultiple = 2;
        private int mWaveHz = 2;
        private int mWaveToTop;
        private Wave mWave;
        private Solid mSolid;
        public WaveView(Context context) {
            super(context);
            setOrientation(VERTICAL);
            mWave = new Wave(context, null);
            mWave.initializeWaveSize(mWaveMultiple, mWaveHeight, mWaveHz);
            mWave.setAboveWaveColor(mAboveWaveColor);
            mWave.setBlowWaveColor(mAboveWaveColor);
            mWave.initializePainters();
            mSolid = new Solid(context, null);
            mSolid.setAboveWavePaint(mWave.getAboveWavePaint());
            mSolid.setBlowWavePaint(mWave.getBlowWavePaint());
            addView(mWave);
            addView(mSolid);
            setProgress(mProgress);
        }

        public void setLowStorage(boolean lowStorage) {
            if (lowStorage) mAboveWaveColor = Utils.getColorAttrDefaultColor(getContext(), android.R.attr.colorError);
            else mAboveWaveColor = Utils.getColorAttrDefaultColor(getContext(), android.R.attr.colorAccent);
            mWave.setAboveWaveColor(mAboveWaveColor);
            mWave.setBlowWaveColor(mAboveWaveColor);
        }

        public void setProgress(int progress) {
            this.mProgress = progress > 100 ? 100 : progress;
            computeWaveToTop();
        }
        public int getProgress() {
            return this.mProgress;
        }
        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus) {
                computeWaveToTop();
            }
        }
        private void computeWaveToTop() {
            mWaveToTop = (int) (getHeight() * (1f - mProgress / 100f));
            ViewGroup.LayoutParams params = mWave.getLayoutParams();
            if (params != null) {
                ((LinearLayout.LayoutParams) params).topMargin = mWaveToTop;
            }
            mWave.setLayoutParams(params);
        }
        @Override
        public Parcelable onSaveInstanceState() {
            Parcelable superState = super.onSaveInstanceState();
            WaveView.SavedState ss = new WaveView.SavedState(superState);
            ss.progress = mProgress;
            return ss;
        }
        @Override
        public void onRestoreInstanceState(Parcelable state) {
            WaveView.SavedState ss = (WaveView.SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            setProgress(ss.progress);
        }
        private class SavedState extends BaseSavedState {
            int progress;
            SavedState(Parcelable superState) {
                super(superState);
            }
            private SavedState(Parcel in) {
                super(in);
                progress = in.readInt();
            }
            @Override
            public void writeToParcel(Parcel out, int flags) {
                super.writeToParcel(out, flags);
                out.writeInt(progress);
            }
            public final Creator<WaveView.SavedState> CREATOR = new Creator<WaveView.SavedState>() {
                public WaveView.SavedState createFromParcel(Parcel in) {
                    return new WaveView.SavedState(in);
                }
                public WaveView.SavedState[] newArray(int size) {
                    return new WaveView.SavedState[size];
                }
            };
        }
    }

    protected class Wave extends View {
        private final int WAVE_HEIGHT_HUGE = 36;
        private final int WAVE_HEIGHT_LARGE = 16;
        private final int WAVE_HEIGHT_MIDDLE = 8;
        private final int WAVE_HEIGHT_LITTLE = 5;
        private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
        private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
        private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;
        private final float WAVE_HZ_FAST = 0.13f;
        private final float WAVE_HZ_NORMAL = 0.09f;
        private final float WAVE_HZ_SLOW = 0.05f;
        public final int DEFAULT_ABOVE_WAVE_ALPHA = 100;
        public final int DEFAULT_BLOW_WAVE_ALPHA = 50;
        private final float X_SPACE = 20;
        private final double PI2 = 2 * Math.PI;
        private Path mAboveWavePath = new Path();
        private Path mBlowWavePath = new Path();
        private Paint mAboveWavePaint = new Paint();
        private Paint mBlowWavePaint = new Paint();
        private int mAboveWaveColor;
        private int mBlowWaveColor;
        private float mWaveMultiple;
        private float mWaveLength;
        private int mWaveHeight;
        private float mMaxRight;
        private float mWaveHz;
        private float mAboveOffset = 0.0f;
        private float mBlowOffset;
        private Wave.RefreshProgressRunnable mRefreshProgressRunnable;
        private int left, right, bottom;
        private double omega;
        public Wave(Context context, AttributeSet attrs) {
            this(context, attrs, R.attr.waveViewStyle);
        }
        public Wave(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(mBlowWavePath, mBlowWavePaint);
            canvas.drawPath(mAboveWavePath, mAboveWavePaint);
        }
        public void setAboveWaveColor(int aboveWaveColor) {
            this.mAboveWaveColor = aboveWaveColor;
        }
        public void setBlowWaveColor(int blowWaveColor) {
            this.mBlowWaveColor = blowWaveColor;
        }
        public Paint getAboveWavePaint() {
            return mAboveWavePaint;
        }
        public Paint getBlowWavePaint() {
            return mBlowWavePaint;
        }
        public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
            mWaveMultiple = getWaveMultiple(waveMultiple);
            mWaveHeight = getWaveHeight(waveHeight);
            mWaveHz = getWaveHz(waveHz);
            mBlowOffset = mWaveHeight * 0.4f;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mWaveHeight * 2);
            setLayoutParams(params);
        }
        public void initializePainters() {
            mAboveWavePaint.setColor(mAboveWaveColor);
            mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
            mAboveWavePaint.setStyle(Paint.Style.FILL);
            mAboveWavePaint.setAntiAlias(true);
            mBlowWavePaint.setColor(mBlowWaveColor);
            mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
            mBlowWavePaint.setStyle(Paint.Style.FILL);
            mBlowWavePaint.setAntiAlias(true);
        }
        private float getWaveMultiple(int size) {
            switch (size) {
                case WaveView.LARGE:
                    return WAVE_LENGTH_MULTIPLE_LARGE;
                case WaveView.MIDDLE:
                    return WAVE_LENGTH_MULTIPLE_MIDDLE;
                case WaveView.LITTLE:
                    return WAVE_LENGTH_MULTIPLE_LITTLE;
            }
            return 0;
        }
        private int getWaveHeight(int size) {
            switch (size) {
                case WaveView.HUGE:
                    return WAVE_HEIGHT_HUGE;
                case WaveView.LARGE:
                    return WAVE_HEIGHT_LARGE;
                case WaveView.MIDDLE:
                    return WAVE_HEIGHT_MIDDLE;
                case WaveView.LITTLE:
                    return WAVE_HEIGHT_LITTLE;
            }
            return 0;
        }
        private float getWaveHz(int size) {
            switch (size) {
                case WaveView.LARGE:
                    return WAVE_HZ_FAST;
                case WaveView.MIDDLE:
                    return WAVE_HZ_NORMAL;
                case WaveView.LITTLE:
                    return WAVE_HZ_SLOW;
            }
            return 0;
        }
        private void calculatePath() {
            mAboveWavePath.reset();
            mBlowWavePath.reset();
            getWaveOffset();
            float y;
            mAboveWavePath.moveTo(left, bottom);
            for (float x = 0; x <= mMaxRight; x += X_SPACE) {
                y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
                mAboveWavePath.lineTo(x, y);
            }
            mAboveWavePath.lineTo(right, bottom);
            mBlowWavePath.moveTo(left, bottom);
            for (float x = 0; x <= mMaxRight; x += X_SPACE) {
                y = (float) (mWaveHeight * Math.sin(omega * x + mBlowOffset) + mWaveHeight);
                mBlowWavePath.lineTo(x, y);
            }
            mBlowWavePath.lineTo(right, bottom);
        }
        @Override
        protected void onWindowVisibilityChanged(int visibility) {
            super.onWindowVisibilityChanged(visibility);
            if (View.GONE == visibility) {
                removeCallbacks(mRefreshProgressRunnable);
            } else {
                removeCallbacks(mRefreshProgressRunnable);
                mRefreshProgressRunnable = new Wave.RefreshProgressRunnable();
                post(mRefreshProgressRunnable);
            }
        }
        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus) {
                if (mWaveLength == 0) {
                    startWave();
                }
            }
        }
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            if (mWaveLength==0){
                startWave();
            }
        }
        private void startWave() {
            if (getWidth() != 0) {
                int width = getWidth();
                mWaveLength = width * mWaveMultiple;
                left = getLeft();
                right = getRight();
                bottom = getBottom() + 2;
                mMaxRight = right + X_SPACE;
                omega = PI2 / mWaveLength;
            }
        }
        private void getWaveOffset() {
            if (mBlowOffset > Float.MAX_VALUE - 100) {
                mBlowOffset = 0;
            } else {
                mBlowOffset += mWaveHz;
            }
            if (mAboveOffset > Float.MAX_VALUE - 100) {
                mAboveOffset = 0;
            } else {
                mAboveOffset += mWaveHz;
            }
        }
        private class RefreshProgressRunnable implements Runnable {
            public void run() {
                synchronized (this) {
                    long start = System.currentTimeMillis();
                    calculatePath();
                    invalidate();
                    long gap = 16 - (System.currentTimeMillis() - start);
                    postDelayed(this, gap < 0 ? 0 : gap);
                }
            }
        }
    }

    protected class Solid extends View {
        private Paint aboveWavePaint;
        private Paint blowWavePaint;
        public Solid(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            setLayoutParams(params);
        }
        public void setAboveWavePaint(Paint aboveWavePaint) {
            this.aboveWavePaint = aboveWavePaint;
        }
        public void setBlowWavePaint(Paint blowWavePaint) {
            this.blowWavePaint = blowWavePaint;
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), blowWavePaint);
            canvas.drawRect(getLeft(), 0, getRight(), getBottom(), aboveWavePaint);
        }
    }
}