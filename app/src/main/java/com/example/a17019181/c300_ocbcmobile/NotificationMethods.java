package com.example.a17019181.c300_ocbcmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

//Done by: Alastair


public class NotificationMethods extends AppCompatActivity {

    private static final String TAG = "NotificationMethods";

    //Config details from notification
    int transaction;
    int amount;


    //Retrieve configuration values from notification
    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("Transaction")) {
                transaction = extras.getInt("Transaction");
                Log.d("Main", "Transaction: " + transaction);
                if (transaction == 1){
                    amount = extras.getInt("Amount");
                    Log.d("Main", "Amount: " + amount);
                }

            }
        }


    }


    //Start notification
    static void StartWorker(){
        Log.d("NotificationMethods", ":StartWorker()");

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(workRequest);
    }

    //Put in records for at least 2 months to see autoConf()
    //Input same day, amount and transaction type
//    public void writeDatabase(){
//
//        for (int i = 0; i < 3; i ++){
//            DatabaseReference date = database.getReference("users/ddw5EpXfTjWfExHJVp3KWtIre0x2/configuration/" + i + "/date");
//            DatabaseReference transaction = database.getReference("users/ddw5EpXfTjWfExHJVp3KWtIre0x2/configuration/" + i + "/transaction");
//            DatabaseReference amount = database.getReference("users/ddw5EpXfTjWfExHJVp3KWtIre0x2/configuration/" + i + "/amount");
//            DatabaseReference atm = database.getReference("users/ddw5EpXfTjWfExHJVp3KWtIre0x2/configuration/" + i + "/atm");
//
//
//            if (i == 0){
//                date.setValue("12 June 2019");
//            }
//            else if (i == 1){
//                date.setValue("12 July 2019");
//            }
//            else{
//                date.setValue("12 August 2019");
//            }
//
//            transaction.setValue(1) ;
//            amount.setValue(50);
//            atm.setValue("Woodlands");
//
//        }
//
//
//
//    }

}
