package com.example.wutrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentPinActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Button button;
    private EditText paymentpin;
    private int pin,dbPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pin);

        paymentpin = (EditText)findViewById(R.id.payment_pin);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("WUAccount").child(auth.getUid());


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot data) {

                dbPin = data.child("paymentPin").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pin = Integer.parseInt(paymentpin.getText().toString());

                if(pin==dbPin){
                   finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Wrong Payment Pin",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
