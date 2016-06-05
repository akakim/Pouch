package com.pouch.data;

/**
 * Created by Ala on 2016-05-30.
 */
public class brandInfo {
    private String brandName;
    private String InstagramURL;
    private String bargain;
    private String position;

    public brandInfo(String brandName, String instagramURL, String bargain, String position) {
        this.brandName = brandName;
        InstagramURL = instagramURL;
        this.bargain = bargain;
        this.position = position;
    }

    public String getInstagramURL() {
        return InstagramURL;
    }

    public String getBargain() {
        return bargain;
    }

    public String getPosition() {
        return position;
    }

    public String getBrandName() {

        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setInstagramURL(String instagramURL) {
        InstagramURL = instagramURL;
    }

    public void setBargain(String bargain) {
        this.bargain = bargain;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
