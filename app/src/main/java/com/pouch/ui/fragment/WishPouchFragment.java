package com.pouch.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pouch.R;

/**
 * Created by USER on 2016-06-10.
 */


public class WishPouchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_item_mypouch, container, false);

        /**
         * TODO: 사용자의 정보에 대한 확인을 해야한다.
         */

        return rootView;
    }
}
