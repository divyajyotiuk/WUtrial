package com.example.wutrial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wutrial.Model.BankServer;
import com.example.wutrial.Model.Banklist;
import com.example.wutrial.Model.Idprooflist;
import com.example.wutrial.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Currency;
import java.util.Locale;


public class RegisterActivity2 extends AppCompatActivity {

    FirebaseAuth auth;

    private FirebaseDatabase secondaryDatabase;
    private DatabaseReference secondaryRef;
    private DatabaseReference mDatabase;
    private String country,phone,countryCode;
    private EditText idproofNo;
    private Spinner idproofSpinner, bankSpinner;
    private static final String[] id_proof = {"Adhar card","Pancard","Other"};
    private static final String[] Bank = {"SBI","HDFC","Other"};
    User user;
    private String uid;
    Idprooflist idproof;
    Banklist bank;

    public static String getCurrencyCode(String countryCode) {
        return Currency.getInstance(new Locale("", countryCode)).getCurrencyCode();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_2);

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
        uid = auth.getUid();

        user = new User();

        mDatabase = FirebaseDatabase.getInstance().getReference("WUAccount");

        Intent intent = getIntent();

        country = intent.getStringExtra("country");
        phone = intent.getStringExtra("phone");
        countryCode = intent.getStringExtra("countryCode");
       // user.setCurrency(getCurrencyCode(countryCode));


        idproof = new Idprooflist();
        bank = new Banklist();


        idproofNo = (EditText)findViewById(R.id.id_proof_no);
        idproofSpinner =(Spinner)findViewById(R.id.id_proof_spinner);
        bankSpinner =(Spinner)findViewById(R.id.bank_spinner);
        Button kycBtn=(Button) findViewById(R.id.kyc_btn);


        ArrayAdapter<String> idProofAdapter=new ArrayAdapter<String>(RegisterActivity2.this,android.R.layout.simple_spinner_dropdown_item,id_proof);
        idproofSpinner.setAdapter(idProofAdapter);
        idproofSpinner.setOnItemSelectedListener(idproof);

        ArrayAdapter<String> bankAdapter=new ArrayAdapter<String>(RegisterActivity2.this,android.R.layout.simple_spinner_dropdown_item,Bank);
        bankSpinner.setAdapter(bankAdapter);
        bankSpinner.setOnItemSelectedListener(bank);



        ValueEventListener bankListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                // Get Post object and use the values to update the UI
                for (DataSnapshot bankSnapshot: dataSnapshot.getChildren()) {

                    BankServer bankServer = bankSnapshot.getValue(BankServer.class);
                    //retreive child data from database
                    Toast.makeText(getApplicationContext(),phone,Toast.LENGTH_SHORT).show();
                    if(bankServer.getPhoneNo().equals(phone)){

                        user.setFullName(bankServer.getName());


                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message


            }
        };
        secondaryRef.addValueEventListener(bankListener);

        kycBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                writeNewUser(uid);

                startActivity(new Intent(RegisterActivity2.this, SetPasscodeActivity.class));
            }
        });


    }


    public void writeNewUser(String userId) {

        user.setData(phone,country,idproof.getIdproof(),bank.getBankname(),idproofNo.getText().toString(),getCurrencyCode(countryCode));

        mDatabase.child(userId).setValue(user);
    }
}
