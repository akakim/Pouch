package com.pouch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.pouch.R;
import com.pouch.ui.adapter.DataAdapter;
public class MainActivity extends AppCompatActivity implements MenuAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private GridView brandList;
    private DataAdapter brandListAdapater;
    Toolbar toolbar;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[]     menuList; // 필요한 catagory들이 들어간다.
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
/*
        brandList     = (GridView)findViewById(R.id.gridView);

        brandListAdapater = new DataAdapter("TONIMORI","BLANK","BLANK","0");

//        brandList.setNumColumns(this.);
        brandList.setAdapter(brandListAdapater);
        brandList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        /*
        mDrawerList =(RecyclerView)findViewById(R.id.left_drawer);

        //set a customshadow that overlays the main content when drawer open
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);

        mDrawerList.setAdapter(new MenuAdapter(menuList, this));

        toolbar = (Toolbar)findViewById(R.id.m_toolbar);// lollipop부터는

       // toolbar.inflateMenu(R.menu.menu_main);

        //setSupportActionBar(toolbar);
        //toolbar.inflateMenu(R.menu.menu_main);

        //enable ActionBar app icon to behave as action to toggle new drawer ??
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
      //  getActionBar().setHomeButtonEnabled(true);

        //ActionBarDrawerToggle ties together the proper interactions
        //between the sliding drawer and the action bar app icon

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
*/
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
            case R.id.navigation_drawer_items_textView_setting:
                if (isTest){
                    Log.v(TAG,"setting menu clicked");
                }
                i = new Intent(this,SettingActivity.class);
                startActivity(i);
                break;
        }
    }
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position){

        Log.v(TAG, "selected " + position);
  //      mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void onClickSetting(View v){
       Intent i = new Intent(MainActivity.this,SettingActivity.class);
        startActivity(i);
    }
}
