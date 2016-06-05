package com.pouch.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


import com.pouch.R;
import com.pouch.Logger.*;

/**
 * Created by Ala on 2016-05-30.
 */
public class ImageFetcher {
    private final static String TAG = "ImageFecher";

    private void checkConnection(Context context){
        final ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnectedOrConnecting()){
            Toast.makeText(context, R.string.no_network_connection_toast,Toast.LENGTH_LONG).show();
            Log.e(TAG,"checkConnection - no connection found ");
        }
    }
}
