package com.example.a17019181.c300_ocbcmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.a17019181.c300_ocbcmobile.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {


    private Switch fingerprintSwitch;
    private static final String TAG = SettingsFragment.class.getName();
    private User post;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);
        getActivity().setTitle("Settings");
        return v;
    }

    public void onStart() {
        super.onStart();

        Bundle bundle = this.getArguments();





        fingerprintSwitch = getActivity().findViewById(R.id.fingerprint_switch);




        if (bundle != null) {
            post = (User) bundle.getSerializable("user_key");
            if (!post.getAndroiduid().equals("")){
                fingerprintSwitch.setChecked(true);


            }

        }



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(uid);

        fingerprintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){


                    Executor newExecutor = Executors.newSingleThreadExecutor();

                    FragmentActivity activity = getActivity();


                    final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                Runnable runnable = () -> {
                                    fingerprintSwitch.setChecked(false);
                                };
                                ExecutorService executorService = Executors.newSingleThreadExecutor();
                                executorService.submit(runnable);


                            } else {
                                Log.d(TAG, "An unrecoverable error occurred");
                            }
                        }



                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            final String android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                                    Settings.Secure.ANDROID_ID);


                            post.setAndroiduid(android_id);
                            databaseReference.setValue(post);



                            SharedPreferences mPrefs = getActivity().getSharedPreferences("fingerprint_key", 0);
                            SharedPreferences.Editor editor = mPrefs.edit();

                            editor.putBoolean("hasFingerprint", ((!post.getAndroiduid().equals("")) ? true : false));
                            editor.commit();


                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Log.d(TAG, "Fingerprint not recognised");
                        }



                    });
                    final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Register your finger")
                            .setDescription("Confirm fingerprint to continue")
                            .setNegativeButtonText("Cancel")
                            .build();

                    myBiometricPrompt.authenticate(promptInfo);



                }else{

                    post.setAndroiduid("");
                    databaseReference.setValue(post);

                    SharedPreferences mPrefs = getActivity().getSharedPreferences("fingerprint_key", 0);
                    SharedPreferences.Editor editor = mPrefs.edit();

                    editor.putBoolean("hasFingerprint", ((!post.getAndroiduid().equals("")) ? true : false));
                    editor.commit();

                }


            }
        });




    }
}
