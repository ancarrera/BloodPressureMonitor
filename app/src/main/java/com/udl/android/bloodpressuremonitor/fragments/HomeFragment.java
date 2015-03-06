package com.udl.android.bloodpressuremonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udl.android.bloodpressuremonitor.BPMActivityController;
import com.udl.android.bloodpressuremonitor.R;

/**
 * Created by Adrian on 06/03/2015.
 */
public class HomeFragment extends Fragment {


    public static HomeFragment getNewInstace(){

        return new HomeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bloodpressuremonitorlayout, null);
        defineListeners(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);



    }


    private void defineListeners(View view){

        ImageView heart = (ImageView) view.findViewById(R.id.heartimage);
        ImageView list  = (ImageView) view.findViewById(R.id.listimage);
        ImageView profile= (ImageView) view.findViewById(R.id.prefilimage);
        ImageView help = (ImageView) view.findViewById(R.id.helpimage);
        ImageView obtainpressure = (ImageView) view.findViewById(R.id.obtainpressures);
        ImageView manaualmesure = (ImageView) view.findViewById(R.id.manualmeasurement);

        heart.setOnClickListener((BPMActivityController)getActivity());
        list.setOnClickListener((BPMActivityController)getActivity());
        profile.setOnClickListener((BPMActivityController)getActivity());
        help.setOnClickListener((BPMActivityController)getActivity());
        obtainpressure.setOnClickListener((BPMActivityController)getActivity());
        manaualmesure.setOnClickListener((BPMActivityController)getActivity());

    }

}
