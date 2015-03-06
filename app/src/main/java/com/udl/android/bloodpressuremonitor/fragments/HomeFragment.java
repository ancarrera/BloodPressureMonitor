package com.udl.android.bloodpressuremonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.udl.android.bloodpressuremonitor.R;

/**
 * Created by Adrian on 06/03/2015.
 */
public class HomeFragment extends Fragment {

    public static MeasurementsFragment getNewInstance(){

        return new MeasurementsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bloodpressuremonitorlayout, null);
        return view;
    }

}
