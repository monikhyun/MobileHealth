package com.example.resister.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
public class LoginRequest extends StringRequest {


    private Map<String,String> parameters;
    public LoginRequest(String userID, String password, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/login/validate/" + userID +"/"+password, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("password", password);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}