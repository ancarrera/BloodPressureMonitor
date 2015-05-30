package com.example.Adrian.myapplication.backend;

import com.google.android.gcm.server.Constants;
import com.google.api.server.spi.Constant;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.Adrian.myapplication.backend.OfyService.*;

@Api(name = "bpmApiRegister",version = "v1",resource = "users", namespace = @ApiNamespace(
        ownerDomain = "backend.myapplication.Adrian.example.com",
        ownerName = "backend.myapplication.Adrian.example.com",
        packagePath = "" ))

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

    @ApiMethod(name="listUsers",path="users",httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<User> listUsers(@Named("count")int count) {
        List<User> records = ofy().load().type(User.class).limit(count).list();
        return CollectionResponse.<User>builder().setItems(records).build();
    }

    @ApiMethod(name = "create",httpMethod = ApiMethod.HttpMethod.POST)
//            scopes = {BackendConstants.EMAIL_SCOPE},
//            clientIds = {BackendConstants.WEB_CLIENT_ID,
//                    BackendConstants.ANDROID__DEBUG_CLIENT_ID,
//                    BackendConstants.ANDROID__APK_CLIENT_ID,
//                    Constant.API_EXPLORER_CLIENT_ID},
//            audiences = {BackendConstants.ANDROID_AUDIENCE})

    public User create(User user/*, com.google.appengine.api.users.User userAuth*/) throws OAuthRequestException, NotFoundException, UserAlreadyExistException {
        //if (userAuth==null) throw new OAuthRequestException("User unauthorized");
        checkExists(null,user.getEmail(),1);
        ofy().save().entity(user).now();
        logger.info("user created " + user.getId());
        return ofy().load().entity(user).now();
    }

    @ApiMethod(name = "update",path = "users/{id}",httpMethod = ApiMethod.HttpMethod.PUT)
    public User update(@Named("id") Long id, User user) throws NotFoundException, UserAlreadyExistException {
        checkExists(id, "udpdate",2);
        ofy().save().entity(user).now();
        logger.info("user upadate:" + user);
        return ofy().load().entity(user).now();
    }

    @ApiMethod(name = "remove",path = "users/{id}",httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException, UserAlreadyExistException {
        checkExists(id,"remove",2);
        ofy().delete().type(User.class).id(id).now();
        logger.info("user deleted " + id);
    }

    private static void checkExists(Long id,String arg,int code) throws UserAlreadyExistException, NotFoundException {
        if (code == 1){
            User user =  ofy().load().type(User.class).filter("email",arg).first().now();
            if (user != null)throw new UserAlreadyExistException("User with mail "+arg+" already exists. Conflict.");
        }else if (code==2){
            User user =  ofy().load().type(User.class).id(id).now();
            if (code==2 && user==null) throw new NotFoundException("User not found.It can not "+arg);
        }


    }
}