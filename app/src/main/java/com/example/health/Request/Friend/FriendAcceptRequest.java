// src/main/java/com/example/health/Request/Friend/FriendAcceptRequest.java
package com.example.health.Request.Friend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FriendAcceptRequest extends StringRequest {

    private final String jwtToken;

    /**
     * @param jwtToken  : "Bearer <JWT>" 형태의 인증 토큰
     * @param userId    : 현재 사용자 ID
     * @param username  : 수락할 요청을 보낸 상대방 username
     * @param listener  : 응답 리스너
     * @param errorListener : 에러 리스너
     */
    public FriendAcceptRequest(
            String jwtToken,
            String userId,
            String username,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.POST,
                "http://10.0.2.2:8080/api/follow/" + userId + "/follow/request/accept/" + username,
                listener,
                errorListener);
        this.jwtToken = jwtToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", jwtToken);
        headers.put("Content-Type", "application/json");
        return headers;
    }
}