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
 * FriendAddCancelRequest
 * -----------------------
 * 친구 추가 요청 취소용 DELETE 요청 클래스
 * API 엔드포인트:
 *   DELETE /api/follow/request/cancel/{userId}/{followId}
 *
 * 기능 설명:
 *   사용자가 이전에 팔로우 요청을 보냈던 대상을 대상으로
 *   팔로우 요청을 취소하는 기능을 수행.
 */
public class FriendAddCancelRequest extends StringRequest {

    // 요청을 보낼 기본 URL
    private static final String BASE_URL = "http://10.0.2.2:8080/api/follow/request/cancel/";

    // 요청에 포함될 헤더 (Authorization: Bearer {JWT})
    private final Map<String, String> headers;

    /**
     * 생성자
     *
     * @param jwtToken JWT 인증 토큰
     * @param userId   요청 취소를 시도하는 사용자 ID
     * @param followId 팔로우 요청을 취소할 대상 사용자 ID
     * @param listener 응답 성공 시 콜백
     * @param errorListener 오류 발생 시 콜백
     */
    public FriendAddCancelRequest(String jwtToken, String userId, String followId,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {
        // DELETE 요청으로 URL 구성: /cancel/{userId}/{followId}
        super(Method.DELETE, BASE_URL + userId + "/" + followId, listener, errorListener);

        // Authorization 헤더 설정
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
    }

    /**
     * HTTP 요청 헤더 설정 (JWT 포함)
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    /**
     * 서버 응답을 문자열(String)로 파싱
     * 기본 응답 인코딩은 UTF-8로 처리
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            // 응답 바이트 데이터를 UTF-8 문자열로 변환
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // 인코딩 예외 발생 시 Volley 오류로 반환
            return Response.error(new ParseError(e));
        }
    }
}