package com.example.wutrial.Model;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class Idprooflist implements AdapterView.OnItemSelectedListener {

    private String idproof;

    public String getIdproof(){

        return idproof;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Todo
        idproof = parent.getItemAtPosition(position).toString();
       // Toast.makeText(parent.getContext(),"Idprooflist" + parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
