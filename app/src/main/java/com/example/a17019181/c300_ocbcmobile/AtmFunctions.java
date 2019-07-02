package com.example.a17019181.c300_ocbcmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AtmFunctions extends AppCompatActivity {

    private EditText mWithdrawal;
    private TextView mAmount;
    private Button withdrawBtn;
    private Button otherFunctionBtn;

    private String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_functions);

        mWithdrawal = findViewById(R.id.withdrawalAmount);
        mAmount = findViewById(R.id.amountLeft);
        withdrawBtn = findViewById(R.id.withdrawBtn);
        otherFunctionBtn = findViewById(R.id.otherFunction);

        Intent intent = getIntent();
        balance = intent.getStringExtra("balance");

        mAmount.setText("Account Balance: " + balance);


        withdrawBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdraw();
            }
        }));

        otherFunctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otherFunction();


            }
        });


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
                mWithdrawal.setError(null);

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


        Intent intent = new Intent(this, QR.class);

        intent.putExtra("preconfigure", mWithdrawal.getText().toString());

        startActivity(intent);


    }

    private void otherFunction(){

        Intent intent = new Intent(this, QR.class);
        startActivity(intent);
    }

}
