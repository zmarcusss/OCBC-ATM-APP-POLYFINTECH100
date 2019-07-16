package com.example.a17019181.c300_ocbcmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a17019181.c300_ocbcmobile.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private TextView quickWithdraw;
    public ProgressDialog mProgressDialog;
    private boolean valid;
    private boolean isQuick;
    private String preconfigure;
    private ImageView fingerprintBtn;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static final String TAG = Login.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        emailField = (EditText) findViewById((R.id.input_email));
        passwordField = (EditText) findViewById(R.id.input_password);
        quickWithdraw = (TextView) findViewById(R.id.quick_withdraw);
        fingerprintBtn = (ImageView) findViewById(R.id.btn_fingerprint);
        Button login_btn = (Button) findViewById(R.id.btn_login);

        SharedPreferences mPrefs = getSharedPreferences("preconfigure_id", 0);
        preconfigure = mPrefs.getString("key", "");

        SharedPreferences mPrefs1 = getSharedPreferences("fingerprint_key", 0);
        boolean hasFingerprint = mPrefs1.getBoolean("hasFingerprint", false);


        fingerprintBtn.setVisibility(hasFingerprint ? View.VISIBLE : View.INVISIBLE);


        if (!preconfigure.equals("")) {
            quickWithdraw.setVisibility(View.VISIBLE);
            quickWithdraw.setText("$" + preconfigure);


            quickWithdraw.setOnClickListener((new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isQuick = true;
                            fingerPrint();

                        }
                    })

            );

        } else {
            quickWithdraw.setVisibility(View.INVISIBLE);

        }


        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");


        login_btn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailField.getText().toString(), passwordField.getText().toString());
            }
        }));


    }


    @Override
    protected void onResume() {
        super.onResume();


        fingerprintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPrint();
            }
        });


    }

    public void onStart() {
        super.onStart();
    }

    private boolean validateForm() {
        valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.redDialog);
            mProgressDialog.setMessage("Authenticating...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void signIn(String email, String password) {
        Log.d("EmailPassword", "signIn:" + email);
        if (!validateForm()) {
            return;
        }


        mAuthentication(email, password);

    }

    private void mAuthentication(String email, String password) {

        if (!((Activity) this).isFinishing()) {
            //show dialog
            showProgressDialog();

        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("EmailPassword", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (!isQuick) {

                                startActivity(new Intent(Login.this, NavigationBar.class));
                            } else {

                                startActivity(new Intent(Login.this, QR.class).putExtra("preconfigure", preconfigure));
                            }
                            finish();
                            Toast.makeText(Login.this, "Successfully Logged in",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("EmailPassword", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                        hideProgressDialog();
                    }
                });
    }


    private void fingerPrint() {


        Executor newExecutor = Executors.newSingleThreadExecutor();

        FragmentActivity activity = this;

        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    isQuick = false;
                } else {
                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }


            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                final String android_id = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.d(TAG, "Fingerprint recognised successfully " + android_id);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            User post = ds.getValue(User.class);

                            if (post.getAndroiduid().equals(android_id)) {


                                mAuthentication(post.getEmail(), post.getPassword());


                                break;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "Fingerprint not recognised");
            }


        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign in")
                .setDescription("Confirm fingerprint to continue")
                .setNegativeButtonText("Cancel")
                .build();

        myBiometricPrompt.authenticate(promptInfo);

    }


}