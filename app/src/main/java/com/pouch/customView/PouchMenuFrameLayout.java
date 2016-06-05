package com.pouch.customView;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.pouch.R;
import com.pouch.ui.*;
/**
 * Created by Ala on 2016-05-25.
 */
public class PouchMenuFrameLayout extends FrameLayout {
    private static final String TAG = "PouchMenu";
    private Drawable mForeground;
    private Rect mInsets;
    private final Rect mTempRect = new Rect();
    private OnInsetsCallback mOnInsetsCallback;
    private boolean isTest = true;


    public PouchMenuFrameLayout(Context context) {
        super(context);
    }
    public PouchMenuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PouchMenuFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnInsetsCallback{
        void onInsetsChanged(Rect insets);
    }

    /**
     * Allows the calling container to specify a callback for custom processing when insets change (i.e. when
     * {@link #fitSystemWindows(Rect)} is called. This is useful for setting padding on UI elements based on
     * UI chrome insets (e.g. a Google Map or a ListView). When using with ListView or GridView, remember to set
     * clipToPadding to false.
     */
    public void setmOnInsetsCallback(OnInsetsCallback onInsetsCallBack){
        mOnInsetsCallback = onInsetsCallBack;
    }

    private void init(Context context,AttributeSet attrs,int defStyle){
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PouchMenuView,defStyle,0);

        if(a==null){
            return;
        }

        mForeground = a.getDrawable(R.styleable.PouchMenuView_insetForeground);
        a.recycle();
        setWillNotDraw(true);
    }
    //???????????????
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        mInsets = new Rect(insets);
        setWillNotDraw(mForeground == null);
        ViewCompat.postInvalidateOnAnimation(this);
        if (mOnInsetsCallback != null) {
            mOnInsetsCallback.onInsetsChanged(insets);
        }
        return true; // consume insets
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mForeground!=null){
            mForeground.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        if(mForeground!=null){
            mForeground.setCallback(this);
        }
    }


}
