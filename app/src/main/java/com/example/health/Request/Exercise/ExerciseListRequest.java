package com.example.health.Request.Exercise;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ExerciseListRequest extends JsonArrayRequest {
    private final String jwtToken;
    private static final String BASE_URL = "http://10.0.2.2:8080/api/exercise/list";

    // jwtToken, method, url, listener, errorListener 모두 받도록 생성자 수정
    private ExerciseListRequest(String jwtToken,
                                int method,
                                String url,
                                Response.Listener<JSONArray> listener,
                                Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.jwtToken = jwtToken;
    }

    // 모든 요청에 JWT 헤더를 붙이기 위해 getHeaders() 오버라이드
    @Override
    public Map<String, String> getHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    /** 1) 전체 운동 목록 조회 */
    public static ExerciseListRequest getAll(
            String jwtToken,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener) {
        return new ExerciseListRequest(
                jwtToken,
                Method.GET,
                BASE_URL,
                listener,
                errorListener
        );
    }

    /** 2) 필터(부위/이름) 검색 (bodyPart, exerciseName 모두 옵셔널) */
    public static ExerciseListRequest search(
            String jwtToken,
            @Nullable String bodyPart,
            @Nullable String exerciseName,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener) {

        StringBuilder url = new StringBuilder(BASE_URL + "/search");
        boolean first = true;
        if (bodyPart != null) {
            url.append(first? "?" : "&")
                    .append("bodypart=").append(encode(bodyPart));
            first = false;
        }
        if (exerciseName != null) {
            url.append(first? "?" : "&")
                    .append("exerciseName=").append(encode(exerciseName));
        }

        return new ExerciseListRequest(
                jwtToken,
                Method.GET,
                url.toString(),
                listener,
                errorListener
        );
    }

    /** 3) 유저별 찜한 운동 조회 */
    public static ExerciseListRequest getFavorites(
            String jwtToken,
            String userId,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener) {

        String url = BASE_URL + "/" + encode(userId);
        return new ExerciseListRequest(
                jwtToken,
                Method.GET,
                url,
                listener,
                errorListener
        );
    }

    // URL 인코딩 헬퍼 (예: 공백/한글 대비)
    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public void addToRequestQueue(Context ctx) {
        Volley.newRequestQueue(ctx).add(this);
    }
}