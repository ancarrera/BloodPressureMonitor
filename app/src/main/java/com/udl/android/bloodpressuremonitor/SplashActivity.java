package com.udl.android.bloodpressuremonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.utils.Language;
import com.udl.android.bloodpressuremonitor.utils.PreferenceConstants;

/**
 * Created by Adrian on 27/02/2015.
 */
public class SplashActivity extends BPMmasterActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Log.d("Lan", Language.checkAppLanguage());
//        Log.d("Lan", Language.getAppCompleteStringLanguage(true));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString(PreferenceConstants.LANGUAGEPREFERENCESKEY,"null");
       if (lang.equals("null")){ //is first time that app is running
            Log.d("lANG",Language.checkAppLanguage());
           SharedPreferences.Editor editor = preferences.edit();
           editor.putString(PreferenceConstants.LANGUAGEPREFERENCESKEY,Language.checkAppLanguage());
           editor.commit();

       }else{
            Language.changeApplicationLanguage(lang,this);
       }
        setContentView(R.layout.splashlayout);
        TextView textView = (TextView) findViewById(R.id.splashtextview);
        textView.setTypeface(getOpenSansItalic());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this,BPMActivityController.class));
                finish();
            }
        },1500);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration){
        super.onConfigurationChanged(configuration);
    }



}
