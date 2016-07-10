package com.pouch.ui;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;


import com.pouch.R;
import com.pouch.data.Item;
import com.pouch.ui.fragment.ProductFragment;
import com.pouch.ui.fragment.ProductFavoriteFragment;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.Tag;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsActivity extends AppCompatActivity implements ProductFragment.OnFragmentInteractionListener,ProductFavoriteFragment.OnFragmentInteractionListener{
    private static final String TAG = "PouchActivity";
    BrandURLs brand = new BrandURLs();// TODO: 브랜드 파싱을 더할 수있다면 여길 이용하자.
    private static String URL_PRIMARY = "http://m.etonymoly.com";

    ConnectivityManager cManager;
    NetworkInfo networkInfo;

    ProductFragment AllOfProuducts;
    ProductFavoriteFragment FavoriteProducts;

    InnerHandler handler;
    TabLayout tabs;
    Intent getbrandName;
    String title;

    // 작은 카테고리만 list로 만든다.
    String [] URLList = new String [80];
    String [] TitleList =new String [80];

    int URLMaxSize = 0;
    ArrayList<Item> Items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products);

        getbrandName = getIntent();
        if(getbrandName.getStringExtra("brandName") == null){
            title = "null value";
        }else{
            title = getbrandName.getStringExtra("brandName");
        }

        getSupportActionBar().setTitle(title);


        //TODO: ActionBar에 refresh버튼생성.


        /*네트워크 상태 조회 */

        if(isInternetConnected()){
            Toast.makeText(getApplicationContext(), "인터넷에 연결되지않아 불러오기를 중단합니다.", Toast.LENGTH_SHORT).show();
            //TODO: 종료를 삭제하고 refres버튼을 이용한다.
            finish();
        }else {
            try{

            }catch(Exception e){
                Log.v(TAG, "네트워크 통신에러");
            }
        }
        Items = new ArrayList<Item>();
        handler = new InnerHandler();



        AllOfProuducts = new ProductFragment();

        FavoriteProducts =new ProductFavoriteFragment();

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

        Log.v(TAG,"exThread");
        new TonimoriInitThread(this).execute();
        Log.v(TAG,"afterThread");
    }

    private boolean isInternetConnected(){
        cManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = cManager.getActiveNetworkInfo();


        return !networkInfo.isConnected();
    }

    public void InitMenuAndItmes(){

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class InnerHandler extends Handler {
        InnerHandler(){}

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int message = msg.what;
            switch(message){
                case 0:
                    Log.v(TAG,"message 0");
                    AllOfProuducts.setItmes(Items);
                    AllOfProuducts.Invalidate();
                    AllOfProuducts.setImage();
                break;

            }
        }


    }

    private class TonimoriInitThread extends AsyncTask<Void,String,Integer>  {

        private ProgressDialog mDialog;
        private Context context;

        private InputStream input;
        private Source source;
        private URL targetURL;



        StringBuilder ImageURL[];
        StringBuilder product_title[];
        StringBuilder del[];
        StringBuilder em[];

        final int NumberOfItmes=20;
        private String TAG = getClass().getSimpleName();
        public TonimoriInitThread(Context context){
            this.context = context;

        }


        @Override
        protected void onPreExecute() {

            mDialog = new ProgressDialog(context);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("네트워크에서 다운로드");
            mDialog.show();

            /* 동적할당의 경우 못해주는건지 못알아낸건지. 여튼 정적할당으로 처리한다. */
            ImageURL = new StringBuilder[NumberOfItmes];
            product_title = new StringBuilder[NumberOfItmes];
            del = new StringBuilder[NumberOfItmes];
            em =new StringBuilder[NumberOfItmes];

        }

        //TODO: 나중에 다른 URL들로부터 값들을 받아오려면 Integer -> String
        @Override
        protected Integer doInBackground(Void... params) {
            int targetSection = 0;
            int start = 0;
            try {
                targetURL = new URL("http://m.etonymoly.com/html/lp_list.asp?cate=140");
                input = targetURL.openStream();

                source = new Source(new InputStreamReader(input, "euc-kr"));
                source.fullSequentialParse();

                // section 타입의 모든 태그를 불러옴.
                List<StartTag> tableTag = source.getAllStartTags(HTMLElementName.SECTION);
                for (int i = 0; i < tableTag.size(); i++) {
                    if (tableTag.get(i).toString().equals("<section class=\"goods-list-section\">")) {
                        targetSection = i;

                        break;
                    }
                }

                Element Target_SECTION = (Element) source.getAllElements(HTMLElementName.SECTION).get(targetSection);
                Element Target_UL = (Element) Target_SECTION.getAllElements(HTMLElementName.UL).get(0);

                // 결과값 유무확인
                Element rule = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(0);



                if (rule.getAttributeValue("class") == null){
                    int count = 0;
                    List<StartTag> startTagList = Target_UL.getAllStartTags(HTMLElementName.LI);
                    for (StartTag tmp : startTagList) {
                        // Log.v("StartTag", tmp.toString());
                        count++;
                    }
                    android.util.Log.v("Count", String.valueOf(count));

                    for (int j = 0; j < count; j++) {
                        Element Target_LI = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(j);
                        android.util.Log.v("Target_LI", Target_LI.toString());


                        Element Target_DIV = (Element) Target_LI.getAllElements(HTMLElementName.DIV).get(0);

                        android.util.Log.v("Target_DIV", Target_DIV.toString());
                        Element Target_SPAN = (Element) Target_DIV.getAllElements(HTMLElementName.SPAN).get(0);

                        Element Target_PRODUCT_DIV = (Element) Target_DIV.getAllElements(HTMLElementName.DIV).get(1);
                        android.util.Log.v("Target_Product_div", Target_PRODUCT_DIV.toString());
                        Element Target_A = (Element) Target_SPAN.getAllElements(HTMLElementName.A).get(0);

                        Element IMGURL = (Element) Target_A.getAllElements(HTMLElementName.IMG).get(0);

                        // Log.v("IMGURL",IMGURL.toString());
                         ImageURL[j] = new StringBuilder(IMGURL.getAttributeValue("src"));

                        // Log.v("imageURL",ImageURL);
                        Element TITLE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        //             Log.v("TITLE_SPAN",TITLE_SPAN.toString());
                        Element PRICE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(1);

                        Element Product_A = (Element) TITLE_SPAN.getAllElements(HTMLElementName.A).get(0);
                        Segment TITLE = Product_A.getFirstElement().getContent();
                         title = TITLE.toString();

                        Element DEL;
                        Element EM = (Element) PRICE_SPAN.getAllElements(HTMLElementName.EM).get(0);

                        List<Tag> lst = PRICE_SPAN.getAllTags();
                        for (Tag tmp : lst) {
                            android.util.Log.v("for", tmp.toString());
                            if (tmp.toString().equals("<del>")) {
                                DEL = (Element) PRICE_SPAN.getAllElements(HTMLElementName.DEL).get(0);
                                android.util.Log.v("DEL", DEL.getContent().toString());
                                del[j] = new StringBuilder(DEL.getContent().toString());
                            }
                            else
                                del[j] = new StringBuilder("");
                            if (tmp.toString().equals("<em>")) {

                                em[j]= new StringBuilder(EM.getContent().toString());

                            }
                        }

                        product_title[j] = new StringBuilder(TITLE.toString());

                        Item tmpItem = new Item(product_title[j].toString(),del[j].toString(),em[j].toString(),URL_PRIMARY+ImageURL[j].toString());
                        Items.add(tmpItem);

                        android.util.Log.v("EM", EM.getContent().toString());
                    }
                }
                else if (rule.getAttributeValue("class").equals("NO_RESULT")) {
                    android.util.Log.v("NO_RESULT", "결과가없음!");

                }



            }
            catch(MalformedURLException e){
                android.util.Log.v(TAG, "MalformedURL");
            }
            catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Integer result){

            mDialog.setMessage("받아온값 "+result);
            mDialog.dismiss();
            handler.sendEmptyMessage(0);
        }
    }


    /*category를 불러오는 task */
    private class task extends AsyncTask<Integer,Integer,Integer>{
        private InputStream input;
        private Source source;
        private URL targetURL;
        int maxSize;
        @Override
        protected Integer doInBackground(Integer... params) {
            int targetTableNumber = 0;
            android.util.Log.v(TAG, "doInBackground...");
            try {

                targetURL = new URL(URL_PRIMARY);
                input = targetURL.openStream();

                source = new Source(new InputStreamReader(input, "euc-kr"));
                source.fullSequentialParse();

                // UL타입의 모든 태그를 불러옴.
                List<StartTag> tableTag = source.getAllStartTags(HTMLElementName.UL);
                for (int i = 0; i<tableTag.size();i++){
                    if(tableTag.get(i).toString().equals("<div class=\"slidebar__category__sub\">")){
                        targetTableNumber = i;
                        //Log.v(TAG,"Target div Location : "+ i);
                    }
                }

                //BBSlocate 번째 의 UL 를 모두 가져온다.
                Element Target_UL =  (Element)source.getAllElements(HTMLElementName.UL).get(targetTableNumber);
                // URLList = new String[Target_UL.length()];
                //TitleList =new String [Target_UL.length()];

                // Log.v(TAG,"index Range" + Target_UL.getAllElements(HTMLElementName.LI).size());

                /**
                 * Expandable ListView로 구현해야되 .ㅠㅠㅠㅜㅠㅠ
                 *
                 */
                for (int j = 0; j<Target_UL.getAllElements(HTMLElementName.LI).size();j++){
                    Element TargetLI = (Element)Target_UL.getAllElements(HTMLElementName.LI).get(j);
                    Element Target_a = (Element)TargetLI.getAllElements(HTMLElementName.A).get(0);
                    if (Target_a.getAttributeValue("href").equals("#")){
                        // category의 사이즈를 정하기위함.
                        maxSize = j;
                        break;
                    }
                    URLList[j]= Target_a.getAttributeValue("href");

                    Segment s = TargetLI.getAllElements(HTMLElementName.A).get(0).getContent();
                    TitleList[j] = s.toString();
//                    URLList[j] = Target_LI

                    if(URLList[j]==null){
                        break;
                    }
                    android.util.Log.v("URL", URLList[j]);
                    android.util.Log.v("Title", TitleList[j]);
                }


            }
            catch(MalformedURLException e){
                android.util.Log.v(TAG, "MalformedURL");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            android.util.Log.v("URL0", URLList[0]);
            super.onPostExecute(integer);
        }
    }

    /*category안의 Item List를불러오는 task */
    private class task2 extends AsyncTask<Integer,Integer,Integer>{
        private InputStream input;
        private Source source;
        private URL targetURL;
        @Override
        protected Integer doInBackground(Integer... params) {
            int targetSection = 0;
            int start = 0;
            try {
                targetURL = new URL("http://m.etonymoly.com/html/lp_list.asp?cate=140");
                input = targetURL.openStream();

                source = new Source(new InputStreamReader(input, "euc-kr"));
                source.fullSequentialParse();

                // section 타입의 모든 태그를 불러옴.
                List<StartTag> tableTag = source.getAllStartTags(HTMLElementName.SECTION);
                for (int i = 0; i < tableTag.size(); i++) {
                    if (tableTag.get(i).toString().equals("<section class=\"goods-list-section\">")) {
                        targetSection = i;

                        android.util.Log.v(TAG, "Target section Location : " + i);
                        android.util.Log.v("table TAG", tableTag.get(i).toString());
                        break;
                    }
                }

                Element Target_SECTION = (Element) source.getAllElements(HTMLElementName.SECTION).get(targetSection);
                Element Target_UL = (Element) Target_SECTION.getAllElements(HTMLElementName.UL).get(0);
                // 결과값 유무확인
                Element rule = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(0);

//                Log.v("Target_UL",Target_UL.toString());
                /*li tag 갯수새기 */


                if (rule.getAttributeValue("class") == null){
                    int count = 0;
                    List<StartTag> startTagList = Target_UL.getAllStartTags(HTMLElementName.LI);
                    for (StartTag tmp : startTagList) {
                        // Log.v("StartTag", tmp.toString());
                        count++;
                    }
                    android.util.Log.v("Count", String.valueOf(count));

                    for (int j = 0; j < count; j++) {
                        Element Target_LI = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(j);
                        android.util.Log.v("Target_LI", Target_LI.toString());


                        Element Target_DIV = (Element) Target_LI.getAllElements(HTMLElementName.DIV).get(0);

                        android.util.Log.v("Target_DIV", Target_DIV.toString());
                        Element Target_SPAN = (Element) Target_DIV.getAllElements(HTMLElementName.SPAN).get(0);

                        //Log.v("Target_SPAN",Target_SPAN.toString());
                        Element Target_PRODUCT_DIV = (Element) Target_DIV.getAllElements(HTMLElementName.DIV).get(1);
                        android.util.Log.v("Target_Product_div", Target_PRODUCT_DIV.toString());
                        Element Target_A = (Element) Target_SPAN.getAllElements(HTMLElementName.A).get(0);
//                Log.v("Target_A",Target_A.toString());
                        Element IMGURL = (Element) Target_A.getAllElements(HTMLElementName.IMG).get(0);

                        // Log.v("IMGURL",IMGURL.toString());
                        String ImageURL = IMGURL.getAttributeValue("src");
                        // Log.v("imageURL",ImageURL);
                        Element TITLE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        //             Log.v("TITLE_SPAN",TITLE_SPAN.toString());
                        Element PRICE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(1);

                        Element Product_A = (Element) TITLE_SPAN.getAllElements(HTMLElementName.A).get(0);
                        Segment TITLE = Product_A.getFirstElement().getContent();
                        String title = TITLE.toString();

/*

                if(DEL == null){
                    Log.v("DEL","NULL");
                }*/
                        Element DEL;
                        Element EM = (Element) PRICE_SPAN.getAllElements(HTMLElementName.EM).get(0);

                        List<Tag> lst = PRICE_SPAN.getAllTags();
                        for (Tag tmp : lst) {
                            android.util.Log.v("for", tmp.toString());
                            if (tmp.toString().equals("<del>")) {
                                DEL = (Element) PRICE_SPAN.getAllElements(HTMLElementName.DEL).get(0);
                                android.util.Log.v("DEL", DEL.getContent().toString());
                                android.util.Log.v("DEL", "IS");
                            }
                            if (tmp.toString().equals("<em>")) {


                                android.util.Log.v("EM", "IS");
                            }
                        }
                        android.util.Log.v("Image URL", ImageURL);
                        android.util.Log.v("Product Title", title);
                        android.util.Log.v("EM", EM.getContent().toString());
                    }
                }
                else if (rule.getAttributeValue("class").equals("NO_RESULT")) {
                    android.util.Log.v("NO_RESULT", "결과가없음!");

                }



            }
            catch(MalformedURLException e){
                android.util.Log.v(TAG, "MalformedURL");
            }
            catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }



    }
    // TODO: filter를 적용할 때 Thread 작성.
}


class BrandURLs{
    //TODO : 각각의 charset도 추가해줘야ㄷ됨.
    HashMap <String,URL> URL_PRIMARY;
    BrandURLs(){
        try {
            URL_PRIMARY = new HashMap<String,URL>();
            URL_PRIMARY.put("토니모리", new URL("http://m.etonymoly.com"));
        }catch(MalformedURLException e){
            Log.println(Log.VERBOSE,"BrandURLs","MalformedURLException : " + e.toString());
            e.printStackTrace();
        }

    }
}