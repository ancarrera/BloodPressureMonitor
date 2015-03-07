package com.udl.android.bloodpressuremonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udl.android.bloodpressuremonitor.R;

/**
 * Created by Adrian on 07/03/2015.
 */
public class ObtainManualPressures extends Fragment {

    public static ObtainManualPressures getNewInstance() {

        return new ObtainManualPressures();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bloodpressuremonitorlayout, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}