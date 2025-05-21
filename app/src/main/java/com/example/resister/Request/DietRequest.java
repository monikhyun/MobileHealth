package com.example.resister.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class DietRequest extends StringRequest {
    private final Map<String, String> parameters;

    public DietRequest(String userID, String date, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/DietData.jsp", listener, null);
        parameters = new HashMap<>();
        parameters.put("userid", userID);
        parameters.put("date", date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
