package com.example.a17019181.c300_ocbcmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Done By: Marcus Chen ZiRui (17019181)
public class AtmFunctions extends AppCompatActivity {

    private EditText mWithdrawal;
    private TextView mAmount;
    private Button withdrawBtn;
    private Button tenBtn;
    private Button fiftyBtn;
    private Button hundredBtn;
    private Button resetBtn;
    private int value = 0;


    private String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_functions);

        mWithdrawal = findViewById(R.id.withdrawalAmount);
        mAmount = findViewById(R.id.amountLeft);
        withdrawBtn = findViewById(R.id.withdrawBtn);

        tenBtn = findViewById(R.id.tenDollar);
        fiftyBtn = findViewById(R.id.fiftyDollar);
        hundredBtn = findViewById(R.id.hundredDollar);
        resetBtn = findViewById((R.id.reset));

        tenBtn.setOnClickListener((View v) -> {

            value += 10;
            mWithdrawal.setText("" + value);
        });

        fiftyBtn.setOnClickListener((View v) -> {

            value += 50;
            mWithdrawal.setText("" + value);
        });

        hundredBtn.setOnClickListener((View v) -> {

            value += 100;
            mWithdrawal.setText("" + value);
        });


        resetBtn.setOnClickListener((View v) -> {

            value = 0;
            mWithdrawal.setText("" + value);

        });


        Intent intent = getIntent();
        balance = intent.getStringExtra("balance");

        mAmount.setText("Account Balance: " + balance);


        withdrawBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdraw();
            }
        }));


    }

    private boolean validateAmount() {
        boolean valid = true;

        String field = mWithdrawal.getText().toString();
        if (TextUtils.isEmpty(field)) {
            mWithdrawal.setError("Required.");
            valid = false;
        } else {
            mWithdrawal.setError(null);
        }


        try {
            double amountLeft = Double.parseDouble(balance);

            if (Double.parseDouble(field) > amountLeft) {

                mWithdrawal.setError("You do not have enough balance");
                valid = false;
            } else {

                if (Double.parseDouble(field)%10 != 0) {
                    valid = false;
                    mWithdrawal.setError("Only in denominations of $10");
                }else if(Double.parseDouble(field)==0){
                    valid = false;
                    mWithdrawal.setError("Please enter an amount");
                }
                else {
                    mWithdrawal.setError(null);
                }
            }
        } catch (NumberFormatException e) {
            //not a double
            e.printStackTrace();
            mWithdrawal.setError("Invalid amount");
            valid = false;
        }


        return valid;

    }

    private void withdraw() {
        if (!validateAmount()) {
            return;
        }


        final Intent intent = new Intent(this, QR.class);

        intent.putExtra("preconfigure", mWithdrawal.getText().toString());


        new AlertDialog.Builder(this)
                .setTitle("Withdraw money")
                .setMessage("Do you want to withdraw now?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Set for later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences mPrefs = getSharedPreferences("preconfigure_id", 0);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("key", mWithdrawal.getText().toString());
                        editor.commit();

                        finish();
                    }
                })
                .show();


    }


}
