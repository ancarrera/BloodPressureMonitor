package com.udl.android.bloodpressuremonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.interfaces.HomeButtonsListener;


public class BPMActivity extends BPMmasterActivity
                         implements HomeButtonsListener {

    public static enum HomeButton{

        HEART,
        LIST,
        PROFILE,
        HELP,
        BLUETOOTH
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodpressuremonitorlayout);
        configureBaseActionBar();
        defineListeners();
    }


    private void defineListeners(){

        ImageView heart = (ImageView) findViewById(R.id.heartimage);
        ImageView list  = (ImageView) findViewById(R.id.listimage);
        ImageView perfil = (ImageView) findViewById(R.id.prefilimage);
        ImageView help = (ImageView) findViewById(R.id.helpimage);

    }

    private void configureActionBar(){
        configureBaseActionBar();
    }

    @Override
    public void OnHomeButtonClick(View view) {
        int tag = Integer.parseInt((String)view.getTag());
       switch (tag){

           case 1:
               System.out.println("TAG ES 1");
               break;
           case 2:
               System.out.println("TAG ES 2");
               break;
           case 3:
               System.out.println("TAG ES 3");
               break;
           case 4:
               System.out.println("TAG ES 4");
               break;
           default:
               break;

       }

    }
}
