package com.example.a17019181.c300_ocbcmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AtmLocations extends FragmentActivity implements OnMapReadyCallback {
    private TextView mTextViewResult;
    private RequestQueue mQueue;


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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }




    public void requestWithSomeHttpHeaders() {
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


                                LatLng marker = new LatLng(lat,lng);
                                map.addMarker(new MarkerOptions().position(marker).title("ATM"));
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