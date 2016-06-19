package com.pouch.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.pouch.R;
import com.pouch.ui.fragment.SaleInfoFragment;
import com.pouch.ui.fragment.EventInfoFragment;


public class EventInfoActivity extends AppCompatActivity {
    SaleInfoFragment mSaleInfoFragment;
    EventInfoFragment mEventInfoFragment;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        mSaleInfoFragment = new SaleInfoFragment();
        mEventInfoFragment = new EventInfoFragment();
        tabLayout = (TabLayout)findViewById(R.id.eventInfoTabLayout);

        // AppCompatActivity
        getSupportFragmentManager().beginTransaction().replace(R.id.container,mSaleInfoFragment).commit();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                Fragment selected = null;
                if (pos == 0) {
                    selected = mSaleInfoFragment;
                } else
                    selected =  mEventInfoFragment;

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
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
