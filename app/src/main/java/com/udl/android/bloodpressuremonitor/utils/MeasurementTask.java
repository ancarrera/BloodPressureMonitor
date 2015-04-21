package com.udl.android.bloodpressuremonitor.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.example.adrian.myapplication.backend.measurementApi.MeasurementApi;
import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;

import java.io.IOException;

/**
 * Created by adrian on 20/4/15.
 */
public class MeasurementTask extends AsyncTask<Measurement,Void,Measurement> {
    private BPMmasterActivity context;

    public MeasurementTask(Context context){
        this.context = (BPMmasterActivity)context;
    }

    protected void onPreExecute(){
        context.showDialog(false);
    }
    @Override
    protected Measurement doInBackground(Measurement... params) {
        MeasurementApi.Builder builder = new MeasurementApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl(Constants.LOCAL_TEST_EMULATOR_URL)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        MeasurementApi measurementApi = builder.build();
        try {
            return measurementApi.insertMeasurement(params[0]).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Measurement measurement){
        context.dialogDismiss();
        if (measurement!=null){
            showAlertDialog(true);
        }else{
            showAlertDialog(false);
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
