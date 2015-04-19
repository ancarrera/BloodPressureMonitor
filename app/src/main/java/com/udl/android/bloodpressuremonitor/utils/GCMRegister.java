package com.udl.android.bloodpressuremonitor.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.adrian.myapplication.backend.registration.Registration;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by adrian on 28/3/15.
 */
public class GCMRegister {

    private static GCMRegister gcmRegister;
    private static boolean isDebug = true;

    private GCMRegister(){}

    public static GCMRegister getInstance(){
        if (gcmRegister==null){

            gcmRegister = new GCMRegister();

        }
        return gcmRegister;
    }

    private static Registration registerInDebugMode(){

        Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
        .setRootUrl(Constants.LOCAL_TEST_EMULATOR_URL)
        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            @Override
            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                abstractGoogleClientRequest.setDisableGZipContent(true);
            }
        });


        return  builder.build();
    }

    private static Registration registerInRealeaseMode(){

        return new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).build();

    }

    public void executeSendRegistrationToBackend(Context context,String regid){

        Registration registration =null;
        try {
            if (isDebug)
                registration =registerInDebugMode();

            else
                registration = registerInRealeaseMode();

            if (registration!=null) {
                registration.register(regid).execute();
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
            if (isDebug)
                registration =registerInDebugMode();

            else
                registration = registerInRealeaseMode();

            if (registration!=null) {
                registration.unregister(regid).execute();
                Log.d("SEND", "Good send unregister" + regid);
            }else
                throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Error sending register regid",Toast.LENGTH_LONG);
        }
    }



}
