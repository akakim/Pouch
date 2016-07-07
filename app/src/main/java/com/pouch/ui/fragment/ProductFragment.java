package com.pouch.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pouch.R;
import com.pouch.data.Item;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<Item> Items;

    private int mRowSelected = 0;
    private GridLayout GridItems;
    static final int ImageList [] ={
            R.drawable.cream,
            R.drawable.skin,
            R.drawable.blush,
            R.drawable.eyeshadow,
            R.drawable.skin,
            R.drawable.blush
    };
    private String TAG= getClass().getSimpleName();


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_product, container, false);
        GridItems = (GridLayout)rootView.findViewById(R.id.product);
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
//        this.Items.addAll(Items.get(0));
 //       this.Items = Items;
    }

    public void Invalidate() {
        if(Items == null){
            Log.e("ProductFragment", "Item is not initialize");
        }
        else if (Items.size() != 0) {
            LinearLayout[] testLinearLayout;
            FrameLayout[] testBack;
            ImageView testArr[];
            testArr = new ImageView[Items.size()];



            testBack = new FrameLayout[Items.size()];
            testLinearLayout = new LinearLayout[Items.size()];


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels / 3,
                    getResources().getDisplayMetrics().heightPixels / 3);
            params.weight = 1.0f;
            params.setLayoutDirection(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);


            for (int i = 0; i < Items.size(); i++) {

                testLinearLayout[i] = new LinearLayout(ProductFragment.this.getContext());
                testLinearLayout[i].setLayoutParams(params);

                testBack[i] = new FrameLayout(ProductFragment.this.getContext());
                testBack[i].setLayoutParams(params);
                testBack[i].setBackgroundColor(Color.DKGRAY);

                testArr [i] = new ImageView(this.getContext());
                testArr[i].setImageResource(ImageList[0]);
                CancelableParams.gravity = Gravity.CENTER;
                testArr[i].setLayoutParams(CancelableParams);
                testArr[i].setOnClickListener(new View.OnClickListener() {
                    int selected = mRowSelected;
                    @Override
                    public void onClick(View v) {
                        mRowSelected = selected;
                    }
                });

                testArr[i].setOnLongClickListener(new View.OnLongClickListener(){
                    int selected = mRowSelected;
                    @Override
                    public boolean onLongClick(View v) {
                        mRowSelected = selected;
                        Log.v("mRowSelected "+mRowSelected,"");
                        return false;
                    }

                });
                mRowSelected++;

                testBack[i].addView(testLinearLayout[i]);
                testLinearLayout[i].addView(testArr[i]);


            }

            // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
            mRowSelected = 0;

            for (int i = 0; i < Items.size(); i++) {
                GridItems.addView(testBack[i]);
            }
        }
        else {
            Log.e(TAG+"Items size","0");
        }
    }
}
