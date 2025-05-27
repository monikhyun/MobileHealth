package com.example.resister;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.resister.Request.DietInsertRequest;

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
    private String userId;
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
        this.userId = getIntent().getStringExtra("userId");
        if (this.userId == null || this.userId.isEmpty()) {
            Log.e("DIET_ERROR", "userId is null!");
            return;
        }

        btnBreakfast.setOnClickListener(v -> setMealtime("아침", btnBreakfast));
        btnLunch.setOnClickListener(v -> setMealtime("점심", btnLunch));
        btnDinner.setOnClickListener(v -> setMealtime("저녁", btnDinner));

        btnSubmit.setOnClickListener(v -> {
            // 모든 값 null/empty 체크
            String name = editName.getText().toString();
            BigDecimal cal = new BigDecimal(editCal.getText().toString());
            Integer carb = Integer.valueOf(editCarb.getText().toString());
            Integer protein = Integer.valueOf(editProtein.getText().toString());
            Integer fat = Integer.valueOf(editFat.getText().toString());
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // 서버에서 원하는 ENUM 값으로 변환
            String mealTimeEnum = "";
            switch (mealtime) {
                case "아침":
                    mealTimeEnum = "BREAKFAST";
                    break;
                case "점심":
                    mealTimeEnum = "LUNCH";
                    break;
                case "저녁":
                    mealTimeEnum = "DINNER";
                    break;
                default:
                    mealTimeEnum = "";
                    break;
            }

            // JSON body 만들기
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("userId", this.userId);
                jsonBody.put("name", name);
                jsonBody.put("mealtime", mealTimeEnum);
                jsonBody.put("calories", cal);
                jsonBody.put("carb", carb);
                jsonBody.put("protein", protein);
                jsonBody.put("fat", fat);
                jsonBody.put("date", today);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "입력값 오류!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("DIET_JSON_BODY", jsonBody.toString()); // 반드시 찍어줘

            DietInsertRequest request = new DietInsertRequest(
                    this.userId,
                    jsonBody,
                    response -> {
                        // response에는 서버가 보낸 plain text 메시지가 담김
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "등록 실패: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
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
