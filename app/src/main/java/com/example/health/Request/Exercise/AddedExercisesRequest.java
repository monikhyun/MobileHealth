package com.example.health.Request.Exercise;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;


public class AddedExercisesRequest extends JsonArrayRequest {

    // Base URL (자신의 환경에 맞게 수정)
    private static final String BASE_URL = "http://10.0.2.2:8080/api/exercise/add/todo/";

    public AddedExercisesRequest(
            String jwt,
            String userId,
            String isoDate,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener
    ) {
        super(
                Method.GET,
                BASE_URL + userId + "/" + isoDate,
                /* request body (null) */ null,
                listener,
                errorListener
        );
        this.jwt = jwt;
    }

    private final String jwt;

    @Override
    public java.util.Map<String, String> getHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        if (jwt != null && !jwt.isEmpty()) {
            headers.put("Authorization", "Bearer " + jwt);
        }
        return headers;
    }

    @Override
    protected Response<org.json.JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, StandardCharsets.UTF_8);
            org.json.JSONArray jsonArray = new org.json.JSONArray(jsonString);
            return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new com.android.volley.ParseError(e));
        }
    }
}