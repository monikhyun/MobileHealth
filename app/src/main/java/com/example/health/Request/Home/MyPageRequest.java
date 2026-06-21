package com.example.health.Request.Home;

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
 * MyPageRequest
 * ----------------------------
 * 마이페이지 상단에 표시되는 사용자 프로필 정보를 가져오는 GET 요청 클래스
 *
 * 요청 URL 예:
 *   GET /api/home/profile/{userId}
 *
 * 응답 형태:
 *   JSON (String 형식으로 수신됨)
 */
public class MyPageRequest extends StringRequest {

    // 요청 URL 기본 경로
    private static final String BASE_URL = "http://10.0.2.2:8080/api/home/profile/";

    // 요청 헤더 (JWT 포함)
    private final Map<String, String> headers;

    /**
     * 생성자
     *
     * @param jwtToken      인증용 JWT 토큰 ("Bearer ..." 형식)
     * @param userId        조회 대상 사용자 ID
     * @param listener      서버 응답 성공 시 콜백
     * @param errorListener 서버 응답 실패 시 콜백
     */
    public MyPageRequest(
            String jwtToken,
            String userId,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        // GET 요청 구성: /api/home/profile/{userId}
        super(Method.GET, BASE_URL + userId, listener, errorListener);

        // 헤더에 JWT 포함
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
    }

    /**
     * 요청에 포함할 HTTP 헤더 반환
     * (JWT 토큰 포함)
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    /**
     * 서버 응답 바이트 데이터를 문자열로 변환하여 반환
     *
     * @param response Volley의 NetworkResponse 객체
     * @return 성공 시 문자열 응답, 실패 시 파싱 오류
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            // 응답 헤더에서 인코딩 타입 추출, 기본값은 UTF-8
            String charset = HttpHeaderParser.parseCharset(response.headers, "UTF-8");

            // 바이트 데이터를 문자열로 디코딩
            String json = new String(response.data, charset);

            // 정상적으로 응답 반환
            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // 인코딩 실패 시 오류 처리
            return Response.error(new ParseError(e));
        }
    }
}