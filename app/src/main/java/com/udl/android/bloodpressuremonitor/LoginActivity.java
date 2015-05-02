package com.udl.android.bloodpressuremonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adrian.myapplication.backend.bpmApiLogin.model.Login;
import com.example.adrian.myapplication.backend.bpmApiLogin.BpmApiLogin;
import com.example.adrian.myapplication.backend.bpmApiLogin.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udl.android.bloodpressuremonitor.application.BPMmasterActivity;
import com.udl.android.bloodpressuremonitor.backend.BackendCalls;
import com.udl.android.bloodpressuremonitor.utils.Constants;

import java.io.IOException;

/**
 * Created by Adrian on 27/02/2015.
 */
public class LoginActivity extends BPMmasterActivity {

    public static final int LOGIN_KILL=033;
    private TextView emailtextview;
    private TextView passwordtextview;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivitylayout);

        emailtextview = (TextView) findViewById(R.id.login_mail);
        passwordtextview = (TextView) findViewById(R.id.login_passwd);

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
                if(!emailtextview.getText().toString().equals("")
                        && !passwordtextview.getText().toString().equals("")
                        && emailIsCorrect()) {
                    Login login = new Login();
                    login.setEmail(emailtextview.getText().toString());
                    login.setPassword(passwordtextview.getText().toString());
                    new checkLoginData().execute(login);
                }else{
                    showDialogEvents();
                }
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

    private void showDialogEvents(){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.dialogincorrectfields));
        alert.setPositiveButton(getResources().getString(R.string.dialogpositivbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setMessage(getResources().getString(R.string.loginincorrect));
        AlertDialog center = alert.show();
        TextView messageText = (TextView)center.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        center.show();

    }

    private class checkLoginData extends AsyncTask<Login,Void,User>{

        @Override
        public void onPreExecute(){
            showDialog(false);
        }

        @Override
        public User doInBackground(Login... param){

            BpmApiLogin loginapi = BackendCalls.getInstance().buildLogin();

            try {
                return loginapi.checklogin(param[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onPostExecute(User user){
            dialogDismiss();
            if (user!=null){
                Constants.SESSION_USER_ID = user.getId();
                startActivity(new Intent(LoginActivity.this, BPMActivityController.class));
                finish();
            }else{
                showLoginError();
            }
        }

    }

    private boolean emailIsCorrect() {
        if (emailtextview.getText().toString().contains("@") && emailtextview.getText().toString().contains(".")) {

            return true;
        }

        return false;
    }
    private void showLoginError(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .setMessage(getResources().getString(R.string.userloginerror));
        builder.show();

    }
}
