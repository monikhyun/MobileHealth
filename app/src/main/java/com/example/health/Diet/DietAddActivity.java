package com.example.health.Diet;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.health.R;
import com.example.health.Request.Diet.DietInsertRequest;
import com.example.health.Request.Diet.DietUpdateRequest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DietAddActivity extends AppCompatActivity {
    private EditText editName, editCal, editCarb, editProtein, editFat;
    private Button btnSubmit, btnCancel, btnBreakfast, btnLunch, btnDinner;

    private String mealtime = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietadd);

        Intent intent = getIntent();
        boolean isEdit = "edit".equals(intent.getStringExtra("mode"));
        Long dietId = intent.getLongExtra("dietId", -1);
        Log.d("dietid",""+dietId);
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

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userID = prefs.getString("USER_ID", null);  // 없으면 null 반환

        if (userID != null) {
            // 정상적으로 꺼내진 경우
            Log.d("USER_ID", userID);
        } else {
            // 저장된 값이 없을 경우
            Log.d("USER_ID", "No userId found");
        }
        btnBreakfast.setOnClickListener(v -> setMealtime("아침", btnBreakfast));
        btnLunch.setOnClickListener(v -> setMealtime("점심", btnLunch));
        btnDinner.setOnClickListener(v -> setMealtime("저녁", btnDinner));

        // 편집 모드
        if (isEdit) {
            editName.setText(intent.getStringExtra("name"));
            editCal.setText(String.valueOf(intent.getDoubleExtra("calories", 0)));
            editCarb.setText(String.valueOf(intent.getIntExtra("carb", 0)));
            editProtein.setText(String.valueOf(intent.getIntExtra("protein", 0)));
            editFat.setText(String.valueOf(intent.getIntExtra("fat", 0)));
            String tm = intent.getStringExtra("mealtime");
            if ("BREAKFAST".equals(tm)) setMealtime("아침", btnBreakfast);
            else if ("LUNCH".equals(tm)) setMealtime("점심", btnLunch);
            else if ("DINNER".equals(tm)) setMealtime("저녁", btnDinner);
            btnSubmit.setText("수정");
        }

        btnSubmit.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String mealTimeEnum = "";
            switch (mealtime) {
                case "아침": mealTimeEnum = "BREAKFAST"; break;
                case "점심": mealTimeEnum = "LUNCH"; break;
                case "저녁": mealTimeEnum = "DINNER"; break;
            }
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id",dietId);
                jsonBody.put("userId", userID);
                jsonBody.put("name", editName.getText().toString());
                jsonBody.put("mealtime", mealTimeEnum);
                jsonBody.put("calories", new BigDecimal(editCal.getText().toString()));
                jsonBody.put("carb", Integer.valueOf(editCarb.getText().toString()));
                jsonBody.put("protein", Integer.valueOf(editProtein.getText().toString()));
                jsonBody.put("fat", Integer.valueOf(editFat.getText().toString()));
                jsonBody.put("date", today);
            } catch (JSONException e) {
                Toast.makeText(this, "입력값 오류!", Toast.LENGTH_SHORT).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(this);
            // 수정 모드
            if (isEdit) {
                Log.d("mealID",""+dietId);
                String url = "http://10.0.2.2:8080/api/diet/record/" + userID + "/" + dietId + "/update";
                DietUpdateRequest updateReq = new DietUpdateRequest(
                        userID, dietId, jsonBody,
                        resp -> { Toast.makeText(this, resp, Toast.LENGTH_SHORT).show(); setResult(RESULT_OK); finish(); },
                        err -> { err.printStackTrace(); Toast.makeText(this, "수정 실패", Toast.LENGTH_LONG).show(); }
                );
                queue.add(updateReq);
                // 처음 저장
            } else {
                DietInsertRequest insertReq = new DietInsertRequest(
                        userID, jsonBody,
                        resp -> { Toast.makeText(this, resp, Toast.LENGTH_SHORT).show(); setResult(RESULT_OK); finish(); },
                        err -> { err.printStackTrace(); Toast.makeText(this, "등록 실패", Toast.LENGTH_LONG).show(); }
                );
                queue.add(insertReq);
            }
        });

        btnCancel.setOnClickListener(v -> finish());
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

