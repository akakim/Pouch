package com.pouch.util;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class LazyLoadImage extends AsyncTask<String, Void, Bitmap> {

    ImageView mDestination;

    //Set up cache size and cache
    private static int mCacheSize = 4 * 1024 * 1024; // 4 mb
    private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(mCacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }
    };


    public LazyLoadImage(ImageView destination, String urlString) {
        mDestination = destination;

        if (mCache.get(urlString) != null) {
            mDestination.setImageBitmap(mCache.get(urlString));
        }else {
            this.execute(urlString);
        }
    }

    public LazyLoadImage(ImageView destination, String urlString, Bitmap placeHolder) {
        mDestination = destination;

        if (mCache.get(urlString) != null) {
            mDestination.setImageBitmap(mCache.get(urlString));
        }else {
            setPlaceHolder(urlString, placeHolder);
            this.execute(urlString);
        }
    }

    public LazyLoadImage() {
    }

    private void setPlaceHolder(String urlString, Bitmap placeholder) {
        mDestination.setImageBitmap(placeholder);
    }

    public void clearCache() {
        mCache.evictAll();
    }

    @Override
    protected Bitmap doInBackground(String... arg0) {

        //If the URI that is passed in arg0[0] is already in mCache then I return it without downloading it again
        if (mCache.get(arg0[0]) != null) {
            return mCache.get(arg0[0]);
        }else {

            Bitmap lazyImage = null;
            URL myFileUrl = null;

            try {
                myFileUrl= new URL(arg0[0]);
                HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                lazyImage = BitmapFactory.decodeStream(is);

                //Store the image in mCache for quick assess from anywhere in app
                synchronized (mCache) {
                    if (mCache.get(arg0[0]) == null) {
                        mCache.put(arg0[0], lazyImage);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return lazyImage;
        }
    }

    @Override
    protected void onCancelled(Bitmap result) {

    }

    @Override
    protected void onPostExecute(Bitmap result) {

    /*
     * The returned image to the ImageView that was passed in on create
     *  (either from mCache or when downloaded the first time)
     */
        mDestination.setImageBitmap(result);

        super.onPostExecute(result);
    }

}