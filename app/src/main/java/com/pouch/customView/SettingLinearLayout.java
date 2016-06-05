package com.pouch.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Ala on 2016-06-01.
 */
public class SettingLinearLayout extends LinearLayout {

    private static final String TAG = "SettingLinearLayout";
    private boolean isTest= true;

    public SettingLinearLayout(Context context) {
        super(context);
    }

    public SettingLinearLayout(Context context,AttributeSet attrs){
        super(context,attrs);
    }


}
