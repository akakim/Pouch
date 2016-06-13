package com.pouch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.pouch.R;
import com.pouch.ui.adapter.DataAdapter;
public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private DrawerLayout                 mDrawerLayout;
    private Toolbar BottomToolbar;


    private GridView                     brandList;
    private DataAdapter                  brandListAdapater;

    private CharSequence                 mTitle;
    private CharSequence                 mDrawerTitle;
    private String[]                     menuList; // 필요한 catagory들이 들어간다.
    private boolean isTest=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        // activity의 타이틀값을 받아온다.
        mTitle = mDrawerTitle = getTitle();

        menuList = getResources().getStringArray(R.array.menu_array);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);

        BottomToolbar = (Toolbar)findViewById(R.id.m_bottomTabBar);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
//       mDrawerToggle.syncState();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
  /*
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        int menu = v.getId();
        if(isTest)   {
            Log.v(TAG,"onClick v.getID()"+v.getId());
        }

        Intent i = null;
        switch(menu){
            case R.id.Review:
                break;
            case R.id.Search:
                i = new Intent(this,SearchActivity.class);
                startActivity(i);
                break;
            case R.id.Instagram:
                break;
            case R.id.Pouch:
                if (isTest){
                    Log.v(TAG,"Pouch clicked");
                }
                i  =new Intent(this,ItemPouchActivity.class);
                startActivity(i);
                break;
            case R.id.navigation_drawer_items_textView_setting:
                if (isTest){
                    Log.v(TAG,"setting menu clicked");
                }
                i = new Intent(this,SettingActivity.class);
                startActivity(i);
                break;
        }
    }
}
