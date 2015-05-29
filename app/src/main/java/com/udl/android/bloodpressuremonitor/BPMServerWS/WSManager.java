package com.udl.android.bloodpressuremonitor.BPMServerWS;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adrian.myapplication.backend.bpmApiLogin.model.User;
import com.google.api.client.http.HttpMethods;
import com.udl.android.bloodpressuremonitor.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adrian on 28/5/15.
 */
public class WSManager {

    private static WSManager SINGLETON_INSTANCE;

    private static final String BASE_URL = "http://192.168.168.25:8082/";
    private static final String CREATE_USER = "users/create";
    private static final String LOGIN_USER = "login";



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

        call(context, BASE_URL + CREATE_USER, Request.Method.POST, params, new APIReponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("password")) { //check if response status 200 is user
                        callback.onSuccess("OK");
                    }else{
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

        call(context, BASE_URL + LOGIN_USER, Request.Method.POST, params, new APIReponseCallback() {
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

    public boolean isCorrectResponse(String response) throws JSONException {

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
        user.setAge(object.getString("age"));
        user.setCity(object.getString("city"));
        user.setCountry(object.getString("country"));

        return user;
    }
}
