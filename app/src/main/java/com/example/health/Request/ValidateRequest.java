package com.example.health.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
public class ValidateRequest extends StringRequest {

    private Map<String,String> parameters;
    public ValidateRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/auth/register/validate/" +userID, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}