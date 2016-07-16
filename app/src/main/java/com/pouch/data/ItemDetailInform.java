package com.pouch.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by USER on 2016-07-16.
 */
public class ItemDetailInform extends Item implements Parcelable {
    ArrayList<String> Head;
    ArrayList<String> Tail;
    public static final Creator<ItemDetailInform> CREATOR = new Creator<ItemDetailInform>() {
        @Override
        public ItemDetailInform createFromParcel(Parcel in) {
            return new ItemDetailInform(in);
        }

        @Override
        public ItemDetailInform[] newArray(int size) {
            return new ItemDetailInform[size];
        }
    };

    public ItemDetailInform(Parcel src) {
        super(src);

        Head = new ArrayList<String>();
        Tail = new ArrayList<String>();
    }

    public ItemDetailInform(String title, String prePrice, String price, String imageURL, String ProductURL) {
        super(title, prePrice, price, imageURL, ProductURL);
        Head = new ArrayList<String>();
        Tail = new ArrayList<String>();
    }


    public ItemDetailInform(String title, String prePrice, String price, String imageURL, String ProductURL,ArrayList<String> head,ArrayList<String> tail) {
        super(title, prePrice, price, imageURL, ProductURL);
        Head = head;
        Tail = tail;
    }

    public ArrayList<String> getHead() {
        return Head;
    }

    public void setHead(ArrayList<String> head) {
        Head = head;
    }

    public ArrayList<String> getTail() {
        return Tail;
    }

    public void setTail(ArrayList<String> tail) {
        Tail = tail;
    }
}
