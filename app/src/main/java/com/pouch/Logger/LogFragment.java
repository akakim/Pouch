package com.pouch.Logger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * simple fragment which contains a LogView and uses is to output log data it receives
 * through the LogNode interface
 * LogNode interface를 통해 실질적으로 보여주는 부분을 구현한것.
 */
public class LogFragment extends Fragment{
    private LogView mLogView;
    private ScrollView mScorllView;

    public LogFragment(){}

    public View inflateViews(){
        mScorllView = new ScrollView(getActivity());
        /**
         * 코드상으로 UI 화면을 설정하는 부분. (실제로 나타내는건 infaltor가 해주겠지 .
         */
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mScorllView.setLayoutParams(scrollParams);

        mLogView = new LogView(getActivity());
        ViewGroup.LayoutParams logParams = new ViewGroup.LayoutParams(scrollParams);
        logParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        mLogView.setLayoutParams(logParams);
        mLogView.setClickable(true);
        mLogView.setFocusable(true);
        mLogView.setTypeface(Typeface.MONOSPACE);

        //Want to set padding as 16dips, set paadingtakes pixess
        // 패딩값 삽입.
        int paddingDips = 16;
        double scale = getResources().getDisplayMetrics().density;
        int paddingPixels=(int) (paddingDips * (scale) + 0.5);
        mLogView.setPadding(paddingPixels,paddingPixels,paddingPixels,paddingPixels); // ??
        mLogView.setCompoundDrawablePadding(paddingPixels);// ??
        mLogView.setGravity(Gravity.BOTTOM);
        // 텍스트를 설정하는듯?
        mLogView.setTextAppearance(getActivity(),android.R.style.TextAppearance_Holo_Medium);
        mScorllView.addView(mLogView);
        return mScorllView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View result = inflateViews();
        // text가 바뀔때마다 뭔가 이벤트를 발생시키는듯???
        mLogView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mScorllView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        return result;
    }

    public LogView getmLogView(){
        return mLogView;
    }
}
/**
 * mLogView.setTypeface(Typeface.MONOSPACE)
 */
