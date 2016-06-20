package com.pouch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
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

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private DrawerLayout                 mDrawerLayout;
    private Toolbar BottomToolbar;

    private ArrayList<brandInfo> BrandList;
    private URL[] brandInstagramURL;
    private String[] brandURL;
    private String[] brandName;

    private GridLayout                   GridBrand;
    private MainAdapter                  brandListAdapater;

    private CharSequence                 mTitle;
    private CharSequence                 mDrawerTitle;


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
        brandName = getResources().getStringArray(R.array.menu_array);
        brandURL = getResources().getStringArray(R.array.instagram_url);
        for(int i =0;i<brandURL.length;i++){
            try {
                brandInstagramURL[i] = new URL(brandURL[i]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

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


        brandListAdapater = new MainAdapter(this,BrandList);



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

    private class MainAdapter extends BaseAdapter {

        private brandInfo mBrand;
        private Context mContext;
        private final String TAG = this.getClass().getSimpleName();
        private LayoutInflater layoutInflater;
        private List<brandInfo> listStorage;
        private boolean IsTest = true;
        public MainAdapter(Context context,List<brandInfo> customizedListView){
            mContext = context;
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listStorage = customizedListView;
        }

        @Override
        public int getCount() {
            return listStorage.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder listImageHolder;

            mRowSelected = position;
            if(IsTest) {
                Log.v(TAG, "getView()");
            }

            if (convertView == null){
                listImageHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.activity_main_item,parent,false);
                listImageHolder.imageListView = (ImageView)convertView.findViewById(R.id.Main_ImageItem);
                listImageHolder.imageListView.setImageResource(listStorage.get(position).getBrandImageRes());
                listImageHolder.imageListView.setOnClickListener(new View.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(View v) {
                                                                         mQuickAction.show(v);
                                                                     }
                                                                 }
                );
                convertView.setTag(listImageHolder);
            }
            else {
                // findViewById로 호출을 자꾸 하게되면 성능이 느려진다.
                //조금이나마 빨리하기위해 이렇게함.
                listImageHolder =  (ViewHolder)convertView.getTag();
            }

            return convertView;
        }


    }
    static class ViewHolder{
        ImageView imageListView;
    }
}
