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

public class MyPageRequest extends StringRequest {
    // 에뮬레이터에서 localhost:8080 으로 요청
    private static final String BASE_URL = "http://10.0.2.2:8080/api/home/profile/";
    private final Map<String, String> headers;

    public MyPageRequest(
            String jwtToken,
            String userId,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.GET, BASE_URL + userId, listener, errorListener);
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String charset = HttpHeaderParser.parseCharset(response.headers, "UTF-8");
            String json = new String(response.data, charset);
            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}