// com/example/health/Home/InBodyEditActivity.java
package com.example.health.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.example.health.R;
import com.example.health.Request.Home.InBodyEditRequest;
import org.json.JSONObject;

public class InBodyEditActivity extends AppCompatActivity {

    private TextView dateTv;
    private EditText heightEt, smmEt, lbmEt, bmiEt, fatEt;
    private Button btnCancel, buttonSubmit;
    private String jwt, userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbody_load_edit);

        // --- 1) 뷰 바인딩 ---
        dateTv       = findViewById(R.id.date);
        heightEt     = findViewById(R.id.height);
        smmEt        = findViewById(R.id.smm);
        lbmEt        = findViewById(R.id.lbm);
        bmiEt        = findViewById(R.id.bmi);
        fatEt        = findViewById(R.id.fat);
        btnCancel    = findViewById(R.id.btnCancel);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // --- 2) JWT / USER_ID ---
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        jwt    = prefs.getString("JWT_TOKEN", null);
        userId = prefs.getString("USER_ID",   null);

        // --- 3) 소수점 입력 허용 ---
        int decFlags = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        heightEt.setInputType(decFlags);
        smmEt   .setInputType(decFlags);
        lbmEt   .setInputType(decFlags);
        bmiEt   .setInputType(decFlags);
        fatEt   .setInputType(decFlags);

        // --- 4) 인텐트로 넘어온 기존 값으로 미리 채우기 ---
        Intent i = getIntent();
        dateTv  .setText(i.getStringExtra("edit_date"));
        heightEt.setText(i.getStringExtra("edit_weight"));
        smmEt   .setText(i.getStringExtra("edit_smm"));
        lbmEt   .setText(i.getStringExtra("edit_lbm"));
        bmiEt   .setText(i.getStringExtra("edit_bmi"));
        fatEt   .setText(i.getStringExtra("edit_fat_percent"));



        // --- 5) 취소 버튼 ---
        btnCancel.setOnClickListener(v -> finish());

        // --- 6) 확인 버튼: 수정 요청 보내기 ---
        buttonSubmit.setOnClickListener(v -> {
            // 입력값 읽기
            String date       = dateTv.getText().toString().trim();
            String weight     = heightEt.getText().toString().trim();
            String smm        = smmEt.getText().toString().trim();
            String lbm        = lbmEt.getText().toString().trim();
            String bmi        = bmiEt.getText().toString().trim();
            String fatPercent = fatEt.getText().toString().trim();

            Log.d("EDIT_DEBUG",
                    "date=" + date
                            + " weight=" + weight
                            + " smm=" + smm
                            + " lbm=" + lbm
                            + " bmi=" + bmi
                            + " fat=" + fatPercent
            );

            // 로그인 정보 체크
            if (jwt == null || userId == null) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 빈칸 체크
            if (date.isEmpty() || weight.isEmpty() || smm.isEmpty() ||
                    lbm.isEmpty()  || bmi.isEmpty()   || fatPercent.isEmpty()) {
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
                // 요청 실행
                Volley.newRequestQueue(this).add(req);

            } catch (Exception ex) {
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