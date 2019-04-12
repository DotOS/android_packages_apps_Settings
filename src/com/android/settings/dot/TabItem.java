//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.settings.dot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.R.styleable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;

public class TabItem extends View {
    public CharSequence text;
    public final Drawable icon;
    public final int customLayout;

    public TabItem(Context context) {
        this(context, (AttributeSet)null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, styleable.TabItem);
        this.text = a.getText(styleable.TabItem_android_text);
        this.icon = a.getDrawable(styleable.TabItem_android_icon);
        this.customLayout = a.getResourceId(styleable.TabItem_android_layout, 0);
        a.recycle();
    }
}
