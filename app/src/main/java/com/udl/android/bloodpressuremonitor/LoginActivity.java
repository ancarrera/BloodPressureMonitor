package com.udl.android.bloodpressuremonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;

import java.util.IllegalFormatCodePointException;

/**
 * Created by Adrian on 27/02/2015.
 */
public class LoginActivity extends BPMmasterActivity {

    public static final int LOGIN_KILL=033;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivitylayout);

        final Button regbutton = (Button)findViewById(R.id.registerbutton);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), LOGIN_KILL);
            }
        });
        final Button logbutton = (Button)findViewById(R.id.loginenviar);
        logbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, BPMActivityController.class));
                finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==LOGIN_KILL){
            if (resultCode==RESULT_OK){
                finish();
            }
        }
    }
}
