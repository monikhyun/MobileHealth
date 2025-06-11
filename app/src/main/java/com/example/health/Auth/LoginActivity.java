<<<<<<<< HEAD:app/src/main/java/com/example/health/Auth/LoginActivity.java
package com.example.health.Auth;
========
package com.example.resister.Auth;
>>>>>>>> feat/diet:app/src/main/java/com/example/resister/Auth/LoginActivity.java

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import com.example.health.R;
import com.example.health.Request.LoginRequest;

import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tvMembership = findViewById(R.id.tvMembership);
        tvMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        final EditText etId = findViewById(R.id.etId);
        final EditText etPasswd = findViewById(R.id.etPasswd);
        final Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = etId.getText().toString();
                final String userPasswd = etPasswd.getText().toString();

                // 응답으로 NetworkResponse를 받도록 리스너 타입 변경
                Response.Listener<NetworkResponse> responseListener = new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            // 1) 바디 파싱
                            String body = new String(response.data, StandardCharsets.UTF_8);
                            JSONObject jsonResponse = new JSONObject(body);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                String authHeader = response.headers.get("Authorization");


                                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear().apply();
                                editor.putString("JWT_TOKEN", authHeader);
                                editor.putString("USER_ID", userID);
                                editor.apply();
                                Log.d("jwt", authHeader);
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "아이디 또는 비밀번호를 확인해주세요.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,
                                    "응답 처리 중 오류가 발생했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                // 에러 리스너
                Response.ErrorListener errorListener = error -> {
                    Toast.makeText(LoginActivity.this,
                            "서버 연결에 실패했습니다.",
                            Toast.LENGTH_SHORT).show();
                };

                // LoginRequest 생성
                LoginRequest loginRequest = new LoginRequest(
                        userID,
                        userPasswd,
                        responseListener,
                        errorListener
                );

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
