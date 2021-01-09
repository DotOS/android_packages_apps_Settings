package com.android.settings.fuelgauge

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView

import com.android.settings.R

class BatteryStatsView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val statsLastFullCharge: TextView
    private val statsLastFullChargeTitle: TextView
    private val statsSOT: TextView

    init {
        LayoutInflater.from(context).inflate(
            R.layout.dot_battery_stats, this, true
        )
        statsLastFullCharge = findViewById(R.id.statsLastCharge)
        statsLastFullChargeTitle = findViewById(R.id.statsLastChargeTitle)
        statsSOT = findViewById(R.id.statsSOT)
    }

    fun updateLastFullCharge(chars: CharSequence) {
        statsLastFullCharge.text = chars
    }

    fun updateLastFullChargeTitle(resID: Int) {
        statsLastFullChargeTitle.text = context!!.getString(resID)
    }

    fun updateSOT(chars: CharSequence) {
        statsSOT.text = chars
    }
}