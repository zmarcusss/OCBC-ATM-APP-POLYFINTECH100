package com.example.a17019181.c300_ocbcmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.a17019181.c300_ocbcmobile.NotificationMethods.StartWorker;

public class BootBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Log.d("BootBroadcastReceiver", ":onReceive()");

            StartWorker();
        }
    }
}
