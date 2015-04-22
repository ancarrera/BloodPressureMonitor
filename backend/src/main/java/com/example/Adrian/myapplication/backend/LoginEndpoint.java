package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.Constant;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;

import java.util.logging.Logger;

import static com.example.Adrian.myapplication.backend.OfyService.*;

@Api(name = "bpmApiLogin",version = "v1",resource="login", namespace = @ApiNamespace(
        ownerDomain = "backend.myapplication.Adrian.example.com",
        ownerName = "backend.myapplication.Adrian.example.com",
        packagePath = ""))
public class LoginEndpoint {

    private static final Logger logger = Logger.getLogger(LoginEndpoint.class.getName());

    @ApiMethod(name = "checklogin", path = "login/check", httpMethod = ApiMethod.HttpMethod.POST,clientIds = {
            BackendConstants.WEB_CLIENT_ID,
            BackendConstants.ANDROID_CLIENT_ID,
            Constant.API_EXPLORER_CLIENT_ID},
            audiences = {BackendConstants.ANDROID_AUDIENCE},
            scopes = {BackendConstants.EMAIL_SCOPE})
    public User checkLogin(Login login, com.google.appengine.api.users.User authuser) throws ErrorLoginException,OAuthRequestException {
        if (authuser == null) throw new OAuthRequestException("Unauthotized user");
        logger.info("Este es el email "+login.getEmail()+" y esta la contraseña "+login.getPassword());
        User user = ofy().load().type(User.class).filter("email",login.getEmail()).filter("password",login.getPassword()).first().now();
        if (user==null)
            throw new ErrorLoginException("User not matching");

        return user;


    }
}
