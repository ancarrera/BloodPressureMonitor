package com.udl.android.bloodpressuremonitor.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import com.example.adrian.myapplication.backend.measurementApi.MeasurementApi;
import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;


import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;

/**
 * Created by adrian on 1/5/15.
 */
public class PendingMeasurement {

    private Measurement measurement;
    private BPMmasterActivity context;

    public PendingMeasurement(BPMmasterActivity context){
        this.context = context;
    }

    public  boolean checkIfPending(){
        SharedPreferences preferences = context.getSharedPreferences(
                PreferenceConstants.GCM_PREFERENCES,Context.MODE_PRIVATE);
        String pending = preferences.getString(PreferenceConstants.PENDING_MEASUREMENT,"");
        if (pending.equalsIgnoreCase("")){
            return false;
        }else{
            String[] measurementsplit = pending.split(" ");
            Measurement measurement = new Measurement();
            measurement.setDate(measurementsplit[0]);
            measurement.setSystolic(Integer.parseInt(measurementsplit[1]));
            measurement.setDiastolic(Integer.parseInt(measurementsplit[2]));
            measurement.setPulse(Integer.parseInt(measurementsplit[3]));
            this.measurement = measurement;

            return true;
        }
    }

    public void showPendingDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(context.getResources().getString(R.string.pendingmeasurement));
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.setPositiveButton(context.getResources().getString(R.string.sendbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new MeasurementTask(context,true).execute(measurement);
            }
        });
        builder.show();

    }

    public static void saveMeasurement(Context context,Measurement measurement) {
        SharedPreferences preferences = context.getSharedPreferences(
                PreferenceConstants.GCM_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(PreferenceConstants.PENDING_MEASUREMENT,measurement.getDate()+" "+measurement.getSystolic()+" "
                +measurement.getDiastolic()+ " "+measurement.getPulse()).commit();
    }

    public static void removePending(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                PreferenceConstants.GCM_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(PreferenceConstants.PENDING_MEASUREMENT,"").commit();
    }

}
