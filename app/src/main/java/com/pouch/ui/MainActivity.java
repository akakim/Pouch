package com.pouch.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.pouch.R;
import com.pouch.customView.QuickAction;
import com.pouch.data.ActionItem;
import com.pouch.data.brandInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.pouch.util.ImageFetcher;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private DrawerLayout                 mDrawerLayout;
    private Toolbar BottomToolbar;

    private ArrayList<brandInfo> BrandList;
    private URL[] brandInstagramURL;
    private String[] brandURL;
    private String[] brandName;

    // 상품에 대한 정보를 담는 Layout.
    private GridLayout                   GridBrand;


///    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;


    private CharSequence                 mTitle;
    private CharSequence                 mDrawerTitle;

    private LinearLayout layout;


    private String[]                     menuList; // 필요한 catagory들이 들어간다.
    private boolean isTest=true;

    /* QuickAction을 위한 변수. */
    private static final int ID_EVENT = 1;
    private static final int ID_SEARCH = 2;
    private static final int ID_SHOW_PRODUCT = 3;

    private static int mRowSelected = 0;
    private QuickAction mQuickAction;

    /*카카오와 연동을 위한 추가적인 변수들.*/

    Intent getUserInformation;
    private ImageView Profile_img;
    private TextView userNameTextView;
    private String profileUrl;
    private String userName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int imgID = R.drawable.cream;
        brandName = getResources().getStringArray(R.array.brandlist);
        brandURL = getResources().getStringArray(R.array.instagram_url);
        brandInstagramURL = new URL[brandName.length];

        InitGridLayout();

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



        ActionItem addItem 		= new ActionItem(ID_EVENT, "Event", getResources().getDrawable(R.drawable.ic_add));
        ActionItem acceptItem 	= new ActionItem(ID_SEARCH, "Accept", getResources().getDrawable(R.drawable.ic_accept));
        ActionItem uploadItem 	= new ActionItem(ID_SHOW_PRODUCT, "Upload", getResources().getDrawable(R.drawable.ic_up));

        mQuickAction 	= new QuickAction(this);

        mQuickAction.addActionItem(addItem);
        mQuickAction.addActionItem(acceptItem);
        mQuickAction.addActionItem(uploadItem);

        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);
                String NextActivity = BrandList.get(mRowSelected).getBrandName();
                Intent i;
                switch (actionId){
                    case ID_EVENT:

                        break;
                    case ID_SEARCH:
                        break;
                    case ID_SHOW_PRODUCT:
                        i = new Intent(getApplicationContext(),ProductsActivity.class);
                        i.putExtra("brandName",brandName[mRowSelected]);
                        startActivity(i);
                        break;
                }
            }
        });
        //setup on dismiss listener, set the icon back to normal
        mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        /*setting drawerLayout */
        initDrawLayout();
    }

    private void initDrawLayout(){
        Profile_img = (ImageView)findViewById(R.id.profileImage);
        userNameTextView = (TextView)findViewById(R.id.navigation_drawer_items_textView_NICKNAME);
        getUserInformation = getIntent();
        profileUrl = getUserInformation.getExtras().getString("ProfileURL");
        userName = getUserInformation.getExtras().getString("UserName");
       if (profileUrl != null) {
           Picasso.with(getApplicationContext()).load(profileUrl).fit().into(Profile_img);
       }

        if(userName != null){
            userNameTextView.setText(userName);
        }
    }
    private void InitGridLayout(){

        LinearLayout [] testLinearLayout;
        FrameLayout []testBack;
        ImageView[] testArr;
        testArr = new ImageView[brandName.length];

        testBack = new FrameLayout[brandName.length];
        testLinearLayout = new LinearLayout[brandName.length];



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/2,
                getResources().getDisplayMetrics().heightPixels/2);
        params.weight= 1.0f;
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

        // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
        mRowSelected = 0;

        GridBrand = (GridLayout)findViewById(R.id.main_brandlist);

        for(int i =0;i<testArr.length;i++){
            GridBrand.addView(testBack[i]);
        }
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
                i  =new Intent(this,PouchActivity.class);
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
