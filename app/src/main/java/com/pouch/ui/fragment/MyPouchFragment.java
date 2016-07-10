package com.pouch.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pouch.R;

public class MyPouchFragment extends Fragment {
    private int mRowSelected = 0;
    private GridLayout GridItems;
    static final int ImageList [] ={
            R.drawable.cream,
            R.drawable.blush,
            R.drawable.eyeliner,
            R.drawable.fact,
            R.drawable.eyeshadow,
            R.drawable.skin,
            R.drawable.blush,
            R.drawable.eyeshadow,
            R.drawable.skin,
            R.drawable.blush
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_item_mypouch, container, false);


        LinearLayout[] testLinearLayout;
        FrameLayout[]testBack;
        ImageView[] testArr;
        testArr = new ImageView[ImageList.length];

        testBack = new FrameLayout[ImageList.length];
        testLinearLayout = new LinearLayout[ImageList.length];



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/3,
                getResources().getDisplayMetrics().heightPixels/3);
        params.weight= 1.0f;
        params.setLayoutDirection(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);


        for(int i =0;i<ImageList.length;i++){

            testLinearLayout[i]  = new LinearLayout(MyPouchFragment.this.getContext());
            testLinearLayout[i].setLayoutParams(params);

            testBack[i] = new FrameLayout(MyPouchFragment.this.getContext());
            testBack[i].setLayoutParams(params);
            testBack[i].setBackgroundColor(Color.DKGRAY);

            testArr [i] = new ImageView(MyPouchFragment.this.getContext());
            testArr[i].setImageResource(ImageList[i]);
            CancelableParams.gravity= Gravity.CENTER;
            testArr[i].setLayoutParams(CancelableParams);
            testArr[i].setOnClickListener(new View.OnClickListener(){
                int selected = mRowSelected;

                @Override
                public void onClick(View v) {
                    mRowSelected = selected;
                }
            });
            mRowSelected++;

            testBack[i].addView(testLinearLayout[i]);
            testLinearLayout[i].addView(testArr[i]);


        }

        // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
        mRowSelected = 0;

        GridItems = (GridLayout)rootView.findViewById(R.id.main_brandlist);
        for(int i =0;i<testArr.length;i++){
            GridItems.addView(testBack[i]);
        }

        return rootView;
    }



    private void InitGridLayout(){



    }
}
