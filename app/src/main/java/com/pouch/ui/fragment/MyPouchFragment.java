package com.pouch.ui.fragment;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.TextView;

import com.pouch.R;
import com.pouch.database.helper.PouchDatabase;
import com.pouch.database.helper.PouchTableList;
import com.pouch.ui.PouchActivity;

import java.util.ArrayList;

public class MyPouchFragment extends Fragment {
    private int mRowSelected = 0;
    private GridLayout GridItems;
    PouchDatabase db;

    ArrayList<String> titles = null;
    ArrayList<String> prices = null;
    ArrayList<byte[]>thumbnails = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        db = ((PouchActivity)getActivity()).getDB();
        super.onCreate(savedInstanceState);

        titles = new ArrayList<>();
        prices = new ArrayList<>();
        thumbnails = new ArrayList<>();

        db = ((PouchActivity)getActivity()).getDB();
        Cursor c = db.getRowSQL(PouchTableList.TABLE_MY_POUCH);
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_item_mypouch, container, false);

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


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.setLayoutDirection(LinearLayout.HORIZONTAL);

            FrameLayout.LayoutParams Frame_params = new FrameLayout.LayoutParams(width_pixel, height_pixel);

            LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);


            for (int i = 0; i < titles.size(); i++) {

                testLinearLayout[i] = new LinearLayout(MyPouchFragment.this.getContext());
                testLinearLayout[i].setLayoutParams(params);
                testLinearLayout[i].setOrientation(LinearLayout.VERTICAL);

                testBack[i] = new FrameLayout(MyPouchFragment.this.getContext());
                testBack[i].setLayoutParams(Frame_params);
                testBack[i].setBackgroundColor(Color.DKGRAY);

                testArr[i] = new ImageView(MyPouchFragment.this.getContext());
                titleArr = new TextView[titles.size()];

                testArr[i].setImageBitmap(BitmapFactory.decodeByteArray(thumbnails.get(i), 0, thumbnails.get(i).length));

                CancelableParams.gravity = Gravity.CENTER;


                testArr[i].setLayoutParams(CancelableParams);
                testArr[i].setOnClickListener(new View.OnClickListener() {
                    int selected = mRowSelected;

                    @Override
                    public void onClick(View v) {
                        mRowSelected = selected;
                    }
                });
                mRowSelected++;

                titleArr[i] = new TextView(MyPouchFragment.this.getContext());
                titleArr[i].setText(titles.get(i));
                titleArr[i].setTextSize(12.0f);
                titleArr[i].setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                CancelableParams.weight = 0.3f;
                titleArr[i].setLayoutParams(CancelableParams);
                titleArr[i].setPadding(20, 20, 0, 0);

                testBack[i].addView(testLinearLayout[i]);
                testLinearLayout[i].addView(testArr[i]);


            }

            // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
            mRowSelected = 0;

            GridItems = (GridLayout) rootView.findViewById(R.id.main_brandlist);
            for (int i = 0; i < testArr.length; i++) {
                GridItems.addView(testBack[i]);
            }
        }

        return rootView;
    }



    private void InitGridLayout(){



    }
}
