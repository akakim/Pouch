package com.pouch.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.customView.AnimatedExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2016-06-10.
 */

// TODO : 애니매이션 효과 적용중 가장 끝 메뉴에서 거슬리는 부분이 있다. 효과에 대해서 약간 수정해야한다.

public class WishPouchFragment extends Fragment {
    private static final String TAG = WishPouchFragment.class.getSimpleName();
    private AnimatedExpandableListView listView;
    private RegistAdapter adapter;
    DisplayMetrics metrics;
    int width;
    List<GroupItem> items;
    String initBrandlst [];
    boolean isTest = true;

    private void initItems(){

        initBrandlst = getResources().getStringArray(R.array.brandlist);

        for (int i =0;i <initBrandlst.length;i++){
            GroupItem group = new GroupItem(initBrandlst[i]);
            ChildItem child = new ChildItem();
            child.title = initBrandlst[i];
            group.items.add(child);
            items.add(group);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_item_wishpouch, container, false);

        /**
         * TODO: 사용자의 정보에 대한 확인을 해야한다.
         */
        items = new ArrayList<GroupItem>();



        initItems();
        if(isTest) {
            Log.v(TAG, "group Item Title : " + items.get(0).items.get(0).title);
            Log.v("getContext() : ",getContext().toString());
        }


        adapter = new RegistAdapter(getContext());
        adapter.setData(items);
        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.listView);

        listView.setAdapter(adapter);

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

        return rootView;
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

                convertView = inflater.inflate(R.layout.activity_item_wishpouch_item,parent,false);
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

            if(convertView == null){
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.activity_item_wishpouch_group_item,parent,false);
                holder.title = (TextView)convertView.findViewById(R.id.getWishGroupItem);
                convertView.setTag(holder);
            }else{
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
