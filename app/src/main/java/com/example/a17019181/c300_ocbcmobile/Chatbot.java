package com.example.a17019181.c300_ocbcmobile;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Chatbot extends Activity {

    /**
     * REFERENCES:
     * ==================================================================================
     * https://medium.com/@abhi007tyagi/android-chatbot-with-dialogflow-8c0dcc8d8018
     * https://github.com/dialogflow/dialogflow-java-client-v2
     * https://developers.google.com/api-client-library/java/apis/dialogflow/v2
     * https://dialogflow.com/docs/sdks
     * https://dialogflow.com/docs/samples
     * ==================================================================================
     */

    private static final String TAG = Chatbot.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;
    private static final int CUSTOM = 10003;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;
    private TextView tvWelcomeMsg, tvPreset1, tvPreset2, tvPreset3;
    private Button btnPreset1, btnPreset2, btnPreset3;

    // Java V2 //
    private SessionsClient sessionsClient;
    private SessionName session;

    // GpsTracker class //
    GpsTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);



        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = findViewById(R.id.chatLayout);

        ImageView sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);

        tvWelcomeMsg = findViewById(R.id.tvWelcomeMsg);
        tvPreset1 = findViewById(R.id.tvPreset1);
        tvPreset2 = findViewById(R.id.tvPreset2);
        tvPreset3 = findViewById(R.id.tvPreset3);

        btnPreset1 = findViewById(R.id.btnPreset1);
        btnPreset2 = findViewById(R.id.btnPreset2);
        btnPreset3 = findViewById(R.id.btnPreset3);

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        btnPreset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryEditText.setText(tvPreset1.getText().toString());
                sendMessage(sendBtn);
            }
        });

        btnPreset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryEditText.setText(tvPreset2.getText().toString());
                sendMessage(sendBtn);
            }
        });

        btnPreset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryEditText.setText(tvPreset3.getText().toString());
                sendMessage(sendBtn);
            }
        });

        // Java V2
        initV2Chatbot();




    }

    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.java2_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(Chatbot.this, "Enter a query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");

            // Java V2
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
            new RequestJavaV2Task(Chatbot.this, session, sessionsClient, queryInput).execute();
        }
    }

    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            Log.d(TAG, "V2 Bot Reply: " + botReply);


            // Triggers check location //
            if (botReply.equalsIgnoreCase("REQUEST_ATM_LOCATOR_FUN")) {
                getGpsCoordinates();
            } else {

                showTextView(botReply, BOT);
            }

        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("Sorry, an Error had occurred.", BOT);
        }
    }

    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            case CUSTOM:
                // Testing purpose //
                layout = getCustomLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        Linkify.addLinks(tv, Linkify.ALL);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(Chatbot.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(Chatbot.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    FrameLayout getCustomLayout() {
        LayoutInflater inflater = LayoutInflater.from(Chatbot.this);
        return (FrameLayout) inflater.inflate(R.layout.custom_msg_layout, null);
    }

    private void getGpsCoordinates() {
        // Check permission //
        if (ContextCompat.checkSelfPermission(Chatbot.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, prompt user //
            ActivityCompat.requestPermissions(Chatbot.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            showTextView("Permission is needed, please allow Location services.", BOT);

        } else {
            // Permission has already been granted //
            // Get location //
            gps = new GpsTracker(Chatbot.this);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                //showTextView("DEBUG INFO\nLat: " + latitude + "\nLong: " + longitude + "\nGPS DATA", BOT);

                callApi(latitude, longitude, 0.5);

            } else {
                gps.showSettingsAlert();
            }
        }
    }

    public void callApi(double latitude, double longitutde, double radius) {
        String url = "https://api.ocbc.com:8243/atm_locator/1.1?category=1&country=SG&latitude=" + latitude + "&longitude=" + longitutde + "&radius=" + radius;
        RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        String result = "";
                        try {
                            JSONObject jo = new JSONObject(data);
                            JSONArray atmListArray = jo.getJSONArray("atmList");

                            int count = 0;
                            for (int i = 0; i < atmListArray.length(); i++) {

                                result += (i + 1) + ") " + atmListArray.getJSONObject(i).getString("landmark") + " at "
                                        + atmListArray.getJSONObject(i).getString("address")
                                        + " S" + atmListArray.getJSONObject(i).getString("postalCode") + ".\n\n";

                                count++;

                            }

                            Log.d("Chatbot", "There are " + count + " ATMs Found --- LONG/LAT DATA"+longitutde +" "+latitude);
                            // Send Message as BOT //

                            String beautifyString = "Here you go:\nThere are " + count + " ATMs near you.\n\n" + result;


                            if(longitutde == 0.0 && latitude == 0.0){
                                showTextView("Sorry, unable to process this request, please try again.", CUSTOM);
                            }else{
                                if (count == 0) {
                                    showTextView("There are no ATMs near you.", CUSTOM);

                                } else {
                                    showTextView(beautifyString.trim(), CUSTOM);
                                }
                            }



                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Chatbot", "Error:  " + error.toString());
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
    }
}