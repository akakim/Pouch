package com.pouch.data;

import java.net.URL;

/**
 * Created by Ala on 2016-05-30.
 */
public class brandInfo {
    private String brandName;
    private URL InstagramURL;
    private int brandImageRes;


    public brandInfo(String brandName, URL instagramURL,int brandImageRes) {
        this.brandName = brandName;
        InstagramURL = instagramURL;
        this.brandImageRes = brandImageRes;

    }

    public URL getInstagramURL() {
        return InstagramURL;
    }

    public int getBrandImageRes() {
        return brandImageRes;
    }


    public String getBrandName() {

        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setInstagramURL(URL instagramURL) {
        InstagramURL = instagramURL;
    }

    public void setBrandImageRes(int brandImageRes) {
        this.brandImageRes = brandImageRes;
    }

}
