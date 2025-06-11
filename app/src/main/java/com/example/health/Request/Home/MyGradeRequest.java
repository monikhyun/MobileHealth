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

public class MyGradeRequest extends StringRequest {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/home/grade/";
    private final Map<String, String> headers;

    /**
     * @param jwtToken        서버 인증용 JWT (Bearer 포함하지 않은 순수 토큰)
     * @param userId          조회할 사용자 ID
     * @param listener        성공 시 JSON 문자열을 돌려 받는 리스너
     * @param errorListener   에러 시 호출되는 리스너
     */
    public MyGradeRequest(
            String jwtToken,
            String userId,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.GET, BASE_URL + userId, listener, errorListener);
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        headers.put("Accept", "application/json");
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            // 서버가 UTF-8로 보내준 JSON을 그대로 String으로 변환
            String json = new String(response.data, "UTF-8");
            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}