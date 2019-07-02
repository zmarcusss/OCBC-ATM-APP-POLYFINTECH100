package com.example.a17019181.c300_ocbcmobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.example.a17019181.c300_ocbcmobile.Model.Atm;
import com.example.a17019181.c300_ocbcmobile.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.Result;


public class QR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView qrScan;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uid;
    private FirebaseUser user;
    private Atm post;
    private String preconfigure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrScan = new ZXingScannerView(this);


        setContentView(qrScan);

        Intent intent = getIntent();
        preconfigure = intent.getStringExtra("preconfigure");


        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("sessionList");


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            Toast.makeText(this, "NO PERMISION", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);


        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        qrScan.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrScan.setResultHandler(this);
        qrScan.startCamera();


    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
//        final DatabaseReference atmRef = databaseReference.child(rawResult.getText());
//
//        atmRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                post = dataSnapshot.getValue(Atm.class);
//                post.setUserId(uid);
//                atmRef.setValue(post);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        Log.d("iojooijoi", rawResult.getText());
        Gson gson = new Gson();
        final String deserializeString = gson.fromJson(rawResult.getText().toString(), String.class);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                post = dataSnapshot.getValue(Atm.class);
                if (deserializeString.equals(post.getAtmKiosk())) {
                    post.setMobileApp(uid);
                    databaseReference.setValue(post);


                    if (preconfigure != null) {
                        final DatabaseReference userRef = firebaseDatabase.getReference().child("users").child(uid);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User post = dataSnapshot.getValue(User.class);
                                post.setPreconfigure(Double.parseDouble(preconfigure));
                                userRef.setValue(post);




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        qrScan.stopCamera();
        finish();
        startActivity(new Intent(this, NavigationBar.class));





    }
}
