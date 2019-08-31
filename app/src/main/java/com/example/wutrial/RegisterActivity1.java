package com.example.wutrial;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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

    public void signInWithPhone(final PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Intent intent = new Intent(getBaseContext(), RegisterActivity2.class);
                    intent.putExtra("countryCode",ccp.getSelectedCountryNameCode());
                    intent.putExtra("country",ccp.getSelectedCountryEnglishName());
                    intent.putExtra("phone",ccp.getFormattedFullNumber());
                    startActivity(intent);

                }else
                {
                    Toast.makeText(getApplicationContext(),"Invalid verification code",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verify(View v){
        String input_code = otp.getText().toString();
        if(otp.length()<6){
            Toast.makeText(getApplicationContext(),"Invalid verification code",Toast.LENGTH_SHORT).show();
        }else
        {
            verifyPhone(verification_code,input_code);
        }

    }

    public void verifyPhone(String verify_code,String input_code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_code, input_code);
        signInWithPhone(credential);
    }

//
//    //for redirecting to main activity after registration
    private void onAuthSuccess(FirebaseUser user) {

        Toast.makeText(getApplicationContext(),auth.getUid(),Toast.LENGTH_SHORT).show();
      //  startActivity(new Intent(RegisterActivity1.this, PasscodeActivity.class));
       // finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check auth on Activity start

       FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                  //  Toast.makeText(getApplicationContext(),auth.getUid(),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity1.this, PasscodeActivity.class));
                    finish();
                    // Sign in logic here.
                }
            }
        };

       mAuthListener.onAuthStateChanged(auth);

//        if (auth.getCurrentUser() != null) {
//            onAuthSuccess(auth.getCurrentUser());
//        }
    }
}
