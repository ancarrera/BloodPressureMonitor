package com.udl.android.bloodpressuremonitor.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.adrian.myapplication.backend.measurementApi.MeasurementApi;
import com.example.adrian.myapplication.backend.measurementApi.model.CollectionResponseMeasurement;
import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udl.android.bloodpressuremonitor.BPMActivityController;
import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.adapters.MeasurementAdapter;
import com.udl.android.bloodpressuremonitor.backend.BackendCalls;
import com.udl.android.bloodpressuremonitor.utils.Constants;

import java.io.IOException;
import java.util.List;

/**
 * Created by Adrian on 5/3/15.
 */
public class MeasurementsFragment extends Fragment {

    private ListView listview;

    public static MeasurementsFragment getNewInstance(){

        return new MeasurementsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.measurementslayout, null);
       listview = (ListView) view.findViewById(R.id.measurementlist);
       return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        if (BPMActivityController.downloadAllMeasurements){
            new GetMeasurements().execute();
        }
    }


    private class GetMeasurements extends AsyncTask<Void,Void,CollectionResponseMeasurement>{

        public void onPreExecute(){
            getControllerActivity().showDialog(false);
        }

        @Override
        public CollectionResponseMeasurement doInBackground(Void... params){
            MeasurementApi measurementApi = BackendCalls.getInstance().buildMeasurement();
            try {
                return measurementApi.listMeasurements(Constants.SESSION_USER_ID).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        public void onPostExecute(CollectionResponseMeasurement result) {
            getControllerActivity().dialogDismiss();
            if (result == null) {
                showErrorDialog();
            }else {
                configureView(result.getItems());
            }
        }


    }

    private void showErrorDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getControllerActivity())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setMessage(getResources().getString(R.string.errorreceivingpressures));
            dialog.show();

    }


    private void configureView(List<Measurement> measurements){
        //List<Pressure> list = DBMock.getMeasurementsFake();
        MeasurementAdapter adapter = new MeasurementAdapter(getActivity(),measurements);
        listview.setAdapter(adapter);

    }

    private BPMActivityController getControllerActivity(){
        return (BPMActivityController)getActivity();
    }



}
