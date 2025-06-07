package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FriendSearchRequest extends StringRequest {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/follow/search/";
    private final Map<String, String> headers;

    public FriendSearchRequest(String jwtToken, String userId, String keyword,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(Method.GET, BASE_URL + userId + "/" + keyword, listener, errorListener);
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}