package com.example.wutrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasscodeActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Button button;
    private EditText passcode;
    private int passCode,dbPasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("WUAccount").child(auth.getUid());


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot data) {

                dbPasscode = data.child("loginPass").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        passcode = (EditText)findViewById(R.id.passcode_login);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                passCode = Integer.parseInt(passcode.getText().toString());

                if(passCode==dbPasscode){
                  Intent intent=new Intent(PasscodeActivity.this,MainActivity.class);
                  startActivity(intent);
              }else{
                  Toast.makeText(getApplicationContext(),"Wrong Passcode",Toast.LENGTH_SHORT).show();
              }

            }
        });
    }
}
