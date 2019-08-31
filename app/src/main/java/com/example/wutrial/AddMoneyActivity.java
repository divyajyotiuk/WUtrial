package com.example.wutrial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.wutrial.Model.BankServer;
import com.example.wutrial.Model.LogOutTimerUtil;
import com.example.wutrial.Model.TxnList;
import com.example.wutrial.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMoneyActivity extends Activity implements LogOutTimerUtil.LogOutListener {

    private FirebaseAuth auth;
    private FirebaseDatabase secondaryDatabase;
    private DatabaseReference secondaryRef;
    private DatabaseReference accountRef, txnlistRef;
    private String phone,key,currency;
    private Double amount,balance,walletBalance;
    private EditText addMoney;
    private Button addMoneyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_money);

        addMoney = (EditText)findViewById(R.id.add_money_edit);
        addMoneyBtn = (Button)findViewById(R.id.requestButton);

        //bankserver database

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:875508770867:web:0dea2fbe0bdb808c") // Required for Analytics.
                .setApiKey("AIzaSyCf1vh20ZxwAj_q-xewYVZH3Yl8w3B7xVc") // Required for Auth.
                .setDatabaseUrl("https://bankserver-dafa0.firebaseio.com") // Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this /* Context */, options, "secondary");

        // Retrieve my other app.
        FirebaseApp app = FirebaseApp.getInstance("secondary");
        // Get the database for the other app.
        secondaryDatabase = FirebaseDatabase.getInstance(app);

        secondaryRef = secondaryDatabase.getReference("accounts");

        auth = FirebaseAuth.getInstance();

        accountRef = FirebaseDatabase.getInstance().getReference("WUAccount").child(auth.getUid());
        txnlistRef = FirebaseDatabase.getInstance().getReference("TxnList");



        accountRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot data) {

                phone = data.child("phoneNo").getValue(String.class);
                walletBalance =  data.child("walletBalance").getValue(Double.class);
                currency = data.child("currency").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ValueEventListener bankListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get Post object and use the values to update the UI
                for (DataSnapshot bankSnapshot: dataSnapshot.getChildren()) {

                    BankServer bankServer = (BankServer) bankSnapshot.getValue(BankServer.class);
                    //retreive child data from database

                    if(bankServer.getPhoneNo().equals(phone)){
                       key = bankSnapshot.getKey();
                       balance = bankServer.getBalance();

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message


            }
        };
        secondaryRef.addValueEventListener(bankListener);

        addMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(addMoney.getText().toString().length()>0){

                    startActivity(new Intent(AddMoneyActivity.this,PaymentPinActivity.class));

                    amount = Double.parseDouble(addMoney.getText().toString());
                    writeTxn();
                    secondaryRef.child(key).child("balance").setValue(balance-amount);
                    updateWalletBalance(amount);
                    finish();

                }else
                {
                    Toast.makeText(getApplicationContext(),"Enter Amount",Toast.LENGTH_SHORT).show();
                }

            }
        });

     //add entry in txnList


    }

    public void updateWalletBalance(Double amount){

        accountRef.child("walletBalance").setValue(walletBalance+amount);
    }

    public void writeTxn(){

        String txnKey = txnlistRef.push().getKey();
        TxnList tlist = new TxnList();
        tlist.setAmount(amount);
        tlist.setCurrency(currency);
        tlist.setFrom("From your bank");
        tlist.setTo(auth.getUid());
        tlist.setTxnID(txnKey);
        tlist.setTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        tlist.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Map<String,Object> txn = tlist.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/"+txnKey, txn);

        txnlistRef.updateChildren(childUpdates);


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

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        // write your stuff here
        Intent myIntent = new Intent(AddMoneyActivity.this, PasscodeActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////

}
