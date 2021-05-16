package com.android.settings.deviceinfo.aboutphone

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import android.os.storage.StorageManager
import android.os.storage.VolumeInfo
import android.provider.Settings
import android.text.format.Formatter
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.settings.Utils
import java.io.IOException
import kotlin.math.sin

import com.android.settings.R;

object Size {
    const val HUGE = 0
    const val LARGE = 1
    const val MIDDLE = 2
    const val LITTLE = 3
}

class StorageCardView(context: Context, attrs: AttributeSet?) : AboutBaseCard(context, attrs) {
    private var freeBytes: Long = 0
    private var usedBytes: Long = 0
    private var totalBytes: Long = 0
    private var mUsedPercent = -1
    private var waveView: WaveView
    private var anim: ProgressBarAnimation? = null

    init {
        layoutParams = LayoutParams(resources.getDimensionPixelSize(R.dimen.storage_card_min_width), resources.getDimensionPixelSize(R.dimen.storage_card_min_height))
        layout = RelativeLayout(context)
        layout.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.storage_card_min_height))
        waveView = WaveView(context)
        waveView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        waveView.alpha = 0.5f
        layout.isClickable = true
        val storageTitle = TextView(context)
        storageTitle.textSize = 18f
        storageTitle.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary))
        storageTitle.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        storageTitle.alpha = 0.9f
        storageTitle.setPadding(defaultPadding, defaultPadding, 0, 0)
        storageTitle.text = resources.getString(R.string.storage_card_title)
        layout.addView(waveView)
        layout.addView(storageTitle)
        setupStorageInfo(context)
        addView(layout)
        setTouchListener(layout)
        radius = defaultRadius.toFloat()
        layout.setOnClickListener {
            context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
        }
    }

    private fun setupStorageInfo(context: Context?) {
        val storageInfoUsed = TextView(context)
        storageInfoUsed.textSize = 26f
        storageInfoUsed.id = R.id.storage_info_used_id
        storageInfoUsed.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary))
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        storageInfoUsed.layoutParams = params
        storageInfoUsed.gravity = Gravity.START or Gravity.BOTTOM
        storageInfoUsed.alpha = 0.9f
        storageInfoUsed.setPadding(defaultPadding, 0, 0, defaultPadding)
        val storageInfoTotal = TextView(context)
        storageInfoTotal.textSize = 16f
        storageInfoTotal.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary))
        val params2 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params2.addRule(RelativeLayout.END_OF, R.id.storage_info_used_id)
        params2.addRule(RelativeLayout.ALIGN_BASELINE, R.id.storage_info_used_id)
        storageInfoTotal.layoutParams = params2
        storageInfoTotal.alpha = 0.9f
        val storageInfo = TextView(context)
        storageInfo.textSize = 14f
        storageInfo.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorSecondary))
        val params3 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params3.addRule(RelativeLayout.ABOVE, R.id.storage_info_used_id)
        storageInfo.layoutParams = params3
        storageInfo.alpha = 0.7f
        storageInfo.setPadding(defaultPadding, 0, 0, 0)
        storageInfo.text = resources.getString(R.string.storage_card_info)
        manageStorageInfo(storageInfoUsed, storageInfoTotal)
        layout.addView(storageInfoUsed)
        layout.addView(storageInfoTotal)
        layout.addView(storageInfo)
    }

    private fun manageStorageInfo(storageInfoUsed: TextView, storageInfoTotal: TextView) {
        val storageManager: StorageManager? = context.getSystemService(StorageManager::class.java)
        if (storageManager != null) {
            val volumes = storageManager.volumes
            for (vol in volumes) {
                val path = vol.getPath()
                if (vol.isMountedReadable) {
                    if (vol.getType() == VolumeInfo.TYPE_PRIVATE) {
                        val stats = context.getSystemService(StorageStatsManager::class.java)
                        try {
                            totalBytes = stats.getTotalBytes(vol.getFsUuid())
                            freeBytes = stats.getFreeBytes(vol.getFsUuid())
                            usedBytes = totalBytes - freeBytes
                        } catch (e: IOException) {
                            Log.w("StorageManager", e)
                        }
                    }
                    val used = Formatter.formatFileSize(context, usedBytes, Formatter.FLAG_SHORTER)
                    val total =
                        Formatter.formatFileSize(context, totalBytes, Formatter.FLAG_SHORTER)
                    storageInfoUsed.text = used
                    storageInfoTotal.text = String.format("/%s", total)
                    if (totalBytes > 0) {
                        mUsedPercent = (usedBytes * 100 / totalBytes).toInt()
                        waveView.progress = mUsedPercent
                    }
                    waveView.setLowStorage(freeBytes < storageManager.getStorageLowBytes(path))
                }
            }
            anim = ProgressBarAnimation(waveView, 100f, mUsedPercent.toFloat())
            anim!!.duration = 600
            waveView.startAnimation(anim!!)
        } else {
            // Just for the sake of layout preview
            storageInfoUsed.text = "127 GB"
            storageInfoTotal.text = String.format("/%s", "128 GB")
        }
    }

    private inner class ProgressBarAnimation(private val waveView: WaveView?, private val from: Float, private val to: Float) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            waveView!!.progress = value.toInt()
        }
    }

    private inner class WaveView(context: Context?) : LinearLayout(context) {
        private var mAboveWaveColor = Utils.getColorAttrDefaultColor(context, android.R.attr.colorAccent)
        private var mProgress = 0
        private val mWaveHeight = 2
        private val mWaveMultiple = 2
        private val mWaveHz = 2
        private var mWaveToTop = 0
        private val mWave: Wave
        private val mSolid: Solid
        fun setLowStorage(lowStorage: Boolean) {
            mAboveWaveColor = if (lowStorage) Utils.getColorAttrDefaultColor(context, android.R.attr.colorError) else Utils.getColorAttrDefaultColor(context, android.R.attr.colorAccent)
            mWave.setAboveWaveColor(mAboveWaveColor)
            mWave.setBlowWaveColor(mAboveWaveColor)
        }

        var progress: Int
            get() = mProgress
            set(progress) {
                mProgress = if (progress > 100) 100 else progress
                computeWaveToTop()
            }

        override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
            super.onWindowFocusChanged(hasWindowFocus)
            if (hasWindowFocus) {
                computeWaveToTop()
            }
        }

        private fun computeWaveToTop() {
            mWaveToTop = (height * (1f - mProgress / 100f)).toInt()
            val params = mWave.layoutParams
            if (params != null) {
                (params as LayoutParams).topMargin = mWaveToTop
            }
            mWave.layoutParams = params
        }

        public override fun onSaveInstanceState(): Parcelable {
            val superState = super.onSaveInstanceState()
            val ss: SavedState = SavedState(superState)
            ss.progress = mProgress
            return ss
        }

        public override fun onRestoreInstanceState(state: Parcelable) {
            val ss = state as SavedState
            super.onRestoreInstanceState(ss.superState)
            progress = ss.progress
        }

        private inner class SavedState : BaseSavedState {
            var progress = 0

            constructor(superState: Parcelable?) : super(superState)
            private constructor(`in`: Parcel) : super(`in`) {
                progress = `in`.readInt()
            }

            override fun writeToParcel(out: Parcel, flags: Int) {
                super.writeToParcel(out, flags)
                out.writeInt(progress)
            }

            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        init {
            orientation = VERTICAL
            mWave = Wave(context, null)
            mWave.initializeWaveSize(mWaveMultiple, mWaveHeight, mWaveHz)
            mWave.setAboveWaveColor(mAboveWaveColor)
            mWave.setBlowWaveColor(mAboveWaveColor)
            mWave.initializePainters()
            mSolid = Solid(context, null)
            mSolid.setAboveWavePaint(mWave.aboveWavePaint)
            mSolid.setBlowWavePaint(mWave.blowWavePaint)
            addView(mWave)
            addView(mSolid)
            progress = mProgress
        }
    }

    private inner class Wave @JvmOverloads constructor(context: Context?, attrs: AttributeSet?, defStyle: Int = R.attr.waveViewStyle) : View(context, attrs, defStyle) {
        private val WAVE_HEIGHT_HUGE = 36
        private val WAVE_HEIGHT_LARGE = 16
        private val WAVE_HEIGHT_MIDDLE = 8
        private val WAVE_HEIGHT_LITTLE = 5
        private val WAVE_LENGTH_MULTIPLE_LARGE = 1.5f
        private val WAVE_LENGTH_MULTIPLE_MIDDLE = 1f
        private val WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f
        private val WAVE_HZ_FAST = 0.13f
        private val WAVE_HZ_NORMAL = 0.09f
        private val WAVE_HZ_SLOW = 0.05f
        val DEFAULT_ABOVE_WAVE_ALPHA = 255
        val DEFAULT_BLOW_WAVE_ALPHA = 100
        private val X_SPACE = 20f
        private val PI2 = 2 * Math.PI
        private val mAboveWavePath = Path()
        private val mBlowWavePath = Path()
        val aboveWavePaint = Paint()
        val blowWavePaint = Paint()
        private var mAboveWaveColor = 0
        private var mBlowWaveColor = 0
        private var mWaveMultiple = 0f
        private var mWaveLength = 0f
        private var mWaveHeight = 0
        private var mMaxRight = 0f
        private var mWaveHz = 0f
        private var mAboveOffset = 0.0f
        private var mBlowOffset = 0f
        private var mRefreshProgressRunnable: RefreshProgressRunnable? = null
        private var omega = 0.0
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawPath(mBlowWavePath, blowWavePaint)
            canvas.drawPath(mAboveWavePath, aboveWavePaint)
        }

        fun setAboveWaveColor(aboveWaveColor: Int) {
            mAboveWaveColor = aboveWaveColor
        }

        fun setBlowWaveColor(blowWaveColor: Int) {
            mBlowWaveColor = blowWaveColor
        }

        fun initializeWaveSize(waveMultiple: Int, waveHeight: Int, waveHz: Int) {
            mWaveMultiple = getWaveMultiple(waveMultiple)
            mWaveHeight = getWaveHeight(waveHeight)
            mWaveHz = getWaveHz(waveHz)
            mBlowOffset = mWaveHeight * 0.4f
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mWaveHeight * 2)
            layoutParams = params
        }

        fun initializePainters() {
            aboveWavePaint.color = mAboveWaveColor
            aboveWavePaint.alpha = DEFAULT_ABOVE_WAVE_ALPHA
            aboveWavePaint.style = Paint.Style.FILL
            aboveWavePaint.isAntiAlias = true
            blowWavePaint.color = mBlowWaveColor
            blowWavePaint.alpha = DEFAULT_BLOW_WAVE_ALPHA
            blowWavePaint.style = Paint.Style.FILL
            blowWavePaint.isAntiAlias = true
        }

        private fun getWaveMultiple(size: Int): Float {
            when (size) {
                Size.LARGE -> return WAVE_LENGTH_MULTIPLE_LARGE
                Size.MIDDLE -> return WAVE_LENGTH_MULTIPLE_MIDDLE
                Size.LITTLE -> return WAVE_LENGTH_MULTIPLE_LITTLE
            }
            return 0f
        }

        private fun getWaveHeight(size: Int): Int {
            when (size) {
                Size.HUGE -> return WAVE_HEIGHT_HUGE
                Size.LARGE -> return WAVE_HEIGHT_LARGE
                Size.MIDDLE -> return WAVE_HEIGHT_MIDDLE
                Size.LITTLE -> return WAVE_HEIGHT_LITTLE
            }
            return 0
        }

        private fun getWaveHz(size: Int): Float {
            when (size) {
                Size.LARGE -> return WAVE_HZ_FAST
                Size.MIDDLE -> return WAVE_HZ_NORMAL
                Size.LITTLE -> return WAVE_HZ_SLOW
            }
            return 0f
        }

        private fun calculatePath() {
            mAboveWavePath.reset()
            mBlowWavePath.reset()
            waveOffset
            var y: Float
            mAboveWavePath.moveTo(mLeft.toFloat(), mBottom.toFloat())
            run {
                var x = 0f
                while (x <= mMaxRight) {
                    y = (mWaveHeight * sin(omega * x + mAboveOffset) + mWaveHeight).toFloat()
                    mAboveWavePath.lineTo(x, y)
                    x += X_SPACE
                }
            }
            mAboveWavePath.lineTo(mRight.toFloat(), mBottom.toFloat())
            mBlowWavePath.moveTo(mLeft.toFloat(), mBottom.toFloat())
            var x = 0f
            while (x <= mMaxRight) {
                y = (mWaveHeight * Math.sin(omega * x + mBlowOffset) + mWaveHeight).toFloat()
                mBlowWavePath.lineTo(x, y)
                x += X_SPACE
            }
            mBlowWavePath.lineTo(mRight.toFloat(), mBottom.toFloat())
        }

        override fun onWindowVisibilityChanged(visibility: Int) {
            super.onWindowVisibilityChanged(visibility)
            if (GONE == visibility) {
                removeCallbacks(mRefreshProgressRunnable)
            } else {
                removeCallbacks(mRefreshProgressRunnable)
                mRefreshProgressRunnable = RefreshProgressRunnable()
                post(mRefreshProgressRunnable)
            }
        }

        override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
            super.onWindowFocusChanged(hasWindowFocus)
            if (hasWindowFocus) {
                if (mWaveLength == 0f) {
                    startWave()
                }
            }
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            if (mWaveLength == 0f) {
                startWave()
            }
        }

        private fun startWave() {
            if (width != 0) {
                val width = width
                mWaveLength = width * mWaveMultiple
                mLeft = left
                mRight = right
                mBottom = bottom + 2
                mMaxRight = right + X_SPACE
                omega = PI2 / mWaveLength
            }
        }

        private val waveOffset: Unit
            get() {
                if (mBlowOffset > Float.MAX_VALUE - 100) {
                    mBlowOffset = 0f
                } else {
                    mBlowOffset += mWaveHz
                }
                if (mAboveOffset > Float.MAX_VALUE - 100) {
                    mAboveOffset = 0f
                } else {
                    mAboveOffset += mWaveHz
                }
            }

        private inner class RefreshProgressRunnable : Runnable {
            override fun run() {
                synchronized(this) {
                    val start = System.currentTimeMillis()
                    calculatePath()
                    invalidate()
                    val gap = 16 - (System.currentTimeMillis() - start)
                    postDelayed(this, if (gap < 0) 0 else gap)
                }
            }
        }
    }

    private inner class Solid @JvmOverloads constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
        private var aboveWavePaint: Paint? = null
        private var blowWavePaint: Paint? = null
        fun setAboveWavePaint(aboveWavePaint: Paint?) {
            this.aboveWavePaint = aboveWavePaint
        }

        fun setBlowWavePaint(blowWavePaint: Paint?) {
            this.blowWavePaint = blowWavePaint
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(left.toFloat(), 0f, right.toFloat(), bottom.toFloat(), blowWavePaint)
            canvas.drawRect(left.toFloat(), 0f, right.toFloat(), bottom.toFloat(), aboveWavePaint)
        }

        init {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.weight = 1f
            layoutParams = params
        }
    }
}