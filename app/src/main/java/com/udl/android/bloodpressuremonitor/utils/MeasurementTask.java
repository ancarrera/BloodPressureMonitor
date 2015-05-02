package com.udl.android.bloodpressuremonitor.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.adrian.myapplication.backend.measurementApi.MeasurementApi;
import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.backend.BackendCalls;

import java.io.IOException;

/**
 * Created by adrian on 20/4/15.
 */
public class MeasurementTask extends AsyncTask<Measurement,Void,Measurement> {
    private BPMmasterActivity context;
    private boolean isPending = false;
    private Measurement oldmeasurement;

    public MeasurementTask(Context context,boolean isPending){
        this.isPending = isPending;
        this.context = (BPMmasterActivity)context;
    }

    protected void onPreExecute(){
        context.showDialog(false);
    }
    @Override
    protected Measurement doInBackground(Measurement... params) {

        MeasurementApi measurementApi = BackendCalls.getInstance().buildMeasurement();
        String lan = checkAppLenguage(context);
        try {
            return measurementApi.insertMeasurement(Constants.SESSION_USER_ID,lan,params[0]).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.oldmeasurement  = params[0];
        return null;
    }

    private String checkAppLenguage(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getString("languageList","").equalsIgnoreCase("")){
            return preferences.getString("languageList","");
        }
        return "";
    }

    protected void onPostExecute(Measurement measurement){
        context.dialogDismiss();
        if (isPending){
            PendingMeasurement.removePending(context);
        }

        if (measurement!=null){
            showAlertDialog(true);
        }else{
            showAlertDialog(false);
            if (!isPending)
                PendingMeasurement.saveMeasurement(context,oldmeasurement);
        }
    }


    private void showAlertDialog(boolean wasGoodSend){

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if (wasGoodSend){
            dialog.setMessage(context.getResources().getString(R.string.goodsendmeasurement));
        }else{
            dialog.setMessage(context.getResources().getString(R.string.errorsendmeasurement));
        }
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();
    }
}
