// com/example/health/Request/Exercise/ExerciseFavoriteRequest.java
package com.example.health.Request.Exercise;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ExerciseFavoriteRequest extends StringRequest {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/exercise/list";
    private final String jwtToken;

    /**
     * @param jwtToken     "Bearer " 제외한 토큰
     * @param userId       사용자 ID
     * @param exerciseName 운동 이름
     * @param favorite     true=찜하기, false=찜취소
     */
    public ExerciseFavoriteRequest(
            String jwtToken,
            String userId,
            String exerciseName,
            boolean favorite,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        super(Method.GET,
                buildUrl(userId, exerciseName, favorite),
                listener,
                errorListener
        );
        this.jwtToken = jwtToken;
    }

    private static String buildUrl(String userId, String exerciseName, boolean fav) {
        try {
            String encoded = URLEncoder.encode(exerciseName, "UTF-8");
            return BASE_URL
                    + (fav ? "/favorite/" : "/unfavorite/")
                    + userId
                    + "?exerciseName=" + encoded;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> h = new HashMap<>();
        h.put("Authorization", "Bearer " + jwtToken);
        return h;
    }

    public void addToRequestQueue(Context ctx) {
        Volley.newRequestQueue(ctx).add(this);
    }
}