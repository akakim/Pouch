package com.pouch.ui;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;


import com.pouch.R;
import com.pouch.data.Item;
import com.pouch.data.categoryInform;
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

public class ProductsActivity extends AppCompatActivity implements ProductFragment.OnFragmentInteractionListener
        ,ProductFavoriteFragment.OnFragmentInteractionListener
        ,DrawerLayout.DrawerListener
        {
    private static final String TAG = "PouchActivity";
    BrandURLs brand = new BrandURLs();// TODO: 브랜드 파싱을 더할 수있다면 여길 이용하자.
    private static String URL_PRIMARY = "http://m.etonymoly.com";

    ConnectivityManager cManager;
    NetworkInfo networkInfo;

    ProductFragment AllOfProuducts;

    InnerHandler handler;

    Intent getbrandName;
    String title;

    // 작은 카테고리만 list로 만든다.
    URL [] URLList = new URL [80];
    String [] TitleList =new String [80];


    ListView CategoryList;
    CategroyAdapter adapter;
    ArrayList <categoryInform> CategoryData;
    ArrayList<Item> Items;
    FragmentManager fm;
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

        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.show_product_container, AllOfProuducts).commit();

        CategoryList = (ListView)findViewById(R.id.category_list);
        CategoryData = new ArrayList<>();


        new TonimoriInitThread(this).execute();
        new TonymolyCate(this).execute();
    }

    private boolean isInternetConnected(){
        cManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = cManager.getActiveNetworkInfo();


        return !networkInfo.isConnected();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        Log.v(TAG,"onDrawerSlide");
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        Log.v(TAG,"onDrawerOpened");
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        Log.v(TAG,"onDrawerClosed");
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        Log.v(TAG,"onDrawerStaeChanged");
    }


    public void setListViewHeightBasedOnItems(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();

        if(listAdapter == null) {
            Log.e (TAG,"listview's adapter is null");
            return;
        }

        int numberOfItems = listAdapter.getCount();
        Log.v(TAG,"numberOfItems "+ numberOfItems);

        int totalItemsHeight= 0;
        for(int itemPos= 0; itemPos< numberOfItems;itemPos++){
            View viewItem = listAdapter.getView(itemPos,null,listView);
            viewItem.measure(0,0);
            totalItemsHeight += viewItem.getMeasuredHeight();
        }

        int totalDividerHeight= listView.getDividerHeight() * (numberOfItems -1);

        // set list height
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight +totalDividerHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private class InnerHandler extends Handler {
        InnerHandler(){}

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int message = msg.what;
            switch(message){
                case 0:
                    AllOfProuducts.setItmes(Items);
                    AllOfProuducts.Invalidate();
                break;
                case 1:
                    AllOfProuducts.clean();
                    AllOfProuducts.setItmes(Items);
                    AllOfProuducts.Invalidate();
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
        StringBuilder ProductURL[];

        final int NumberOfItmes=50;
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
            ProductURL = new StringBuilder[NumberOfItmes];
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

                    for (int j = 0; j < count; j++) {
                        Element Target_LI = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(j);


                        Element Target_DIV = (Element) Target_LI.getAllElements(HTMLElementName.DIV).get(0);
                        Element Target_SPAN = (Element) Target_DIV.getAllElements(HTMLElementName.SPAN).get(0);

                        Element Target_PRODUCT_DIV = (Element) Target_DIV.getAllElements(HTMLElementName.DIV).get(1);
                        Element Target_A = (Element) Target_SPAN.getAllElements(HTMLElementName.A).get(0);

                        Element IMGURL = (Element) Target_A.getAllElements(HTMLElementName.IMG).get(0);

                        // Log.v("IMGURL",IMGURL.toString());
                         ImageURL[j] = new StringBuilder(IMGURL.getAttributeValue("src"));

                        // Log.v("imageURL",ImageURL);
                        Element TITLE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        //             Log.v("TITLE_SPAN",TITLE_SPAN.toString());
                        Element PRICE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(1);

                        Element Product_A = (Element) TITLE_SPAN.getAllElements(HTMLElementName.A).get(0);
                        ProductURL[j] = new StringBuilder(Product_A.getAttributeValue("href"));
                        Segment TITLE = Product_A.getFirstElement().getContent();
                         title = TITLE.toString();

                        Element DEL;
                        Element EM = (Element) PRICE_SPAN.getAllElements(HTMLElementName.EM).get(0);

                        List<Tag> lst = PRICE_SPAN.getAllTags();
                        for (Tag tmp : lst) {

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

                        Item tmpItem = new Item(product_title[j].toString(),del[j].toString(),
                                em[j].toString(),URL_PRIMARY+ImageURL[j].toString()
                                ,URL_PRIMARY+"/html/"+ProductURL[j].toString());
                        Items.add(tmpItem);

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
            mDialog.onDetachedFromWindow();
            mDialog = null;
        }
    }

    /*category를 불러오는 task */
    private class TonymolyCate extends AsyncTask<Integer,Integer,Integer>{
        private InputStream input;
        private Source source;
        private URL targetURL;
        int maxSize;
        private Context context;
        TonymolyCate(Context context){
            this.context = context;

        }
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

                for (int j = 0; j<Target_UL.getAllElements(HTMLElementName.LI).size();j++){
                    Element TargetLI = (Element)Target_UL.getAllElements(HTMLElementName.LI).get(j);
                    Element Target_a = (Element)TargetLI.getAllElements(HTMLElementName.A).get(0);
                    if (Target_a.getAttributeValue("href").equals("#")){
                        // category의 사이즈를 정하기위함.
                        maxSize = j;
                        break;
                    }
                    URLList[j]= new URL(URL_PRIMARY+Target_a.getAttributeValue("href"));

                    Segment s = TargetLI.getAllElements(HTMLElementName.A).get(0).getContent();

                    TitleList[j] = s.toString();

                    if(URLList[j]==null){
                        break;
                    }
                    CategoryData.add(new categoryInform(TitleList[j], URLList[j]));
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

            adapter = new CategroyAdapter(context,CategoryData);
            CategoryList.setAdapter(adapter);
            //setListViewHeightBasedOnItems(CategoryList);
            super.onPostExecute(integer);
        }
    }

    /*category안의 Item List를불러오는 task */
    private class getCategoryItemTask extends AsyncTask<String,Integer,Integer>{
        private InputStream input;
        private Source source;
        private URL targetURL;

        StringBuilder ImageURL[];
        StringBuilder product_title[];
        StringBuilder del[];
        StringBuilder em[];
        StringBuilder ProductURL[];
        final int NumberOfItmes=50;

        @Override
        protected void onPreExecute(){
            Items.clear();
            ImageURL = new StringBuilder[NumberOfItmes];
            product_title = new StringBuilder[NumberOfItmes];
            del = new StringBuilder[NumberOfItmes];
            em =new StringBuilder[NumberOfItmes];
            ProductURL = new StringBuilder[NumberOfItmes];
        }

        @Override
        protected Integer doInBackground(String... params) {
            int targetSection = 0;
            int start = 0;
            try {
                targetURL = new URL(params[0]);
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

                /*li tag 갯수새기 */


                if (rule.getAttributeValue("class") == null){
                    int count = 0;
                    List<StartTag> startTagList = Target_UL.getAllStartTags(HTMLElementName.LI);
                    for (StartTag tmp : startTagList) {
                        // Log.v("StartTag", tmp.toString());
                        count++;
                    }

                    for (int j = 0; j < count; j++) {
                        Element Target_LI = (Element) Target_UL.getAllElements(HTMLElementName.LI).get(j);
                        Element Target_DIV = (Element) Target_LI.getAllElements(HTMLElementName.DIV).get(0);
                        Element Target_SPAN = (Element) Target_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        Element Target_PRODUCT_DIV = (Element) Target_DIV.getAllElements(HTMLElementName.DIV).get(1);

                        Element Target_A = (Element) Target_SPAN.getAllElements(HTMLElementName.A).get(0);
                        Element IMGURL = (Element) Target_A.getAllElements(HTMLElementName.IMG).get(0);
                                ImageURL[j] = new StringBuilder(IMGURL.getAttributeValue("src").toString());

//                        String ImageURL = IMGURL.getAttributeValue("src");
                        Element TITLE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(0);
                        Element PRICE_SPAN = (Element) Target_PRODUCT_DIV.getAllElements(HTMLElementName.SPAN).get(1);

                        Element Product_A = (Element) TITLE_SPAN.getAllElements(HTMLElementName.A).get(0);
                        ProductURL[j] = new StringBuilder(Product_A.getAttributeValue("href"));
                        Segment TITLE = Product_A.getFirstElement().getContent();
                        product_title[j] = new StringBuilder(TITLE.toString());

                        Element DEL;
                        Element EM = (Element) PRICE_SPAN.getAllElements(HTMLElementName.EM).get(0);

                        List<Tag> lst = PRICE_SPAN.getAllTags();
                        for (Tag tmp : lst) {
                            android.util.Log.v("for", tmp.toString());
                            if (tmp.toString().equals("<del>")) {
                                DEL = (Element) PRICE_SPAN.getAllElements(HTMLElementName.DEL).get(0);
                                del[j] = new StringBuilder(DEL.getContent().toString());
                            }else {
                                del[j] =  new StringBuilder("");
                            }
                            if (tmp.toString().equals("<em>")) {

                                em[j] = new StringBuilder(EM.getContent().toString());
                                android.util.Log.v("EM", "IS");
                            }
                        }

                        Item tmp = new Item(product_title[j].toString(),del[j].toString()
                                ,em[j].toString(),URL_PRIMARY+ImageURL[j].toString()
                                ,URL_PRIMARY+"/html/"+ProductURL[j].toString());
                        Items.add(tmp);
                    }
                }
                else if (rule.getAttributeValue("class").equals("NO_RESULT")) {
                    android.util.Log.v("NO_RESULT", "결과가없음!");

                }



            }
            catch(MalformedURLException e){
                android.util.Log.v(TAG, "MalformedURL");
                return 2;
            }
            catch(Exception e){
                e.printStackTrace();
                return 1;
            }


            return 0;
        }

        @Override
        protected void onPostExecute(Integer result){
            if (result == 0){
                AllOfProuducts.setItmes(Items);
                AllOfProuducts.Invalidate();
                AllOfProuducts.setImage();
            }else {
                Log.v("ERROR CODE : ",String.valueOf(result));
            }

        }

    }
    // TODO: filter를 적용할 때 Thread 작성.


    static class ViewHolder{
        Button button;
    }
    private class CategroyAdapter extends BaseAdapter{

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<categoryInform>categoryList;


        public CategroyAdapter(Context context, ArrayList<categoryInform> categoryList){
            this.context = context;
            this.categoryList = categoryList;
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder Item;
            if(convertView == null){
                Item = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.product_category_item,parent,false);

                Item.button =(Button)convertView.findViewById(R.id.item_button);
                Item.button.setText(categoryList.get(position).getTitle());
                Item.button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        AllOfProuducts.clean();
                        new getCategoryItemTask().execute(categoryList.get(position).getLocation().toString());
                    }
                });
                convertView.setTag(Item);
            }

            else{
                Item = (ViewHolder)convertView.getTag();
            }
            return convertView;
        }

        @Override
        public int getViewTypeCount(){
            return getCount();
        }

        @Override
        public int getItemViewType(int position){
            return position;
        }
    }



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