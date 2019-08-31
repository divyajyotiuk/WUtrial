package com.example.wutrial;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.wutrial.Model.LogOutTimerUtil;
import com.example.wutrial.Model.TxnList;
import com.example.wutrial.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendMoneyActivity extends Activity implements LogOutTimerUtil.LogOutListener {

    private FirebaseFunctions mFunctions;

    private DatabaseReference mDatabaseRef, txnListRef;
    private FirebaseAuth auth;

    private Double senderBalance, receiverBalance, amount;
    private String senderCurrency, receiverCurrency;
    private String sender, receiver,key;

    private EditText id, sendMoney;
    private Button sendBtn;

    private static final int RESULT_PICK_CONTACT = 1;
    private Button select;
    private EditText number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_money);

        mFunctions = FirebaseFunctions.getInstance();

        sendMoney = (EditText) findViewById(R.id.enter_amount);
        id = (EditText) findViewById(R.id.enter_id);
        sendBtn = (Button) findViewById(R.id.sendButton);

        auth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("WUAccount");
        txnListRef = FirebaseDatabase.getInstance().getReference("TxnList");

        mDatabaseRef.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot data) {

                senderBalance = data.child("walletBalance").getValue(Double.class);
                senderCurrency = data.child("currency").getValue(String.class);
                sender = data.child("userID").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //search for userid in db
        ValueEventListener receiverListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {


                    User user = (User) accountSnapshot.getValue(User.class);
                    receiver = id.getText().toString();
                //    Toast.makeText(getApplicationContext(), accountSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                    if (receiver.equals(user.getUserID())) {

                        Toast.makeText(getApplicationContext(), receiver, Toast.LENGTH_SHORT).show();
                        key = accountSnapshot.getKey();
                     //   receiverBalance = user.getWalletBalance();
                     //   receiverCurrency = user.getCurrency();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        mDatabaseRef.addValueEventListener(receiverListener);
//
//        mDatabaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            public void onDataChange(DataSnapshot data) {
//
//                receiverBalance = data.child("walletBalance").getValue(Double.class);
//                receiverCurrency = data.child("currency").getValue(String.class);
//                receiver = data.child("userID").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //call merkleRoot cloud function


                if (sendMoney.getText().toString().length() > 0) {

                    startActivity(new Intent(SendMoneyActivity.this, PaymentPinActivity.class));

                    amount = Double.parseDouble(sendMoney.getText().toString());

                    mDatabaseRef.child(auth.getUid()).child("walletBalance").setValue(senderBalance - amount);
//                    mDatabaseRef.child(key).child("walletBalance").setValue(receiverBalance + amount);

                    writeTxnList();

                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
//for error handling
        callCloudFunction()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // ...
                        }

                        // ...
                    }
                });


        Spinner currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SendMoneyActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.currency));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(myAdapter);

        //////////////////contacts/////////////////////////
        select = (Button) findViewById(R.id.contacts);
        number = findViewById(R.id.enter_id);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, RESULT_PICK_CONTACT);

            }
        });
        ///////////////////////////////////////////////
    }

    public Task<String> callCloudFunction(){

        return mFunctions.getHttpsCallable("merkleRoot").call().continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                // This continuation runs on either success or failure, but if the task
                // has failed then getResult() will throw an Exception which will be
                // propagated down.
                String result = (String) task.getResult().getData();
                return result;
            }
        });


    }

    public void writeTxnList() {

        String txnKey = txnListRef.push().getKey();
        TxnList tlist = new TxnList();
        tlist.setAmount(amount);
        tlist.setCurrency(receiverCurrency);
        tlist.setFrom(sender);
        tlist.setTo(receiver);
        tlist.setTxnID(txnKey);
        tlist.setTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        tlist.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Map<String, Object> txn = tlist.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + txnKey, txn);

        txnListRef.updateChildren(childUpdates);


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
        Intent myIntent = new Intent(SendMoneyActivity.this, PasscodeActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent data) {

        if (ResultCode == RESULT_OK) {

            switch (RequestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }

        }
        else
        {
            Toast.makeText(this,"Failed tp pick contact",Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor;
        try
        {
            String phonum = null;
            Uri uri =data.getData();
            cursor=getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phonum=cursor.getString(phoneIndex);
            number.setText(phonum);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}