package com.udl.android.bloodpressuremonitor.backend;

import com.example.adrian.myapplication.backend.bpmApiLogin.BpmApiLogin;
import com.example.adrian.myapplication.backend.bpmApiRegister.BpmApiRegister;
import com.example.adrian.myapplication.backend.measurementApi.MeasurementApi;
import com.example.adrian.myapplication.backend.registration.Registration;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udl.android.bloodpressuremonitor.utils.Constants;

import java.io.IOException;

/**
 * Created by adrian on 2/5/15.
 */
public class BackendCalls {

    private static BackendCalls BACKEND_INSTANCE;

    private BackendCalls(){}

    public static BackendCalls getInstance(){

        if (BACKEND_INSTANCE==null){
            BACKEND_INSTANCE = new BackendCalls();
        }

        return BACKEND_INSTANCE;
    }

    public Registration buildRegistration(){

        Registration.Builder builder;
        if(Constants.LOCAL_BACKEND){
            builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.LOCAL_URL)
                    .setApplicationName(Constants.APP_NAME)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
        }else{
            builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.CLOUD_URL)
                    .setApplicationName(Constants.APP_NAME);
        }
        return builder.build();
    }

    public BpmApiLogin buildLogin(){
        BpmApiLogin.Builder builder;
        if (Constants.LOCAL_BACKEND){
            builder = new BpmApiLogin.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.LOCAL_URL)
                    .setApplicationName(Constants.APP_NAME)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
        }else{
            builder = new BpmApiLogin.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.CLOUD_URL)
                    .setApplicationName(Constants.APP_NAME);
        }


        return builder.build();
    }

    public BpmApiRegister buildRegister(GoogleAccountCredential credential){
        BpmApiRegister.Builder builder;
        if (Constants.LOCAL_BACKEND)
            builder = new BpmApiRegister.Builder(AndroidHttp.newCompatibleTransport()
                    , new AndroidJsonFactory(), credential)
                    .setRootUrl(Constants.LOCAL_URL)
                    .setApplicationName(Constants.APP_NAME)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
        else{
            builder = new BpmApiRegister.Builder(AndroidHttp.newCompatibleTransport()
                    , new AndroidJsonFactory(), credential)
                    .setRootUrl(Constants.CLOUD_URL)
                    .setApplicationName(Constants.APP_NAME);
        }

        return builder.build();
    }

    public MeasurementApi buildMeasurement(){
        MeasurementApi.Builder builder;
        if (Constants.LOCAL_BACKEND){
            builder = new MeasurementApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.LOCAL_URL)
                    .setApplicationName(Constants.APP_NAME)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
        }else{
            builder = new MeasurementApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.CLOUD_URL)
                    .setApplicationName(Constants.APP_NAME);
        }
        return builder.build();
    }
}
