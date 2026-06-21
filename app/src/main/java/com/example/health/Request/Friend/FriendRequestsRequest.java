package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * FriendRequestsRequest
 * ------------------------
 * 사용자가 받은 친구 요청 목록을 불러오는 GET 요청 클래스
 *
 * 요청 URL 예시:
 *   GET /api/follow/{userId}/follow/request
 * 응답 형식:
 *   JSONArray (친구 요청 리스트)
 */
public class FriendRequestsRequest extends JsonArrayRequest {

    // JWT 인증 토큰 저장용 필드
    private final String jwtToken;

    /**
     * 생성자
     *
     * @param jwtToken       JWT 인증 토큰 ("Bearer xxx" 형식 포함)
     * @param userId         친구 요청을 받아올 사용자 ID
     * @param listener       응답 성공 시 콜백 (JSONArray 형태로 받음)
     * @param errorListener  요청 실패 시 콜백
     */
    public FriendRequestsRequest(
            String jwtToken,
            String userId,
            Response.Listener<JSONArray> listener,
            Response.ErrorListener errorListener
    ) {
        // JsonArrayRequest 생성자 호출
        super(
                Method.GET, // HTTP GET 요청
                "http://10.0.2.2:8080/api/follow/" + userId + "/follow/request", // URL 구성
                null,       // 본문(body)은 없음
                listener,
                errorListener
        );
        this.jwtToken = jwtToken;
    }

    /**
     * 요청에 사용할 HTTP 헤더 설정
     * JWT 인증 및 JSON 응답 요청
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);         // ex: "Bearer eyJhbGciOi..."
        headers.put("Accept", "application/json");      // JSON 응답을 원함
        return headers;
    }
}