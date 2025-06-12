package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * FriendListRequest
 * -------------------
 * 사용자가 팔로우하고 있는 친구 목록을 조회하는 GET 요청 클래스.
 * API 예시: GET /api/follow/following/{userId}
 */
public class FriendListRequest extends StringRequest {

    // 서버의 기본 요청 주소
    private static final String BASE_URL = "http://10.0.2.2:8080/api/follow/following/";

    // 요청 시 포함될 헤더 정보 저장용 맵
    private final Map<String, String> headers;

    /**
     * 생성자
     *
     * @param jwtToken       JWT 인증 토큰 ("Bearer xxx" 형식 포함)
     * @param userId         친구 목록을 가져올 사용자 ID
     * @param listener       서버 응답 성공 시 콜백
     * @param errorListener  서버 요청 실패 시 콜백
     */
    public FriendListRequest(
            String jwtToken,
            String userId,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        // GET 요청으로 URL 구성: /following/{userId}
        super(Method.GET, BASE_URL + userId, listener, errorListener);

        // 헤더 설정: JWT 포함 및 응답 형식 JSON 명시
        headers = new HashMap<>();
        headers.put("Authorization", jwtToken);         // ex: "Bearer eyJhbGciOi..."
        headers.put("Accept", "application/json");       // JSON 응답을 받기 위함
    }

    /**
     * HTTP 요청 시 헤더 반환
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    /**
     * 네트워크 응답을 문자열로 파싱 (기본 UTF-8 인코딩 사용)
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            // 바이트 배열을 UTF-8 문자열로 변환
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // 인코딩 에러 발생 시 Volley 에러 객체로 반환
            return Response.error(new ParseError(e));
        }
    }
}