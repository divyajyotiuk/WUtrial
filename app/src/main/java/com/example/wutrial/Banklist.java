package com.example.wutrial;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class Banklist implements AdapterView.OnItemSelectedListener {

    private String bankname;

    public String getBankname(){
        return bankname;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Todo
        bankname = parent.getItemAtPosition(position).toString();
       // Toast.makeText(parent.getContext(),"Banklist" + parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
