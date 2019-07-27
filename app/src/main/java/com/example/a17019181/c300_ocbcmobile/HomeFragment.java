package com.example.a17019181.c300_ocbcmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a17019181.c300_ocbcmobile.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

//Done By: Marcus Chen ZiRui (17019181)

public class HomeFragment extends Fragment {

    View myView;
    private ImageView atmLocator;
    private ImageView qrScanner;
    private ImageView authentication;
    private TextView username;
    private TextView balance;
    private ImageView nfc;

    private User post;
    private Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_fragment, container, false);
        getActivity().setTitle("Home");


        return myView;
    }


    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();

        username = getActivity().findViewById(R.id.user_name);
        balance = getActivity().findViewById(R.id.balance);

        bundle = this.getArguments();


        if (bundle != null) {
            post = (User) bundle.getSerializable("user_key");
            username.setText(post.getUsername());
            balance.setText(String.format("$%.2f", post.getBalance()));
        }


        atmLocator = getActivity().findViewById(R.id.atm_locator);

        atmLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AtmLocations.class));
            }
        });

        qrScanner = getActivity().findViewById(R.id.qr_scanner);

        qrScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), AtmFunctions.class);

                intent.putExtra("balance", post.getBalance() + "");

                startActivity(intent);
            }
        });

        authentication = getActivity().findViewById(R.id.authentication);

        authentication.setOnClickListener((View v) -> startActivity(new Intent(getActivity(), QR.class)));

        nfc = getActivity().findViewById(R.id.nfc_authentication);

        nfc.setOnClickListener((View v) -> startActivity(new Intent(getActivity(), NFC.class)));

    }
}
