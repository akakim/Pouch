package com.pouch.ui;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.pouch.R;
import com.pouch.common.LoginActivity;
import com.pouch.customView.AnimatedExpandableListView;
import com.pouch.customView.AnimatedExpandableListView.*;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG="SettingActivity";
    private boolean isTest=  true;
    private AnimatedExpandableListView listView;
    private SettingAdapter adapter;
    DisplayMetrics metrics;
    int width;

    Intent getUserInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        List<GroupItem> items = new ArrayList<GroupItem>();

        GroupItem ID_item = new GroupItem();
        GroupItem Brand_item = new GroupItem();
        GroupItem push_Alarm = new GroupItem();

        getUserInformation = getIntent();
        String id = "NICKNAME"; // getUserInformation.getExtras().getString("UserName");
            id = getUserInformation.getExtras().getString("name");
        ID_item.title=id; // TODO: 나중에 사용자가 로그인시 처리해야됨.
        Brand_item.title="브랜드 등록";
        push_Alarm.title = "푸쉬알람설정";

        ChildItem id_child = new ChildItem();
        id_child.title = "닉네임 변경";
        ChildItem id_logOut = new ChildItem();
        id_logOut.title = "로그아웃";

        ChildItem push_setting = new ChildItem();
        push_setting.title = "토글버튼생성";

        ID_item.items.add(id_child);
        ID_item.items.add(id_logOut);
        push_Alarm.items.add(push_setting);

        items.add(ID_item);
        items.add(Brand_item);
        items.add(push_Alarm);

        adapter = new SettingAdapter(this);

        adapter.setData(items);

        listView = (AnimatedExpandableListView) findViewById(R.id.listView);
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
                //Toast.makeText(getApplicationContext(),"get groupPosition : "+groupPosition,Toast.LENGTH_SHORT).show();


                switch (groupPosition){
                    // 자식이 있는 ID와 푸쉬알람설정만을 애니매이션 설정한다.

                    case 0:
                        if (listView.isGroupExpanded(groupPosition)) {
                            listView.collapseGroupWithAnimation(groupPosition);
                        } else {
                            listView.expandGroupWithAnimation(groupPosition);
                        }
                        break;
                    // 브랜드 등록
                    case 1:
                        Intent i = new Intent(getApplicationContext(),RegistBrandActivity.class);
                        startActivity(i);
                        break;
                    //푸쉬 알람설정
                    case 2:
                        if (listView.isGroupExpanded(groupPosition)) {
                            listView.collapseGroupWithAnimation(groupPosition);
                        } else {
                            listView.expandGroupWithAnimation(groupPosition);
                        }
                        break;
                }

                return true;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Toast.makeText(getApplicationContext(),"groupPosition : "+groupPosition + " childPosition : "+childPosition,Toast.LENGTH_SHORT).show();
                    if(isTest){
                        Log.v(TAG,"groupPosition : "+groupPosition + " childPosition : "+childPosition);
                    }
                switch (groupPosition){
                    // 자식이 있는 ID와 푸쉬알람설정만을 애니매이션 설정한다.
                    case 0:
                        // 닉네임 변경.
                        if(childPosition == 0){
                            Toast.makeText(getApplicationContext(),"닉네임을 변경합니다",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            UserManagement.requestLogout(new LogoutResponseCallback() {
                                @Override
                                public void onCompleteLogout() {
                                    redirectLoginActivity();
                                }
                            });
                            Toast.makeText(getApplicationContext(),"로그아웃합니다. ",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),"push 알람 설정. ",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
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
    }

    //
    private static class ChildItem {
        String title;
    }

    // 왜 홀더를 만들어야만하는가.
    // title과 자식의 textView를 가르키게도니다.
    private static class ChildHolder {
        TextView title;
        Switch   aSwitch;
    }

    private static class GroupHolder {
        ImageView header;
        TextView title;
    }

    private class SettingAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private static final String TAG = "SettingAdapter";
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public SettingAdapter(Context context) {
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
            if(convertView ==null){
                holder = new ChildHolder();

                convertView = inflater.inflate(R.layout.activity_setting_togglableitem, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                holder.aSwitch = (Switch)convertView.findViewById(R.id.switchable);


                if(groupPosition ==2){
                    holder.aSwitch.setVisibility(View.VISIBLE);
                }

                convertView.setTag(holder);
            }else {

                // getGroupView와 유사하다.
                holder = (ChildHolder) convertView.getTag();
                if(groupPosition == 2){
                    holder.aSwitch.setVisibility(View.VISIBLE);
                }else{
                    holder.aSwitch.setVisibility(View.INVISIBLE);
                }
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
                convertView = inflater.inflate(R.layout.activity_setting_group_item,parent,false);
                holder.title = (TextView)convertView.findViewById(R.id.textTitle);
                holder.header = (ImageView)convertView.findViewById(R.id.title_icon);
                switch(groupPosition){
                    case 0:
                        holder.header.setImageResource(R.drawable.person);
                        break;
                    case 1:
                        holder.header.setImageResource(R.drawable.brand);
                        break;
                    case 2:
                        holder.header.setImageResource(R.drawable.alarm);
                        break;
                }
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

    public void redirectLoginActivity(){
        Intent i = new Intent(this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
