package com.example.a17019181.c300_ocbcmobile;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//Done By: Marcus Chen ZiRui (17019181)

public class AtmLocations extends FragmentActivity implements OnMapReadyCallback {
    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private  SupportMapFragment mapFragment;

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atm_locations_activitiy);



        mTextViewResult = findViewById(R.id.text_view_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        Button buttonParse = findViewById(R.id.button_parse);

        mQueue = Volley.newRequestQueue(this);

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWithSomeHttpHeaders();

            }
        });

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, permissions, 1);




        }else{
            if(gps_enabled ) {
                initMap();
            }else{
                Log.d("ASJDOIJD","hi");
            }

        }


    }

    private void initMap(){
        getDeviceLocation();
       mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void getDeviceLocation(){


        try {

            final Task location = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        try {
                            LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f));
                            map.addMarker(new MarkerOptions().position(location));
                        }catch(NullPointerException e){

                        }



                    } else {
                        Toast.makeText(AtmLocations.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch(SecurityException e){

        }
    }

    private void requestWithSomeHttpHeaders() {
        mQueue= Volley.newRequestQueue(this);
        String url = "https://api.ocbc.com:8243/atm_locator/1.1";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        mTextViewResult.setText(response);

                        try {
                            JSONObject responseJsonObject = new JSONObject(response);
                            JSONArray responseJsonObjectArray = responseJsonObject.getJSONArray("atmList");
                            for (int i= 0; i<responseJsonObjectArray.length(); i++){


                                JSONObject atm = responseJsonObjectArray.getJSONObject(i);

                                String latitude = atm.getString("latitude");
                                String longitude = atm.getString("longitude");

                                double lat = Double.parseDouble(latitude);
                                double lng = Double.parseDouble(longitude);



                                LatLng location = new LatLng(lat,lng);
//                                map.addMarker(new MarkerOptions().position(location).title("ATM"));



                                BitmapDrawable bitmapIcon=(BitmapDrawable)getResources().getDrawable(R.drawable.atm);
                                Bitmap b = bitmapIcon.getBitmap();
                                Bitmap customMarker = Bitmap.createScaledBitmap(b, 100, 100, false);


                                MarkerOptions marker = new MarkerOptions()
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromBitmap(customMarker))
                                        .title(atm.getString("address"))
                                        ;

                                map.addMarker(marker);
                            }

                        } catch(JSONException e){
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer e73cc83b254a92368c20b3e0c98bb436");

                return params;
            }
        };
        mQueue.add(getRequest);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


    }





}

