package com.pouch.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pouch.R;
import com.pouch.data.Item;
import com.pouch.ui.ProductDetailActivity;
import com.pouch.util.ImageCache;
import com.pouch.util.ImageFetcher;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String KEY_ITEM_DATA = "item_data";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<Item> Items;

    private int mRowSelected = 0;
    private GridLayout GridItems;
    private ScrollView ScrollItem;

    ImageView ItemView[];
    TextView ItemTitle[];
    TextView ItemPreValue[];
    TextView ItemCurValue[];

    private String TAG= getClass().getSimpleName();
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    private ImageFetcher mImageFetcher;


    public ProductFragment() {
        // Required empty public constructor

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Items= new ArrayList<Item>();

        /* something 초기화. */
        mImageThumbSize = getResources().getDisplayMetrics().widthPixels / 3;
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(),mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_product, container, false);
        GridItems = (GridLayout)rootView.findViewById(R.id.product);
        ScrollItem = (ScrollView)rootView.findViewById(R.id.scrollView);


                // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);

    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setItmes(ArrayList<Item> Items){


        for(Item i : Items){
            this.Items.add(i);
        }
    }

    public void Invalidate() {
        if(Items == null){
            Log.e("ProductFragment", "Item is not initialize");
        }

        else if (Items.size() != 0) {
            LinearLayout[] GridLayoutLinearLayout;
            FrameLayout[] GridLayoutFrameLayout;
            RelativeLayout[] GridLayoutRelativeLayout;
            ItemView = new ImageView[Items.size()];
            ItemTitle = new TextView[Items.size()];
            ItemPreValue = new TextView[Items.size()];
            ItemCurValue = new TextView[Items.size()];


            GridLayoutFrameLayout = new FrameLayout[Items.size()];
            GridLayoutLinearLayout = new LinearLayout[Items.size()];
            GridLayoutRelativeLayout = new RelativeLayout[Items.size()];

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels / 2,
                    getResources().getDisplayMetrics().heightPixels / 2);

//            params.setLayoutDirection(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            RelativeLayout.LayoutParams rel_params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,getResources().getDisplayMetrics().heightPixels / 3);
            RelativeLayout.LayoutParams Title_params =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams Price_params =new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels / 6,RelativeLayout.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < Items.size(); i++) {

                GridLayoutLinearLayout[i] = new LinearLayout(ProductFragment.this.getContext());
                GridLayoutLinearLayout[i].setLayoutParams(params);
                GridLayoutLinearLayout[i].setOrientation(LinearLayout.VERTICAL);

                GridLayoutFrameLayout[i] = new FrameLayout(ProductFragment.this.getContext());
                GridLayoutFrameLayout[i].setLayoutParams(params);

                GridLayoutRelativeLayout[i] = new RelativeLayout(ProductFragment.this.getContext());
                GridLayoutRelativeLayout[i].setLayoutParams(rel_params);


                GridLayoutFrameLayout[i].setBackgroundColor(Color.DKGRAY);

                ItemView [i] = new ImageView(this.getContext());
                mImageFetcher.loadImage(Items.get(i).getImageURL(), ItemView[i]);
                CancelableParams.setMargins(20, 20, 20, 20);
                CancelableParams.gravity = Gravity.CENTER;

                ItemView[i].setLayoutParams(CancelableParams);

                ItemView[i].setFocusable(true);
//                ItemView[i].setClickable(true);
                ItemView[i].setOnClickListener(new View.OnClickListener() {
                    int selected = mRowSelected;
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                        Log.v("mRowSelected selected",mRowSelected+" : "+selected);
                        intent.putExtra("selectedItem",selected);
                        intent.putParcelableArrayListExtra(KEY_ITEM_DATA,Items);
                        startActivity(intent);
                    }
                });


                ItemTitle[i] = new TextView(this.getContext());
                ItemTitle[i].setText(Items.get(i).getTitle());
                Title_params.alignWithParent=true;
                ItemTitle[i].setGravity(Gravity.CENTER);
                ItemTitle[i].setTextSize(15.0f);
    //            ItemTitle[i].setLayoutParams(Title_params);

                ItemPreValue[i] = new TextView(this.getContext());
                ItemPreValue[i].setText(Items.get(i).getPrePrice());
                ItemPreValue[i].setPaintFlags(ItemPreValue[i].getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ItemPreValue[i].setTextSize(10.0f);
//                ItemPreValue[i].setLayoutParams(Price_params);

                ItemCurValue[i] = new TextView(this.getContext());
                ItemCurValue[i].setText(Items.get(i).getPrice());
                ItemCurValue[i].setTextSize(10.0f);
  //              ItemCurValue[i].setLayoutParams(Price_params);

                mRowSelected++;

                /* frame layout
                 * └LinearLayout
                 *   └ ImageView
                 *      Title
                 *      Pre Cur
                 */
          //      GridLayoutRelativeLayout[i].addView(ItemTitle[i]);

                GridLayoutLinearLayout[i].addView(ItemView[i],0);
                GridLayoutLinearLayout[i].addView(ItemTitle[i],1);
                GridLayoutLinearLayout[i].addView(ItemPreValue[i],2);
                GridLayoutLinearLayout[i].addView(ItemCurValue[i],3);

                GridLayoutFrameLayout[i].addView(GridLayoutLinearLayout[i]);



            }

            // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
            mRowSelected = 0;

            for (int i = 0; i < Items.size(); i++) {
                GridItems.addView(GridLayoutFrameLayout[i]);
            }
        }
        else {
            Log.e(TAG + "Items size", "0");
        }
    }

    public void clean(){
        this.GridItems.removeAllViews();
        Items.clear();

    }

    public void setImage(){
        for (int i =0;i<Items.size();i++)
        mImageFetcher.loadImage(Items.get(i).getImageURL(), ItemView[i]);
    }
}
