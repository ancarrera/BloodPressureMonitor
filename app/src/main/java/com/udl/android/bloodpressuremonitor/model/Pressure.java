package com.udl.android.bloodpressuremonitor.model;

import java.util.Date;

/**
 * Created by Adrian on 5/3/15.
 */
public class Pressure {

    private String systolic;
    private String diastolic;
    private String pulse;
    private Date date;


    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
