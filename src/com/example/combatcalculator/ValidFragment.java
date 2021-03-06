package com.example.combatcalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ValidFragment extends DialogFragment {
	
	public final static String TYPE_ERROR = "com.example.combatcalculator.TYPE_ERROR";
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	String type = getArguments().getString(TYPE_ERROR);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(type)
               .setPositiveButton(R.string.go_back, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}