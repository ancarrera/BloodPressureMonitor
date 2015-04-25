package com.udl.android.bloodpressuremonitor.test;

import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.udl.android.bloodpressuremonitor.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Adrian on 05/03/2015.
 */
public class DBMock {

    public static List<Measurement> getMeasurementsFake(){

        List<Measurement> list = new ArrayList<>();
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        final int max = 40;
        calendar.add(Calendar.DAY_OF_MONTH,-max);
        Measurement pressures;

        for (int i=0;i<max;i++){

            pressures = new Measurement();
            pressures.setSystolic(random.nextInt(220));
            pressures.setDiastolic(random.nextInt(220));
            pressures.setPulse(random.nextInt(220));
            pressures.setDate(DateUtils.dateToString(calendar.getTime(),DateUtils.DATEFORMAT));
            list.add(pressures);
            calendar.add(Calendar.DAY_OF_MONTH,1);

        }

        Collections.sort(list, new Comparator<Measurement>() {
            @Override
            public int compare(Measurement lhs, Measurement rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });
        return list;
    }
}
