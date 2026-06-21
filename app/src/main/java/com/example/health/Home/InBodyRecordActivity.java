// 패키지 및 필요한 클래스 import
package com.example.health.Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.example.health.R;
import com.example.health.Request.Home.InBodyRecordRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * InBodyRecordActivity
 * ---------------------
 * 새로운 인바디 기록을 입력받아 서버에 저장하는 기능을 담당하는 액티비티.
 * 체중, SMM, LBM, BMI, 체지방률 등 항목 입력 후 저장 요청 전송.
 */
public class InBodyRecordActivity extends AppCompatActivity {

    // 상단바 버튼들
    private ImageView btnProfile, btnAlarm, btnSetting;

    // 날짜 표시 및 입력 필드들
    private TextView dateTv;
    private EditText heightEt, smmEt, lbmEt, bmiEt, fatEt;

    // 취소 및 제출 버튼
    private Button btnCancel, buttonSubmit;

    /**
     * 액티비티 시작 시 호출됨
     * 레이아웃 설정 및 뷰 바인딩, 이벤트 처리 등을 수행
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbody_edit);  // 레이아웃 XML 적용

        // --- 1) 뷰 바인딩 ---
        btnProfile = findViewById(R.id.btn_profile);
        btnAlarm   = findViewById(R.id.btn_alarm);
        btnSetting = findViewById(R.id.btn_setting);

        dateTv     = findViewById(R.id.date);
        heightEt   = findViewById(R.id.height);
        smmEt      = findViewById(R.id.smm);
        lbmEt      = findViewById(R.id.lbm);
        bmiEt      = findViewById(R.id.bmi);
        fatEt      = findViewById(R.id.fat);

        btnCancel     = findViewById(R.id.btnCancel);
        buttonSubmit  = findViewById(R.id.buttonSubmit);

        // --- 2) 현재 날짜를 화면에 표시 ---
        LocalDate today = LocalDate.now();
        dateTv.setText(today.format(DateTimeFormatter.ISO_DATE));  // yyyy-MM-dd 형식
        Log.d("asdasdasd", "asd" + today);  // 로그 출력 (디버깅용)

        // --- 3) 입력 필드: 소수점 숫자 입력 허용 설정 ---
        int decimalFlags = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        heightEt.setInputType(decimalFlags);
        smmEt   .setInputType(decimalFlags);
        lbmEt   .setInputType(decimalFlags);
        bmiEt   .setInputType(decimalFlags);
        fatEt   .setInputType(decimalFlags);

        // --- 4) 취소 버튼 클릭 시 액티비티 종료 ---
        btnCancel.setOnClickListener(v -> finish());

        // --- 5) 제출 버튼 클릭 시 서버에 기록 요청 전송 ---
        buttonSubmit.setOnClickListener(v -> {
            // JWT 및 유저 ID 불러오기
            String jwt = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("JWT_TOKEN", null);
            String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("USER_ID", null);

            // 로그인 정보가 없으면 요청 중단
            if (jwt == null || userId == null) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 입력 필드 값 가져오기
            String date = dateTv.getText().toString().trim();
            String w    = heightEt.getText().toString().trim();
            String smm  = smmEt.getText().toString().trim();
            String lbm  = lbmEt.getText().toString().trim();
            String bmi  = bmiEt.getText().toString().trim();
            String fat  = fatEt.getText().toString().trim();

            // --- 필수 입력 항목이 비어있으면 사용자에게 안내 ---
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
                // --- 서버 전송용 InBodyRecordRequest 객체 생성 ---
                InBodyRecordRequest req = new InBodyRecordRequest(
                        jwt, userId, date, w, smm, lbm, bmi, fat,
                        response -> {
                            // --- 저장 성공 시 알림 후 마이페이지로 이동 ---
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            TextView tv = toastView.findViewById(R.id.text_toast_message);
                            tv.setText("인바디 기록 저장!");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();

                            // 마이페이지로 전환
                            startActivity(new Intent(this, MyPageActivity.class));
                            finish();
                        },
                        error -> {
                            // --- 저장 실패 시 사용자에게 실패 알림 ---
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

                // --- Volley 요청 큐에 추가하여 서버 전송 ---
                Volley.newRequestQueue(this).add(req);

            } catch (Exception ex) {
                // --- 예외 발생 시 사용자에게 오류 메시지 ---
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

        // --- (선택) 상단바 버튼 클릭 처리 (현재는 미사용 상태) ---
        btnProfile.setOnClickListener(v -> finish());  // 현재는 뒤로가기 역할
        btnAlarm.setOnClickListener(v -> {});
        btnSetting.setOnClickListener(v -> {});
    }
}