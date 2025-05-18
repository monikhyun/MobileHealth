package com.example.resister;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.resister.Request.DietInsertRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DietAddActivity extends AppCompatActivity {
    private EditText editName, editCal, editCarb, editProtein, editFat;
    private Button btnSubmit,btnCancel,btnBreakfast,btnLunch,btnDinner;
    private String userID;
    private String mealtime = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietadd);

        editName = findViewById(R.id.editMealName);
        editCal = findViewById(R.id.editCalorie);
        editCarb = findViewById(R.id.editCarb);
        editProtein = findViewById(R.id.editProtein);
        editFat = findViewById(R.id.editFat);
        btnSubmit = findViewById(R.id.buttonSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        btnBreakfast = findViewById(R.id.btnBreakfast);
        btnLunch = findViewById(R.id.btnLunch);
        btnDinner = findViewById(R.id.btnDinner);
        userID = getIntent().getStringExtra("userID");


        btnBreakfast.setOnClickListener(v -> setMealtime("아침", btnBreakfast));
        btnLunch.setOnClickListener(v -> setMealtime("점심", btnLunch));
        btnDinner.setOnClickListener(v -> setMealtime("저녁", btnDinner));

        btnSubmit.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String cal = editCal.getText().toString();
            String carb = editCarb.getText().toString();
            String protein = editProtein.getText().toString();
            String fat = editFat.getText().toString();

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Response.Listener<String> listener = response -> {
                setResult(RESULT_OK);
                finish();
            };

            DietInsertRequest request = new DietInsertRequest(userID, name, cal, carb, protein, fat, today, mealtime, listener);
            RequestQueue queue = Volley.newRequestQueue(DietAddActivity.this);
            queue.add(request);
        });
        btnCancel.setOnClickListener(v -> {
            finish(); // 현재 화면 닫고 이전 DietActivity로 돌아가기
        });
    }
    private void setMealtime(String time, Button selectedButton) {
        mealtime = time;

        // 모든 버튼 배경 초기화 (개별 인스턴스 사용)
        btnBreakfast.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
        btnLunch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
        btnDinner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));

        // 선택된 버튼만 배경 강조
        selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB6C1")));
    }
}
