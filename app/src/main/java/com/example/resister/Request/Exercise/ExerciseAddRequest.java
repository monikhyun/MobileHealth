package com.example.resister.Request.Exercise;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ExerciseAddRequest extends StringRequest {
    private final String token;

    public ExerciseAddRequest(String token, String userId,
                              String date, String exerciseName,
                              Response.Listener<String> listener,
                              @Nullable Response.ErrorListener errorListener) {
        super(Method.POST,
                "http://10.0.2.2:8080/api/exercise/add/"
                        + userId + "/" + date + "/" + exerciseName,
                listener, errorListener);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
