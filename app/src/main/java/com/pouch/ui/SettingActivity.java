package com.pouch.ui;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.data.SettingMenu;

public class SettingActivity extends ListActivity {
    private static final String TAG="SettingActivity";
    private boolean isTest=  true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setListAdapter(new PouchSettingAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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


    private class PouchSettingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return SettingMenu.Menus.length;
        }

        @Override
        public String getItem(int position) {
            return SettingMenu.Menus[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_setting_item, parent, false);
            }
            //((ImageView) convertView.findViewById(android.R.id.preImageSettingText));
            ((TextView) convertView.findViewById(R.id.SettingText)).setText(getItem(position));
            if(SettingMenu.isIntent[position]) {
                if(isTest){
                    Log.v(TAG, "position " + position);
                }
                ((ImageView) convertView.findViewById(R.id.nextActivity)).setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }
}
