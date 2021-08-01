package com.android.settings.deviceinfo.aboutphone

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

import com.google.android.material.card.MaterialCardView

import com.android.settings.R;

import com.android.settingslib.Utils
@SuppressLint("ClickableViewAccessibility")
open class AboutBaseCard : MaterialCardView {
    protected var layout: RelativeLayout
    protected var defaultPadding = 38
    var defaultRadius = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        defaultRadius = resources.getDimensionPixelSize(R.dimen.contextual_card_corner_radius)
        layoutParams = LayoutParams(resources.getDimensionPixelSize(R.dimen.storage_card_min_width), resources.getDimensionPixelSize(R.dimen.storage_card_min_height))
        layout = RelativeLayout(context)
        layout.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        layout.setPadding(defaultPadding, (defaultPadding * 1.5).toInt(), defaultPadding, (defaultPadding * 1.5).toInt())
        layout.setBackgroundColor(resources.getColor(android.R.color.monet_contextual_color_device_default, context.theme))
        addView(layout)
        radius = defaultRadius.toFloat()
        setCardBackgroundColor(resources.getColor(android.R.color.monet_contextual_color_device_default, context.theme))
        cardElevation = 0f
        strokeColor = resources.getColor(R.color.contextual_card_stroke_color, context.theme)
        strokeWidth = resources.getDimensionPixelSize(R.dimen.contextual_card_stroke_width)
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