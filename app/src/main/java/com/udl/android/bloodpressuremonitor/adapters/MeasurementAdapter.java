package com.udl.android.bloodpressuremonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.udl.android.bloodpressuremonitor.R;

import java.util.List;

/**
 * Created by Adrian on 5/3/15.
 */
public class MeasurementAdapter extends BaseAdapter {

    private Context context;
    private List<Measurement> list;

    public MeasurementAdapter(Context context, List<Measurement> list){

        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Measurement getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null){

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.measurementcell,null);
            holder.date = (TextView) convertView.findViewById(R.id.datecell);
            holder.systolic = (TextView) convertView.findViewById(R.id.systoliccell);
            holder.diastolic = (TextView) convertView.findViewById(R.id.diastoliccell);
            holder.pulse = (TextView) convertView.findViewById(R.id.pulsecell);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        Measurement pressures = getItem(position);

        holder.date.setText(pressures.getDate());
        holder.pulse.setText(""+pressures.getPulse());
        holder.systolic.setText(""+pressures.getSystolic());
        holder.diastolic.setText(""+pressures.getDiastolic());

        return convertView;
    }



    public static class ViewHolder{
        TextView date;
        TextView systolic;
        TextView diastolic;
        TextView pulse;
    }
}
