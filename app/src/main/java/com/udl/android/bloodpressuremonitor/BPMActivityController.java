package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.customviews.HeartBeatView;
import com.udl.android.bloodpressuremonitor.fragments.HearRateMonitorFragment;
import com.udl.android.bloodpressuremonitor.fragments.HomeFragment;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
import com.udl.android.bloodpressuremonitor.fragments.ObtainManualPressures;
import com.udl.android.bloodpressuremonitor.utils.PreferenceConstants;

import org.w3c.dom.Text;


public class BPMActivityController extends BPMmasterActivity
                         implements View.OnClickListener {

    public static enum HomeButton{

        HEART,
        LIST,
        PROFILE,
        HELP,
        BLUETOOTH
    }

    private TextView headertextview;
    private ImageButton buttonbar,secondbuttonbar;
    private NetworkStatusReceiver receiver;

    private static String networkconnectionstatus;
    public static boolean downloadAllMeasurements = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivitylayout);
        configureActionBar();
        selectFragment(HomeFragment.getNewInstace(),false,false);

    }

    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        networkconnectionstatus = preferences.getString("networkList", "WiFi");
        checkStatusConnectionPreferences(this);

        receiver = new NetworkStatusReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    private void configureActionBar(){

        configureBaseActionBar();
        View view = getActionBarView();
        headertextview = (TextView) view.findViewById(R.id.textactionbar);
        headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());
        buttonbar = (ImageButton) findViewById(R.id.actionbarbutton);
        secondbuttonbar = (ImageButton) findViewById(R.id.secondbutton);
        buttonbar.setVisibility(View.INVISIBLE);
        secondbuttonbar.setVisibility(View.VISIBLE);
        buttonbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  onBack();
            }
        });
        secondbuttonbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(BPMActivityController.this,BPMpreferencesActivity.class));
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void selectFragment(Fragment fragment,boolean isBack,boolean animation){

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        int id = R.id.fragmentframe;
        if (animation) {
            if (isBack) {
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.slide_out_down);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up,
                        R.anim.fade_out);
            }
        }
        if (fragmentManager.findFragmentById(id) == null) {
            fragmentTransaction.add(id, fragment);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(id, fragment);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onClick(View view) {

        int tag = Integer.parseInt((String)view.getTag());
        switch (tag){
            case 1:
                HearRateMonitorFragment hearRateMonitorFragment = HearRateMonitorFragment.getNewInstance();
                selectFragment(hearRateMonitorFragment,false,true);
                headertextview.setText(getResources().getString(R.string.heartbeatheader).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 2:
                MeasurementsFragment measurementsFragment = MeasurementsFragment.getNewInstance();
                selectFragment(measurementsFragment,false,true);
                headertextview.setText(getResources().getString(R.string.measurementlistheader).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 3:
                System.out.println("TAG ES 3");
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 4:
                ObtainManualPressures obtainManualPressures = ObtainManualPressures.getNewInstance();
                selectFragment(obtainManualPressures,false,true);
                headertextview.setText(getResources().getString(R.string.manualpressure).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 5:
                break;
            case 6:
                break;
            default:
                break;

        }

    }

    private void onBack(){
        secondbuttonbar.setVisibility(View.VISIBLE);
        buttonbar.setVisibility(View.INVISIBLE);
        Fragment lasfragment = getSupportFragmentManager().findFragmentById(R.id.fragmentframe);
        if(lasfragment instanceof HomeFragment) {
            showExitDialog();
        }else if(lasfragment instanceof HearRateMonitorFragment){
            getSupportFragmentManager().beginTransaction().remove(lasfragment).commit();
        }else {
            headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());
            selectFragment(HomeFragment.getNewInstace(), true, true);

        }

    }

    @Override
    public void onBackPressed(){
        onBack();
    }

    private void showExitDialog(){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.dialogexit);
        alert.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }



    private static class NetworkStatusReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            checkStatusConnectionPreferences(context);
        }
    }

    @Override
    public void onPause(){
        if (receiver!=null) unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private static void checkStatusConnectionPreferences(Context context){

        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {


            if (info.getType() == connectivityManager.TYPE_WIFI &&
                    networkconnectionstatus.equals(PreferenceConstants.WIFI_CONNECT)){
                downloadAllMeasurements = true;
                return;
            }else if (networkconnectionstatus.equals(PreferenceConstants.ALL_CONNECT)){
                downloadAllMeasurements = true;
                return;
            }

        }
        downloadAllMeasurements = false;


    }

}
