package com.pouch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.pouch.R;
import com.pouch.data.Item;
import com.pouch.ui.fragment.ProductDetailFragment;
import com.pouch.util.ImageCache;
import com.pouch.util.ImageFetcher;
import com.pouch.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener,
        ProductDetailFragment.CustomOnClickListener{


    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String KEY_ITEM_DATA = "item_data";
    public static final String PRODUCT_DATA_SET = "product_data_set" ;
    public static final String PRODUCT_DATA_KEY_SET = "product_data_key_set" ;


    SharedPreferences sharedPreferences;

    private ViewPager mPager;

    private ImageFetcher mImageFetcher;
    private ImagePagerAdapter mAdapter;

    ConnectivityManager cManager;
    NetworkInfo networkInfo;
    private String TAG = getClass().getSimpleName();

    ArrayList<Item> Items;
    int selectedItem = 0;

    ProductDetailFragment instnance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent=  getIntent();
        selectedItem = intent.getIntExtra("selectedItem",0);
        Items = intent.getParcelableArrayListExtra(KEY_ITEM_DATA);

        if(isInternetConnected()){
            Toast.makeText(getApplicationContext(), "인터넷에 연결되지않아 불러오기를 중단합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            try{

            }catch(Exception e){
                Log.v(TAG, "네트워크 통신에러");
            }
        }


        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        // 이 샘플을 위해 우리는 가장긴 폭의 절반을 이미지를 resize할 것이다.
        //
        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);


        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), Items.size());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin(16);
        mPager.setOffscreenPageLimit(2);

        // Set up activity to go full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience


        if (Utils.hasHoneycomb()) {

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar == null){
                Log.e(TAG,"actionbar is null");
            }
            else {
                // Hide title text and set home as up
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);

                // Hide and show the ActionBar as the visibility changes
                mPager.setOnSystemUiVisibilityChangeListener(
                        new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int vis) {
                                if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                    actionBar.hide();
                                } else {
                                    actionBar.show();
                                }
                            }
                        });

                // Start low profile mode and hide ActionBar
                mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                actionBar.hide();
            }
        }

        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        if (extraCurrentItem != -1) {
            Log.v(TAG,"extraCurrentItem -1");
            mPager.setCurrentItem(extraCurrentItem);


        }
        mPager.setCurrentItem(selectedItem);
       // product_data_key_set = new HashSet<String>();
    }

    private boolean isInternetConnected(){
        cManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = cManager.getActiveNetworkInfo();


        return !networkInfo.isConnected();
    }


    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public void onClicked(int id) {
        Log.v(TAG,"onClikced");
        switch (id){
            case R.id.shared_prereference:

                StringBuilder key = new StringBuilder(Items.get(selectedItem).getImageURL().toString());
                sharedPreferences = getSharedPreferences(PRODUCT_DATA_SET, Context.MODE_APPEND);

                SharedPreferences.Editor editor =  sharedPreferences.edit();
                Log.v("onClicked , key value",key.toString());

                editor.putString(PRODUCT_DATA_KEY_SET, key.toString());

                editor.putString(key.toString()+"_TITLE",Items.get(selectedItem).getTitle());

               // editor.putString(key.toString()+"_title",Items.get(selectedItem).);
                ProductDetailFragment fragment = ( ProductDetailFragment)mAdapter.getItem(selectedItem);
                ArrayList<String> Head = fragment.getHeadValues();
                ArrayList<String> Tail = fragment.getTailValues();

                editor.putString(key.toString()+"_SIZE",String.valueOf(Head.size()));
                for(int i =0; i<Head.size();i++) {
                    editor.putString(key.toString() + "_HEAD_"+String.valueOf(i),Tail.get(i).toString());
                    editor.putString(key.toString() + "_TAIL_"+String.valueOf(i), Tail.get(i).toString());
                }

                editor.commit();
            break;
            case R.id.check_path:

                Toast.makeText(getApplicationContext(),"get path" + mImageFetcher.getUri(),Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {


         return ProductDetailFragment.newInstance(Items.get(position).getImageURL().toString()
         ,Items.get(position).getProductURL().toString(),Items.get(position));
        }
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
