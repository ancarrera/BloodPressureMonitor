package com.udl.android.bloodpressuremonitor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.fragments.MeasurementsFragment;
import com.udl.android.bloodpressuremonitor.model.Pressure;
import com.udl.android.bloodpressuremonitor.utils.DateUtils;

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
    public Pressure getItem(int position) {
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

        Pressure pressure = getItem(position);

        holder.date.setText(DateUtils.dateToString(pressure.getDate(),DateUtils.DATEFORMAT));
        holder.pulse.setText(pressure.getPulse());
        holder.systolic.setText(pressure.getSystolic());
        holder.diastolic.setText(pressure.getDiastolic());

        return convertView;
    }



    public static class ViewHolder{
        TextView date;
        TextView systolic;
        TextView diastolic;
        TextView pulse;
    }
}
