package com.android.settings.display.darkmode

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

import com.android.settings.R;

import com.android.settingslib.Utils;

import com.google.android.material.card.MaterialCardView

class DarkModeAdapter : RecyclerView.Adapter<DarkModeAdapter.ViewHolder>() {

    val items: ArrayList<DarkModeOption> = ArrayList()

    init {
        items.add(DarkModeOption(Type.LIGHT))
        items.add(DarkModeOption(Type.DARK))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.dmCard)
        val preview: FrameLayout = view.findViewById(R.id.dmPreview)
        val title: TextView = view.findViewById(R.id.dmTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dark_mode_preview, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        val accentColor = Utils.getColorAttrDefaultColor(context, android.R.attr.colorAccent)
        val textColor = Utils.getColorAttrDefaultColor(context, android.R.attr.textColorSecondary)
        val uiManager = context.getSystemService(UiModeManager::class.java)
        val isLightTheme = item.type == Type.LIGHT
        val themeEquiavlent = if (isLightTheme) UiModeManager.MODE_NIGHT_NO else UiModeManager.MODE_NIGHT_YES
        item.enabled = themeEquiavlent == uiManager.nightMode
        if (item.enabled) {
            holder.card.strokeColor = accentColor
            holder.title.setTextColor(accentColor)
        } else {
            holder.card.strokeColor = textColor
            holder.title.setTextColor(textColor)
        }
        holder.title.text = if (isLightTheme) context.getString(R.string.dot_lightMode) else context.getString(R.string.dot_darkMode)
        holder.preview.addView(getPreview(context, item.type))
        holder.preview.isClickable = true
        holder.preview.setOnClickListener {
            uiManager.setNightModeActivated(!isLightTheme)
            item.enabled = themeEquiavlent == uiManager.nightMode
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getPreview(context: Context, type: Type): View {
        val view = if (type == Type.LIGHT)
            LayoutInflater.from(context).inflate(R.layout.dark_mode_preview_light, null)
        else
            LayoutInflater.from(context).inflate(R.layout.dark_mode_preview_dark, null)
        val wallpaperLayout = view.requireViewById<ImageView>(R.id.wallpaperImage)
        val wallpaperManager = WallpaperManager.getInstance(context)
        val pfd = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM)
        if (pfd != null)
            wallpaperLayout.setImageDrawable(BitmapDrawable(context.resources, BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)))
        else
            wallpaperLayout.setImageDrawable(wallpaperManager.drawable)
        return view
    }

    class DarkModeOption(val type: Type) {
        var enabled: Boolean = false
    }

    enum class Type {
        LIGHT, DARK
    }
}