package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adrian on 28/02/2015.
 */
public class RegisterActivity extends BPMmasterActivity
                              implements LocationListener{

    public enum AlertsID{

        PROBLEM_MAIL,
        PROBLEM_MANDATORY_FIELDS,
        PROBLEM_LOCATION,
        LOCATION_FINDED
    }


       private Button autobutton;
       private Button siginbutton;

       private EditText name,surnames,age,locationcity,locationprovince,locationcountry,email;

       private String sName,sSurname,sAge,sLocation;

       private LocationManager locationManager;
       private String providerBestCriteria;

       private boolean locationbuttonpressed=false;


       private Location lastKnownLocation;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerlayout);

        ((TextView)findViewById(R.id.descriptionregister)).setTypeface(getOpenSansRegular());

        autobutton = (Button)findViewById(R.id.autolocation);
        siginbutton = (Button) findViewById(R.id.signingbutton);

        name  = (EditText) findViewById(R.id.edittextregister1);
        surnames  = (EditText) findViewById(R.id.edittextregister2);
        email = (EditText) findViewById(R.id.edittextregister3);
        age  = (EditText) findViewById(R.id.edittextregister4);
        locationcity  = (EditText) findViewById(R.id.edittextregister5);
        locationcountry = (EditText) findViewById(R.id.edittextregister6);

        defineListeners();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        providerBestCriteria = locationManager.getBestProvider(new Criteria(),true);
        if (providerBestCriteria != null) {
            lastKnownLocation = locationManager.getLastKnownLocation(providerBestCriteria);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if (providerBestCriteria != null)
        locationManager.requestLocationUpdates(providerBestCriteria, 30000, 1, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    private void defineListeners(){
        autobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationbuttonpressed = true;
                if (lastKnownLocation!=null){
                    obtainLocationData(lastKnownLocation);
                }else{
                    showDialogEvents(AlertsID.PROBLEM_LOCATION);
                }
            }
        });

        siginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSentData();

            }
        });
    }

    private void checkSentData(){

        if (name.getText().toString().equals("")
                || surnames.getText().toString().equals("")
                || age.getText().toString().equals("")
                || email.getText().toString().equals("")
                || locationcity.getText().toString().equals("")){

            showDialogEvents(AlertsID.PROBLEM_MANDATORY_FIELDS);
        }else if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")){
            showDialogEvents(AlertsID.PROBLEM_MAIL);
        }
    }

    private void showDialogEvents(AlertsID type){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dialogincorrectfields));

        switch (type){
            case PROBLEM_MAIL:
                alert.setMessage(getResources().getString(R.string.dialogemail));
                email.setText("");
                break;
            case PROBLEM_MANDATORY_FIELDS:
                alert.setMessage(getResources().getString(R.string.mandatoryfields));
                break;
            case PROBLEM_LOCATION:
                alert.setMessage(getResources().getString(R.string.dialogproblem));
                break;
        }
        alert.setPositiveButton(getResources().getString(R.string.dialogpositivbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog center = alert.show();
        TextView messageText = (TextView)center.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        center.show();

    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    public String obtainLocationData(Location location){

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            String fnialAddress = builder.toString(); //This is the complete address.
            String p = " ";

        } catch (IOException e) {}
        catch (NullPointerException e) {}
        locationbuttonpressed = false;
        return "";
    }


}
