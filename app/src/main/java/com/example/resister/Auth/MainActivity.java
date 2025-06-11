package com.example.resister.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resister.Diet.ExerciseListActivity;

public class MainActivity extends AppCompatActivity {

    TextView tvId;

    ImageView iconWorkout,icon_meal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvId = findViewById(R.id.tvId);

        iconWorkout = findViewById(R.id.icon_workout);
        icon_meal = findViewById(R.id.icon_meal);
        // 1) SharedPreferences 열기
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // 2) USER_ID 읽어오기 (없으면 null)
        String userID = prefs.getString("USER_ID", null);

        if (userID != null) {
            // 3) 사용자 환영 메시지
            tvId.setText(userID + "님 환영합니다.");
        } else {
            // 로그인 정보가 없으면 로그인 화면으로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        iconWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExerciseListActivity.class);
                startActivity(intent);
            }
        });

        icon_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });

    }
}