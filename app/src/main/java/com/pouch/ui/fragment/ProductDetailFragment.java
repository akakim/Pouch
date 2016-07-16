package com.pouch.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.data.Item;
import com.pouch.ui.ProductDetailActivity;
import com.pouch.util.ImageFetcher;
import com.pouch.util.ImageWorker;
import com.pouch.util.Utils;
import com.pouch.widget.DialogBuilder;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by USER on 2016-07-14.
 */
public class ProductDetailFragment extends Fragment implements ImageWorker.OnImageLoadedListener {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String PRODUCT_DATA_EXTRA = "product_data";

    public static final String PRODUCT_DATA_SET = "product_data_set" ;


    private String mImageUrl;
    private String mProductUrl;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private ImageFetcher mImageFetcher;
    private static Item Info;


    ArrayList<String> HeadValues ;
    ArrayList<String> TailValues ;
    private ListView lstView;
    Adapter newAdapter;

    private Button push_SharedPreference;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ProductDetailActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((ProductDetailActivity) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUrl, mImageView, this);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (View.OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
            mImageView.setOnClickListener((View.OnClickListener) getActivity());
        }
        HeadValues = new ArrayList<>();
        TailValues = new ArrayList<>();

    }

    @Override
    public void onImageLoaded(boolean success) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        customListener = (CustomOnClickListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        mProductUrl = getArguments() != null ? getArguments().getString(PRODUCT_DATA_EXTRA) : null;

        Log.v("onCreate", mProductUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.product_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        lstView = (ListView)v.findViewById(R.id.listView);
        this.push_SharedPreference = (Button)v.findViewById(R.id.shared_prereference);

        new getDetailInform().execute(mProductUrl);


        return v;
    }


    public ArrayList<String> getHeadValues() {
        return HeadValues;
    }

    public ArrayList<String> getTailValues() {
        return TailValues;
    }

    public static ProductDetailFragment newInstance(String ImageURL,String InfoURL,Item i){
        final ProductDetailFragment p = new ProductDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, ImageURL);
        args.putString(PRODUCT_DATA_EXTRA, InfoURL);
        Info = i;
        p.setArguments(args);

        return p;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
            HeadValues.clear();
            TailValues.clear();
        }
    }

    /*
    * 클릭하면 일단은 그냥 값들만 저장한다.
    *
    * 나중ㅇ에 내가전에도 이걸 클릭했었는지 안했었는지를 확인하려면 .
    * db에서 값을 불러와야지ㅣ..
    *
    * 각 key는 ImageURL로써 구분이된다 그리고 Set으로 만들어준다.
    * ImageURL은 해당 제품의 제목, 가격 ,상세 정보에 대해서 넣어준다.
    * key를 위한 key값을 생성한다.
    *
    * */

    private class getDetailInform extends AsyncTask<String,Integer,Integer> {
        private URL targetURL;
        private InputStream input;
        private Source source;


        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Integer doInBackground(String... params) {
            try {
                targetURL = new URL(params[0]);

                input = targetURL.openStream();

                source = new Source (new InputStreamReader(input,"euc-kr"));
                source.fullSequentialParse();
                int select = 0;
                List<StartTag> tableTag = source.getAllStartTags(HTMLElementName.TABLE);

                for(int i =0; i<tableTag.size();i++){
                    if(tableTag.get(i).toString().equals("<table class=\"goods-info\">")){
                        select = i;
                    }
                }

                Element Target_table = source.getAllElements(HTMLElementName.TABLE).get(select);

                // tbody는 인식을 아예안한다.. 뭐지..
              //  Element Target_tbody = Target_table.getAllElements(HTMLElementName.TBODY).get(0);

                for (int i =0;i<Target_table.getAllElements(HTMLElementName.TR).size();i++){
                    Element Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(i);
                    Element Target_th = Target_tr.getAllElements(HTMLElementName.TH).get(0);
                    String Head = Target_th.getContent().toString();
                    Head.trim();
                    if(Head.equals("판매가격")){
                        HeadValues.add(Head);
                        Element Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                        Element Target_input = Target_td.getAllElements(HTMLElementName.INPUT).get(0);

                        String Tail = Target_input.getAttributeValue("value");
                        Tail.trim();
                        Tail+="원";
                        TailValues.add(Tail);
                    }else if(Head.equals("피부타입")){
                        HeadValues.add(Head);
                        Element Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                        Element first = Target_td.getFirstElement();
                        Element second = first.getFirstElement();
                        String Tail = second.getContent().toString();
                        Tail.trim();
                        TailValues.add(Tail);
                    }else if(Head.equals("용량/사이즈")) {
                        HeadValues.add(Head);
                        Element Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                        Element first = Target_td.getFirstElement();
                        Element second = first.getFirstElement();
                        String Tail = second.getContent().toString();
                        Tail.trim();
                        TailValues.add(Tail);
                    }
                    else if(Head.equals("피부고민")){
                        HeadValues.add(Head);
                        Element Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                        Element first = Target_td.getFirstElement();
                        Element second = first.getFirstElement();
                        String Tail = second.getContent().toString();
                        Tail.trim();
                        TailValues.add(Tail);
                    }

                }
                /*
                Element Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(0);
                HeadValues.add(Target_tr.getName());
                Element Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);

                Element Target_del = Target_td.getAllElements(HTMLElementName.DEL).get(0);
                Segment result = Target_del.getContent();
                value = new StringBuilder(result.toString());

                Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(1);
                HeadValues.add(Target_tr.getName());
                Element Target_strong = Target_tr.getAllElements(HTMLElementName.STRONG).get(0);
                Segment valid_price = Target_strong.getContent();
                price_valid = new StringBuilder(valid_price.toString());
                price_valid.delete(0,10);

                Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(5);
                Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                Segment type = Target_td.getContent();
                skin_type = new StringBuilder(type.toString());

                Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(6);
                Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                Segment problem = Target_td.getContent();
                skin_problem = new StringBuilder(problem.toString());

                Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(7);
                Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                Segment getSize = Target_td.getContent();
                size = new StringBuilder(getSize.toString());
        Log.v("Size()", "(): "+ Target_table.getAllElements(HTMLElementName.TR).size());
                Log.v("str",Target_table.getAllElements(HTMLElementName.TR).toString());

                Target_tr = Target_table.getAllElements(HTMLElementName.TR).get(8);
                Target_td = Target_tr.getAllElements(HTMLElementName.TD).get(0);
                Segment expDate = Target_td.getContent();
                exp_date = new StringBuilder("");*/


            }catch(MalformedURLException e){
                Log.v("MalformedURL",targetURL.toString());
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Integer result){
            newAdapter = new Adapter(getActivity().getApplicationContext(),HeadValues,TailValues);
            lstView.setAdapter(newAdapter);

//            product_data_set.add()
        }
    }
    private class ViewHolder{
        TextView Head;
        TextView Tail;
    }
    private class Adapter extends BaseAdapter{
        Context context;
        LayoutInflater inflater;
        ArrayList<String> HeadValues ;
        ArrayList<String> TailValues ;

        Adapter(Context context,ArrayList<String> Header,ArrayList<String> Tail){

            this.context = context;
            this.HeadValues = Header;
            this.TailValues = Tail;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return HeadValues.size();
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
            ViewHolder item;
            if(convertView==null){
                item =new ViewHolder();
                convertView = inflater.inflate(R.layout.product_detail_item,parent,false);
                item.Head = (TextView)convertView.findViewById(R.id.head);
                item.Tail = (TextView)convertView.findViewById(R.id.tail);
                item.Head.setText(HeadValues.get(position));
                item.Tail.setText(TailValues.get(position));
                convertView.setTag(item);
            }

            else{
                item = (ViewHolder)convertView.getTag();
            }

            return convertView;
        }
    }

    // Activity 로 데이터를 전달할 커스텀 리스너
    public interface CustomOnClickListener {
        public void onClicked(int id);
    }

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    private CustomOnClickListener customListener;

    // 버튼에 설정한 OnClickListener의 구현, 버튼이 클릭 될 때마다 Activity의 커스텀 리스너를 호출함
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customListener.onClicked(v.getId());
        }
    };

}
