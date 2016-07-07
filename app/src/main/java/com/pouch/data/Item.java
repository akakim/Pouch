package com.pouch.data;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by USER on 2016-07-07.
 */
public class Item {
    String Title;
    String PrePrice;
    String Price;
    URL imageURL;
    public Item(String title,String prePrice,String price,String url){
        this.Title = title;
        this.PrePrice = prePrice;
        this.Price = price;
        try {
            this.imageURL = new URL(url);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v("Title : ", title);
        Log.v("PrePrice : ", PrePrice);
        Log.v("Price : ", Price);
        Log.v("URL : ", url);
    }
}
