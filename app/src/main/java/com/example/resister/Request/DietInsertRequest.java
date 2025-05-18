package com.example.resister.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DietInsertRequest extends StringRequest {
    private final Map<String, String> params;

    public DietInsertRequest(String userID, String name, String cal, String carb, String protein,
                             String fat, String date, String mealtime, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/InsertDiet.jsp", listener, null);
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("name", name);
        params.put("mealTime",mealtime);
        params.put("cal", cal);
        params.put("carb", carb);
        params.put("protein", protein);
        params.put("fat", fat);
        params.put("date", date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
