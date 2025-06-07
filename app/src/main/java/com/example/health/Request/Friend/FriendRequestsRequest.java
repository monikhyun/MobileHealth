package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestsRequest extends JsonArrayRequest {

    private final String jwtToken;

    public FriendRequestsRequest(
            String jwtToken,
            String userId,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener
    ) {
        super(
                Method.GET,
                "http://10.0.2.2:8080/api/follow/" + userId + "/follow/request",
                null,
                listener,
                errorListener
        );
        this.jwtToken = jwtToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);
        headers.put("Accept", "application/json");
        return headers;
    }
}