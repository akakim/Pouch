package com.pouch.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pouch.R;

/**
 * Created by Ala on 2016-05-19.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>{

    private ImageView ProfileImage;

    private String[] mDataset;
    private OnItemClickListener mListener;
    private static final String TAG="MenuAdapter";

    public interface OnItemClickListener{
        public void onClick(View view,int position);
    }

    public MenuAdapter(String[] myDataSet,OnItemClickListener listener){
        this.mDataset =myDataSet;
        this.mListener = listener;
    }

    /**
     * 초기 화면구성.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        Log.v(TAG,"OnCreateViewHolder");
        LayoutInflater viewInflater = LayoutInflater.from(parent.getContext());
        View v = viewInflater.inflate(R.layout.draw_list_item, parent, false);
        TextView tv = (TextView)v.findViewById(android.R.id.text1);
        return new ViewHolder(tv);
    }


    /**
     * Binding 되면 호출되는곳.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position){
        holder.mTextView.setText(mDataset[position]);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"OnBindViewHolder.onClick");
                mListener.onClick(v,position);
            }
        });
    }
    @Override
    public int getItemCount(){
        return mDataset.length;
    }

    /**
     * Innerclass로 굳이 선언해줬고 Innerclass이기때문에 static으로 선언해줘야하는 것같다.
     * Custom ViewHolder for our planet View
     * View Holder 를 원하는 대로 꾸미기.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTextView;

        public ViewHolder(TextView v){
            super(v);
            mTextView = v;
        }
    }
}


