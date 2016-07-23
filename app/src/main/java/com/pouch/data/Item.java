package com.pouch.data;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by USER on 2016-07-07.
 */
public class Item implements Parcelable{
    String Title;
    String PrePrice;
    String Price;
    URL imageURL;
    URL ProductURL;

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public URL getProductURL() {
        return ProductURL;
    }

    public void setProductURL(URL productURL) {
        ProductURL = productURL;
    }

    public Item(Item item){
        this.Title = item.getTitle();
        this.PrePrice = item.getPrePrice();
        this.Price = item.getPrice();
        this.imageURL = item.getImageURL();
        this.ProductURL = item.getProductURL();
    }
    public Item(String title,String prePrice,String price,String imageURL,String ProductURL){
        this.Title = title;
        if (prePrice == null){
            prePrice = "null";
        }else {
            this.PrePrice = prePrice;
        }
        this.Price = price;
        try {
            this.imageURL = new URL(imageURL);
            this.ProductURL=new URL(ProductURL);
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }

    }

    public Item(Parcel src){
        this.Title = src.readString();
        this.PrePrice = src.readString();
        this.Price = src.readString();
        String ImageURL = src.readString();
        String ProductURL = src.readString();
        try {
            this.imageURL = new URL(ImageURL);
            this.ProductURL = new URL(ProductURL);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(Title);
        dest.writeString(PrePrice);
        dest.writeString(Price);
        dest.writeString(imageURL.toString());
        dest.writeString(ProductURL.toString());
    }

}
