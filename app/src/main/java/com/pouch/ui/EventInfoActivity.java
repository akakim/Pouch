package com.pouch.ui;


import android.bluetooth.le.AdvertiseData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.pouch.R;
import com.pouch.util.ImageCache;
import com.pouch.util.ImageFetcher;
import com.squareup.picasso.Picasso;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class EventInfoActivity extends AppCompatActivity {
    String url = "http://m.etonymoly.com/html/event/event_list.asp";
    String PRIVARY_URL = "http://m.etonymoly.com";
    String MIDDLE_URL="/html/event/";
    private static final String IMAGE_CACHE_DIR = "images";

    ListView listView;
    Adapter adapter;
    ArrayList<String> destination;
    ArrayList<URL> ImageSource;
    final boolean isTest= false;

    ConnectivityManager cManager;
    NetworkInfo networkInfo;

    ImageView[] imageView;
    private ImageFetcher mImageFetcher;

    InnerHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        destination = new ArrayList<>();
        ImageSource = new ArrayList<>();
        handler = new InnerHandler();
        listView= (ListView)findViewById(R.id.listView);


        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);


        new EventThumbnailTask().execute(url);
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


    private boolean isInternetConnected(){
        cManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = cManager.getActiveNetworkInfo();


        return !networkInfo.isConnected();
    }


    private class InnerHandler extends Handler {
        InnerHandler(){

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:

                    break;
            }
        }
    }
    private class EventThumbnailTask extends AsyncTask<String,Integer,Integer>{

        URL target;
        Source source;
        InputStream input;
        @Override
        protected Integer doInBackground(String... params) {
            try {
                target = new URL(params[0]);
                input = target.openStream();
                source = new Source(new InputStreamReader(input,"euc-kr"));
                source.fullSequentialParse();

                int pos = 0;
                List<StartTag> taglst = source.getAllStartTags(HTMLElementName.UL);
                for (int i = 0;i<taglst.size();i++){
                    if(taglst.get(i).toString().equals("<ul class=\"event_list\">")){
                        pos = i;
                    }
                }
                Element TARGET_UL =  source.getAllElements(HTMLElementName.UL).get(pos);
               // Log.v("Task","Target_UL : " + TARGET_UL.getFirstElement().toString());

                for(int i =0; i< TARGET_UL.getAllElements(HTMLElementName.LI).size();i++){
                    Element TARGET_LI = TARGET_UL.getAllElements(HTMLElementName.LI).get(i);
                    //Log.v("Task","Target_LI : " + TARGET_LI.getFirstElement().toString());
                    Element TARGET_DIV = TARGET_LI.getAllElements(HTMLElementName.DIV).get(0);
                    Element TARGET_SPAN= TARGET_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                    Element TARGET_A = TARGET_SPAN.getAllElements(HTMLElementName.A).get(0);
                    String href = TARGET_A.getAttributeValue("href");
                    destination.add(PRIVARY_URL+MIDDLE_URL+href);
                    Element TARGET_IMG = TARGET_A.getAllElements(HTMLElementName.IMG).get(0);
                    String src = TARGET_IMG.getAttributeValue("src");
                    ImageSource.add(new URL(PRIVARY_URL+src));
                    if(destination.size() != 0 && ImageSource.size() != 0 && isTest){
                        Log.v("destination",destination.get(i).toString());
                        Log.v("ImageSource ",ImageSource.get(i).toString());
                    }
                }

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Integer result){
            adapter = new Adapter(getApplicationContext(),ImageSource);
            listView.setAdapter(adapter);


            /*
            handler.sendEmptyMessage(0);
            /*
            for(int i =0;i<ImageSource.size();i++){
                ImageView item= new ImageView(getApplicationContext());
                mImageFetcher.loadImage(ImageSource.get(i), item);
                listView.addView(item);
                adapter.notifyDataSetChanged();
            }*/

        }
    }

    static class ViewHolder {
        ImageView image_item;
    }
    private class Adapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<URL> ImageURL;


        Adapter(Context context){
            this.context = context;
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
           ImageURL  = new ArrayList<>();
        }

        Adapter(Context context,  ArrayList<URL> imageURL){
            this.context = context;
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);

            this.ImageURL = imageURL;
        }

        public Adapter() {
            super();
        }

        public void setImageURL(ArrayList<URL> ImageURL){
            this.ImageURL = ImageURL;
        }

        @Override
        public int getCount() {
            return ImageURL.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder item;
            if (convertView == null){
                item = new ViewHolder();
                convertView = inflater.inflate(R.layout.activity_event_info_item,parent,false);
                item.image_item = (ImageView)convertView.findViewById(R.id.item);
                item.image_item.setImageResource(R.drawable.empty_photo);
                //mImageFetcher.loadImage(ImageURL.get(position),item[position].image_item);

                item.image_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(destination.get(position)));
                        startActivity(i);
                    }
                });
                convertView.setTag(item);
            }else {
                item = (ViewHolder)convertView.getTag();
            }

            Picasso.with(context).load(ImageURL.get(position).toString()).into(item.image_item);
            return convertView;
        }
    }
}
