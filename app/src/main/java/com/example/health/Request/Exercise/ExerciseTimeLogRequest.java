// src/main/java/com/example/health/Request/ExerciseTimeLogRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ExerciseTimeLogRequest extends JsonObjectRequest {
    /**
     * @param jwtToken     "Bearer <JWT>"
     * @param userId       사용자 ID
     * @param isoDate      "yyyy-MM-dd"
     * @param listener     onResponse(JSONObject) -- {"userId":...,"date":"...","time":123}
     * @param errorListener
     */
    public ExerciseTimeLogRequest(
            String jwtToken,
            String userId,
            String isoDate,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.GET,
                "http://10.0.2.2:8080/api/exercise/timer/load/"
                        + userId + "/" + isoDate,
                null,
                listener,
                errorListener);
        this.jwtToken = jwtToken;
    }

    private final String jwtToken;

    @Override
    public java.util.Map<String, String> getHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("Authorization", jwtToken);
        return headers;
    }
}