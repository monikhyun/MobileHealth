package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * FriendAddRequest
 * -----------------
 * 친구 추가(팔로우 요청)를 위한 POST 요청 클래스
 *
 * API 엔드포인트 예시:
 * POST /api/follow/request/{userId}/{followId}
 */
public class FriendAddRequest extends StringRequest {

    // 서버 요청 URL 기본값
    private static final String URL = "http://10.0.2.2:8080/api/follow/request/";

    // 요청 헤더 저장용 맵 (Authorization 헤더 포함)
    private final Map<String, String> headers;

    /**
     * 생성자
     *
     * @param jwtToken JWT 인증 토큰 (Bearer prefix 포함 필요)
     * @param userId   요청을 보내는 사용자 ID
     * @param followId 요청을 받는 사용자 ID
     * @param listener 성공 시 응답 콜백
     * @param errorListener 실패 시 에러 콜백
     */
    public FriendAddRequest(String jwtToken, String userId, String followId,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        // POST 방식 요청, URL은 /{userId}/{followId} 형식으로 구성
        super(Method.POST, URL + userId + "/" + followId, listener, errorListener);

        // 요청 헤더에 JWT 토큰 설정
        headers = new HashMap<>();
        headers.put("Authorization", jwtToken);  // 예: "Bearer eyJhbGciOi..."
    }

    /**
     * 요청 시 필요한 헤더 반환 (JWT 포함)
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
}