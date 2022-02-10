package com.android.settings.deviceinfo.aboutphone

import android.content.Context
import android.os.Build
import android.os.SystemProperties
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.widget.*

import com.android.settings.R

import androidx.appcompat.app.AlertDialog

typealias onDeviceChanged = ((deviceName: String) -> Unit)?

class AboutDevice : FrameLayout {

    private var listener: onDeviceChanged = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.device_info, this)
        // ROM Version
        val version = SystemProperties.get("ro.modversion")
        val type = SystemProperties.get("ro.dot.releasetype")

        findViewById<TextView>(R.id.romVersion).text = (if (type == "GAPPS") {
            version + " " + context.getString(R.string.about_device_version_type_gapps)
        } else {
            version + " " + context.getString(R.string.about_device_version_type_vanilla)
        }).toString()

        // Device
        var mDeviceName = Settings.Global.getString(
            context.contentResolver,
            Settings.Global.DEVICE_NAME
        )
        if (mDeviceName == null) {
            mDeviceName = Build.MODEL
        }
        findViewById<LinearLayout>(R.id.deviceEdit).setOnClickListener {
            val alert: AlertDialog.Builder = AlertDialog.Builder(context, R.style.Theme_AlertDialog)
            val dialogView: View = View.inflate(context, R.layout.dot_device_name_dialog, null)
            val mEditText: EditText = dialogView.findViewById(R.id.device_edit_text)
            alert.setTitle(context.getString(R.string.my_device_info_device_name_preference_title))
            alert.setView(dialogView)
            alert.setPositiveButton(android.R.string.ok) { dialog, _ ->
                listener?.invoke(mEditText.text.toString())
                dialog.dismiss()
            }
            alert.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            // Update device name before showing dialog
            mDeviceName = Settings.Global.getString(
                context.contentResolver,
                Settings.Global.DEVICE_NAME
            )
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
            findViewById<TextView>(R.id.deviceName).text = deviceName
            listener?.invoke(deviceName)
        }
    }

    fun setDeviceName(deviceName: String) {
        findViewById<TextView>(R.id.deviceName).text = deviceName
    }
}