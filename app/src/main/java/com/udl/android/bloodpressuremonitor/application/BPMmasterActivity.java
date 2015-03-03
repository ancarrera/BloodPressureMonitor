package com.udl.android.bloodpressuremonitor.application;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by Adrian on 27/02/2015.
 */
public class BPMmasterActivity extends FragmentActivity {

    private ProgressDialog dialog;
    private Typeface opensansregular;
    private Typeface opensansbold;
    private Typeface opensansitalic;
    private Typeface mntcorsiva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        opensansregular = Typeface.createFromAsset(getAssets(),"OpenSans-Regular.ttf");
        opensansbold = Typeface.createFromAsset(getAssets(),"OpenSans-Bold.ttf");
        opensansitalic = Typeface.createFromAsset(getAssets(),"OpenSans-Italic.ttf");
        mntcorsiva = Typeface.createFromAsset(getAssets(),"mtcorsive.ttf");
        getActionBar().hide();
    }

    @Override
    public BPMmasterActivity getApplicationContext(){
        return (BPMmasterActivity) super.getApplicationContext();
    }

    public void showDialog(boolean isCancelable){

        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }

        dialog = new ProgressDialog(this);
        dialog.setCancelable(isCancelable);
        dialog.show();
    }

    public void dialogDismiss(){
        if (dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
    }

    protected void configureBaseActionBar(){
        getActionBar().show();
    }

    protected View getActionBarView(){

        return getActionBar().getCustomView();
    }

    protected Typeface getOpenSansRegular(){

        return opensansregular;
    }

    protected Typeface getOpenSansBold(){
        return opensansbold;
    }

    protected Typeface getOpenSansItalic(){
        return opensansitalic;
    }

    protected Typeface getMntcorsiva(){return mntcorsiva;}

}
