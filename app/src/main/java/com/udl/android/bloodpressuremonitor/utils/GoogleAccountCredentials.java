package com.udl.android.bloodpressuremonitor.utils;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 22/4/15.
 */
public class GoogleAccountCredentials {

    private GoogleAccountCredential credential;
    private Context context;

    public static GoogleAccountCredentials getNewInstance(Context context){

        return new GoogleAccountCredentials().setContext(context);
    }

    public GoogleAccountCredential getCredentials(){

        if (credential==null) {
            credential = GoogleAccountCredential.usingAudience(context,
                    "server:client_id:" + Constants.WEB_CLIENT_ID);
        }

        return credential;
    }

    public GoogleAccountCredentials setContext(Context context){
        this.context = context;
        return this;
    }


}
