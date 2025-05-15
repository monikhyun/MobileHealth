package com.example.resister.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
public class LoginRequest extends StringRequest {


    private Map<String,String> parameters;
    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/login/validate/" + userID +"/"+userPassword, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}