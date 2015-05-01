package com.udl.android.bloodpressuremonitor.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adrian.myapplication.backend.measurementApi.MeasurementApi;
import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udl.android.bloodpressuremonitor.BPMActivityController;
import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.utils.Constants;
import com.udl.android.bloodpressuremonitor.utils.DateUtils;
import com.udl.android.bloodpressuremonitor.utils.MeasurementTask;
import com.udl.android.bloodpressuremonitor.utils.PendingMeasurement;

import java.io.IOException;

/**
 * Created by Adrian on 07/03/2015.
 */
public class ObtainManualPressures extends Fragment {

    private EditText systolictext;
    private EditText diastolictext;
    private EditText pulsetext;

    private Button sendbutton;

    int systolicpressure,diastolicpressure,pulse;

    final int maxsystolic = 240,minsystolic=50, maxdiastolic = 140,mindiastolic = 30, minpulse = 30, maxpulse = 240;


    public static ObtainManualPressures getNewInstance() {

        return new ObtainManualPressures();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manualpressureslayout, null);
        sendbutton = (Button)view.findViewById(R.id.mpressuressend);
        systolictext = (EditText) view.findViewById(R.id.mpressuresedittext1);
        diastolictext = (EditText) view.findViewById(R.id.mpressuresedittext2);
        pulsetext = (EditText) view.findViewById(R.id.mpressuresedittext3);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingMeasurement pendingm = new PendingMeasurement((BPMActivityController)ObtainManualPressures.this.getActivity());
                if (pendingm.checkIfPending()) {
                    pendingm.showPendingDialog();
                } else {

                    if (systolictext.getText().toString().equals("")
                            || diastolictext.getText().toString().equals("") ||
                            pulsetext.getText().toString().equals("")) {

                        showFieldsDialog(0);
                    } else {
                        systolicpressure = Integer.parseInt(systolictext.getText().toString());
                        diastolicpressure = Integer.parseInt(diastolictext.getText().toString());
                        pulse = Integer.parseInt(pulsetext.getText().toString());
                        if (systolicpressure > maxsystolic || systolicpressure < minsystolic) {

                            showFieldsDialog(1);
                        } else if (diastolicpressure > maxdiastolic || diastolicpressure < mindiastolic) {

                            showFieldsDialog(2);
                        } else if (pulse > maxpulse || pulse < minpulse) {

                            showFieldsDialog(3);
                        } else {
                            Measurement measurement = new Measurement();
                            measurement.setSystolic(Integer.parseInt(systolictext.getText().toString()));
                            measurement.setDiastolic(Integer.parseInt(diastolictext.getText().toString()));
                            measurement.setPulse(Integer.parseInt(pulsetext.getText().toString()));
                            measurement.setDate(DateUtils.getCurrentDate());
                            new MeasurementTask(getActivity(),false).execute(measurement);
                            systolictext.setText("");
                            diastolictext.setText("");
                            pulsetext.setText("");
                        }

                    }
                }
            }
        });
    }


    private void showFieldsDialog(int tag){

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.dialogincorrectfields));
        switch (tag){
            case 1:
                alert.setMessage(getResources().getString(R.string.systolicpressuredialog));
                systolictext.setText("");
                break;
            case 2:
                alert.setMessage(getResources().getString(R.string.diastolicpressuredialog));
                diastolictext.setText("");
                break;
            case 3:
                pulsetext.setText("");
                alert.setMessage(getResources().getString(R.string.pulsepressuredialog));
                break;
            default:
                alert.setMessage(getResources().getString(R.string.mandatoryfields));
                break;
        }
        alert.setPositiveButton(getResources().getString(R.string.dialogpositivbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();


    }

}