package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.ServiceException;

/**
 * Created by adrian on 2/5/15.
 */
public class UserAlreadyExistException extends ServiceException {
    public UserAlreadyExistException(String statusMessage) {
        super(409, statusMessage);
    }
}

