package com.example.health.Request.Home;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InBodyRecordRequest extends JsonObjectRequest {
    private static final String URL = "http://10.0.2.2:8080/api/home/profile/inbody/";

    /**
     * @param jwtToken     "Bearer " 제외한 토큰
     * @param userId       사용자 ID
     * @param date         yyyy-MM-dd 형식의 날짜 문자열
     * @param weight       몸무게 (kg), 소수점 1자리까지 가능
     * @param smm          골격근량 (kg)
     * @param lbm          제지방량 (kg)
     * @param bmi          BMI (kg/m²)
     * @param fatPercent   체지방률 (%)
     */
    public InBodyRecordRequest(
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
        super(Method.POST,
                URL + userId,
                buildBody(date, weight, smm, lbm, bmi, fatPercent),
                listener,
                errorListener);
        this.jwtToken = jwtToken;
    }

    private final String jwtToken;

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