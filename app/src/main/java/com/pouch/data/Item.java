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
        if (prePrice == null){
            prePrice = "null";
        }else {
            this.PrePrice = prePrice;
        }
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

    public String getTitle() {
        return Title;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public String getPrice() {
        return Price;
    }

    public String getPrePrice() {
        return PrePrice;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setPrePrice(String prePrice) {
        PrePrice = prePrice;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }
}
