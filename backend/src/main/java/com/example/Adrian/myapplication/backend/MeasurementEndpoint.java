package com.example.Adrian.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.Adrian.myapplication.backend.OfyService.*;

@Api(name = "measurementApi", version = "v1", resource = "measurement", namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Adrian.example.com",
                ownerName = "backend.myapplication.Adrian.example.com",
                packagePath = "" )
)
public class MeasurementEndpoint {

    private static final Logger logger = Logger.getLogger(MeasurementEndpoint.class.getName());

    @ApiMethod(name = "getMeasurement",path="measurements",httpMethod = ApiMethod.HttpMethod.GET)
    public Measurement getMeasurement(@Named("id") Long id) {
        logger.info("Calling getMeasurement method");
        Measurement measurement = ofy().load().type(Measurement.class).id(id).now();
        return measurement;
    }

    @ApiMethod(name = "insertMeasurement",path="measurements",httpMethod = ApiMethod.HttpMethod.POST)
    public Measurement insertMeasurement(Measurement measurement) {
        logger.info("Calling insertMeasurement method");
        ofy().save().entity(measurement).now();
        return ofy().load().entity(measurement).now();
    }
}