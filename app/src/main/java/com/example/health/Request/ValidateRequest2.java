package com.example.health.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest2 extends StringRequest {

    private Map<String,String> parameters;
    public ValidateRequest2(String username, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/auth/register/validate2/" +username, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", username);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
