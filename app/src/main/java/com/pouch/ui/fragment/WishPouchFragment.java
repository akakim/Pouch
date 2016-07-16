package com.pouch.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.customView.AnimatedExpandableListView;
import com.pouch.data.ItemDetailInform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by USER on 2016-06-10.
 */

// TODO : 애니매이션 효과 적용중 가장 끝 메뉴에서 거슬리는 부분이 있다. 효과에 대해서 약간 수정해야한다.

public class WishPouchFragment extends Fragment {
    private static final String TAG = WishPouchFragment.class.getSimpleName();
    public static final String PRODUCT_DATA_SET = "product_data_set" ;
    public static final String PRODUCT_DATA_KEY_SET = "product_data_key_set" ;
    private int mRowSelected = 0;
    SharedPreferences sharedPreferences;
    ArrayList<ItemDetailInform> item_inform;

    private GridLayout GridItems;
    static final int ImageList [] ={
            R.drawable.eyeliner,
            R.drawable.fact,
            R.drawable.eyeshadow,
            R.drawable.skin,
            R.drawable.blush,
            R.drawable.eyeshadow,
            R.drawable.skin,
            R.drawable.blush
    };


    /*초기화시 기존의 자료가 있는지 없는지를 점검후
    * 있다면 gridlayout에 넣어주고
    * 없다면 pass.
    * */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");





//        product_data_set = sharedPreferences.getStringSet()
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(PRODUCT_DATA_SET,Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key_set = sharedPreferences.getString(PRODUCT_DATA_KEY_SET, "null");
        Log.v(TAG,"key_set : "+ key_set);
        String title = sharedPreferences.getString(key_set + "_TITLE", "null");

        Log.v(TAG,"title : "+ title);
        ArrayList<String> head = new ArrayList<>();
        ArrayList<String> tail = new ArrayList<>();
        int size = 0;
        try {
            size = Integer.parseInt(sharedPreferences.getString(key_set + "_SIZE", "0"));
            Log.v(TAG,"size : "+size);
        }catch (Exception e){
            e.printStackTrace();
        }
         if (size != 0){
             for (int i =0; i<size;i++){
                 StringBuilder headBuilder = new StringBuilder(sharedPreferences.getString(key_set + "_HEAD_"+i, "null"));
                 StringBuilder tailBuilder = new StringBuilder(sharedPreferences.getString(key_set + "_TAIL_"+i, "null"));
                 Log.v(TAG,"tail : "+ tailBuilder.toString());
                 head.add(headBuilder.toString());
                 tail.add(tail.toString());
             }
         }


        /*
        StringBuilder key = new StringBuilder(Items.get(selectedItem).getImageURL().toString());
        sharedPreferences = getSharedPreferences(PRODUCT_DATA_SET, Context.MODE_APPEND);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString(PRODUCT_DATA_KEY_SET, key.toString());
        editor.putString(key.toString()+"_title",Items.get(selectedItem).getTitle());
        // editor.putString(key.toString()+"_title",Items.get(selectedItem).);
        ProductDetailFragment fragment = ( ProductDetailFragment)mAdapter.getItem(selectedItem);
        ArrayList<String> Head = fragment.getHeadValues();
        ArrayList<String> Tail = fragment.getTailValues();

        for(int i =0; i<Head.size();i++) {
                    editor.putString(key.toString() + "_HEAD_"+String.valueOf(i),Tail.get(i).toString());
                    editor.putString(key.toString() + "_TAIL_"+String.valueOf(i), Tail.get(i).toString());
                }

        editor.commit();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView()");
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

            testLinearLayout[i]  = new LinearLayout(WishPouchFragment.this.getContext());
            testLinearLayout[i].setLayoutParams(params);

            testBack[i] = new FrameLayout(WishPouchFragment.this.getContext());
            testBack[i].setLayoutParams(params);
            testBack[i].setBackgroundColor(Color.DKGRAY);

            testArr [i] = new ImageView(WishPouchFragment.this.getContext());
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
}
