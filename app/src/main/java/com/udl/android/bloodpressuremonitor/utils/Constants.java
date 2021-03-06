package com.udl.android.bloodpressuremonitor.utils;

/**
 * Created by Adrian on 28/3/15.
 */
public class Constants {

    public static final String EXTRA_MESSAGE = "message";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String SENDER_ID = "1029013275941";
    public static final String TAG ="GCM_DEBUG";
    public static final int NOTIFICATION= 1;
    public static Long SESSION_USER_ID;
    public static String SESSION_MD5_TOKEN; //user pass in MD5
    //GCM
    public static final String APP_NAME = "BPM";
    public static final boolean LOCAL_BACKEND = false;
    public static final String LOCAL_URL ="http://10.0.2.2:8080/_ah/api/";
    //public static final String LOCAL_URL ="http://192.168.168.25:8080/_ah/api/";
    public static final String CLOUD_URL ="https://metal-center-92523.appspot.com/_ah/api/";
    public static final String WEB_CLIENT_ID ="1029013275941-cttvi7im3r49r8l435dgviko82crnrv9.apps.googleusercontent.com";

    //BPMServer
    public static final boolean IS_EXPRESSJS_SERVER = true;
    public static final String URL_BASE = "http://10.0.2.2:8089/";

}
