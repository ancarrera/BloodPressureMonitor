package com.udl.android.bloodpressuremonitor.datastore;

import android.content.Context;

import com.example.adrian.myapplication.backend.bpmApiLogin.model.User;

/**
 * Created by Adrian on 28/5/15.
 */
public class DataStore {

    private static DataStore SINGLETON_INSTANCE;

    private DataStore(){}
    private User currentUser;

    public static DataStore getInstance(){

        if (SINGLETON_INSTANCE == null){
            SINGLETON_INSTANCE = new DataStore();
        }

        return SINGLETON_INSTANCE;
    }

    public void setUser(User user){
       this.currentUser = user;
    }

    public User getUser(){
        return currentUser;
    }

}
