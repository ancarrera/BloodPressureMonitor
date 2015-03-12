package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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

import java.util.Set;


public class BPMActivityController extends BPMmasterActivity
                         implements View.OnClickListener {

    public static enum HomeButton{

        HEART,
        LIST,
        PROFILE,
        HELP,
        BLUETOOTH
    }

    public static enum BluetoothDialog{

        NOT_SUPPORTED,
        NOT_ENABLED,
        COULD_NOT_CONNECTED


    }

    private TextView headertextview;
    private ImageButton buttonbar,secondbuttonbar;
    private NetworkStatusReceiver receiver;

    private static String networkconnectionstatus;
    public static boolean downloadAllMeasurements = true;

    public static int BLUETOOTH_ENABLE_PROCESS = 288;

    private BluetoothAdapter bluetoothAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivitylayout);
        configureActionBar();
        selectFragment(HomeFragment.getNewInstace(),false,false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onStart(){
        super.onStart();

        receiver = new NetworkStatusReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        networkconnectionstatus = preferences.getString("networkList", "WiFi");
        checkStatusConnectionPreferences(this);
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

                if (bluetoothAdapter == null) {
                    showDialogBluetoothCases(BluetoothDialog.NOT_SUPPORTED);
                }else{
                    if (!bluetoothAdapter.isEnabled()) {

                        showDialogBluetoothCases(BluetoothDialog.NOT_ENABLED);
                    }else{

                    }
                }

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

    private void showDialogBluetoothCases(BluetoothDialog type){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dialogtitlebluetooth));
        if (type == BluetoothDialog.NOT_SUPPORTED){

            alert.setMessage(R.string.nonsupportedbluetooth);
            alert.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

        }else if (type == BluetoothDialog.NOT_ENABLED){

            alert.setMessage(R.string.non_enabledbluetooth);
            alert.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,BLUETOOTH_ENABLE_PROCESS );

                }
            });
            alert.setNegativeButton(getResources().getString(android.R.string.no),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        }else if (type == BluetoothDialog.COULD_NOT_CONNECTED ){

            alert.setMessage(R.string.failenablebluetooth);
            alert.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        }

        AlertDialog dialog = alert.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                   Intent data) {
        if (requestCode == BLUETOOTH_ENABLE_PROCESS) {
            if (resultCode == RESULT_OK) {

            }else{
                showDialogBluetoothCases(BluetoothDialog.COULD_NOT_CONNECTED);
            }
        }
    }

    private String[] findBluetoothDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        String[] devices=null;
        if (pairedDevices.size() > 0) {
             devices = new String[pairedDevices.size()];
            int count = 0;
            for (BluetoothDevice device : pairedDevices) {
                devices[0] =device.getName() + "\n" + device.getAddress();
                count++;
            }
        }
        return devices;
    }

    private void findAndShowBluetoothDevices(){
        String[] devices = findBluetoothDevices();
        if (devices != null){

        }
    }

    private void showDevicesDialog(String[] devices){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialogdevices));
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
