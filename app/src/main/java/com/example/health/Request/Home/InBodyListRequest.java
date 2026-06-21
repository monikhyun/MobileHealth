package com.example.health.Request.Home;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InBodyListRequest extends StringRequest {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/home/profile/inbody/";
    private final Map<String, String> headers = new HashMap<>();

    /**
     * @param jwtToken       "Bearer " 포함하지 않은 순수 토큰
     * @param userId         조회할 유저 아이디
     * @param listener       성공 시 JSON 문자열을 받는 리스너
     * @param errorListener  실패 시 에러를 받는 리스너
     */
    public InBodyListRequest(String jwtToken,
                             String userId,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(Method.GET, BASE_URL + userId, listener, errorListener);
        headers.put("Authorization", "Bearer " + jwtToken);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
}