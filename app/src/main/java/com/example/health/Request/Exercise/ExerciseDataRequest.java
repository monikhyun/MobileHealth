// src/main/java/com/example/health/Request/ExerciseDataRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 운동 메타데이터 조회 (/api/exercise/add/{exerciseName})
 */
public class ExerciseDataRequest extends JsonObjectRequest {

    public ExerciseDataRequest(
            String exerciseName,  // raw string, e.g. "풀 업"
            Response.Listener<org.json.JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(
                Method.GET,
                buildUrl(exerciseName),
                null,
                listener,
                errorListener
        );
    }

    private static String buildUrl(String exerciseName) {
        // 1) exerciseName을 UTF-8로 인코딩
        String encodedName = URLEncoder.encode(exerciseName, StandardCharsets.UTF_8);
        // URLEncoder.encode은 공백을 "+"로 바꾸므로,
        // Spring 쪽 PathVariable에서 제대로 해석하게 하려면 "+"를 "%20"으로 치환해 줍니다.
        encodedName = encodedName.replace("+", "%20");
        return "http://10.0.2.2:8080/api/exercise/add/" + encodedName;
    }

    @Override
    public Map<String, String> getHeaders() {
        // 필요시 JWT 토큰 등을 포함할 수 있습니다.
        Map<String,String> headers = new HashMap<>();
        // headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}