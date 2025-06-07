package com.example.health.Request;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public class LoginRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> listener;
    private final String url;

    public LoginRequest(
            String userID,
            String password,
            Response.Listener<NetworkResponse> listener,
            Response.ErrorListener errorListener) {
        super(Method.POST,
                "http://10.0.2.2:8080/api/auth/login/validate/"
                        + userID + "/" + password,
                errorListener);
        this.listener = listener;
        this.url = getUrl();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        // 그대로 NetworkResponse로 감싸서 헤더와 바디 모두 전달
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        // 호출부에서 response.headers.get("Authorization") 로 JWT를 꺼낼 수 있습니다.
        listener.onResponse(response);
    }
}