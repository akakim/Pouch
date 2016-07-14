package com.pouch.data;

import java.net.URL;

/**
 * Created by Ala on 2016-07-11.
 */
public class categoryInform {
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public URL getLocation() {
        return Location;
    }

    public void setLocation(URL location) {
        Location = location;
    }

    String Title;
    URL Location;

    public categoryInform(String title, URL location) {
        Title = title;
        Location = location;
    }
}
