package com.example.wutrial;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SendMoneyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_money);

        Spinner currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SendMoneyActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.currency));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(myAdapter);
    }
}
