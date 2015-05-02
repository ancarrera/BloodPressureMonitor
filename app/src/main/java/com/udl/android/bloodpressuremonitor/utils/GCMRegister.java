package com.udl.android.bloodpressuremonitor.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.adrian.myapplication.backend.registration.Registration;
import com.udl.android.bloodpressuremonitor.backend.BackendCalls;

import java.io.IOException;

/**
 * Created by adrian on 28/3/15.
 */
public class GCMRegister {

    private static GCMRegister gcmRegister;

    private GCMRegister(){}

    public static GCMRegister getInstance(){
        if (gcmRegister==null){

            gcmRegister = new GCMRegister();

        }
        return gcmRegister;
    }

    public void executeSendRegistrationToBackend(Context context,String regid){

        Registration registration =null;
        try {
            registration = BackendCalls.getInstance().buildRegistration();

            if (registration!=null) {
                registration.register(Constants.SESSION_USER_ID,regid).execute();
                Log.d("SEND", "Good send" + regid);
            }else
                throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Error sending register regid",Toast.LENGTH_LONG);
        }
    }

    public void executeSendUnRegistrationToBackend(Context context,String regid){

        Registration registration =null;
        try {
            registration = BackendCalls.getInstance().buildRegistration();
            if (registration!=null) {
                registration.unregister(Constants.SESSION_USER_ID,regid).execute();
                Log.d("SEND", "Good send unregister" + regid);
            }else
                throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Error sending register regid",Toast.LENGTH_LONG);
        }
    }

}
