package com.android.settings.display.darkmode

import android.content.Context
import android.util.AttributeSet

import com.android.settings.R

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DarkModeView : RecyclerView {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    init {
        adapter = DarkModeAdapter()
        layoutManager = GridLayoutManager(context, 2)
    }
}