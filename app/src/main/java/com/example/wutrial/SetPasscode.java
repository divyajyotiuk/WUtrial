package com.example.wutrial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetPasscode extends AppCompatActivity {

     private EditText password,confirm;
     private Button button;
     private AwesomeValidation awesomeValidation;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpasscode);

        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("WUAccount").child(uid);

        awesomeValidation=new AwesomeValidation(ValidationStyle.BASIC);

            updateUI();

        }

    public void updateUI() {


        password =(EditText) findViewById(R.id.password);
        confirm =(EditText) findViewById(R.id.confirm);
        button = (Button) findViewById(R.id.button);
        String regexPassword = "[0-9]+";
        awesomeValidation.addValidation(SetPasscode.this,R.id.password,regexPassword,R.string.pass);
        awesomeValidation.addValidation(SetPasscode.this,R.id.confirm,R.id.password,R.string.cpass);

        //update passcode in database

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(awesomeValidation.validate())
                {
                    Toast.makeText(SetPasscode.this,"Success",Toast.LENGTH_SHORT).show();

                    int passcode = Integer.parseInt(password.getText().toString());
                    mDatabase.child("loginPass").setValue(passcode);

                    Intent intent=new Intent(SetPasscode.this,SetPaymentPin.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(SetPasscode.this,"Fail",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
