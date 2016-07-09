package com.pouch.ui;


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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.customView.AnimatedExpandableListView;

import java.util.ArrayList;
import java.util.List;

//TODO : 수정중. 
public class RegistBrandActivity extends AppCompatActivity {
    private static final String TAG = RegistBrandActivity.class.getSimpleName();
    private ListView listView;
    private RegistAdapter adapter;
    DisplayMetrics metrics;
    int width;
    List<GroupItem> items;
    String initBrandlst [];
    private void initItems(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        items = new ArrayList<GroupItem>();



        initItems();
        Log.v(TAG, "group Item Title : " + items.get(0).items.get(0).title);



        adapter = new RegistAdapter(this);
        adapter.setData(items);
        listView = (AnimatedExpandableListView) findViewById(R.id.RegistView);
        listView.setAdapter(adapter);



        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        listView.setIndicatorBoundsRelative(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.

                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    Log.v(TAG, "getChildCount :" + listView.getChildCount());
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
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
    // group menu
    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();

        public GroupItem(){
            title = null;
        }
        public GroupItem(GroupItem group){
            this.title = group.title;
            this.items = group.items;
        }

        public GroupItem(String title){
            this.title = title;
        }
        public GroupItem(String title,ChildItem child){
            this.title = title;
            items.add(child);
        }


    }

    private class RegistAdapter extends BaseAdapter {

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
            return null;
        }
    }
}
