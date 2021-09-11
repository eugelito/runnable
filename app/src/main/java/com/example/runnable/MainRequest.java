package com.example.runnable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//JC - Request to the Register.PHP file on the server and receive a response as a string
public class MainRequest extends StringRequest {

    //JC - Reference to the 'runnablemetrics.php' file on the server
    private static final String MAIN_REQUEST_URL = "http://etroyo01.students.cs.qub.ac.uk/public_html/RunnableMetrics.php";

    private Map<String, String> params;

    //JC - Constructor asks for details 'name', 'email', 'username', 'password',
    public MainRequest(String distance, String steps, String speed, String duration, Response.Listener<String> listener) {
        //JC - Send and retrieve data to and from runnablemetrics.php
        super(Method.POST, MAIN_REQUEST_URL, listener, null);

        //JC - Pass information to request
        params = new HashMap<>();

        //JC - Put following data into HashMap
        params.put("distance", distance);
        params.put("steps", steps);
        params.put("speed", speed);
        params.put("duration", duration);

    }

    //JC - Volley accesses data to params
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}