package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udl.android.bloodpressuremonitor.adapters.ViewPagerHelpAdapter;
import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.customviews.HeartBeatView;
import com.udl.android.bloodpressuremonitor.fragments.HearRateMonitorFragment;
import com.udl.android.bloodpressuremonitor.fragments.HelpFragment;
import com.udl.android.bloodpressuremonitor.fragments.HomeFragment;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
import com.udl.android.bloodpressuremonitor.fragments.ObtainManualPressures;
import com.udl.android.bloodpressuremonitor.fragments.ProfileFragment;
import com.udl.android.bloodpressuremonitor.utils.PreferenceConstants;

import junit.framework.Test;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;


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
        COULD_NOT_CONNECTED,
        DISCOVER_CANCELLED,


    }

    public static enum ViewPagerType{

        BLUETOOTH_EXAMPLE,
        HEARTRATE_EXPLAIN
    }

    private String TAG_RECEIVER_XML="BLUETOOTH_XML";


    private final int SHOW_TOAST = 0;
    private final int PROGRESSDIALOG_WAITCONN = 1;
    private final int PROGRESSDIALOGDISSMIS_WAITCONN = 2;
    private final int BLUETOOTHDATA_RESULT = 3;
    private final int DISMISS_FAIL = 4;

    private TextView headertextview;
    private ImageButton buttonbar,secondbuttonbar;
    private NetworkStatusReceiver receiver;

    private static String networkconnectionstatus;
    public static boolean downloadAllMeasurements = true;

    private ViewPager viewPagerview;
    private FrameLayout frameLayout;

    public static final int BLUETOOTH_ENABLE_PROCESS = 288;
    public static final int SIGNAL_KILL_CONTROLLER = 111;
    private final int MAX_TIME_DISCOVERABLE= 300;
    private final int MAX_TIME_WAIT_OTHER_DEVICE=30000;
    private final int DISCOVERABLE_BLUETOOTH_PROCESS=333;

    private BluetoothAdapter bluetoothAdapter;


    private boolean connectedbluetooth = false;
    private ArrayAdapter<String> adapterdialog;
    private ArrayList<BluetoothDevice> devicesfound;
    private ProgressDialog progressDialog;

    private Handler mainhandler;

    private String systolicPressure;
    private String diastolicPressure;
    private String pulse;

    private boolean initbluetoothprocess = true;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivitylayout);
        configureActionBar();
        selectFragment(HomeFragment.getNewInstace(),false,false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesfound = new ArrayList<>();

        frameLayout = (FrameLayout) findViewById(R.id.fragmentframe);
        viewPagerview = (ViewPager) findViewById(R.id.viewpager);

        mainhandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){

                    case SHOW_TOAST:
                        Toast.makeText(BPMActivityController.this,(String)msg.obj,Toast.LENGTH_LONG).show();
                        break;

                    case PROGRESSDIALOG_WAITCONN:
                        createDialog(getResources().getString(R.string.connectionstablished));
                        progressDialog.show();
                        break;
                    case DISMISS_FAIL:
                        progressDialog.dismiss();
                        final AlertDialog.Builder dialog= new AlertDialog.Builder(BPMActivityController.this);
                        dialog.setMessage(getResources().getString(R.string.socketconnfail));
                        dialog.setPositiveButton(getResources().getString(android.R.string.ok),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case PROGRESSDIALOGDISSMIS_WAITCONN:
                        progressDialog.dismiss();
                        break;
                    case BLUETOOTHDATA_RESULT:
                        final String linebreak = System.getProperty("line.separator");
                        final String pressure_unit = "mm Hg";
                        final String pulse_unit = "bpm";

                        final  AlertDialog.Builder dialogresult = new AlertDialog.Builder(BPMActivityController.this);

                        dialogresult.setMessage(getResources().getString(R.string.measurements)+
                                linebreak+linebreak+getResources().getString(R.string.systolic)+" "+systolicPressure+" "+pressure_unit
                                +linebreak+getResources().getString(R.string.diastolic)+" "+diastolicPressure+" "+pressure_unit+linebreak
                                +getResources().getString(R.string.pulse)+" "+pulse+ " "+pulse_unit);

                        dialogresult.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialogresult.show();

//
//                        Log.d(TAG_RECEIVER_XML,"Systolic: "+systolicPressure);
//                        Log.d(TAG_RECEIVER_XML,"Diastolic: "+diastolicPressure);
//                        Log.d(TAG_RECEIVER_XML,"Pulse: "+pulse);
                        break;
                }


            }
        };


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

                startActivityForResult(new Intent(BPMActivityController.this,BPMpreferencesActivity.class),SIGNAL_KILL_CONTROLLER);
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
                buttonbar.setVisibility(View.INVISIBLE);
                if (bluetoothAdapter == null) {
                    showDialogBluetoothCases(BluetoothDialog.NOT_SUPPORTED);
                }else{
                    if (!bluetoothAdapter.isEnabled()) {

                        showDialogBluetoothCases(BluetoothDialog.NOT_ENABLED);
                    }else{

                        onActivityResult(BLUETOOTH_ENABLE_PROCESS,RESULT_OK,null);
                    }
                }
                secondbuttonbar.setVisibility(View.VISIBLE);
                break;
            case 4:
                ObtainManualPressures obtainManualPressures = ObtainManualPressures.getNewInstance();
                selectFragment(obtainManualPressures,false,true);
                headertextview.setText(getResources().getString(R.string.manualpressure).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 5:
                ProfileFragment profileFragment = ProfileFragment.getNewInstance();
                selectFragment(profileFragment,false,true);
                headertextview.setText(getResources().getString(R.string.headerprofile));
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 6:
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                headertextview.setText(getResources().getString(R.string.help));
                frameLayout.setVisibility(View.GONE);
                viewPagerview.setVisibility(View.VISIBLE);
                List<Fragment> fragments = new ArrayList<>();
                HelpFragment bluetoothpage = HelpFragment.getNewInstance(ViewPagerType.BLUETOOTH_EXAMPLE);
                fragments.add(bluetoothpage);
                HelpFragment heartrate = HelpFragment.getNewInstance(ViewPagerType.HEARTRATE_EXPLAIN);
                fragments.add(heartrate);
                ViewPagerHelpAdapter adapter = new ViewPagerHelpAdapter(getSupportFragmentManager(),fragments);
                viewPagerview.setAdapter(adapter);
                break;
            default:
                break;

        }

    }

    private void onBack(){


        secondbuttonbar.setVisibility(View.VISIBLE);
        buttonbar.setVisibility(View.INVISIBLE);
        Fragment lasfragment = getSupportFragmentManager().findFragmentById(R.id.fragmentframe);

        if (lasfragment instanceof HomeFragment && frameLayout.getVisibility()==View.VISIBLE ) {
            showExitDialog();
        } else if(viewPagerview.getVisibility()==View.VISIBLE) {
            removeViewPager();
            headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());

        }else if(lasfragment instanceof HearRateMonitorFragment){
            getSupportFragmentManager().beginTransaction().remove(lasfragment).commit();
            putHomeFragmentInTop(true);
        }else {
            putHomeFragmentInTop(true);

        }

        frameLayout.setVisibility(View.VISIBLE);
        viewPagerview.setVisibility(View.GONE);

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

        super.onPause();
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
        }else if (type==BluetoothDialog.DISCOVER_CANCELLED){
            alert.setMessage(getResources().getString(R.string.processcanceled));
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
                initBluetoothProcess();

            }else{
                showDialogBluetoothCases(BluetoothDialog.COULD_NOT_CONNECTED);
            }
        }else if (requestCode==SIGNAL_KILL_CONTROLLER){
            if(resultCode == RESULT_OK){
                finish();
            }

        }else if (requestCode==DISCOVERABLE_BLUETOOTH_PROCESS){
            if (resultCode == RESULT_CANCELED){
                showDialogBluetoothCases(BluetoothDialog.DISCOVER_CANCELLED);
            }else{
                bluetoothProcess();
            }

        }
    }

    private void initBluetoothProcess(){

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX_TIME_DISCOVERABLE);
        startActivityForResult(discoverableIntent,DISCOVERABLE_BLUETOOTH_PROCESS);


    }



    private void createDialog(String message){

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
        }else{
            progressDialog.setMessage(message);
        }

    }

    private class AcceptThread extends Thread {
        BluetoothServerSocket serverSocket = null;

        public AcceptThread() {

        }

        public void run() {
            BluetoothSocket socket = null;
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BPMRFCOMM",UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
                socket = serverSocket.accept(MAX_TIME_WAIT_OTHER_DEVICE);
                mainhandler.sendEmptyMessage(PROGRESSDIALOGDISSMIS_WAITCONN);
                if (socket != null) {
                    mainhandler.sendEmptyMessage(PROGRESSDIALOG_WAITCONN);
                    InputStream stream = socket.getInputStream();
                    while (stream.available()<=0){}
                    if(stream.available()>0){
                        xmlParsePressures(stream);
                        socket.close();

                    }
                }
               mainhandler.sendEmptyMessage(PROGRESSDIALOGDISSMIS_WAITCONN);

            } catch (IOException e) {
                mainhandler.sendEmptyMessage(DISMISS_FAIL);
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (receiver!=null) unregisterReceiver(receiver);

    }

    private void xmlParsePressures(InputStream stream){

        try {


            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setInput(new InputStreamReader(stream));

            int eventType = parser.getEventType();
            String element = "";
            boolean END_DOCUMENT = false;
            while (!END_DOCUMENT) {


                if (eventType == XmlPullParser.START_TAG){
                    element = parser.getName();
                }
                else if(eventType == XmlPullParser.TEXT) {

                    if(element.equals("systolic-pressure")){
                        systolicPressure =parser.getText();
                        //Log.d("XML","ParseNext "+systolicPressure);
                    }
                    if (element.equals("diastolic-pressure")){
                        diastolicPressure = parser.getText();
                        //Log.d("XML","ParseNext "+diastolicPressure);
                    }
                    if (element.equals("pulse")){
                        pulse = parser.getText();
                        END_DOCUMENT = true;
                        //Log.d("XML","ParseNext "+pulse);
                    }
                }
                eventType = parser.next();
            }

            Message message = new Message();
            message.what=BLUETOOTHDATA_RESULT;
            mainhandler.sendMessage(message);

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void putHomeFragmentInTop(boolean back){
        headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());
        selectFragment(HomeFragment.getNewInstace(), true, back);
    }

    private void removeViewPager() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.viewpager);
        if (fragment!=null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void bluetoothProcess(){

        createDialog(getResources().getString(R.string.waitconnection));
        progressDialog.show();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        Thread thread = new AcceptThread();
        thread.start();
    }
    

}
