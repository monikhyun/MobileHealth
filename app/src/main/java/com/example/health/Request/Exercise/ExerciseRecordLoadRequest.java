// src/main/java/com/example/health/Request/ExerciseRecordLoadRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 저장된 세트 목록(JSON 배열)을 가져오는 Request
 * GET /api/exercise/add/{userId}/{date}/{exerciseName}
 */
public class ExerciseRecordLoadRequest extends JsonArrayRequest {

    public ExerciseRecordLoadRequest(
            String userId,
            String date,             // YYYY-MM-DD
            String exerciseName,     // raw, e.g. "풀 업"
            Response.Listener<org.json.JSONArray> listener,
            Response.ErrorListener errorListener
    ) {
        super(
                Method.GET,
                buildUrl(userId, date, exerciseName),
                null,
                listener,
                errorListener
        );
    }

    private static String buildUrl(String userId, String date, String exerciseName) {
        String encodedName = URLEncoder.encode(exerciseName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "http://10.0.2.2:8080/api/exercise/add/"
                + userId + "/"
                + date + "/"
                + encodedName;
    }

    @Override
    public java.util.Map<String, String> getHeaders() {
        java.util.Map<String,String> headers = new java.util.HashMap<>();
        // headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}