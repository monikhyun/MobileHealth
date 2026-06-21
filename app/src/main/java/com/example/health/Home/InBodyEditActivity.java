// 패키지 위치 선언
package com.example.health.Home;

// 안드로이드 및 라이브러리 import
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.health.Request.Home.InBodyEditRequest;

import org.json.JSONObject;

/**
 * InBodyEditActivity
 * ------------------
 * 사용자가 인바디 데이터를 수정하는 화면을 담당하는 액티비티.
 * 날짜, 체중, 골격근량(SMM), 제지방량(LBM), BMI, 체지방률을 수정 가능.
 */
public class InBodyEditActivity extends AppCompatActivity {

    // 날짜 텍스트 뷰 및 인바디 값 입력 필드
    private TextView dateTv;
    private EditText heightEt, smmEt, lbmEt, bmiEt, fatEt;

    // 취소 및 저장 버튼
    private Button btnCancel, buttonSubmit;

    // 로그인 인증 정보
    private String jwt, userId;

    /**
     * 액티비티가 생성될 때 호출됨. 초기 설정 및 뷰 바인딩 수행.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbody_load_edit);  // 레이아웃 XML 적용

        // --- 1) 뷰 연결 ---
        dateTv       = findViewById(R.id.date);       // 날짜 표시 텍스트뷰
        heightEt     = findViewById(R.id.height);     // 체중 입력 필드
        smmEt        = findViewById(R.id.smm);        // 골격근량 입력 필드
        lbmEt        = findViewById(R.id.lbm);        // 제지방량 입력 필드
        bmiEt        = findViewById(R.id.bmi);        // BMI 입력 필드
        fatEt        = findViewById(R.id.fat);        // 체지방률 입력 필드
        btnCancel    = findViewById(R.id.btnCancel);  // 취소 버튼
        buttonSubmit = findViewById(R.id.buttonSubmit); // 저장 버튼

        // --- 2) SharedPreferences 에서 JWT 토큰과 사용자 ID 읽기 ---
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        jwt    = prefs.getString("JWT_TOKEN", null);
        userId = prefs.getString("USER_ID",   null);

        // --- 3) 소수점 포함 숫자만 입력할 수 있도록 입력 타입 설정 ---
        int decFlags = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        heightEt.setInputType(decFlags);
        smmEt.setInputType(decFlags);
        lbmEt.setInputType(decFlags);
        bmiEt.setInputType(decFlags);
        fatEt.setInputType(decFlags);

        // --- 4) 이전 화면에서 전달된 인바디 데이터로 입력 필드 채우기 ---
        Intent i = getIntent();  // 전달받은 인텐트
        dateTv.setText(i.getStringExtra("edit_date"));
        heightEt.setText(i.getStringExtra("edit_weight"));
        smmEt.setText(i.getStringExtra("edit_smm"));
        lbmEt.setText(i.getStringExtra("edit_lbm"));
        bmiEt.setText(i.getStringExtra("edit_bmi"));
        fatEt.setText(i.getStringExtra("edit_fat_percent"));

        // --- 5) 취소 버튼 클릭 시 현재 화면 종료 ---
        btnCancel.setOnClickListener(v -> finish());

        // --- 6) 저장 버튼 클릭 시 서버로 수정 요청 보내기 ---
        buttonSubmit.setOnClickListener(v -> {
            // 입력 필드의 값들 읽어오기
            String date       = dateTv.getText().toString().trim();
            String weight     = heightEt.getText().toString().trim();
            String smm        = smmEt.getText().toString().trim();
            String lbm        = lbmEt.getText().toString().trim();
            String bmi        = bmiEt.getText().toString().trim();
            String fatPercent = fatEt.getText().toString().trim();

            // 디버깅용 로그 출력
            Log.d("EDIT_DEBUG",
                    "date=" + date
                            + " weight=" + weight
                            + " smm=" + smm
                            + " lbm=" + lbm
                            + " bmi=" + bmi
                            + " fat=" + fatPercent
            );

            // 로그인 정보가 없을 경우 토스트 알림 후 중단
            if (jwt == null || userId == null) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 필수 항목이 비어있을 경우 사용자에게 입력 요청
            if (date.isEmpty() || weight.isEmpty() || smm.isEmpty() ||
                    lbm.isEmpty() || bmi.isEmpty() || fatPercent.isEmpty()) {
                View toastView = LayoutInflater.from(this)
                        .inflate(R.layout.toast_friend_request, null);
                ((TextView) toastView.findViewById(R.id.text_toast_message))
                        .setText("모든 값을 입력해주세요.");
                Toast t = new Toast(this);
                t.setView(toastView);
                t.setDuration(Toast.LENGTH_SHORT);
                t.show();
                return;
            }

            try {
                // 수정 요청 객체 생성
                InBodyEditRequest req = new InBodyEditRequest(
                        jwt, userId,
                        date, weight, smm, lbm, bmi, fatPercent,
                        response -> {
                            // 수정 성공 시 사용자에게 알림 후 마이페이지로 이동
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            ((TextView) toastView.findViewById(R.id.text_toast_message))
                                    .setText("수정 완료");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                            startActivity(new Intent(this, MyPageActivity.class));
                            finish();
                        },
                        error -> {
                            // 요청 실패 시 사용자에게 알림
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            ((TextView) toastView.findViewById(R.id.text_toast_message))
                                    .setText("수정 실패");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                        }
                );

                // Volley 네트워크 큐에 요청 추가 (비동기 처리)
                Volley.newRequestQueue(this).add(req);

            } catch (Exception ex) {
                // 예외 발생 시 사용자에게 오류 표시
                ex.printStackTrace();
                View toastView = LayoutInflater.from(this)
                        .inflate(R.layout.toast_friend_request, null);
                ((TextView) toastView.findViewById(R.id.text_toast_message))
                        .setText("요청 생성 오류");
                Toast t = new Toast(this);
                t.setView(toastView);
                t.setDuration(Toast.LENGTH_SHORT);
                t.show();
            }
        });
    }
}