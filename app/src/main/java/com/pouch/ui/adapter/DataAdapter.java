package com.pouch.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.pouch.data.brandInfo;
/**
 * Created by Ala on 2016-05-30.
 */
public class DataAdapter extends BaseAdapter {

    private brandInfo mBrand;
    private Context mContext;
    private static String TAG = "DataAdapter";

    public DataAdapter(Context context){
        mContext = context;

    }

    public DataAdapter(Context context ,brandInfo brandInfo){
        this.mContext = context;
        this.mBrand = brandInfo;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "getView()");

        if (mBrand == null){
            return null;
        }


        return null;
    }
}
