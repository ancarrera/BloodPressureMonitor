package com.udl.android.bloodpressuremonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.adapters.MeasurementAdapter;
import com.udl.android.bloodpressuremonitor.model.Pressure;
import com.udl.android.bloodpressuremonitor.test.DBMock;

import java.util.List;

/**
 * Created by Adrian on 5/3/15.
 */
public class MeasurementsFragment extends Fragment {

    private ListView listview;

    public static MeasurementsFragment getNewInstance(){

        return new MeasurementsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.measurementslayout, null);
       listview = (ListView) view.findViewById(R.id.measurementlist);
       return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        List<Pressure> list = DBMock.getMeasurementsFake();
        MeasurementAdapter adapter = new MeasurementAdapter(getActivity(),list);
        listview.setAdapter(adapter);




    }


}
