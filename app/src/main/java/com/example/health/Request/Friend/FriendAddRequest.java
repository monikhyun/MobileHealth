package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FriendAddRequest extends StringRequest {
    private static final String URL = "http://10.0.2.2:8080/api/follow/request/";
    private final Map<String, String> headers;

    public FriendAddRequest(String jwtToken, String userId, String followId, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, URL + userId + "/" + followId, listener, errorListener);
        headers = new HashMap<>();
        headers.put("Authorization", jwtToken);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
}