package com.udl.android.bloodpressuremonitor.model;

/**
 * Created by Adrian on 07/03/2015.
 */
public class User {

    public static String UUID;

    public static User instance;

    private String name;
    private String firstsurname;
    private String secondsurname;
    private String age;
    private String habitualresidence;

    public static User getInstace(){

        if (instance == null){
            instance = new User();
        }
        return instance;
    }

    public static String getUUID() {
        return UUID;
    }

    public static void setUUID(String UUID) {
        User.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstsurname() {
        return firstsurname;
    }

    public void setFirstsurname(String firstsurname) {
        this.firstsurname = firstsurname;
    }

    public String getSecondsurname() {
        return secondsurname;
    }

    public void setSecondsurname(String secondsurname) {
        this.secondsurname = secondsurname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHabitualresidence() {
        return habitualresidence;
    }

    public void setHabitualresidence(String habitualresidence) {
        this.habitualresidence = habitualresidence;
    }
}
