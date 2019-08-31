package com.example.wutrial.Model;

import com.example.wutrial.MainActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InitialiseBankServer {
    private FirebaseDatabase secondaryDatabase;
    private DatabaseReference secondaryRef;

    public void init(){
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:875508770867:web:0dea2fbe0bdb808c") // Required for Analytics.
                .setApiKey("AIzaSyCf1vh20ZxwAj_q-xewYVZH3Yl8w3B7xVc") // Required for Auth.
                .setDatabaseUrl("https://bankserver-dafa0.firebaseio.com") // Required for RTDB.
                .build();
        //FirebaseApp.initializeApp(this/* Context */, options, "secondary");

        // Retrieve my other app.
        FirebaseApp app = FirebaseApp.getInstance("secondary");
        // Get the database for the other app.
        secondaryDatabase = FirebaseDatabase.getInstance(app);

        secondaryRef = secondaryDatabase.getReference("accounts");
    }

}
