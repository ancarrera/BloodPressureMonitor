package com.udl.android.bloodpressuremonitor.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.R;

/**
 * Created by adrian on 20/3/15.
 */
public class ProfileFragment extends Fragment {

    private TextView name,surname,age,city,administration,country;

    public static ProfileFragment getNewInstance(){

        ProfileFragment profileFragment =new ProfileFragment();
        return profileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.perfillayout, null);
        name = (TextView) view.findViewById(R.id.nametextview);
        surname = (TextView) view.findViewById(R.id.surnametextview);
        age = (TextView) view.findViewById(R.id.agetextview);
        city = (TextView) view.findViewById(R.id.citytextview);
        administration = (TextView) view.findViewById(R.id.administrationtextview);
        country = (TextView)view.findViewById(R.id.countrytextview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //WS
    }
}
