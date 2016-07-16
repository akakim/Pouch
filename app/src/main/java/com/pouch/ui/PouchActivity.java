package com.pouch.ui;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pouch.Logger.Log;
import com.pouch.R;
import com.pouch.ui.fragment.MyPouchFragment;
import com.pouch.ui.fragment.WishPouchFragment;


public class PouchActivity extends AppCompatActivity {


    MyPouchFragment   mMyPouch;
    WishPouchFragment mWishPouch;
    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_pouch);

        mMyPouch = new MyPouchFragment();
        mWishPouch = new WishPouchFragment();
        tabs = (TabLayout)findViewById(R.id.pouchTabLayout);

        /*Fragment 생성. */
        getSupportFragmentManager().beginTransaction().replace(R.id.item_pouch_container,mMyPouch).commit();


        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                Fragment selected = null;
                if(pos ==0){
                    selected = mMyPouch;
                }
                else
                    selected = mWishPouch;

                getSupportFragmentManager().beginTransaction().replace(R.id.item_pouch_container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
