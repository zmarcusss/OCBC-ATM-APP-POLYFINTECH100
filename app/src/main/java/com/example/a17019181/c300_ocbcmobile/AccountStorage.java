package com.example.a17019181.c300_ocbcmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Done by: Alastair

public class AccountStorage {
    private static final String PREF_USERID = "userid";
    private static final String TAG = "AccountStorage";
    private static final Object sAccountLock = new Object();

    //User id to use for authentication
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static String uid = user.getUid();

    private static String userid = uid;
    private static String sAccount = null;


    public static String GetAccount(Context c) {
        synchronized (sAccountLock) {
            if (sAccount == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String account = prefs.getString(PREF_USERID, userid);
                sAccount = account;
            }
            return sAccount;
        }
    }
}
