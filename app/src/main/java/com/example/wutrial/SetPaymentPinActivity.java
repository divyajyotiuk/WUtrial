package com.example.wutrial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetPaymentPinActivity extends AppCompatActivity {

    private EditText paypin,confirm_pin;
    private Button button;
    private AwesomeValidation awesomeValidation;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpaymentpin);

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("WUAccount").child(uid);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        updateUI();

    }

        public void updateUI () {


            paypin = (EditText) findViewById(R.id.paypin);
            confirm_pin = (EditText) findViewById(R.id.confirm_pin);
            button = (Button) findViewById(R.id.button);

            String regexPassword = "[0-9]+";

            awesomeValidation.addValidation(SetPaymentPinActivity.this, R.id.paypin, regexPassword, R.string.pinerr);
            awesomeValidation.addValidation(SetPaymentPinActivity.this, R.id.confirm_pin, R.id.paypin, R.string.cpinerr);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (awesomeValidation.validate()) {

                        int paymentpin = Integer.parseInt(paypin.getText().toString());
                        Toast.makeText(SetPaymentPinActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        mDatabase.child("paymentPin").setValue(paymentpin);
                      //  Intent intent = new Intent(SetPaymentPinActivity.this, passcode.class);
                      //  startActivity(intent);
                    } else {
                        Toast.makeText(SetPaymentPinActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }