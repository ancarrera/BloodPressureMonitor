package com.example.Adrian.myapplication.backend;

import com.googlecode.objectify.annotation.Id;

/**
 * Created by Adrian on 17/4/15.
 */
public class Measurement {

    @Id
    Long id;

    private int systolic;
    private int diastolic;
    private int pulse;

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }
}
