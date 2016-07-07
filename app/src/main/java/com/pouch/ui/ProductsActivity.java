package com.pouch.ui;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.pouch.Logger.Log;
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
                Log.println(Log.VERBOSE, TAG, "네트워크 통신에러");
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

                break;

            }
        }


    }

    private class TonimoriInitThread extends AsyncTask<Void,String,Integer>{

        private ProgressDialog mDialog;
        private Context context;

        private InputStream input;
        private InputStream inputForItem;
        private Source source;
        private URL target;
        private URL initTarget;

        StringBuilder ImageURLBuiler [];
        StringBuilder titleBuilder [];
        StringBuilder delBuilder[],emBuilder[];


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

            super.onPreExecute();
        }

        //TODO: 나중에 다른 URL들로부터 값들을 받아오려면 Integer -> String
        @Override
        protected Integer doInBackground(Void... params) {
            Log.v(TAG,"DoinBackground");

            //Integer rating = params[0];
           // publishProgress("rate", Integer.toString(rating));
            int targetTableNumber = 0;
            int targetSectionNumber = 0;
            try {
                Log.v(TAG,"DoinBackground");
                target = new URL("http://m.etonymoly.com");
                input = target.openStream();

                initTarget = new URL("http://m.etonymoly.com/html/lp_list.asp?cate=140");
                inputForItem = initTarget.openStream();
                // TODO: 각각의 사이트도 다르니 charset또한 추가해줘야됨.
                source = new Source (new InputStreamReader(input,"euc-kr"));
                source.fullSequentialParse();

                List<StartTag> tableTag = source.getAllStartTags(HTMLElementName.UL);
                List<StartTag> getItemTag = source.getAllStartTags(HTMLElementName.SECTION);
                for (int i = 0; i<tableTag.size();i++){
                    // 카테고리를 찾는다.
                    if(tableTag.get(i).toString().equals("<div class=\"slidebar__category__sub\">")){
                        targetTableNumber = i;

                        }
                    Log.v(TAG,String.valueOf(i));
                }

                /* 카테고리 리스트를 불러온다. */

                Element Target_UL =  (Element)source.getAllElements(HTMLElementName.UL).get(targetTableNumber);
                for (int j = 0; j<Target_UL.getAllElements(HTMLElementName.LI).size();j++){
                    Element TargetLI = (Element)Target_UL.getAllElements(HTMLElementName.LI).get(j);
                    Element Target_a = (Element)TargetLI.getAllElements(HTMLElementName.A).get(0);
                    if (Target_a.getAttributeValue("href").equals("#")){
                        // category의 사이즈를 정하기위함.
                        URLMaxSize = j;
                        break;
                    }
                    URLList[j]= Target_a.getAttributeValue("href");

                    Segment s = TargetLI.getAllElements(HTMLElementName.A).get(0).getContent();
                    TitleList[j] = s.toString();

                    if(URLList[j]==null){
                        break;
                    }
                   // rating++;
                }

                /*아이템 리스트 초기값을 불러온다. */

                // 초기 아이템 list를 불러온다.
                for (int i = 0; i < getItemTag.size(); i++) {
                    if (getItemTag.get(i).toString().equals("<section class=\"goods-list-section\">")) {
                        targetSectionNumber = i;
                        break;
                    }
                }

                Element Target_SECTION = (Element) source.getAllElements(HTMLElementName.SECTION).get(targetSectionNumber);

                Target_UL = (Element) Target_SECTION.getAllElements(HTMLElementName.UL).get(0);

                Element rule = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(0);



                if (rule.getAttributeValue("class") == null){
                    int count = 0;
                    List<StartTag> startTagList = Target_UL.getAllStartTags(HTMLElementName.LI);
                    for (StartTag tmp : startTagList) {
                        count++;
                        ImageURLBuiler = new StringBuilder[count];
                    }

                    for (int j = 0; j < count; j++) {
                        Element Target_LI = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(j);
                        Element Target_DIV = (Element) Target_LI.getAllElements(HTMLElementName.DIV).get(0);
                        Element Target_SPAN = (Element) Target_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        Element Target_PRODUCT_DIV = (Element) Target_DIV.getAllElements(HTMLElementName.DIV).get(1);
                        Element Target_A = (Element) Target_SPAN.getAllElements(HTMLElementName.A).get(0);
                        Element IMGURL = (Element) Target_A.getAllElements(HTMLElementName.IMG).get(0);
                        ImageURLBuiler[j] = new StringBuilder(IMGURL.getAttributeValue("src"));

                        Element TITLE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        Element PRICE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(1);

                        Element Product_A = (Element) TITLE_SPAN.getAllElements(HTMLElementName.A).get(0);
                        Segment TITLE = Product_A.getFirstElement().getContent();

                        titleBuilder[j] = new StringBuilder(TITLE.toString());

                        Element DEL;
                        Element EM = (Element) PRICE_SPAN.getAllElements(HTMLElementName.EM).get(0);

                        List<Tag> lst = PRICE_SPAN.getAllTags();


                        for (Tag tmp : lst) {
                            if (tmp.toString().equals("<del>")) {

                                DEL = (Element) PRICE_SPAN.getAllElements(HTMLElementName.DEL).get(0);
                                delBuilder[j] = new StringBuilder(DEL.getContent().toString());
                            }
                            if (tmp.toString().equals("<em>")) {

                                emBuilder[j] = new StringBuilder( EM.getContent().toString());
                                android.util.Log.v("EM", "IS");
                            }
                        }
                        Log.v(TAG, Items.get(j).toString());
                      //  rating++;

                    }

                   // getSupportFragmentManager().beginTransaction().replace(R.id.show_product_container, AllOfProuducts).commit();
                }
                else if (rule.getAttributeValue("class").equals("NO_RESULT")) {
                    android.util.Log.v("NO_RESULT", "결과가없음!");
                }

            }catch (MalformedURLException e){
                Log.println(Log.VERBOSE,TAG,"URL Malformed");
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }





            return 0;
      }

        @Override
        protected void onProgressUpdate(String... progress){

        }

        @Override
        protected void onPostExecute(Integer result){

            for(int i =0; i<ImageURLBuiler.length;i++){
                Items.add(new Item(titleBuilder.toString(),delBuilder.toString(), emBuilder.toString(),ImageURLBuiler.toString()));
            }

            handler.sendEmptyMessage(0);
            mDialog.dismiss();



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