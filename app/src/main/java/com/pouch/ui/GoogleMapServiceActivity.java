package com.pouch.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pouch.R;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleMapServiceActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private final String TAG = getClass().getSimpleName();
    private int userIcon,shopIcon;

    //the map
    private GoogleMap map;

    //location manager
    private LocationManager locationManager;
    Location UserLocation = null;
    private Marker userMarker;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;//most returned from google
    private MarkerOptions[] places;

    String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_service);
        // 위치 관리자 설정.
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //???
        bestProvider = locationManager.getBestProvider(new Criteria(), true);

        userIcon = R.drawable.yellow_point;
        shopIcon = R.drawable.purple_point;


        try{
            UserLocation = locationManager.getLastKnownLocation(bestProvider);
        }catch(SecurityException e){
            Log.e(TAG, "권한설정");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        bestProvider = locationManager.getBestProvider(new Criteria(),true);

        Location lastLoc = null;
        try {
            // 네트워크에서
            lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (SecurityException e){
            Log.w("TAG", "권한 설정 참조");
        }
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();

        LatLng lastPosition = new LatLng(lat,lng);

        if(userMarker!=null)userMarker.remove();

        userMarker = map.addMarker(new MarkerOptions()
                .position(lastPosition)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition,15),3000,null);

        Intent i = getIntent();
        String brandName= i.getStringExtra("keyword");

        //TODO: key값 변경.
        String placesSearchStr =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+lat+","+lng+
                        "&radius=5000&sensor=false" +
                        "&name=" + brandName+//|bar|store|museum|art_gallery
                        "&key=AIzaSyDaJGsw467dw4M4izwXI22XTBM7ysBjQAQ";//ADD KEY

        new GetPlaces().execute(placesSearchStr);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 마커를더한다.
        LatLng UserPosition = new LatLng(UserLocation.getLatitude(),UserLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(UserPosition ).title("나의 위치"));
        map.moveCamera(CameraUpdateFactory.newLatLng(UserPosition));

        try {
            map.setMyLocationEnabled(true);   //자신의 현재위치 표시기능을 작동시켜라
        }catch(SecurityException e){
            Log.w(TAG, "권한 에러");
            e.printStackTrace();
        }

        placeMarkers = new Marker[MAX_PLACES];

        try {
            locationManager.requestLocationUpdates(bestProvider, 30000, 100, this);
        }catch (SecurityException e){
            Log.w("TAG", "권한설정 참조");
        }

        update();
    }

    private void update(){
        bestProvider = locationManager.getBestProvider(new Criteria(),true);

        Location lastLoc = null;
        try {
            // 네트워크에서
            lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (SecurityException e){
            Log.w("TAG", "권한 설정 참조");
        }
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();

        LatLng lastPosition = new LatLng(lat,lng);

        if(userMarker!=null)userMarker.remove();

        userMarker = map.addMarker(new MarkerOptions()
                .position(lastPosition)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition,15),3000,null);

        Intent i = getIntent();
        String brandName= i.getStringExtra("keyword");

        //TODO: key값 변경.
        String placesSearchStr =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+lat+","+lng+
                        "&radius=5000&sensor=false" +
                        "&name=" + brandName+//|bar|store|museum|art_gallery
                        "&key=AIzaSyBAFBgO1fXMELTcgZ2DS4DXIHmV87IjeE4";//ADD KEY

        Log.v(TAG+"URL",placesSearchStr);
        new GetPlaces().execute(placesSearchStr);

        try {
            locationManager.requestLocationUpdates(bestProvider, 30000, 100, this);
        }catch (SecurityException e){
            Log.w("TAG", "권한 설정 참조");
        }

    }

    private class GetPlaces extends AsyncTask<String, Void, String> {
        private final String TAG = getClass().getSimpleName();

        @Override
        protected String doInBackground(String... placesURL) {
            StringBuilder placesBuilder = new StringBuilder();
            for(String placeSearchURL : placesURL){
                try{
                    URL url = new URL(placeSearchURL);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                    if(connection != null){
                        connection.setConnectTimeout(10000);
                        connection.setRequestMethod("POST");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        int responseCode = connection.getResponseCode();
                        if(responseCode == HttpURLConnection.HTTP_OK){
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String lineIn;
                            while((lineIn = reader.readLine())!=null){
                                placesBuilder.append(lineIn);
                            }

                        }else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
                            Toast.makeText(getApplicationContext(),"다시 요청해주세요 ",Toast.LENGTH_SHORT);
                            return "HTTP_CLIENT_TIMEOUT";
                        }
                    }
                }catch (MalformedURLException e){
                    Log.e(TAG+" DoInBackground","MalformedURLException");
                    e.printStackTrace();
                }catch(IOException e){
                    Log.e(TAG+" DoInBackground","IOException");
                    e.printStackTrace();
                }
            }

            return placesBuilder.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
                        placeMarkers[pm].remove();
                }
            }

            try{
                JSONObject resultObject = new JSONObject(result);
                JSONArray  placesArray = resultObject.getJSONArray("results");

               /*
                JSONObject status = resultObject.getJSONObject("status");

                Log.v("JSON Status ",status.toString());
                String statusStr = status.toString();
                if (status.equals("ZERO_RESULTS")){
                    return ;
                }
                else if (status.equals("OK")){*/
                    places = new MarkerOptions[placesArray.length()];
                    for (int point = 0; point < placesArray.length(); point++) {
                        boolean missingValue=false;
                        LatLng placeLatLng=null;
                        String placeName="";
                        String vicinity="";

                        try {
                            missingValue = false;
                            JSONObject placeObject = placesArray.getJSONObject(point);

                            JSONObject loc = placeObject.getJSONObject("geometry")
                                    .getJSONObject("location");

                            //read lat lng
                            placeLatLng = new LatLng(Double.valueOf(loc.getString("lat")),
                                            Double.valueOf(loc.getString("lng")));

                            vicinity = placeObject.getString("vicinity");
                            placeName = placeObject.getString("name");
                           } catch (JSONException jse) {
                                Log.e(TAG,"missing value");
                            missingValue = true;
                            jse.printStackTrace();
                            }
                        if(missingValue)
                            places[point] = null;
                        else{
                            places[point] = new MarkerOptions()
                                    .position(placeLatLng)
                                    .title(placeName)
                                    .icon(BitmapDescriptorFactory.fromResource(shopIcon))
                                .snippet(vicinity);


                        }
                    }

//                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if(places!=null && placeMarkers!=null){
                for(int p=0; p<places.length && p<placeMarkers.length; p++){
                    //will be null if a value was missing
                    if(places[p]!=null)
                        placeMarkers[p]=map.addMarker(places[p]);
                }
            }

        }

    }

}
