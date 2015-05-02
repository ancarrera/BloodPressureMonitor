package com.example.Adrian.myapplication.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.Adrian.myapplication.backend.OfyService.*;

@Api(name = "measurementApi", version = "v1", resource = "measurement", namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Adrian.example.com",
                ownerName = "backend.myapplication.Adrian.example.com",
                packagePath = "" )
)



public class MeasurementEndpoint {

    private static final String API_KEY = System.getProperty("gcm.api.key");
    private static final int DIANA_SYSTOLIC_LIMIT=170;
    private static final int DIANA_DIASTOLIC_LIMIT=110;
    private static final int REST_DIANA_LIMIT = 20;

    private static final Logger logger = Logger.getLogger(MeasurementEndpoint.class.getName());

    @ApiMethod(name = "listMeasurements",path="users/{id}/measurements",httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Measurement> listMeasurementsByUser(@Named("id") Long id) {
        logger.info("Calling getMeasurement method");
        User user = ofy().load().type(User.class).id(id).now();
        logger.info("List measurements per users "+id+" size "+user.getMeasurementList().size());
        return CollectionResponse.<Measurement>builder().setItems(user.getMeasurementList()).build();
    }

    @ApiMethod(name = "receive",path="users/{id}/measurements/{index}",httpMethod = ApiMethod.HttpMethod.GET)
    public Measurement listMeasurementByUser(@Named("id") Long id,@Named("index")Integer index) {
        logger.info("Calling getMeasurement method");
        User user = ofy().load().type(User.class).id(id).now();
        logger.info("List measurements per users " + id + " size " + user.getMeasurementList().size());
        return user.getMeasurementAtIndex(index);
    }

    @ApiMethod(name = "insertMeasurement",path="users/{id}/measurements/{lan}",httpMethod = ApiMethod.HttpMethod.POST)
    public Measurement insertMeasurement(@Named("id")Long id, Measurement measurement,@Named("lan")String language) throws IOException {
        logger.info("Calling insertMeasurement method with language " + language);
        User user = ofy().load().type(User.class).id(id).now();
        user.addMeasurement(measurement);
        user.sumOneInsertion();
        ofy().save().entity(user).now();
        if (isPushInsertion(user)){
            List<Measurement> fiveLastMeasures = getFiveLastMeasurements(user);
            Average average = calculateAverages(fiveLastMeasures);
            int status = calculatePatientStatus(average);
            String message = prepareMessage(status,language);
            sendMessage(message,user);
        }
        return measurement;
    }

    private boolean isPushInsertion(User user){
        if (user.getTotalinsertions() % 5 == 0){
            logger.info("insertions % 5 ==0");
            return true;
        }
        logger.info("insertions % 5 !=0");
        return false;
    }

    //status 0 the patient has good health
    //status 1 the patient has regular health
    //status 2 the patient has bad health

    private int calculatePatientStatus(Average average){
        if (average.diastolic>DIANA_DIASTOLIC_LIMIT || average.systolic >DIANA_SYSTOLIC_LIMIT){
            logger.info("Status 2");
            return 2;
        }else if ((DIANA_DIASTOLIC_LIMIT - average.diastolic)<=REST_DIANA_LIMIT
                 || (DIANA_SYSTOLIC_LIMIT - average.systolic)<=REST_DIANA_LIMIT){
            logger.info("Status 1");
            return 1;
        }else {
            logger.info("Status 0");
            return 0;
        }
    }

    private String prepareMessage(int status,String lan){
        String message ="";
        final String part1_es = "Segun las ultimas 5 mediciones introducidas tu estado de salud es ";
        final String part1_en = "According to the latest 5 measurements made your health is ";
        final String part2_en_zero = "good";
        final String part2_es_zero = "bueno";
        final String part2_one = "regular";
        final String part2_en_two = "bad";
        final String part2_es_two = "malo";

        if (lan.equalsIgnoreCase("en")) {
            switch (status) {
                case 0:
                    message = part1_en + part2_en_zero;
                    break;
                case 1:
                    message = part1_en + part2_one;
                    break;
                case 2:
                    message = part1_en + part2_en_two;
                    break;
            }
        }else if (lan.equalsIgnoreCase("es")){
            switch (status) {
                case 0:
                    message = part1_es + part2_es_zero;
                    break;
                case 1:
                    message = part1_es + part2_one;
                    break;
                case 2:
                    message = part1_es + part2_es_two;
                    break;
            }
        }
        logger.info("Message text "+message);
        return message;
    }

    private void sendMessage(String message,User user) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class)
                .filter("userId", user.getId()).limit(5).list(); //limited to five devices per user.
        for (RegistrationRecord record : records) {
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                logger.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    record.setRegId(canonicalRegId);
                    record.setUserID(user.getId());
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    ofy().delete().entity(record).now();
                } else {
                    logger.warning("Error when sending message : " + error);
                }
            }
        }
    }

    private List<Measurement> getFiveLastMeasurements(User user){

        int totalInsertions = user.getTotalinsertions();
        List<Measurement> measurements = new ArrayList<>();

        for (int i=totalInsertions-1;i>=(totalInsertions-5);i--){
            measurements.add(user.getMeasurementAtIndex(i));
        }
        return measurements;
    }

    private Average calculateAverages(List<Measurement> measurements){
        Average average = new Average();
        int tmp_sys = 0,tmp_dia = 0,tmp_pul = 0;
        for (Measurement measurement: measurements){
            tmp_sys += measurement.getSystolic();
            tmp_dia += measurement.getDiastolic();
            tmp_pul += measurement.getPulse();
        }
        average.systolic = (int) (tmp_sys /5);
        average.diastolic = (int) (tmp_dia / 5);
        average.pulse = (int) (tmp_pul / 5);

        return average;
    }
}