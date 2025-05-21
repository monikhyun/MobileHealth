package com.example.resister.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class DietInsertRequest extends JsonObjectRequest {
    public DietInsertRequest(JSONObject jsonBody,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) {
        super(Method.POST, "http://10.0.2.2:8080/api/diet/record/v", jsonBody, listener, errorListener);
    }
    public DietInsertRequest(String userId, String name, BigDecimal calories, int carb, int protein,
                             int fat, LocalDate date, String mealtime,
                             Response.Listener<JSONObject> listener,
                             Response.ErrorListener errorListener) throws JSONException {

        super(Method.POST, "http://10.0.2.2:8080/api/diet/record/v", buildJson(userId, name, calories, carb, protein, fat, date, mealtime), listener, errorListener);
    }

    private static JSONObject buildJson(String userId, String name, BigDecimal calories, int carb, int protein,
                                        int fat, LocalDate date, String mealtime) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("name", name);
        json.put("calories", calories);
        json.put("carb", carb);
        json.put("protein", protein);
        json.put("fat", fat);
        json.put("date", date.toString());
        json.put("mealtime", mealtime);
        return json;
    }
}
