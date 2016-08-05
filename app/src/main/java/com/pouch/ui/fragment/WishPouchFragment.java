package com.pouch.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.database.helper.PouchDatabase;
import com.pouch.database.helper.PouchTableList;
import com.pouch.ui.PouchActivity;

import java.util.ArrayList;


public class WishPouchFragment extends Fragment {
    private static final String TAG = WishPouchFragment.class.getSimpleName();
    private int mRowSelected = 0;
    PouchDatabase db;

    ArrayList<String> titles = null;
    ArrayList<String> prices = null;
    ArrayList<byte[]>thumbnails = null;
    private GridLayout GridItems;


    /*초기화시 기존의 자료가 있는지 없는지를 점검후
    * 있다면 gridlayout에 넣어주고
    * 없다면 pass.
    * */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        titles = new ArrayList<>();
        prices = new ArrayList<>();
        thumbnails = new ArrayList<>();

        db = ((PouchActivity)getActivity()).getDB();
        Cursor c = db.getRowSQL(PouchTableList.TABLE_PRODUCT_INFO);
        int numberOfItems = 0;
        for(;c.moveToNext()!=false;) {
;

            titles.add(c.getString(1));
            prices.add(c.getString(2));
            byte image [] = c.getBlob(3);
            thumbnails.add(image);


        }
        ArrayList<String> head = new ArrayList<>();
        ArrayList<String> tail = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView()");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_item_mypouch, container, false);
        GridItems = (GridLayout)rootView.findViewById(R.id.main_brandlist);

        int width_pixel = 460;
        int height_pixel = 500;
        LinearLayout[] testLinearLayout;
        FrameLayout[]testBack;
        ImageView[] testArr;
        TextView[] titleArr;

        if(titles.size()>0) {
            testArr = new ImageView[titles.size()];
            titleArr = new TextView[titles.size()];


            testBack = new FrameLayout[titles.size()];
            testLinearLayout = new LinearLayout[titles.size()];


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;

            FrameLayout.LayoutParams Frame_params = new FrameLayout.LayoutParams(width_pixel, height_pixel);
            params.weight = 1.0f;

            LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int FrameColors[] = {Color.GREEN, Color.BLACK, Color.RED};


            for (int i = 0; i < titles.size(); i++) {

                testLinearLayout[i] = new LinearLayout(WishPouchFragment.this.getContext());
                testLinearLayout[i].setOrientation(LinearLayout.VERTICAL);
                testLinearLayout[i].setLayoutParams(params);
                testLinearLayout[i].setBackgroundColor(Color.BLACK);
                testLinearLayout[i].setGravity(Gravity.CENTER);

                testBack[i] = new FrameLayout(WishPouchFragment.this.getContext());
                testBack[i].setLayoutParams(Frame_params);
                testBack[i].setBackgroundColor(FrameColors[i % 3]);

                testArr[i] = new ImageView(WishPouchFragment.this.getContext());
                testArr[i].setImageBitmap(BitmapFactory.decodeByteArray(thumbnails.get(i), 0, thumbnails.get(i).length));

                CancelableParams.gravity = Gravity.CENTER;
                CancelableParams.weight = 0.7f;
                testArr[i].setLayoutParams(CancelableParams);
                testArr[i].setOnClickListener(new View.OnClickListener() {
                    int selected = mRowSelected;

                    @Override
                    public void onClick(View v) {
                        mRowSelected = selected;
                    }
                });
                mRowSelected++;

                titleArr[i] = new TextView(WishPouchFragment.this.getContext());
                titleArr[i].setText(titles.get(i));
                titleArr[i].setTextSize(12.0f);
                titleArr[i].setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                CancelableParams.weight = 0.3f;
                titleArr[i].setLayoutParams(CancelableParams);
                titleArr[i].setPadding(20, 20, 0, 0);

                testBack[i].addView(testLinearLayout[i]);
                testLinearLayout[i].addView(testArr[i]);
                testLinearLayout[i].addView(titleArr[i]);

            }

            // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
            mRowSelected = 0;


            for (int i = 0; i < titles.size(); i++) {
                GridItems.addView(testBack[i]);
            }
        }
        return rootView;
    }
}
