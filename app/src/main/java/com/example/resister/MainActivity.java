package com.example.resister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resister.R;

public class MainActivity extends AppCompatActivity {

    TextView tvId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvId = findViewById(R.id.tvId);
        Intent inIntent = getIntent();
        String userID = SessionManager.getInstance().getUserId();
        tvId.setText(userID + "님 환영합니다.");
    }
}