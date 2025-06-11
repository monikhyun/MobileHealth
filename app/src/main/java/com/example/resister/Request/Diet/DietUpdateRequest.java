package com.example.resister.Request.Diet;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class DietUpdateRequest extends StringRequest {
    private final JSONObject jsonBody;

    public DietUpdateRequest(String userId,
                             Long dietId,
                             JSONObject jsonBody,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(Method.PUT,

                "http://10.0.2.2:8080/api/diet/record/" + userId + "/" + dietId + "/update",
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