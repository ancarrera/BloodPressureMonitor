package com.example.Adrian.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 1/4/15.
 */
@Entity
public class User {

    @Id
    Long id;
    private String name;
    private String firstsurname;
    private String secondsurname;
    private String age;
    @Index
    private String email;
    private String city;
    private String administration;
    private String country;
    private int totalinsertions;
    private List<Measurement> measurementList;
    @Index
    private String password;

    public User(){
        this.measurementList = new ArrayList<>();
        totalinsertions = 0;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdministration() {
        return administration;
    }

    public void setAdministration(String administration) {
        this.administration = administration;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Measurement> getMeasurementList() {
        return measurementList;
    }

    public void addMeasurement(Measurement measurement){
            measurementList.add(measurement);
    }
    public Measurement getMeasurementAtIndex(int index){
        return measurementList.get(index);
    }
    public void setMeasurementList(List<Measurement> measurementList) {
        this.measurementList = measurementList;
    }

    public int getTotalinsertions() {
        return totalinsertions;
    }

    public void sumOneInsertion() {
        totalinsertions++;
    }
}
