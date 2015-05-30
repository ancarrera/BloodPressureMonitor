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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.udl.android.bloodpressuremonitor.BPMServerWS.WSManager;
import com.udl.android.bloodpressuremonitor.adapters.ViewPagerHelpAdapter;
import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.fragments.HearRateMonitorFragment;
import com.udl.android.bloodpressuremonitor.fragments.HelpFragment;
import com.udl.android.bloodpressuremonitor.fragments.HomeFragment;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
import com.udl.android.bloodpressuremonitor.fragments.ObtainManualPressures;
import com.udl.android.bloodpressuremonitor.fragments.ProfileFragment;
import com.udl.android.bloodpressuremonitor.utils.Constants;
import com.udl.android.bloodpressuremonitor.utils.DateUtils;
import com.udl.android.bloodpressuremonitor.utils.GCMRegister;
import com.udl.android.bloodpressuremonitor.utils.MeasurementTask;
import com.udl.android.bloodpressuremonitor.utils.PendingMeasurement;
import com.udl.android.bloodpressuremonitor.utils.PreferenceConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class BPMActivityController extends BPMmasterActivity
                         implements View.OnClickListener {

    public static enum HomeButton {

        HEART,
        LIST,
        PROFILE,
        HELP,
        BLUETOOTH
    }

    public static enum BluetoothDialog {

        NOT_SUPPORTED,
        NOT_ENABLED,
        COULD_NOT_CONNECTED,
        DISCOVER_CANCELLED,


    }

    public static enum ViewPagerType {

        BLUETOOTH_EXAMPLE,
        HEARTRATE_EXPLAIN
    }

    private final String TAG_RECEIVER_XML = "BLUETOOTH_XML";


    private final int SHOW_TOAST = 0;
    private final int PROGRESSDIALOG_WAITCONN = 1;
    private final int PROGRESSDIALOGDISSMIS_WAITCONN = 2;
    private final int BLUETOOTHDATA_RESULT = 3;
    private final int DISMISS_FAIL = 4;

    private TextView headertextview;
    private ImageButton buttonbar, secondbuttonbar;
    private NetworkStatusReceiver receiver;

    private static String networkconnectionstatus;
    public static boolean downloadAllMeasurements = true;

    private ViewPager viewPagerview;
    private FrameLayout frameLayout;

    public static final int BLUETOOTH_ENABLE_PROCESS = 288;
    public static final int SIGNAL_KILL_CONTROLLER = 111;
    private final int MAX_TIME_DISCOVERABLE = 300;
    private final int MAX_TIME_WAIT_OTHER_DEVICE = 30000;
    private final int DISCOVERABLE_BLUETOOTH_PROCESS = 333;

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

    private GoogleCloudMessaging gcm;
    private String registrationid;

    private SharedPreferences preferences;
    private Long CURRENT_USER = Constants.SESSION_USER_ID;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        managePush(intent);

        setContentView(R.layout.homeactivitylayout);
        configureActionBar();
        selectFragment(HomeFragment.getNewInstace(), false, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesfound = new ArrayList<>();

        frameLayout = (FrameLayout) findViewById(R.id.fragmentframe);
        viewPagerview = (ViewPager) findViewById(R.id.viewpager);

        mainhandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {

                    case SHOW_TOAST:
                        Toast.makeText(BPMActivityController.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                        break;

                    case PROGRESSDIALOG_WAITCONN:
                        createDialog(getResources().getString(R.string.connectionstablished));
                        progressDialog.show();
                        break;
                    case DISMISS_FAIL:
                        progressDialog.dismiss();
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(BPMActivityController.this);
                        dialog.setMessage(getResources().getString(R.string.socketconnfail));
                        dialog.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
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

                        final AlertDialog.Builder dialogresult = new AlertDialog.Builder(BPMActivityController.this);

                        dialogresult.setMessage(getResources().getString(R.string.measurements) +
                                linebreak + linebreak + getResources().getString(R.string.systolic) + " " + systolicPressure + " " + pressure_unit
                                + linebreak + getResources().getString(R.string.diastolic) + " " + diastolicPressure + " " + pressure_unit + linebreak
                                + getResources().getString(R.string.pulse) + " " + pulse + " " + pulse_unit);
                        final Measurement measurement = new Measurement();
                        measurement.setSystolic(Integer.parseInt(systolicPressure));
                        measurement.setDiastolic(Integer.parseInt(diastolicPressure));
                        measurement.setPulse(Integer.parseInt(pulse));
                        measurement.setDate(DateUtils.getCurrentDate());

                        dialogresult.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Constants.IS_EXPRESSJS_SERVER){
                                    WSManager.getInstance().sendNewMeasurement(BPMActivityController.this, measurement,
                                            MeasurementTask.checkAppLenguage(BPMActivityController.this),new WSManager.BPMCallback<String>() {
                                        @Override
                                        public void onSuccess(String response) {
                                            alertSentMeasurementCorrectly();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            alertSendMeasurement();
                                            e.printStackTrace();
                                        }
                                    });
                                }else{
                                    new MeasurementTask(BPMActivityController.this, false).execute(measurement);
                                }

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
        PendingMeasurement pendingm = new PendingMeasurement(this);
        if (pendingm.checkIfPending()) {
            pendingm.showPendingDialog();
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String option = preferences.getString(PreferenceConstants.NOTIFICATIONPREFERENCES, "");
        if (!option.equalsIgnoreCase("no"))
            registerGCM();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        managePush(intent);

    }

    @Override
    public void onStart() {
        super.onStart();

        receiver = new NetworkStatusReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onResume() {
        super.onResume();

        networkconnectionstatus = preferences.getString(PreferenceConstants.NETWORKPREFERENCES, "WiFi");
        checkStatusConnectionPreferences(this);
    }

    private void configureActionBar() {

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

                startActivityForResult(new Intent(BPMActivityController.this,
                        BPMpreferencesActivity.class), SIGNAL_KILL_CONTROLLER);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void selectFragment(Fragment fragment, boolean isBack, boolean animation) {

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


        int tag = Integer.parseInt((String) view.getTag());
        switch (tag) {
            case 1:
                HearRateMonitorFragment hearRateMonitorFragment = HearRateMonitorFragment.getNewInstance();
                selectFragment(hearRateMonitorFragment, false, true);
                headertextview.setText(getResources().getString(R.string.heartbeatheader).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 2:
                MeasurementsFragment measurementsFragment = MeasurementsFragment.getNewInstance();
                selectFragment(measurementsFragment, false, true);
                headertextview.setText(getResources().getString(R.string.measurementlistheader).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 3:
                buttonbar.setVisibility(View.INVISIBLE);
                PendingMeasurement pendingm = new PendingMeasurement(this);
                if (pendingm.checkIfPending()) {
                    pendingm.showPendingDialog();
                } else {
                    if (bluetoothAdapter == null) {
                        showDialogBluetoothCases(BluetoothDialog.NOT_SUPPORTED);
                    } else {
                        if (!bluetoothAdapter.isEnabled()) {

                            showDialogBluetoothCases(BluetoothDialog.NOT_ENABLED);
                        } else {

                            onActivityResult(BLUETOOTH_ENABLE_PROCESS, RESULT_OK, null);
                        }
                    }
                }

                secondbuttonbar.setVisibility(View.VISIBLE);
                break;
            case 4:
                ObtainManualPressures obtainManualPressures = ObtainManualPressures.getNewInstance();
                selectFragment(obtainManualPressures, false, true);
                headertextview.setText(getResources().getString(R.string.manualpressure).toUpperCase());
                buttonbar.setVisibility(View.VISIBLE);
                secondbuttonbar.setVisibility(View.INVISIBLE);
                break;
            case 5:
                ProfileFragment profileFragment = ProfileFragment.getNewInstance();
                selectFragment(profileFragment, false, true);
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
                ViewPagerHelpAdapter adapter = new ViewPagerHelpAdapter(getSupportFragmentManager(), fragments);
                viewPagerview.setAdapter(adapter);
                break;
            default:
                break;

        }

    }

    private void onBack() {


        secondbuttonbar.setVisibility(View.VISIBLE);
        buttonbar.setVisibility(View.INVISIBLE);
        Fragment lasfragment = getSupportFragmentManager().findFragmentById(R.id.fragmentframe);

        if (lasfragment instanceof HomeFragment && frameLayout.getVisibility() == View.VISIBLE) {
            showExitDialog();
        } else if (viewPagerview.getVisibility() == View.VISIBLE) {
            removeViewPager();
            headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());

        } else if (lasfragment instanceof HearRateMonitorFragment) {
            getSupportFragmentManager().beginTransaction().remove(lasfragment).commit();
            putHomeFragmentInTop(true);
        } else {
            putHomeFragmentInTop(true);

        }

        frameLayout.setVisibility(View.VISIBLE);
        viewPagerview.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void showExitDialog() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.dialogexit);
        alert.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
    public void onPause() {

        super.onPause();
    }

    private static void checkStatusConnectionPreferences(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {


            if (info.getType() == connectivityManager.TYPE_WIFI &&
                    networkconnectionstatus.equals(PreferenceConstants.WIFI_CONNECT)) {
                downloadAllMeasurements = true;
                return;
            } else if (networkconnectionstatus.equals(PreferenceConstants.ALL_CONNECT)) {
                downloadAllMeasurements = true;
                return;
            }

        }
        downloadAllMeasurements = false;


    }

    private void showDialogBluetoothCases(BluetoothDialog type) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dialogtitlebluetooth));
        if (type == BluetoothDialog.NOT_SUPPORTED) {

            alert.setMessage(R.string.nonsupportedbluetooth);
            alert.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

        } else if (type == BluetoothDialog.NOT_ENABLED) {

            alert.setMessage(R.string.non_enabledbluetooth);
            alert.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_PROCESS);


                }
            });
            alert.setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        } else if (type == BluetoothDialog.COULD_NOT_CONNECTED) {

            alert.setMessage(R.string.failenablebluetooth);
            alert.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        } else if (type == BluetoothDialog.DISCOVER_CANCELLED) {
            alert.setMessage(getResources().getString(R.string.processcanceled));
            alert.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        }

        AlertDialog dialog = alert.show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        if (requestCode == BLUETOOTH_ENABLE_PROCESS) {
            if (resultCode == RESULT_OK) {
                initBluetoothProcess();

            } else {
                showDialogBluetoothCases(BluetoothDialog.COULD_NOT_CONNECTED);
            }
        } else if (requestCode == SIGNAL_KILL_CONTROLLER) {
            String notification = "";
            if (data != null) {
                notification = data.getExtras().getString("notifications", "");
            }
            if (!notification.equals("")) {
                if (notification.equals("yes")) {
                    registerGCM();
                } else if (notification.equals("no")) {
                    unregisterGCM();
                }
            } else if (resultCode == RESULT_OK) {
                finish();
            }

        } else if (requestCode == DISCOVERABLE_BLUETOOTH_PROCESS) {
            if (resultCode == RESULT_CANCELED) {
                showDialogBluetoothCases(BluetoothDialog.DISCOVER_CANCELLED);
            } else {
                bluetoothProcess();
            }

        }
    }

    private void initBluetoothProcess() {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX_TIME_DISCOVERABLE);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BLUETOOTH_PROCESS);
    }


    private void createDialog(String message) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
        } else {
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
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BPMRFCOMM",
                        UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
                socket = serverSocket.accept(MAX_TIME_WAIT_OTHER_DEVICE);
                mainhandler.sendEmptyMessage(PROGRESSDIALOGDISSMIS_WAITCONN);
                if (socket != null) {
                    mainhandler.sendEmptyMessage(PROGRESSDIALOG_WAITCONN);
                    InputStream stream = socket.getInputStream();
                    while (stream.available() <= 0) {
                    }
                    if (stream.available() > 0) {
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
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);

    }

    private void xmlParsePressures(InputStream stream) {

        try {


            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setInput(new InputStreamReader(stream));

            int eventType = parser.getEventType();
            String element = "";
            boolean END_DOCUMENT = false;
            while (!END_DOCUMENT) {


                if (eventType == XmlPullParser.START_TAG) {
                    element = parser.getName();
                } else if (eventType == XmlPullParser.TEXT) {

                    if (element.equals("systolic-pressure")) {
                        systolicPressure = parser.getText();
                        //Log.d("XML","ParseNext "+systolicPressure);
                    }
                    if (element.equals("diastolic-pressure")) {
                        diastolicPressure = parser.getText();
                        //Log.d("XML","ParseNext "+diastolicPressure);
                    }
                    if (element.equals("pulse")) {
                        pulse = parser.getText();
                        END_DOCUMENT = true;
                        //Log.d("XML","ParseNext "+pulse);
                    }
                }
                eventType = parser.next();
            }

            Message message = new Message();
            message.what = BLUETOOTHDATA_RESULT;
            mainhandler.sendMessage(message);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void putHomeFragmentInTop(boolean back) {
        headertextview.setText(getResources().getString(R.string.principaltext).toUpperCase());
        selectFragment(HomeFragment.getNewInstace(), back, true);
    }

    private void removeViewPager() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.viewpager);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private void bluetoothProcess() {

        createDialog(getResources().getString(R.string.waitconnection));
        progressDialog.show();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        Thread thread = new AcceptThread();
        thread.start();
    }

    private boolean checkGooglePlayServicesAvailable() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,
                        this, Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(Constants.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;

    }

    private String getRegID(Context appcontext) {
        final SharedPreferences preferences = getSharedPreferences(
                PreferenceConstants.GCM_PREFERENCES, Context.MODE_PRIVATE);
        String regid = preferences.getString(Constants.SESSION_USER_ID + "", "");
        if (regid.equals("")) {

            return "";
        }


        int appversion = preferences.getInt(PreferenceConstants.APP_VERSION, 1);
        if (appversion != getAppVersion(appcontext)) {
            return "";
        }

        return regid;

    }

    private String removeRegID(Context context) {
        SharedPreferences preferences = getSharedPreferences(
                PreferenceConstants.GCM_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.SESSION_USER_ID + "", "").commit();
        return "";
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException("Package name not found");
        }
    }

    private void storeNewRegId(Context context, String newregid) {
        final SharedPreferences preferences = context.getSharedPreferences(
                PreferenceConstants.GCM_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.SESSION_USER_ID + "", newregid);
        editor.putInt(PreferenceConstants.APP_VERSION, getAppVersion(context));
        editor.commit();
    }

    private class registerTask extends AsyncTask<Void, Void, String> {

        @Override
        public String doInBackground(Void... params) {

            String regid = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(BPMActivityController.this);
                }
                regid = gcm.register(Constants.SENDER_ID);

                GCMRegister.getInstance()
                        .executeSendRegistrationToBackend(BPMActivityController.this, regid);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return regid;
        }

        @Override
        public void onPostExecute(String result) {
            String msg;
            if (result.isEmpty()) {
                msg = "Registration not found";
            } else {
                msg = result;
                storeNewRegId(BPMActivityController.this, result);
            }

            Toast.makeText(BPMActivityController.this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private class unregisterTask extends AsyncTask<Void, Void, String> {

        @Override
        public String doInBackground(Void... params) {

            String regid = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(BPMActivityController.this);
                }
                gcm.unregister();
                regid = getRegID(BPMActivityController.this);
                registrationid = removeRegID(BPMActivityController.this);
                GCMRegister.getInstance()
                        .executeSendUnRegistrationToBackend(BPMActivityController.this, regid);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private void registerGCM() {

        if (checkGooglePlayServicesAvailable()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            registrationid = getRegID(getApplicationContext());
            if (registrationid.isEmpty()) {
                if (Constants.IS_EXPRESSJS_SERVER) {
                    new registerTaskBPMServer().execute(true);

                }else{
                    new registerTask().execute();
                }
            } else {
                if (Constants.IS_EXPRESSJS_SERVER){
                    new registerTaskBPMServer().execute(false);
                }
                Toast.makeText(this, getResources().getString(R.string.recoveryregidOK), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(Constants.TAG, "Google Cloud Services not found");
        }
    }

    private void unregisterGCM() {

        if (checkGooglePlayServicesAvailable()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            if (Constants.IS_EXPRESSJS_SERVER){
                new unregisterTaskBPMServer().execute();
            }else{
                new unregisterTask().execute();
            }
            Toast.makeText(this, getResources().getString(R.string.unregister), Toast.LENGTH_LONG).show();
        } else {
            Log.d(Constants.TAG, "Google Cloud Services not found");
        }
    }

    private void showPushAlertDialog(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setMessage(msg);
        dialog.show();
    }

    private void managePush(Intent intent) {
        if (intent != null)
            if (intent.hasExtra("message")) {
                String msg = intent.getStringExtra("message");
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                if (fragmentManager.findFragmentById(R.id.fragmentframe) != null) {
                    fragmentManager.beginTransaction().remove(
                            fragmentManager.findFragmentById(R.id.fragmentframe)).commit();
                    putHomeFragmentInTop(false);
                }
                showPushAlertDialog(msg);
            }
    }

    private class unregisterTaskBPMServer extends AsyncTask<Void, Void, String> {

        @Override
        public String doInBackground(Void... params) {


            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(BPMActivityController.this);
                }
                gcm.unregister();
                registrationid = removeRegID(BPMActivityController.this);
                WSManager.getInstance().removeGCMToken(BPMActivityController.this, new WSManager.BPMCallback<String>() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onError(Exception e) {
                        tokenDialog(getResources().getString(R.string.token_error_delete));
                    }
                });

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class registerTaskBPMServer extends AsyncTask<Boolean, Void, String> {
            private String regid;
        @Override
        public String doInBackground(Boolean... params) {
            try {
                if (params[0]){
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(BPMActivityController.this);
                    }
                    regid = gcm.register(Constants.SENDER_ID);
                }else{
                    regid = getRegID(BPMActivityController.this);
                }


                WSManager.getInstance().sendGCMToken(BPMActivityController.this, regid, new WSManager.BPMCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        String msg;
                        if (response.equalsIgnoreCase("OK")) {
                            msg = regid;
                            storeNewRegId(BPMActivityController.this, regid);

                        } else {
                            msg = "Registration not found";
                        }


                        Toast.makeText(BPMActivityController.this, msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        tokenDialog(getResources().getString(R.string.token_error));
                    }
                });

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private void tokenDialog(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setMessage(msg);
        dialog.show();
    }

    private void alertSendMeasurement(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setMessage(getResources().getString(R.string.error_sending_measurement));
        dialog.show();
    }


    private void alertSentMeasurementCorrectly(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setMessage(getResources().getString(R.string.measurement_send));
        dialog.show();
    }

}