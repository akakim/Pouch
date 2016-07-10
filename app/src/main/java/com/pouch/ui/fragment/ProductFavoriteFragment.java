package com.pouch.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pouch.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFavoriteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFavoriteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int mRowSelected = 0;
    private GridLayout GridItems;
    static final int ImageList [] ={
            R.drawable.cream,
            R.drawable.eyeliner,
            R.drawable.fact,
    };

    public ProductFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFavoriteFragment newInstance(String param1, String param2) {
        ProductFavoriteFragment fragment = new ProductFavoriteFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_product_favorite, container, false);

        // Inflate the layout for this fragment
        LinearLayout[] testLinearLayout;
        FrameLayout[]testBack;
        ImageView[] testArr;
        testArr = new ImageView[ImageList.length];

        testBack = new FrameLayout[ImageList.length];
        testLinearLayout = new LinearLayout[ImageList.length];



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels/3,
                getResources().getDisplayMetrics().heightPixels/3);
        params.weight= 1.0f;
        params.setLayoutDirection(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams CancelableParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);


        for(int i =0;i<ImageList.length;i++) {

            testLinearLayout[i] = new LinearLayout(ProductFavoriteFragment.this.getContext());
            testLinearLayout[i].setLayoutParams(params);

            testBack[i] = new FrameLayout(ProductFavoriteFragment.this.getContext());
            testBack[i].setLayoutParams(params);
            testBack[i].setBackgroundColor(Color.DKGRAY);

            testArr[i] = new ImageView(ProductFavoriteFragment.this.getContext());
            testArr[i].setImageResource(ImageList[i]);
            CancelableParams.gravity = Gravity.CENTER;
            testArr[i].setLayoutParams(CancelableParams);
            testArr[i].setOnClickListener(new View.OnClickListener() {
                int selected = mRowSelected;

                @Override
                public void onClick(View v) {
                    mRowSelected = selected;
                }
            });
            mRowSelected++;

            testBack[i].addView(testLinearLayout[i]);
            testLinearLayout[i].addView(testArr[i]);

            // 나중에 선택된 것의 index를 알기 위해 초기화를했다.
            mRowSelected = 0;
        }

        GridItems = (GridLayout)rootView.findViewById(R.id.product_favorite);
        for(int i =0;i<testArr.length;i++){
            GridItems.addView(testBack[i]);
        }

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
