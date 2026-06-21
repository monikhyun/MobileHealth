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
 * FriendSearchRequest
 * ---------------------
 * 친구(사용자)를 검색하기 위한 GET 요청 클래스
 *
 * 요청 URL 예시:
 *   GET /api/follow/search/{userId}/{keyword}
 *
 * 응답 형식:
 *   String (JSON 형태 문자열)
 */
public class FriendSearchRequest extends StringRequest {

    // 기본 API URL 경로
    private static final String BASE_URL = "http://10.0.2.2:8080/api/follow/search/";

    // HTTP 요청에 포함될 헤더 (JWT 포함)
    private final Map<String, String> headers;

    /**
     * 생성자
     *
     * @param jwtToken      JWT 인증 토큰 ("Bearer ..." 형식 포함)
     * @param userId        검색을 수행하는 사용자 ID
     * @param keyword       검색할 키워드 (사용자 이름 등)
     * @param listener      서버 응답 성공 시 콜백 (문자열 형태)
     * @param errorListener 서버 응답 실패 시 콜백
     */
    public FriendSearchRequest(String jwtToken, String userId, String keyword,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        // GET 요청 구성: /search/{userId}/{keyword}
        super(Method.GET, BASE_URL + userId + "/" + keyword, listener, errorListener);

        // 헤더 초기화 및 JWT 설정
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
    }

    /**
     * HTTP 요청에 포함될 헤더 반환 (JWT 포함)
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    /**
     * 서버로부터 받은 응답을 문자열로 변환하여 반환
     *
     * @param response Volley의 NetworkResponse 객체
     * @return 문자열로 변환된 응답 데이터
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            // 응답 바이트 배열을 UTF-8 문자열로 디코딩
            String jsonString = new String(response.data, "UTF-8");
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // 인코딩 실패 시 Volley 파싱 오류로 반환
            return Response.error(new ParseError(e));
        }
    }
}