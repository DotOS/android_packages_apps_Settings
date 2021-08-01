package com.android.settings.deviceinfo.aboutphone

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.view.View

import androidx.appcompat.app.AlertDialog
import com.android.settings.Utils

import com.android.settings.R;

typealias onDeviceChanged = ((deviceName: String) -> Unit)?
class DeviceCardView : AboutBaseCard {

    private lateinit var card_summary: TextView
    private var listener: onDeviceChanged = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DeviceCardView, defStyleAttr, 0)
        layoutParams = LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val card_title = TextView(context)
        card_title.text = a.getString(R.styleable.DeviceCardView_extra_title)
        card_title.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary))
        card_title.setPadding(0, 0, 0, 24)
        card_title.textSize = 18f
        card_title.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        card_summary = TextView(context)
        var mDeviceName = Settings.Global.getString(context.contentResolver,
                Settings.Global.DEVICE_NAME)
        if (mDeviceName == null) {
            mDeviceName = Build.MODEL
        }
        card_summary.text = mDeviceName
        card_summary.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorSecondary))
        card_summary.textSize = 14f
        card_summary.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayout.addView(card_title)
        linearLayout.addView(card_summary)
        linearLayout.setBackgroundColor(resources.getColor(android.R.color.monet_contextual_color_device_default, context.theme))
        layout.addView(linearLayout)
        a.recycle()
        layout.setOnClickListener {
            val alert: AlertDialog.Builder = AlertDialog.Builder(mContext, R.style.Theme_AlertDialog)
            val dialogView: View = View.inflate(mContext, R.layout.dot_device_name_dialog, null)
            val mEditText: EditText = dialogView.findViewById(R.id.device_edit_text)
            alert.setTitle(mContext.getString(R.string.my_device_info_device_name_preference_title))
            alert.setView(dialogView)
            alert.setPositiveButton(android.R.string.ok) { dialog, _ ->
                listener?.invoke(mEditText.text.toString())
                dialog.dismiss()
            }
            alert.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            //update device name before showing dialog
            mDeviceName = Settings.Global.getString(context.contentResolver,
                Settings.Global.DEVICE_NAME)
            if (mDeviceName == null) {
                mDeviceName = Build.MODEL
            }
            mEditText.setText(mDeviceName)
            alert.show() 
        }
    }

    fun setListener(listener: onDeviceChanged) {
        this.listener = listener
    }

    fun setDeviceName(deviceName: String, validator: Boolean) {
        if (validator) {
            card_summary.text = deviceName
            listener?.invoke(deviceName)
        }
    }

    fun setDeviceName(deviceName: String) {
        card_summary.text = deviceName
    }
}