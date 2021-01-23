package com.android.settings.deviceinfo.aboutphone

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView

import com.android.settings.R;
@SuppressLint("ClickableViewAccessibility")
open class AboutBaseCard : CardView {
    protected var layout: RelativeLayout
    protected var defaultPadding = 38
    protected var defaultRadius = 48

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutParams = LayoutParams(resources.getDimensionPixelSize(R.dimen.storage_card_min_width), resources.getDimensionPixelSize(R.dimen.storage_card_min_height))
        layout = RelativeLayout(context)
        layout.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        layout.setPadding(defaultPadding, (defaultPadding * 1.5).toInt(), defaultPadding, (defaultPadding * 1.5).toInt())
        layout.setBackgroundColor(resources.getColor(R.color.search_bar_background, null))
        addView(layout)
        radius = defaultRadius.toFloat()
        setCardBackgroundColor(resources.getColor(R.color.search_bar_background, null))
        setTouchListener(layout)
    }

    fun setTouchListener(view: View) {
        view.isClickable = true
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scaleDownX = ObjectAnimator.ofFloat(
                            this,
                            "scaleX", 0.9f
                    )
                    val scaleDownY = ObjectAnimator.ofFloat(
                            this,
                            "scaleY", 0.9f
                    )
                    scaleDownX.duration = 200
                    scaleDownY.duration = 200
                    val scaleDown = AnimatorSet()
                    scaleDown.play(scaleDownX).with(scaleDownY)
                    scaleDown.start()
                }
                MotionEvent.ACTION_UP -> {
                    val scaleDownX2 = ObjectAnimator.ofFloat(
                            this, "scaleX", 1f
                    )
                    val scaleDownY2 = ObjectAnimator.ofFloat(
                            this, "scaleY", 1f
                    )
                    scaleDownX2.duration = 200
                    scaleDownY2.duration = 200
                    val scaleDown2 = AnimatorSet()
                    scaleDown2.play(scaleDownX2).with(scaleDownY2)
                    scaleDown2.start()
                }
                MotionEvent.ACTION_CANCEL -> {
                    val scaleDownX2 = ObjectAnimator.ofFloat(
                            this, "scaleX", 1f
                    )
                    val scaleDownY2 = ObjectAnimator.ofFloat(
                            this, "scaleY", 1f
                    )
                    scaleDownX2.duration = 200
                    scaleDownY2.duration = 200
                    val scaleDown2 = AnimatorSet()
                    scaleDown2.play(scaleDownX2).with(scaleDownY2)
                    scaleDown2.start()
                }
            }
            false
        }
    }
}