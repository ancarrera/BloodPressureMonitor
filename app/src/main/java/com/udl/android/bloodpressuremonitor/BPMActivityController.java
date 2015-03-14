package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
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
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.customviews.HeartBeatView;
import com.udl.android.bloodpressuremonitor.fragments.HearRateMonitorFragment;
import com.udl.android.bloodpressuremonitor.fragments.HomeFragment;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
import com.udl.android.bloodpressuremonitor.fragments.ObtainManualPressures;
import com.udl.android.bloodpressuremonitor.utils.PreferenceConstants;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
        COULD_NOT_CONNECTED


    }

    private TextView headertextview;
    private ImageButton buttonbar,secondbuttonbar;
    private NetworkStatusReceiver receiver;

    private static String networkconnectionstatus;
    public static boolean downloadAllMeasurements = true;

    public static int BLUETOOTH_ENABLE_PROCESS = 288;

    private BluetoothAdapter bluetoothAdapter;

    private Map<String,BluetoothDevice> map;

    private boolean connectedbluetooth = false;
    private ArrayAdapter<String> adapterdialog;
    private ArrayList<BluetoothDevice> devicesfound;
    private ProgressDialog progressDialog;

    private Handler mainhandler;

    private InputStream  inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                progressDialog.show();
                Log.d("BLUETOOTH","EMPEZANDO A BUSCAR");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Log.d("BLUETOOTH","BUSQUEDA FINALIZADA");
                progressDialog.dismiss();

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                Log.d("BLUETOOTH","DISPOSITIVO ENCONTRADO");
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                adapterdialog.add(device.getName());
                adapterdialog.notifyDataSetChanged();
                devicesfound.add(device);

            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivitylayout);
        configureActionBar();
        selectFragment(HomeFragment.getNewInstace(),false,false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesfound = new ArrayList<>();

        mainhandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){

                    case 0:
                        Toast.makeText(BPMActivityController.this,(String)msg.obj,Toast.LENGTH_LONG).show();
                        break;

                    case 1:
                        createDialog("Conexión establecida. Esperando envio de la medición...");
                        progressDialog.show();
                        break;

                    case 2:
                        progressDialog.dismiss();
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

        IntentFilter filtersearch = new IntentFilter();

        filtersearch.addAction(BluetoothDevice.ACTION_FOUND);
        filtersearch.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filtersearch.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filtersearch);

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

                        onActivityResult(BLUETOOTH_ENABLE_PROCESS,RESULT_OK,null);
                    }
                }
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                createDialog("Buscando dispositivos");
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
                findAndShowBluetoothDevices();
            }else{
                showDialogBluetoothCases(BluetoothDialog.COULD_NOT_CONNECTED);
            }
        }
    }

    private void findAndShowBluetoothDevices(){

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
        startActivity(discoverableIntent);

        AlertDialog.Builder builderdevicesdialog = new AlertDialog.Builder(this);
        builderdevicesdialog.setTitle(getResources().getString(R.string.dialogdevices));
        adapterdialog =new ArrayAdapter<String>(
                this, android.R.layout.simple_selectable_list_item);


        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        builderdevicesdialog.setAdapter(adapterdialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                BluetoothDevice device = devicesfound.get(item);

//                SocconnectionSocket = bluetoothdevicename.createRfcommSocketToServiceRecord(uuid);
//                Thread connection = new AcceptThread(device);
//                connection.start();



                UUID uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

                try {
                   BluetoothSocket socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());

                    socket.connect();
                    inputStream = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                beginListenForData();


            }
        });
        AlertDialog alert = builderdevicesdialog.create();
        alert.show();

    }

    public void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    private void createDialog(String message){


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);

    }



        private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Toast.makeText(BPMActivityController.this, "Paired", Toast.LENGTH_LONG);
                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(BPMActivityController.this, "UnPaired", Toast.LENGTH_LONG);
                    }

                }
            }

        };
    private class AcceptThread extends Thread {
        private BluetoothSocket connectionSocket = null;
        private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        private BluetoothDevice device;

        public AcceptThread(BluetoothDevice bluetoothdevicename) {


            this.device = bluetoothdevicename;

            Message msg = mainhandler.obtainMessage(0);
            try {
                connectionSocket = bluetoothdevicename.createRfcommSocketToServiceRecord(uuid);
                msg.obj = getResources().getString(R.string.connectionbluetooth);
                connectionSocket.connect();
            } catch (Exception e) {
                e.printStackTrace();
                msg.obj = getResources().getString(R.string.connectionrefbluetooth);
            }
            if (msg == null) msg = new Message();

            mainhandler.sendMessage(msg);
        }

        public void run() {

            BluetoothServerSocket socket = null;
            BluetoothSocket BPMsocket = null;

            try {

                if (connectionSocket != null) {
                    bluetoothAdapter.cancelDiscovery();
                    InputStream stream = connectionSocket.getInputStream();
                    while (stream.available()==0){



                    }
//                    socket =  bluetoothAdapter.listenUsingRfcommWithServiceRecord(device.getName(),UUID.fromString("00001101-0000-1000-8000-00805f9b34fc"));
//                    //mainhandler.sendEmptyMessage(1);
//                    BPMsocket = socket.accept();
//                    InputStream stream = BPMsocket.getInputStream();
//                    String hola = "datosssss";
//                    mainhandler.sendEmptyMessage(2);
                    System.out.println("Llego");
                }

               // if (connectionSocket != null) connectionSocket.close();

                if (BPMsocket != null) BPMsocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (receiver!=null) unregisterReceiver(receiver);
        if (mReceiver != null) unregisterReceiver(mReceiver);

    }

}
