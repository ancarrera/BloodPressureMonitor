package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;


import static com.example.Adrian.myapplication.backend.OfyService.*;

import java.util.logging.Logger;

/**
 * Created by adrian on 29/3/15.
 */
@Api(name="helloapi",version="v1",namespace =@ApiNamespace(ownerDomain = "com.example.Adrian.myapplication.backend",
        ownerName = "com.example.Adrian.myapplication.backend"))
public class HelloEndpoint {

    public static Logger logger = Logger.getLogger(HelloEndpoint.class.getName());

    @ApiMethod(name = "greet")
    public void greet(@Named("greet") String greet){

        Greet greetEntity = new Greet();
        greetEntity.setMessage(greet);
        logger.info("Greet save with message"+greetEntity.getMessage());
        ofy().save().entity(greetEntity).now();

    }
}
