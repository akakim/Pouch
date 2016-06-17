package com.pouch.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.pouch.R;
import com.pouch.data.brandInfo;

import java.util.List;

/**
 * 메인 화면이나.gridlayout을 사용하는 것들에 대한 adapter를
 * 만드는게 목적.
 * 다시말하면 데이터와 상환없는 무언가 ..
 * http://inducesmile.com/android/android-gridview-vs-gridlayout-example-tutorial/
 */
public class DataAdapter extends BaseAdapter {

    private brandInfo mBrand;
    private Context mContext;
    private final String TAG = this.getClass().getSimpleName();
    private LayoutInflater layoutInflater;
    private List<brandInfo> listStorage;
    private boolean IsTest = true;
    public DataAdapter(Context context,List<brandInfo> customizedListView){
        mContext = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listImageHolder;
        if(IsTest) {
            Log.v(TAG, "getView()");
        }

        if (convertView == null){
            listImageHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.activity_main_item,parent,false);
            listImageHolder.imageListView = (ImageView)convertView.findViewById(R.id.Main_ImageItem);
            listImageHolder.imageListView.setImageResource(listStorage.get(position).getBrandImageRes());
            convertView.setTag(listImageHolder);
        }
        else {
            // findViewById로 호출을 자꾸 하게되면 성능이 느려진다.
            //조금이나마 빨리하기위해 이렇게함.
            listImageHolder =  (ViewHolder)convertView.getTag();
        }


        return convertView;
    }

    static class ViewHolder{
        ImageView imageListView;
    }
}
