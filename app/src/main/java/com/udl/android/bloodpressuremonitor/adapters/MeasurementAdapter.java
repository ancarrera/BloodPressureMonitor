package com.udl.android.bloodpressuremonitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
import com.udl.android.bloodpressuremonitor.model.Pressure;

import java.util.List;

/**
 * Created by Adrian on 5/3/15.
 */
public class MeasurementAdapter extends BaseAdapter {

    private Context context;
    private List<Pressure> list;

    public MeasurementAdapter(Context context, List<Pressure> list){

        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public static class ViewHolder{

        
    }
}
