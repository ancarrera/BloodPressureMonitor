package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import static com.example.Adrian.myapplication.backend.OfyService.*;

@Api(name = "bpmApiLogin",version = "v1",resource="login", namespace = @ApiNamespace(
        ownerDomain = "backend.myapplication.Adrian.example.com",
        ownerName = "backend.myapplication.Adrian.example.com",
        packagePath = ""))
public class LoginEndpoint {

    private static final Logger logger = Logger.getLogger(LoginEndpoint.class.getName());

    @ApiMethod(name = "checklogin", path = "login/check", httpMethod = ApiMethod.HttpMethod.POST)
    public User checkLogin(Login login) throws ErrorLoginException {

        logger.info("Este es el email "+login.getEmail()+" y esta la contraseña "+login.getPassword());
        User user = ofy().load().type(User.class).filter("email",login.getEmail()).filter("password",login.getPassword()).first().now();
        if (user==null)
            throw new ErrorLoginException("User not matching");

        return user;


    }
}
