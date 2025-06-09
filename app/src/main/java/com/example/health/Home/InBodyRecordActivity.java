package com.example.health.Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.health.R;
import com.example.health.Request.Home.InBodyRecordRequest;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InBodyRecordActivity extends AppCompatActivity {
    private ImageView btnProfile, btnAlarm, btnSetting;
    private TextView dateTv;
    private EditText heightEt, smmEt, lbmEt, bmiEt, fatEt;
    private Button btnCancel, buttonSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbody_edit);

        // 1) 뷰 바인딩
        btnProfile = findViewById(R.id.btn_profile);
        btnAlarm = findViewById(R.id.btn_alarm);
        btnSetting = findViewById(R.id.btn_setting);
        dateTv = findViewById(R.id.date);
        heightEt = findViewById(R.id.height);
        smmEt = findViewById(R.id.smm);
        lbmEt = findViewById(R.id.lbm);
        bmiEt = findViewById(R.id.bmi);
        fatEt = findViewById(R.id.fat);
        btnCancel = findViewById(R.id.btnCancel);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        LocalDate today = LocalDate.now();
        dateTv.setText(today.format(DateTimeFormatter.ISO_DATE));
        Log.d("asdasdasd", "asd" +today);


        // 3) EditText 소수점 입력 허용
        int decimalFlags = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        heightEt.setInputType(decimalFlags);
        smmEt.setInputType(decimalFlags);
        lbmEt.setInputType(decimalFlags);
        bmiEt.setInputType(decimalFlags);
        fatEt.setInputType(decimalFlags);

        // 4) 취소 버튼
        btnCancel.setOnClickListener(v -> finish());

        // 5) 제출 버튼
        buttonSubmit.setOnClickListener(v -> {
            String jwt = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("JWT_TOKEN", null);
            String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("USER_ID", null);
            if (jwt == null || userId == null) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = dateTv.getText().toString().trim();
            String w = heightEt.getText().toString().trim();
            String smm = smmEt.getText().toString().trim();
            String lbm = lbmEt.getText().toString().trim();
            String bmi = bmiEt.getText().toString().trim();
            String fat = fatEt.getText().toString().trim();

            // 필수 입력 체크
            if (w.isEmpty() || smm.isEmpty() || lbm.isEmpty()
                    || bmi.isEmpty() || fat.isEmpty()) {
                View toastView = LayoutInflater.from(this)
                        .inflate(R.layout.toast_friend_request, null);
                TextView tv = toastView.findViewById(R.id.text_toast_message);
                tv.setText("모든 입력 값을 완성해주세요.");
                Toast t = new Toast(this);
                t.setView(toastView);
                t.setDuration(Toast.LENGTH_SHORT);
                t.show();
                return;
            }

            try {
                InBodyRecordRequest req = new InBodyRecordRequest(
                        jwt, userId, date, w, smm, lbm, bmi, fat,
                        response -> {
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            TextView tv = toastView.findViewById(R.id.text_toast_message);
                            tv.setText("인바디 기록 저장!");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                            startActivity(new Intent(this, MyPageActivity.class));
                            finish();
                        },
                        error -> {
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            TextView tv = toastView.findViewById(R.id.text_toast_message);
                            tv.setText("기록 저장에 실패했습니다.");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                        }
                );
                Volley.newRequestQueue(this).add(req);

            } catch (Exception ex) {
                ex.printStackTrace();
                View toastView = LayoutInflater.from(this)
                        .inflate(R.layout.toast_friend_request, null);
                TextView tv = toastView.findViewById(R.id.text_toast_message);
                tv.setText("요청 생성 오류");
                Toast t = new Toast(this);
                t.setView(toastView);
                t.setDuration(Toast.LENGTH_SHORT);
                t.show();
            }
        });

        // (선택) 상단 버튼들
        btnProfile.setOnClickListener(v -> finish());
        btnAlarm.setOnClickListener(v -> {/*TODO*/});
        btnSetting.setOnClickListener(v -> {/*TODO*/});
    }
}