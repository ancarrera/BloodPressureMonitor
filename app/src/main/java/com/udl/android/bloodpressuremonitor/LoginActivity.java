package com.udl.android.bloodpressuremonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;

/**
 * Created by Adrian on 27/02/2015.
 */
public class LoginActivity extends BPMmasterActivity {



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivitylayout);

        final Button regbutton = (Button)findViewById(R.id.registerbutton);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        final Button logbutton = (Button)findViewById(R.id.loginenviar);
        logbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,BPMActivityController.class));
            }
        });
    }
}
