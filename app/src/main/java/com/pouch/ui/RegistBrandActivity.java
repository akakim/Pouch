package com.pouch.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.customView.AnimatedExpandableListView;

import java.util.ArrayList;
import java.util.List;


public class RegistBrandActivity extends AppCompatActivity {
    private static final String TAG = RegistBrandActivity.class.getSimpleName();
    private AnimatedExpandableListView listView;
    private RegistAdapter adapter;
    DisplayMetrics metrics;
    int width;
    List<GroupItem> items;
    String initBrandlst [] = {
      "이니스프리", "더페이스샵", "스킨푸드",
            "토니모리", "SK-Ⅱ" ,"MISSHA"
    };
    private void initItems(){


        for (int i =0;i <initBrandlst.length;i++){
            GroupItem group = new GroupItem(initBrandlst[i]);
            ChildItem child = new ChildItem();
            child.title = initBrandlst[i];
            group.items.add(child);
            items.add(group);

        }
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

    //
    private static class ChildItem {
        String title;

        public ChildItem(){
            title = null;
        }
        public ChildItem(String child) {
            this.title = title;
        }
        public ChildItem(ChildItem i){
            this.title = i.title;
        }
    }

    // 왜 홀더를 만들어야만하는가.
    // title과 자식의 textView를 가르키게도니다.
    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

    private class RegistAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private final String TAG = RegistAdapter.class.getSimpleName();
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public RegistAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        /* get child View expandable ListView와 같이 */

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * getGroupView와 달리 중요하게 animation효과를 집어넣어야하므로 주목.
         *
         * @param groupPosition
         * @param childPosition
         * @param isLastChild
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getRealChildView(int groupPosition,int childPosition,boolean isLastChild, View convertView, ViewGroup parent){
            ChildHolder holder;
            ChildItem item = getChild(groupPosition,childPosition);
            Log.v(TAG, "getRealChildView() item.title : " + item.title);
            if(convertView ==null){
                Log.v(TAG,"makeConvertView");
                holder = new ChildHolder();

                convertView = inflater.inflate(R.layout.activity_regist_item,parent,false);
                holder.title = (TextView) convertView.findViewById(R.id.getWishItem);

                convertView.setTag(holder);
            }else {
                Log.v(TAG,"not make" );
                // getGroupView와 유사하다.
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);

            Log.v(TAG,"getGroupView()" + getGroup(groupPosition));
            if(convertView == null){
                Log.v(TAG,"convertView=null");
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.activity_regist_group_item,parent,false);
                holder.title = (TextView)convertView.findViewById(R.id.RegistTitle);
                convertView.setTag(holder);
            }else{
                Log.v(TAG,"convertView not null");
                holder = (GroupHolder)convertView.getTag();
            }

            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }
}
