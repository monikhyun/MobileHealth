// com/example/health/Request/Home/InBodyEditRequest.java
package com.example.health.Request.Home;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InBodyEditRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/home/profile/inbody/";
    private final String jwtToken;

    /**
     * @param jwtToken   "Bearer " 제외한 토큰
     * @param userId     사용자 ID
     * @param date       yyyy-MM-dd
     * @param weight     kg
     * @param smm        kg
     * @param lbm        kg
     * @param bmi        kg/m²
     * @param fatPercent %
     */
    public InBodyEditRequest(
            String jwtToken,
            String userId,
            String date,
            String weight,
            String smm,
            String lbm,
            String bmi,
            String fatPercent,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener
    ) throws JSONException {
        super(Method.PUT,
                URL + userId,
                buildBody(date, weight, smm, lbm, bmi, fatPercent),
                listener,
                errorListener);
        this.jwtToken = jwtToken;
    }

    private static JSONObject buildBody(
            String date, String weight, String smm,
            String lbm, String bmi, String fatPercent
    ) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("date", date);
        body.put("weight", Double.parseDouble(weight));
        body.put("smm", Double.parseDouble(smm));
        body.put("lbm", Double.parseDouble(lbm));
        body.put("bmi", Double.parseDouble(bmi));
        body.put("fat_percent", Double.parseDouble(fatPercent));
        return body;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> h = new HashMap<>();
        h.put("Authorization", "Bearer " + jwtToken);
        h.put("Content-Type", "application/json");
        return h;
    }
}