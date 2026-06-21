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

/**
 * ExerciseListRequest
 * -------------------
 * 운동 목록을 조회하거나 필터 검색, 찜한 운동을 가져오기 위한 GET 요청 클래스.
 * 서버에서 JSON 배열을 응답받는 구조이며 JWT 인증이 필요하다.
 */
public class ExerciseListRequest extends JsonArrayRequest {

    // JWT 토큰 (Authorization 헤더에 사용)
    private final String jwtToken;

    // 기본 API URL
    private static final String BASE_URL = "http://10.0.2.2:8080/api/exercise/list";

    /**
     * 생성자 - 외부에서 직접 호출하지 않고 정적 메서드를 통해 생성되도록 설계
     */
    private ExerciseListRequest(String jwtToken,
                                int method,
                                String url,
                                Response.Listener<JSONArray> listener,
                                Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.jwtToken = jwtToken;
    }

    /**
     * HTTP 요청에 Authorization 헤더 추가 (JWT 토큰 포함)
     */
    @Override
    public Map<String, String> getHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    /**
     * 1) 전체 운동 목록 조회 요청 생성
     * GET /api/exercise/list
     */
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

    /**
     * 2) 필터를 활용한 운동 검색 요청 생성
     * GET /api/exercise/list/search?bodypart=OO&exerciseName=OO
     *
     * @param bodyPart 검색할 신체 부위 (nullable)
     * @param exerciseName 검색할 운동 이름 (nullable)
     */
    public static ExerciseListRequest search(
            String jwtToken,
            @Nullable String bodyPart,
            @Nullable String exerciseName,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener) {

        // URL에 쿼리 파라미터를 동적으로 붙이기
        StringBuilder url = new StringBuilder(BASE_URL + "/search");
        boolean first = true;
        if (bodyPart != null) {
            url.append(first ? "?" : "&")
                    .append("bodypart=").append(encode(bodyPart));
            first = false;
        }
        if (exerciseName != null) {
            url.append(first ? "?" : "&")
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

    /**
     * 3) 유저가 찜한 운동 목록 조회 요청
     * GET /api/exercise/list/{userId}
     */
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

    /**
     * URL 쿼리 파라미터에 사용하기 위한 문자열 인코딩
     * @param s 인코딩할 문자열
     * @return UTF-8 인코딩된 문자열
     */
    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return s;  // 예외 발생 시 원본 그대로 반환
        }
    }

    /**
     * 현재 요청 객체를 Volley 요청 큐에 등록
     * @param ctx Context (Activity 또는 Application)
     */
    public void addToRequestQueue(Context ctx) {
        Volley.newRequestQueue(ctx).add(this);
    }
}