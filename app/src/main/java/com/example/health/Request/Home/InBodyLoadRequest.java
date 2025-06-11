// com/example/health/Request/Home/InBodyLoadRequest.java
package com.example.health.Request.Home;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class InBodyLoadRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/home/profile/inbody/";
    private final String jwtToken;

    /**
     * @param jwtToken   "Bearer " 제외한 토큰
     * @param userId     사용자 ID
     * @param date       yyyy-MM-dd 형식
     */
    public InBodyLoadRequest(
            String jwtToken,
            String userId,
            String date,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.GET, URL + userId + "/" + date, null, listener, errorListener);
        this.jwtToken = jwtToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        return headers;
    }
}