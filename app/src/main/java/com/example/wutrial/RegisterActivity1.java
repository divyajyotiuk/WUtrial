package com.example.wutrial;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;


import java.util.concurrent.TimeUnit;

public class RegisterActivity1 extends AppCompatActivity{

    CountryCodePicker ccp;
    FirebaseAuth auth;
    EditText ph,otp;
    String verification_code;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_1);

        ph = (EditText)findViewById(R.id.phone_no);
        otp = (EditText)findViewById(R.id.id_otp);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        auth = FirebaseAuth.getInstance();
        ccp.registerCarrierNumberEditText(ph);

        mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verification_code = s;
                Toast.makeText(getApplicationContext(),"OTP sent!",Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void send_sms(View v){
        String number = ccp.getFormattedFullNumber();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS,this,mcallback
        );

    }

    public void signInWithPhone(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User signed in successful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verify(View v){
        String input_code = otp.getText().toString();
            verifyPhone(verification_code,input_code);
    }

    public void verifyPhone(String verify_code,String input_code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_code, input_code);
        signInWithPhone(credential);
    }
}
