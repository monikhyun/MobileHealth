<<<<<<<< HEAD:app/src/main/java/com/example/health/Request/Diet/DietRequest.java
package com.example.health.Request.Diet;
========
package com.example.resister.Request.Diet;
>>>>>>>> feat/diet:app/src/main/java/com/example/resister/Request/Diet/DietRequest.java

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class DietRequest extends StringRequest {
    private final Map<String, String> parameters;

    public DietRequest(String userId, String date, Response.Listener<String> listener) {
        super(Method.POST, "http://10.0.2.2:8080/api/diet/record/" + userId, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("date", date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
