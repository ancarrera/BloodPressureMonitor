package com.udl.android.bloodpressuremonitor.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adrian.myapplication.backend.bpmApiRegister.BpmApiRegister;
import com.example.adrian.myapplication.backend.bpmApiRegister.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udl.android.bloodpressuremonitor.BPMActivityController;
import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.utils.Constants;

import java.io.IOException;

/**
 * Created by adrian on 20/3/15.
 */
public class ProfileFragment extends Fragment {

    private TextView name,surname,age,city,administration,country;
    private BPMActivityController context;

    public static ProfileFragment getNewInstance(){

        ProfileFragment profileFragment =new ProfileFragment();
        return profileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.perfillayout, null);
        name = (TextView) view.findViewById(R.id.nametextview);
        surname = (TextView) view.findViewById(R.id.surnametextview);
        age = (TextView) view.findViewById(R.id.agetextview);
        city = (TextView) view.findViewById(R.id.citytextview);
        administration = (TextView) view.findViewById(R.id.administrationtextview);
        country = (TextView)view.findViewById(R.id.countrytextview);
        context = (BPMActivityController)getActivity();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        new getUserProfile().execute();
    }

    private class getUserProfile extends AsyncTask<Void,Void,User>{

        @Override
        public void onPreExecute(){
            context.showDialog(false);
        }

        @Override
        protected User doInBackground(Void... params) {

            BpmApiRegister.Builder builder = new BpmApiRegister.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(Constants.CLOUD_URL)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            try {
                return builder.build().receive(Constants.SESSION_USER_ID).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(User user){
            context.dialogDismiss();
            if (user!=null){
                name.setText(user.getName());
                surname.setText(user.getFirstsurname()+" "+user.getSecondsurname());
                age.setText(user.getAge());
                city.setText(user.getCity());
                administration.setText(user.getAdministration());
                country.setText(user.getCountry());

            }else{
                showDialogEvents();
            }
        }
    }
    private void showDialogEvents(){

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.erroruserprofile));
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
}
