package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.Adrian.myapplication.backend.OfyService.*;

@Api(name = "bpmApiRegister",version = "v1",resource = "user", namespace = @ApiNamespace(
        ownerDomain = "backend.myapplication.Adrian.example.com",
        ownerName = "backend.myapplication.Adrian.example.com",
        packagePath = ""))
public class UserRegisterEndpoint {

    private static final Logger logger = Logger.getLogger(UserRegisterEndpoint.class.getName());

    @ApiMethod(name = "receive",path = "users/{id}",httpMethod = ApiMethod.HttpMethod.GET)
    public User receive(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting User with ID: " + id);
        User user = ofy().load().type(User.class).id(id).now();
        if (user == null) {
            throw new NotFoundException("not found user " + id);
        }
        return user;
    }

    @ApiMethod(name = "create",path = "users/create",httpMethod = ApiMethod.HttpMethod.POST)
    public User create(User user) {
        ofy().save().entity(user).now();
        logger.info("user created " + user.getId());
        return ofy().load().entity(user).now();
    }

    @ApiMethod(name = "update",path = "users/{id}/update",httpMethod = ApiMethod.HttpMethod.PUT)
    public User update(@Named("id") Long id, User user) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(user).now();
        logger.info("user upadate:" + user);
        return ofy().load().entity(user).now();
    }

    @ApiMethod(name = "remove",path = "users/{id}/remove",httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(User.class).id(id).now();
        logger.info("user deleted " + id);
    }

    public static void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(User.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find User with ID: " + id);
        }
    }
}