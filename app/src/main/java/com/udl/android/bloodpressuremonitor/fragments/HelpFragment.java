package com.udl.android.bloodpressuremonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.BPMActivityController;
import com.udl.android.bloodpressuremonitor.R;

/**
 * Created by adrian on 20/3/15.
 */
public class HelpFragment extends Fragment {

    private BPMActivityController.ViewPagerType type;
    private TextView titlepage;
    private TextView content;


    public static HelpFragment getNewInstance(BPMActivityController.ViewPagerType type){

        HelpFragment helpFragment = new HelpFragment().setType(type);

        return helpFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.helplayout, null);
        titlepage = (TextView) view.findViewById(R.id.pagetitle);
        content = (TextView) view.findViewById(R.id.contentpage);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
       
       managePagesCases();

    }

    private void managePagesCases(){
        switch (type){

            case BLUETOOTH_EXAMPLE:
                titlepage.setText(getResources().getString(R.string.bluetoothinfoheader));
                content.setText(getResources().getString(R.string.bluetoothinfo));
                break;
            case HEARTRATE_EXPLAIN:
                titlepage.setText(getResources().getString(R.string.heartrate));
                content.setText(getResources().getString(R.string.heartrateinfo));
                break;
        }
    }


    public HelpFragment setType(BPMActivityController.ViewPagerType type){

        this.type = type;
        
        return this;
    }

}
