package com.example.resister.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
public class RegisterRequest extends StringRequest {

    private Map<String,String> parameters;
    public RegisterRequest(String userID, String userPassword, String userGender, String userMajor,
                           String userEmail, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/register/ok", listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userID);
        parameters.put("userPassword", userPassword);
        if (userGender.equals("남성")) {
            parameters.put("userGender", "MALE");
        } else if (userGender.equals("여성")) {
            parameters.put("userGender", "FEMALE");
        } else {
            parameters.put("userGender", userGender); // fallback
        }
        parameters.put("userMajor", userMajor);
        parameters.put("userEmail", userEmail);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}