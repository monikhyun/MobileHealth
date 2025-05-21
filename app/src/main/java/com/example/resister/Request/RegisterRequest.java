package com.example.resister.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
public class RegisterRequest extends StringRequest {

    private Map<String,String> parameters;
    public RegisterRequest(String userID, String password, String gender, String username,
            Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/register/ok", listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userID);
        parameters.put("password", password);
        parameters.put("username", username);
        if (gender.equals("남성")) {
            parameters.put("gender", "MALE");
        } else if (gender.equals("여성")) {
            parameters.put("gender", "FEMALE");
        } else {
            parameters.put("gender", gender); // fallback
        }
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}