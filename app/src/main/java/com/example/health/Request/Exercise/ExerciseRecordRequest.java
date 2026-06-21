// src/main/java/com/example/health/Request/ExerciseRecordRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 운동 기록(세트) 저장용 Request
 * POST /api/exercise/add/{userId}/{date}/{exerciseName}/record
 */
public class ExerciseRecordRequest extends StringRequest {

    public ExerciseRecordRequest(
            String userId,
            String date,             // YYYY-MM-DD
            String exerciseName,     // raw, 예: "풀 업"
            int setCount,
            int count,
            double weight,
            boolean done,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        super(
                Method.POST,
                buildUrl(userId, date, exerciseName) + "/record",
                listener,
                errorListener
        );

        // JSON 바디를 멤버 변수에 저장
        this.bodyJson = new JSONObject();
        try {
            this.bodyJson.put("setCount", setCount);
            this.bodyJson.put("count", count);
            this.bodyJson.put("weight", weight);
            this.bodyJson.put("date", date);
            this.bodyJson.put("done", done);
        } catch (Exception e) {
            // JSONException 무시
        }
    }

    private JSONObject bodyJson;

    private static String buildUrl(String userId, String date, String exerciseName) {
        String encodedName = URLEncoder.encode(exerciseName, StandardCharsets.UTF_8)
                .replace("+","%20");
        return "http://10.0.2.2:8080/api/exercise/add/"
                + userId + "/"
                + date + "/"
                + encodedName;
    }

    @Override
    public byte[] getBody() {
        return bodyJson.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.put("Accept", "text/plain");
        // 필요시 Authorization 헤더 추가 가능
        // headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}