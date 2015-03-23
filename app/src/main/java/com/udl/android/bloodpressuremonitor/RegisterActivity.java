package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
       private String providerBestCriteria,providergps,providernetwork;

       private boolean locationbuttonpressed=false;


       private Location lastKnownLocation;

       private String city,administration,country;

    @Override
    public void onCreate(Bundle savedInstanceState){
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
        locationprovince = (EditText) findViewById(R.id.edittextregister6);
        locationcountry = (EditText) findViewById(R.id.edittextregister7);

        defineListeners();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        final boolean gpsenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        final boolean networkenabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(networkenabled)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(gpsenabled && lastKnownLocation == null)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

       providerBestCriteria = locationManager.getBestProvider(new Criteria(),true);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (providerBestCriteria != null)
          locationManager.requestLocationUpdates(providerBestCriteria, 30000,1,this);
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
                    if (country!=null && administration!=null && city!= null)
                        showDialogEvents(AlertsID.LOCATION_FINDED);
                    else
                        showDialogEvents(AlertsID.PROBLEM_LOCATION);
                }else{
                    showDialogEvents(AlertsID.PROBLEM_LOCATION);
                }
            }
        });
        siginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataCorrect()){
                    startActivity(new Intent(RegisterActivity.this,BPMActivityController.class));
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });
    }

    private boolean isDataCorrect(){

        if (name.getText().toString().equals("")
                || surnames.getText().toString().equals("")
                || age.getText().toString().equals("")
                || email.getText().toString().equals("")
                || locationcity.getText().toString().equals("")
                || locationprovince.getText().toString().equals("")
                || locationcountry.getText().toString().equals("")){

            showDialogEvents(AlertsID.PROBLEM_MANDATORY_FIELDS);
            return true;
        }else if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")){
            showDialogEvents(AlertsID.PROBLEM_MAIL);
            return true;
        }

        return false;
    }

    private void showDialogEvents(AlertsID type){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dialogincorrectfields));
        alert.setPositiveButton(getResources().getString(R.string.dialogpositivbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

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
            case LOCATION_FINDED:
                alert.setTitle(getResources().getString(R.string.locationfound));
                String linebreak = System.getProperty("line.separator");
                alert.setMessage(getResources().getString(R.string.locationfoundtext)
                        +linebreak+city+", "+administration+" ("+country+")");
                alert.setPositiveButton(getResources().getString(R.string.yestext), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        locationcity.setText(city);
                        locationprovince.setText(administration);
                        locationcountry.setText(country);
                    }
                });
                alert.setNegativeButton(R.string.notext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
        }
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

    public void obtainLocationData(Location location){

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);

        if (address.size() > 0){
            Address foundAddress = address.get(0);
            country = foundAddress.getCountryName();
            city = foundAddress.getLocality();
            administration = foundAddress.getAdminArea();

        }
        }catch(IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        locationbuttonpressed = false;
    }


}
