package com.pouch.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pouch.R;
import com.pouch.adapter.DataAdapter;
import com.pouch.customView.QuickAction;
import com.pouch.data.ActionItem;
import com.pouch.data.brandInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private DrawerLayout                 mDrawerLayout;
    private Toolbar BottomToolbar;

    private ArrayList<brandInfo> BrandList;
    private URL[] brandInstagramURL;
    private String[] brandURL;
    private String[] brandName;

    private GridLayout                   GridBrand;

    private LinearLayout [] testLinearLayout;
    private FrameLayout []testBack;
    private ImageView[] testArr;
    private TextView[] testArr2;

    private CharSequence                 mTitle;
    private CharSequence                 mDrawerTitle;

    private LinearLayout layout;
    private ImageView img;

    private String[]                     menuList; // 필요한 catagory들이 들어간다.
    private boolean isTest=true;
    private static final int ID_ADD = 1;
    private static final int ID_ACCEPT = 2;
    private static final int ID_UPLOAD = 3;

    private static int mRowSelected = 0;
    private QuickAction mQuickAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int imgID = R.id.cream;
        brandName = getResources().getStringArray(R.array.brandlist);
        brandURL = getResources().getStringArray(R.array.instagram_url);
        brandInstagramURL = new URL[brandName.length];


        testArr = new ImageView[brandName.length];
        testArr2 = new TextView[brandName.length];
        testBack = new FrameLayout[brandName.length];
        testLinearLayout = new LinearLayout[brandName.length];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/2,
                getResources().getDisplayMetrics().heightPixels/2);
        params.weight= 1.0f;
//        params.gravity = Gravity.RIGHT;
        params.setLayoutDirection(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);


        for(int i =0;i<brandURL.length;i++){
            try {
                Log.v("URL : ",brandURL[i]);

                brandInstagramURL[i] = new URL(brandURL[i]);

                testLinearLayout[i]  = new LinearLayout(this);
                testLinearLayout[i].setLayoutParams(params);

                testBack[i] = new FrameLayout(this);
                testBack[i].setLayoutParams(params);
                testBack[i].setBackgroundColor(Color.GREEN);

                testArr [i] = new ImageView(this);
                testArr[i].setImageResource(R.drawable.concealer);
                CancelableParams.gravity=Gravity.CENTER;
                testArr[i].setLayoutParams(CancelableParams);


                testArr[i].setOnClickListener(new View.OnClickListener(){
                    int selected = mRowSelected;


                    @Override
                    public void onClick(View v) {
                        mRowSelected = selected;
                        mQuickAction.show(v);
                    }
                });
                mRowSelected++;

                testBack[i].addView(testLinearLayout[i]);
                testLinearLayout[i].addView(testArr[i]);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        mRowSelected = 0;
        //initialize
        // activity의 타이틀값을 받아온다.
        mTitle = mDrawerTitle = getTitle();

        menuList = getResources().getStringArray(R.array.menu_array);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);

        BottomToolbar = (Toolbar)findViewById(R.id.m_bottomTabBar);

        BrandList = new ArrayList<brandInfo>();
        for(int i =0;i<brandName.length;i++){
            brandInfo tmp = new brandInfo(brandName[i],brandInstagramURL[i],imgID);
            BrandList.add(tmp);
        }

        GridBrand = (GridLayout)findViewById(R.id.main_brandlist);

        for(int i =0;i<testArr.length;i++){
            GridBrand.addView(testBack[i]);
        }


        ActionItem addItem 		= new ActionItem(ID_ADD, "Add", getResources().getDrawable(R.drawable.ic_add));
        ActionItem acceptItem 	= new ActionItem(ID_ACCEPT, "Accept", getResources().getDrawable(R.drawable.ic_accept));
        ActionItem uploadItem 	= new ActionItem(ID_UPLOAD, "Upload", getResources().getDrawable(R.drawable.ic_up));

        mQuickAction 	= new QuickAction(this);

        mQuickAction.addActionItem(addItem);
        mQuickAction.addActionItem(acceptItem);
        mQuickAction.addActionItem(uploadItem);

        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);

                if (actionId == ID_ADD) { //Add item selected
                   Log.v(TAG,"ID_ADD Clicked");
                } else {
                   Log.v(TAG,"NOT ID_ADD Cliked");
                }
            }
        });
        //setup on dismiss listener, set the icon back to normal
        mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    private void InitGridLayout(){

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
