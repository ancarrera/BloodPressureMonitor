package com.udl.android.bloodpressuremonitor;

import android.os.Bundle;
import android.view.View;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;


public class BPMActivity extends BPMmasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodpressuremonitorlayout);
        configureActionBar();
    }

    private void configureActionBar(){
        configureBaseActionBar();
    }

}
