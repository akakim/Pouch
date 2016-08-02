package com.pouch.ui;


import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



import com.pouch.R;
import com.dd.morphingbutton.MorphingButton;
//TODO : 수정중.
public class RegistBrandActivity extends AppCompatActivity {
    private static final String TAG = RegistBrandActivity.class.getSimpleName();
    private ListView listView;
    private RegistAdapter adapter;
    DisplayMetrics metrics;
    int width;
    String Brandlst [];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);


        Brandlst = getResources().getStringArray(R.array.brandlist);
        listView = (ListView) findViewById(R.id.RegistView);
        adapter = new RegistAdapter(this,Brandlst);
        listView.setAdapter(adapter);



        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regist_brand, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    private class RegistAdapter extends BaseAdapter {
        Context context;
        String brandlist [];
        int status [];
        LayoutInflater inflater;
        RegistAdapter(Context context,String brandlist []){
            this.context = context;
            this.brandlist = brandlist;
            status = new int [brandlist.length];
            for (int i = 0; i < status.length; i++) {
                status[i] = 0;
                Log.v(TAG,brandlist[i]);
            }
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return brandlist.length;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView == null){
                holder= new ViewHolder();
                convertView = inflater.inflate(R.layout.activity_regist_item,parent,false);
                holder.brandName = (TextView)convertView.findViewById(R.id.RegistTitle);
                holder.imageView = (MorphingButton)convertView.findViewById(R.id.RegistBtn);
                morphToNotPressed(holder.imageView,0);
                convertView.setTag(holder);
                holder.brandName.setText(brandlist[position]);
                //holder.imageView = (MorphingButton)convertView.findViewById(R.id.)
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // when pressed
                        onMorpButtonClicked(holder.imageView,position);
                    }
                });
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }

        private void onMorpButtonClicked(MorphingButton btn,int position){
            if (status[position] == 0){
                status[position]++;
                morphToPressed(btn,integer(R.integer.mb_animation));
            }
            else {
                status[position] = 0;
                morphToNotPressed(btn, integer(R.integer.mb_animation));
            }
        }
    }

    static class ViewHolder{
        TextView brandName;
        MorphingButton imageView;
    }

    private void morphToNotPressed(final MorphingButton btnMorph,int duration){
        MorphingButton.Params circle =
                MorphingButton.Params.create()
                        .duration(duration)
                        .cornerRadius(dimen(R.dimen.mb_height_56))
                        .width(dimen(R.dimen.mb_height_56))
                        .height(dimen(R.dimen.mb_height_56))
                        .color(color(R.color.mb_red))
                        .colorPressed(color(R.color.mb_red_dark))
                        .icon(R.drawable.btn_not_pressed);
        btnMorph.morph(circle);
    }
    private void morphToPressed(final MorphingButton btnMorph,int duration){
        MorphingButton.Params circle =
                MorphingButton.Params.create()
                        .duration(duration)
                        .cornerRadius(dimen(R.dimen.mb_height_56))
                        .width(dimen(R.dimen.mb_height_56))
                        .height(dimen(R.dimen.mb_height_56))
                        .color(color(R.color.mb_blue))
                        .colorPressed(color(R.color.mb_blue_dark))
                        .icon(R.drawable.btn_pressed);
        btnMorph.morph(circle);
    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

}
