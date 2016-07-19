package com.pouch.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageButton;
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
import java.util.List;


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
    private String[] brandNameGoodKey;

    // 상품에 대한 정보를 담는 Layout.
    private GridLayout                   GridBrand;


///    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;


    private CharSequence                 mTitle;
    private CharSequence                 mDrawerTitle;

    private LinearLayout layout;


    private String[]                     menuList; // 필요한 catagory들이 들어간다.
    private boolean isTest=false;

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
        brandNameGoodKey = getResources().getStringArray(R.array.brandlist_goodKeyword);
        brandURL = getResources().getStringArray(R.array.instagram_url);
        brandInstagramURL = new URL[brandName.length];

        InitGridLayout();

        // activity의 타이틀값을 받아온다.
        mTitle = mDrawerTitle = getTitle();

        menuList = getResources().getStringArray(R.array.menu_array);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);

        BottomToolbar = (Toolbar)findViewById(R.id.m_bottomTabBar);
        ImageButton reviewBtn =(ImageButton) BottomToolbar.findViewById(R.id.Review);
        ImageButton Instagram = (ImageButton) BottomToolbar.findViewById(R.id.Instagram);
        ImageButton Search = (ImageButton) BottomToolbar.findViewById(R.id.Search);
        ImageButton Pouch = (ImageButton) BottomToolbar.findViewById(R.id.Pouch);

        BitmapFactory.Options options = new  BitmapFactory.Options();
        options.inSampleSize = 3;

        reviewBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.review_toolbar,options));
        Instagram.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.instagram_toolbar,options));
        Search.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.search_toolbar,options));
        Pouch.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pouch_toolbar,options));

        //setImageResource(R.drawable.review_toolbar);
//        Instagram.setImageResource(R.drawable.instagram_toolbar);
//        Search.setImageResource(R.drawable.search_toolbar);
 //
 //
 //      Pouch.setImageResource(R.drawable.pouch_toolbar);

        BrandList = new ArrayList<brandInfo>();
        for(int i =0;i<brandName.length;i++){
            brandInfo tmp = new brandInfo(brandName[i],brandInstagramURL[i],imgID);
            BrandList.add(tmp);
        }



        ActionItem addItem 		= new ActionItem(ID_EVENT, "Event", getResources().getDrawable(R.drawable.event_quickactionbar));
        ActionItem acceptItem 	= new ActionItem(ID_SEARCH, "Accept", getResources().getDrawable(R.drawable.map_quickactionbar));
        ActionItem uploadItem 	= new ActionItem(ID_SHOW_PRODUCT, "Upload", getResources().getDrawable(R.drawable.productlist_quickactionbar));

        mQuickAction 	= new QuickAction(this);

        mQuickAction.addActionItem(addItem);
        mQuickAction.addActionItem(acceptItem);
        mQuickAction.addActionItem(uploadItem);

        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);

                Intent i;
                int selected = mQuickAction.getSelectedParent();
                switch (actionId){
                    case ID_EVENT:
                        i = new Intent(getApplicationContext(),EventInfoActivity.class);
                        startActivity(i);
                        break;
                    case ID_SEARCH:
                        i = new Intent(getApplicationContext(),GoogleMapServiceActivity.class);
                        i.putExtra("brandName",brandName[selected]);
                        i.putExtra("keyword",brandNameGoodKey[selected]);
                        startActivity(i);
                        break;
                    case ID_SHOW_PRODUCT:
                        i = new Intent(getApplicationContext(),ProductsActivity.class);
                        i.putExtra("brandName",brandName[selected]);
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
       if (profileUrl != null&& Profile_img != null) {
           Picasso.with(getApplicationContext()).load(profileUrl).fit().into(Profile_img);
       }

        if(userName != null && userNameTextView!=null){
            userNameTextView.setText(userName);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void InitGridLayout(){

        LinearLayout [] LinearLayout_FrameLayoutChild;
        FrameLayout []ItemBackground_FrameLayout;
        ImageView[] BrandShopArr;
        BrandShopArr = new ImageView[brandName.length];

        ItemBackground_FrameLayout = new FrameLayout[brandName.length];
        LinearLayout_FrameLayoutChild = new LinearLayout[brandName.length];

        int imgsrc[] = {
                R.drawable.laneige,
                R.drawable.tonymoly,
                R.drawable.etudehouse,
                R.drawable.iope,
                R.drawable.innisfree,
                R.drawable.missha,
                R.drawable.nature_republic,
                R.drawable.the_face_shop
        };

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/2,
                getResources().getDisplayMetrics().heightPixels/2);
        params.weight= 1.0f;
        params.setLayoutDirection(LinearLayout.HORIZONTAL);
        params.bottomMargin = 10;
        LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for(int i =0;i<brandURL.length;i++){
            try {

                brandInstagramURL[i] = new URL(brandURL[i]);

                LinearLayout_FrameLayoutChild[i]  = new LinearLayout(this);
                LinearLayout_FrameLayoutChild[i].setLayoutParams(params);

                ItemBackground_FrameLayout[i] = new FrameLayout(this);
                ItemBackground_FrameLayout[i].setLayoutParams(params);
                switch (i%4){
                    case 0:
                        ItemBackground_FrameLayout[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),R.drawable.back_1)));
                        break;
                    case 1:
                        ItemBackground_FrameLayout[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),R.drawable.back_2)));
                        break;
                    case 2:
                        ItemBackground_FrameLayout[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),R.drawable.back_3)));
                        break;
                    case 3:
                        ItemBackground_FrameLayout[i].setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),R.drawable.back_4)));
                        break;

                }
                //BitmapDrawable img = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(),imgsrc[i]));
                //BrandShopArr [i] = new ImageView(this);
                //BrandShopArr[i].setImageDrawable(img);

                BrandShopArr [i] = new ImageView(this);
                BitmapFactory.Options options = new  BitmapFactory.Options();
                options.inSampleSize = 2;

                BrandShopArr[i].setImageDrawable(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(),imgsrc[i],options)));

                CancelableParams.gravity=Gravity.CENTER;
                BrandShopArr[i].setLayoutParams(CancelableParams);
                BrandShopArr[i].setOnClickListener(new View.OnClickListener() {
                    int selected = mRowSelected;

                    @Override
                    public void onClick(View v) {
                        // column 값을 전달한다.
                        mQuickAction.show(v,selected%2,getResources().getDisplayMetrics().widthPixels/2);
                        mQuickAction.setSelectedParent(selected);
                    }
                });
                mRowSelected++;

                ItemBackground_FrameLayout[i].addView(LinearLayout_FrameLayoutChild[i]);
                LinearLayout_FrameLayoutChild[i].addView(BrandShopArr[i]);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        mRowSelected = 0;

        GridBrand = (GridLayout)findViewById(R.id.main_brandlist);

        for(int i =0;i<BrandShopArr.length;i++){
            GridBrand.addView(ItemBackground_FrameLayout[i]);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
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
                Uri uri = Uri.parse(brandURL[mRowSelected]);
                i=new Intent(Intent.ACTION_VIEW);
                i.setData(uri);
                startActivity(i);
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
    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
