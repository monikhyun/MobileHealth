package com.example.health.Request.Diet;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class DietInsertRequest extends StringRequest {
    private final JSONObject jsonBody;

    public DietInsertRequest(String userId,
                             JSONObject jsonBody,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(Method.POST,
                "http://10.0.2.2:8080/api/diet/record/" + userId,
                listener,
                errorListener);
        this.jsonBody = jsonBody;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return jsonBody == null
                ? null
                : jsonBody.toString().getBytes(StandardCharsets.UTF_8);
    }
}