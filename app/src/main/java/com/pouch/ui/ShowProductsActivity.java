package com.pouch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.pouch.R;
import com.pouch.ui.fragment.MyPouchFragment;

public class ShowProductsActivity extends AppCompatActivity {
    MyPouchFragment AllOfProuducts;
    MyPouchFragment FavoriteProducts;
    TabLayout tabs;
    Intent getbrandName;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products);

        getbrandName = new Intent();
        if(getbrandName.getStringExtra("brandName") == null){
            title = "null value";
        }else{
            title = getbrandName.getStringExtra("brandName");
        }

        getSupportActionBar().setTitle(title);

        AllOfProuducts = new MyPouchFragment();
        FavoriteProducts =new MyPouchFragment();
        tabs = (TabLayout)findViewById(R.id.showProductTabLayout);

        getSupportFragmentManager().beginTransaction().replace(R.id.show_product_container, AllOfProuducts).commit();

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                Fragment selected = null;
                if (pos == 0) {
                    selected = AllOfProuducts;
                } else
                    selected = FavoriteProducts;

                getSupportFragmentManager().beginTransaction().replace(R.id.item_pouch_container, selected).commit();
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
