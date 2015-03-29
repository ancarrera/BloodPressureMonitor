package com.example.Adrian.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by adrian on 29/3/15.
 */
@Entity
public class Greet {

    @Id
    Long id;

    private String message;

    public Greet() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
