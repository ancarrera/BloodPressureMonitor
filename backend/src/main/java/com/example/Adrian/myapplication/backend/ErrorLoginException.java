package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.ServiceException;

/**
 * Created by Adrian on 2/4/15.
 */
public class ErrorLoginException extends ServiceException {
    public ErrorLoginException(String statusMessage) {
        super(404, statusMessage);
    }
}
