package com.example.a17019181.c300_ocbcmobile;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.Database;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a17019181.c300_ocbcmobile.Model.AtmLocationModel;
import com.example.a17019181.c300_ocbcmobile.Model.Conf;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.a17019181.c300_ocbcmobile.NotificationMethods.StartWorker;


public class NotificationWorker extends Worker {
    private static final String WORK_RESULT = "work_result";
    private static final String TAG = "Background";

    GpsTracker gps;

    String confText;
    String atmText;

    //AtmLocationModel availability
    boolean avail = true;

    //Config details
    String atm;
    int transaction;
    int amount;
    int day;
    String month;
    DatabaseReference read;

    //Initialize Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference atmRead;


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        atmNotif();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();


        autoConfig(uid);

        StartWorker();

        return Result.success();

    }

    private void notification(String task, String text, int id){
        Context context = getApplicationContext();

        // Intent being called when clicking on the notification

        Intent intent = new Intent(context, QR.class); //Change mainactivtiy to configuration page


        intent.setAction(context.getString(R.string.app_name));

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (id == 2){
            intent.putExtra("preconfigure", ""+amount);
            intent.putExtra("Transaction", transaction);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Ignore errors if using latest android
        NotificationChannel channel = new NotificationChannel(task, task, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        // Create notification
        if (id == 1){
            Notification notification = new NotificationCompat.Builder(context, task)
                    .setGroup(context.getString(R.string.app_name))
                    .setContentTitle(task)
                    .setTicker(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build();

            //Send / Replace notifications
            manager.notify(id, notification);
        }
        else if(id == 2){
            Notification notification = new NotificationCompat.Builder(context, task)
                    .setGroup(context.getString(R.string.app_name))
                    .setContentTitle(task)
                    .setTicker(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .build();

            //Send / Replace notifications
            manager.notify(id, notification);
        }

    }

    public boolean readAtm(String atmLocation){
        atmRead = database.getReference("atms");
        final String location = atmLocation;
        atmRead.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    AtmLocationModel post = ds.getValue(AtmLocationModel.class);
                    if (post.getLocation().equalsIgnoreCase(location)){
                        if (post.getStatus() == 0){
                            avail = false;
                            break;
                        }
                        else{
                            avail = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return avail;
    }

    public void autoConfig(String userid){
        read = database.getReference("users/" + userid + "/configuration");

        read.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Conf config = dataSnapshot.child("0").getValue(Conf.class);
                atm = config.getAtm();
                transaction = config.getTransaction();
                amount = config.getAmount();
                day = config.getDay();
                month = config.getMonth();

                int confDeposit = 0;
                int confWithdraw = 0;
                int atmCount = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Conf post = ds.getValue(Conf.class);

                    if (post.getAtm() == atm){
                        atmCount++;
                        if (atmCount >= 2){
                            if (readAtm(post.getAtm()) == false) {
                                atmText = post.getAtm() + " ATM is unavailable";
                            }
                        }
                    }

                    //Check for similar past transactions
                    if (!post.getMonth().equalsIgnoreCase(month)) {
                        if (post.getDay() == day) {
                            if (post.getTransaction() == transaction) {
                                if (post.getTransaction() == 1) {
                                    confWithdraw++;
                                }
                                if (post.getAmount() == amount) {
                                    confDeposit++;
                                    if (confWithdraw == 2) {
                                        confText = "Withdraw $" + amount + "?";
                                        notification("Transaction",confText,2);
                                        break;
                                    }
                                    if (confDeposit == 2){
                                        confText = "Deposit money into bank account?";
                                        notification("Transaction", confText,2);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void atmNotif(){
        gps = new GpsTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            callApi(latitude, longitude, 0.5);

        } else {
            gps.showSettingsAlert();
        }
    }

    public void callApi(double latitude, double longitutde, double radius) {
        String url = "https://api.ocbc.com:8243/atm_locator/1.1?category=1&country=SG&latitude=" + latitude + "&longitude=" + longitutde + "&radius=" + radius;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        String result = "";
                        try {
                            JSONObject jo = new JSONObject(data);
                            JSONArray atmListArray = jo.getJSONArray("atmList");

                            for (int i = 0; i < atmListArray.length(); i++) {

                                if (readAtm(atmListArray.getJSONObject(i).getString("landmark")) == true) {
                                    break;
                                }
                                else{
                                    atmText = atmListArray.getJSONObject(i).getString("landmark") + " ATM is unavailable";
                                    notification("ATM Status", atmText,1);
                                    break;
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
