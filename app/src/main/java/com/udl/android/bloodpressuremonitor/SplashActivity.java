package com.udl.android.bloodpressuremonitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;

/**
 * Created by Adrian on 27/02/2015.
 */
public class SplashActivity extends BPMmasterActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
}
