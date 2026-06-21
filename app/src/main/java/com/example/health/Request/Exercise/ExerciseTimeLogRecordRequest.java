// src/main/java/com/example/health/Request/ExerciseTimeLogRecordRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ExerciseTimeLogRecordRequest extends StringRequest {
    /**
     * @param jwtToken     "Bearer <JWT>"
     * @param userId       사용자 ID
     * @param isoDate      "yyyy-MM-dd"
     * @param seconds      저장할 누적 시간(초)
     * @param listener     서버 응답 문자열
     * @param errorListener
     */
    public ExerciseTimeLogRecordRequest(
            String jwtToken,
            String userId,
            String isoDate,
            int seconds,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) throws Exception {
        super(Method.PUT,
                buildUrl(userId, isoDate, seconds),
                listener,
                errorListener);
        this.jwtToken = jwtToken;
    }

    private final String jwtToken;

    private static String buildUrl(String userId, String isoDate, int seconds) throws Exception {
        return "http://10.0.2.2:8080/api/exercise/timer/"
                + URLEncoder.encode(userId, StandardCharsets.UTF_8.toString())
                + "/" + isoDate
                + "/" + seconds;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);
        headers.put("Accept", "text/plain");
        return headers;
    }
}