package com.udl.android.bloodpressuremonitor.BPMServerWS;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adrian.myapplication.backend.bpmApiLogin.model.User;
import com.example.adrian.myapplication.backend.measurementApi.model.Measurement;
import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adrian on 28/5/15.
 */
public class WSManager {

    private static WSManager SINGLETON_INSTANCE;

    private static final String CREATE_USER_URL = "users/create";
    private static final String LOGIN_USER_URL = "login";
    private static final String GCM_TOKEN_URL = "users/%s/gcmtoken";
    private static final String GET_MEASUREMENTS_URL = "users/%s/measurements";
    private static final String POST_MEASUREMENT_URL = "users/%s/measurements";

    private RequestQueue queue;
    private Map<String,String> headerRequest;

    private WSManager(){
        createRequestHeaders();
    }

    public static WSManager getInstance(){

        if(SINGLETON_INSTANCE == null){
            SINGLETON_INSTANCE = new WSManager();
        }
        return SINGLETON_INSTANCE;
    }

    public interface BPMCallback<T> {
        void onSuccess(T response);
        void onError(Exception e);
    }

    public interface APIReponseCallback {
        void onResponse(String response);
        void onError(Exception e);
    }

    private void createRequestHeaders(){

        headerRequest = new HashMap<>();
        headerRequest.put("Accept", "application/json");
    }

    private void addTokenToHeaderRequest(String token){
        if (!headerRequest.containsKey("access-token")
                ||headerRequest.get("access-token")==null
                || headerRequest.get("access-token").equals(""))
            headerRequest.put("access-token",token);
    }

    private void call(final Context context, String URL, int method, final Map<String, String> postParams, final APIReponseCallback callback){

        if(queue==null)queue = Volley.newRequestQueue(context);



        StringRequest stringRequest = new StringRequest(method,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callback!=null)
                    callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (callback != null)
                    callback.onError(error);

            }
        }){
            @Override
            protected Map<String,String> getParams(){

                return postParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerRequest;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                11000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    public void sendUser(final Context context, com.example.adrian.myapplication.backend.bpmApiRegister.model.User user
            , final BPMCallback<String> callback){

        Map<String,String> params = new HashMap<>();
        params.put("name",user.getName());
        params.put("email",user.getEmail());
        params.put("age",user.getAge());
        params.put("firstsurname",user.getFirstsurname());
        params.put("secondsurname", user.getSecondsurname());
        params.put("password",user.getPassword());
        params.put("administration",user.getAdministration());
        params.put("country",user.getCountry());
        params.put("city", user.getCity());

        call(context, Constants.URL_BASE + CREATE_USER_URL, Request.Method.POST, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("password")) {
                        callback.onSuccess("OK");
                    } else {
                        callback.onSuccess(null);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onSuccess(null);
                }


            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void sendLogin(final Context context,String email , String password, final BPMCallback<User> callback) {

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        call(context, Constants.URL_BASE + LOGIN_USER_URL, Request.Method.POST, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    callback.onSuccess(createUser(jsonObject));

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onSuccess(null);
                }


            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void sendGCMToken(final Context context,String regid, final BPMCallback<String> callback) {

        Map<String, String> params = new HashMap<>();
        params.put("gcmToken", regid);
        addTokenToHeaderRequest(Constants.SESSION_MD5_TOKEN);

        call(context, Constants.URL_BASE + String.format(GCM_TOKEN_URL,Constants.SESSION_USER_ID), Request.Method.PUT, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    if (isCorrectResponse(response)) {
                        callback.onSuccess("OK");
                    } else {
                        onError(new BPMServerException(context.getResources().getString(R.string.token_error)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void removeGCMToken(final Context context, final BPMCallback<String> callback){

        Map<String, String> params = new HashMap<>();
        addTokenToHeaderRequest(Constants.SESSION_MD5_TOKEN);

        call(context, Constants.URL_BASE + String.format(GCM_TOKEN_URL, Constants.SESSION_USER_ID), Request.Method.DELETE, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {

                try {
                    if (isCorrectResponse(response)) {
                        callback.onSuccess("OK");
                    } else {
                        onError(new BPMServerException(context.getResources().getString(R.string.token_error_delete)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });

    }

    public void getMeasurements(final Context context, final BPMCallback<List<Measurement>> callback){

        Map<String, String> params = new HashMap<>();
        addTokenToHeaderRequest(Constants.SESSION_MD5_TOKEN);

        call(context,Constants.URL_BASE + String.format(GET_MEASUREMENTS_URL, Constants.SESSION_USER_ID), Request.Method.GET, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {

                callback.onSuccess(parseMeasurements(response));

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });

    }

    private List<Measurement> parseMeasurements(String response){
        List<Measurement> measurements = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject;
            Measurement measurement;
            for (int i=0;i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                measurement = new Measurement();
                measurement.setDate(jsonObject.getString("date"));
                measurement.setSystolic(Integer.parseInt(jsonObject.getString("systolic")));
                measurement.setDiastolic(Integer.parseInt(jsonObject.getString("diastolic")));
                measurement.setPulse(Integer.parseInt(jsonObject.getString("pulse")));
                measurements.add(measurement);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return measurements;
    }

    public void sendNewMeasurement(final Context context, Measurement measurement,String lang,final BPMCallback<String> callback){
        Map<String, String> params = new HashMap<>();
        params.put("systolic",measurement.getSystolic()+"");
        params.put("diastolic",measurement.getDiastolic()+"");
        params.put("pulse",measurement.getPulse()+"");
        addTokenToHeaderRequest(Constants.SESSION_MD5_TOKEN);

        call(context, Constants.URL_BASE + String.format(POST_MEASUREMENT_URL,Constants.SESSION_USER_ID)+"/"+lang, Request.Method.POST, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    if (new JSONObject(response).getString("systolic") != null){
                        callback.onSuccess("OK");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onError(new BPMServerException(context.getResources().getString(R.string.error_adding_measurement)));
                }


            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });

    }

    private boolean isCorrectResponse(String response) throws JSONException {

        if (response != null && !response.equalsIgnoreCase("")) {
            JSONObject object = new JSONObject(response);
            if (object.has("status")) {
                if (object.getInt("status") == 200) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
     }

    public User createUser(JSONObject object) throws JSONException {

        User user = new User();
        Constants.SESSION_USER_ID = object.getLong("_id");
        user.setEmail(object.getString("email"));
        user.setName(object.getString("name"));
        user.setFirstsurname(object.getString("firstsurname"));
        user.setSecondsurname(object.getString("secondsurname"));
        user.setAdministration(object.getString("administration"));
        user.setPassword(object.getString("password"));
        Constants.SESSION_MD5_TOKEN = object.getString("password");
        user.setAge(object.getString("age"));
        user.setCity(object.getString("city"));
        user.setCountry(object.getString("country"));

        return user;
    }
}
