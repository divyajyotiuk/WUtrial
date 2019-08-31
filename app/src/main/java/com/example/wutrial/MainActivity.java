package com.example.wutrial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

//////////////////////////////////////////////////////////////////////////////////////
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.wutrial.Model.LogOutTimerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements LogOutTimerUtil.LogOutListener {

    private Button sendButton, txnButton, addButton, walletButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Double walletBalance;
    private String currency;

    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("WUAccount").child(auth.getUid());

       listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                walletBalance = dataSnapshot.child("walletBalance").getValue(Double.class);
                currency = dataSnapshot.child("currency").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabase.addListenerForSingleValueEvent(listener);

        sendButton = (Button) findViewById(R.id.sendButton);
        addButton = (Button) findViewById(R.id.addButton);
        txnButton = (Button) findViewById(R.id.txnButton);
        walletButton = (Button) findViewById(R.id.walletButton);

        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
                Intent myIntent = new Intent(MainActivity.this, SendMoneyActivity.class);
                startActivity(myIntent);
            }

        });
        addButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
                Intent myIntent = new Intent(MainActivity.this, AddMoneyActivity.class);
                startActivity(myIntent);
            }

        });
        txnButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
        //        Intent myIntent = new Intent(MainActivity.this, TxnList.class);
        //        startActivity(myIntent);
            }

        });

        walletButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){

                mDatabase.addListenerForSingleValueEvent(listener);
                walletButton.setText("WALLET BALANCE: " + Double.toString(walletBalance)+" " + currency);

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        //balance.clearAnimation();
                        walletButton.setText("WALLET BALANCE");
                    }
                },5000);

            }

        });
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimerUtil.startLogoutTimer(this, this);
       // Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimerUtil.startLogoutTimer(this, this);
       // Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
      //  Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

      //  Log.e(TAG, "onResume()");
    }

    @Override
    public void doLogout() {
        // write your stuff here
        Intent myIntent = new Intent(MainActivity.this, PasscodeActivity.class);
        startActivity(myIntent);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
}

