package com.udl.android.bloodpressuremonitor.test;

import com.udl.android.bloodpressuremonitor.model.Pressure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Adrian on 05/03/2015.
 */
public class DBMock {

    public static List<Pressure> getMeasurementsFake(){

        List<Pressure> list = new ArrayList<>();
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        final int randommax = 40;
        calendar.add(Calendar.DAY_OF_MONTH,-100);
        Pressure pressure;

        for (int i=0;i<randommax;i++){

            pressure = new Pressure();
            pressure.setSystolic(String.valueOf(random.nextInt(220)));
            pressure.setDiastolic(String.valueOf(random.nextInt(220)));
            pressure.setPulse(String.valueOf(random.nextInt(220)));
            pressure.setDate(calendar.getTime());
            list.add(pressure);
            calendar.add(Calendar.DAY_OF_MONTH,1);

        }

        Collections.sort(list, new Comparator<Pressure>() {
            @Override
            public int compare(Pressure lhs, Pressure rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });
        return list;
    }
}
