package com.example.a17019181.c300_ocbcmobile;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OcbcAtmLocatorApi {

    final String logTag = "OcbcAtmLocatorApi-";

    public String callApi(Context mContext, double latitude, double longitutde, double radius) {

        String url = "https://api.ocbc.com:8243/atm_locator/1.1?category=1&country=SG&latitude=" + latitude + "&longitude=" + longitutde + "&radius=" + radius;

        final String[] content = {""};

        RequestQueue queue = Volley.newRequestQueue(mContext);

        final StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        String result = "";

                        try {
                            JSONObject jo = new JSONObject(data);
                            JSONArray atmListArray = jo.getJSONArray("atmList");

                            for (int i = 0; i < atmListArray.length(); i++) {
                                Log.d("test:", atmListArray.getJSONObject(i).getString("category"));
                                //get Address
                                Log.d("test:", atmListArray.getJSONObject(i).getString("address"));
                                //get landmark
                                Log.d("test:", atmListArray.getJSONObject(i).getString("landmark"));
                                result = result + ("(" + i + ")" + atmListArray.getJSONObject(i).getString("category") + "_" + atmListArray.getJSONObject(i).getString("address"));
                            }

                            // Return the result as String //
                            content[0] = result;

                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(logTag, "Error:  " + error.toString());
                    }
                }
        ) {

            //Headers for the API
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer 8fe7870d627f8d56a555879c83bfbc2c");
                params.put("Accept", "application/json");

                return params;
            }
        };
        queue.add(getRequest);

        return content[0];
    }
}
