package com.example.resister;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.resister.Request.DietInsertRequest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
            BigDecimal cal = new BigDecimal(editCal.getText().toString());
            int carb = Integer.parseInt(editCarb.getText().toString());
            int protein = Integer.parseInt(editProtein.getText().toString());
            int fat = Integer.parseInt(editFat.getText().toString());
            LocalDate today = LocalDate.now();
            String MealTime;
            if (mealtime.equals("아침")) {
                MealTime = "BREAKFAST";
            } else if (mealtime.equals("점심")) {
                MealTime = "LUNCH";
            } else if (mealtime.equals("저녁")) {
                MealTime = "DINNER";
            } else {
                MealTime = "";
            }
            Log.d("DIET_DEBUG", "carb: " + carb); // 개별 값 출력

            // JSON 바디를 직접 생성
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("userId", "v");
                jsonBody.put("name", name);
                jsonBody.put("mealtime", MealTime);
                jsonBody.put("calories", cal);
                jsonBody.put("carb", carb);
                jsonBody.put("protein", protein);
                jsonBody.put("fat", fat);
                jsonBody.put("date", today.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return; // JSON 생성 실패 시 종료
            }

            Log.d("DIET_JSON_BODY", jsonBody.toString()); // 전체 JSON 확인용 로그

            Response.Listener<org.json.JSONObject> listener = response -> {
                setResult(RESULT_OK);
                finish();
            };

            com.android.volley.Response.ErrorListener errorListener = error -> {
                error.printStackTrace();
            };

            // 이제 직접 만든 jsonBody를 넘김
            DietInsertRequest request = new DietInsertRequest(jsonBody, listener, errorListener);

            RequestQueue queue = Volley.newRequestQueue(DietAddActivity.this);
            queue.add(request);
        });        btnCancel.setOnClickListener(v -> {
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
