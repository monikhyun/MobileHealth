// src/main/java/com/example/health/Request/ExerciseRecordDeleteRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 운동 기록 세트별 삭제용 Request
 * PUT /api/exercise/add/{userId}/{date}/{exerciseName}/delete/{setCount}
 */
public class ExerciseRecordDeleteRequest extends StringRequest {

    public ExerciseRecordDeleteRequest(
            String userId,
            String date,
            String exerciseName,  // raw, 예: "풀 업"
            int setCount,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        super(
                Method.PUT,
                buildUrl(userId, date, exerciseName, setCount),
                listener,
                errorListener
        );
    }

    private static String buildUrl(String userId, String date, String exerciseName, int setCount) {
        String encodedName = URLEncoder.encode(exerciseName, StandardCharsets.UTF_8)
                .replace("+","%20");
        return "http://10.0.2.2:8080/api/exercise/add/"
                + userId + "/"
                + date + "/"
                + encodedName
                + "/delete/"
                + setCount;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.put("Accept", "text/plain");
        // 필요한 경우 Authorization 헤더 추가 가능
        return headers;
    }
}