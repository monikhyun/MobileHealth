// src/main/java/com/example/health/Request/AddedExerciseDeleteRequest.java
package com.example.health.Request.Exercise;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AddedExerciseDeleteRequest extends StringRequest {

    private final String jwtToken;

    /**
     * @param jwtToken       : "Bearer <JWT>" 형태로 저장된 토큰 문자열
     * @param userId         : 삭제할 사용자 아이디
     * @param isoDate        : "yyyy-MM-dd" 형식의 날짜
     * @param exerciseName   : 삭제할 운동 이름 (공백이 있을 경우 URL 인코딩 필요)
     * @param listener       : 서버에서 반환하는 성공 문자열을 받을 리스너
     * @param errorListener  : 에러 콜백
     */
    public AddedExerciseDeleteRequest(
            String jwtToken,
            String userId,
            String isoDate,
            String exerciseName,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) throws Exception {
        super(
                Method.DELETE,
                buildUrl(userId, isoDate, exerciseName),
                listener,
                errorListener
        );
        this.jwtToken = jwtToken;
    }

    /**
     * URL 빌더:
     *   DELETE http://<서버주소>/api/exercise/remove/{userId}/{date}/{exerciseName}
     *   exerciseName 에 공백이 있을 경우 URLEncoder.encode 로 인코딩 후, "+" → "%20" 로 변환
     */
    private static String buildUrl(String userId, String isoDate, String exerciseName) throws Exception {
        // 1) URLEncoder.encode → 공백이 "+"로 변환됨
        String encoded = URLEncoder.encode(exerciseName, StandardCharsets.UTF_8.toString());
        // 2) 스프링이 PathVariable로 받을 때 "+"를 공백으로 디코딩하지 않으므로, 반드시 "%20" 으로 교체
        String safeName = encoded.replace("+", "%20");

        return "http://10.0.2.2:8080/api/exercise/remove/"
                + userId + "/" + isoDate + "/" + safeName;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        // JWT를 Authorization 헤더에 실어서 보냄
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);
        headers.put("Accept", "text/plain");
        return headers;
    }
}