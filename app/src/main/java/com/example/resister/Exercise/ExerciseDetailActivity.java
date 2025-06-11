package com.example.resister.Exercise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.resister.ExerciseRecordDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class ExerciseDetailActivity extends AppCompatActivity {
    private LinearLayout setContainer;
    private String exerciseName;
    private String date;
    private String userId;

    TextView text_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);

        // ❗ 지역변수로 덮어쓰기 금지
        exerciseName = getIntent().getStringExtra("EXERCISE_NAME");
        date = getIntent().getStringExtra("DATE"); // "yyyy-MM-dd"



        setContainer = findViewById(R.id.set_container);

        // 1️⃣ 운동 메타데이터 먼저 요청해서 이름, 설명, 이미지 표시
        try {
            String metaUrl = "http://10.0.2.2:8080/api/exercise/add/" + userId + "/" + exerciseName;

            JsonObjectRequest metaReq = new JsonObjectRequest(
                    Request.Method.GET, metaUrl, null,
                    resp -> {
                        Log.d("EXERCISE_META", "받은 응답: " + resp.toString());

                        runOnUiThread(() -> {
                            // 운동 이름 표시
                            TextView exercise_name = findViewById(R.id.text_exercise_name);
                            String name = resp.optString("exerciseName", "이름 없음");
                            exercise_name.setText(name);
                            Log.d("EXERCISE_META", "운동 이름 setText 적용됨: " + name);

                            // 설명 표시
                            TextView tvTip = findViewById(R.id.text_tip_content);
                            String description = resp.optString("description", "설명 없음");
                            tvTip.setText(description);
                            Log.d("EXERCISE_META", "운동 팁 setText 적용됨: " + description);

                            // 이미지 표시
                            ImageView iv = findViewById(R.id.image_exercise);
                            String imagePath = resp.optString("exerciseImagePath", null);
                            if (imagePath != null) {
                                int resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
                                if (resId != 0) {
                                    iv.setImageResource(resId);
                                } else {
                                    Log.w("EXERCISE_META", "리소스 이름 불일치, 기본 이미지 사용");
                                    iv.setImageResource(R.drawable.logo);
                                }
                            } else {
                                iv.setImageResource(R.drawable.logo);
                            }
                        });
                    },
                    err -> {
                        Log.e("EXERCISE_META", "메타 요청 실패", err);
                        Toast.makeText(this, "메타데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
            );
            Volley.newRequestQueue(this).add(metaReq);        } catch (Exception e) {
            e.printStackTrace();
        }

        // 기본 세트 1개 추가
        addSetView();

        // 추가 버튼 추가
        View addButtonView = LayoutInflater.from(this).inflate(R.layout.item_add_button, setContainer, false);
        setContainer.addView(addButtonView);
        addButtonView.findViewById(R.id.btn_add_set).setOnClickListener(v -> {
            setContainer.removeView(addButtonView);
            addSetView();
            setContainer.addView(addButtonView);
        });

        loadRecords();
    }

    private void addSetView() {
        View setView = LayoutInflater.from(this).inflate(R.layout.item_exercise_set, setContainer, false);

        TextView setNumberText = setView.findViewById(R.id.text_set_number);
        EditText inputKg        = setView.findViewById(R.id.input_kg);
        EditText inputReps      = setView.findViewById(R.id.input_reps);
        CheckBox completeCheck  = setView.findViewById(R.id.check_complete);
        View removeButton       = setView.findViewById(R.id.btn_remove_set);

        // 제거 버튼
        if (removeButton != null) {
            removeButton.setOnClickListener(v -> {
                setContainer.removeView(setView);
                renumberSets();
            });
        }

        // ✅ 완료 체크박스에 서버 저장 리스너 붙이기
        completeCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                Log.d("EXERCISE_SAVE", "체크 상태 변경됨 → isChecked: " + isChecked);

                // 포커스 해제
                inputKg.clearFocus();
                inputReps.clearFocus();

                String kgStr   = inputKg.getText().toString().trim();
                String repsStr = inputReps.getText().toString().trim();
                if (kgStr.isEmpty() || repsStr.isEmpty()) {
                    Toast.makeText(this, "무게와 반복 수를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                int setNum  = Integer.parseInt(setNumberText.getText().toString());
                int repCount = Integer.parseInt(repsStr);
                BigDecimal weight = new BigDecimal(kgStr);

                // URL 인코딩
                String encodedName = URLEncoder.encode(exerciseName, "UTF-8");
                String recUrl = "http://10.0.2.2:8080/api/exercise/add/"
                        + userId + "/" + date + "/" + encodedName + "/record";

                // JSON 바디 구성
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("setCount", setNum);
                jsonBody.put("count", repCount);
                jsonBody.put("weight", weight);
                jsonBody.put("date", date);
                jsonBody.put("done", isChecked);

                Log.d("EXERCISE_SAVE", "보낼 JSON: " + jsonBody.toString());

                // StringRequest 로 POST
                StringRequest recReq = new StringRequest(
                        Request.Method.POST,
                        recUrl,
                        response -> {
                            // 서버가 응답하는 "1세트 저장완료!" 등의 문자열을 그대로 토스트로
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                            loadRecords();
                        },
                        error -> {
                            Log.e("EXERCISE_SAVE", "서버 저장 실패", error);
                            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                        }
                ) {
                    @Override
                    public byte[] getBody() {
                        return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                Volley.newRequestQueue(this).add(recReq);

            } catch (Exception ex) {
                Log.e("EXERCISE_SAVE", "예외 발생!", ex);
                Toast.makeText(this, "입력값 오류", Toast.LENGTH_SHORT).show();
            }
        });

        // 뷰 추가 위치 계산＋삽입
        int index = setContainer.getChildCount();
        if (index > 0 && setContainer.getChildAt(index - 1).findViewById(R.id.btn_add_set) != null) {
            index--;  // 마지막에 있는 “추가” 버튼 앞에 삽입
        }
        setContainer.addView(setView, index);

        // 세트 번호 다시 매기기
        renumberSets();
    }
    private void renumberSets() {
        int number = 1;
        for (int i = 0; i < setContainer.getChildCount(); i++) {
            View child = setContainer.getChildAt(i);
            if (child.findViewById(R.id.btn_add_set) != null) continue;
            TextView numberView = child.findViewById(R.id.text_set_number);
            if (numberView != null) {
                numberView.setText(String.valueOf(number++));
            }
        }
    }

    private void loadRecords() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);

        String url = "http://10.0.2.2:8080/api/exercise/add/"
                + userId + "/" + date + "/" + exerciseName;

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, url, null,
                arr -> {
                    // ① 메타데이터가 끝나는 인덱스 계산
                    //    text_tip_content 뷰의 index + 1 이 고정 뷰 개수
                    int staticCount = setContainer.indexOfChild(
                            findViewById(R.id.text_tip_content)
                    ) + 1;

                    // ② 고정 뷰(staticCount) 이후의 모든 뷰(세트 리스트, 버튼)만 제거
                    int total = setContainer.getChildCount();
                    if (total > staticCount) {
                        setContainer.removeViews(staticCount, total - staticCount);
                    }

                    // ③ 서버에서 받아온 기록이 없으면 빈 입력폼 하나
                    if (arr.length() == 0) {
                        addSetView();
                    } else {
                        // ④ 기록만큼 addRecordView() 호출
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.optJSONObject(i);
                            double rawWeight = o.optDouble("weight", 0.0);
                            if (Double.isNaN(rawWeight) || Double.isInfinite(rawWeight)) {
                                rawWeight = 0.0;
                            }
                            BigDecimal weightBd = BigDecimal.valueOf(rawWeight)
                                    .setScale(2, RoundingMode.HALF_UP);

                            ExerciseRecordDto dto = new ExerciseRecordDto(
                                    o.optInt("setCount"),
                                    o.optInt("count"),
                                    weightBd,
                                    o.optBoolean("done")
                            );
                            addRecordView(dto, exerciseName);
                        }
                    }

                    // ⑤ “추가” 버튼 다시 붙이기
                    View addBtn = LayoutInflater.from(this)
                            .inflate(R.layout.item_add_button, setContainer, false);
                    setContainer.addView(addBtn);
                    addBtn.findViewById(R.id.btn_add_set)
                            .setOnClickListener(v -> {
                                setContainer.removeView(addBtn);
                                addSetView();
                                setContainer.addView(addBtn);
                            });
                },
                err -> Toast.makeText(this, "기록 조회 실패", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }
    private void addRecordView(ExerciseRecordDto dto, String exerciseName1) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_exercise_set, setContainer, false);
        TextView tvNum = view.findViewById(R.id.text_set_number);
        EditText kg = view.findViewById(R.id.input_kg);
        EditText reps = view.findViewById(R.id.input_reps);
        CheckBox cb = view.findViewById(R.id.check_complete);
        ImageButton btnDel = view.findViewById(R.id.btn_remove_set);

        tvNum.setText(String.valueOf(dto.getSetCount()));
        kg.setText(dto.getWeight().toString());
        reps.setText(String.valueOf(dto.getCount()));
        cb.setChecked(dto.getDone());
        btnDel.setOnClickListener(v -> {
            try {
                String encodedName = URLEncoder.encode(exerciseName1, "UTF-8");
                String delUrl = "http://10.0.2.2:8080/api/exercise/add/"
                        + userId + "/" + date + "/" + encodedName
                        + "/delete/" + dto.getSetCount();

                // JsonObjectRequest → StringRequest 로 교체
                StringRequest delReq = new StringRequest(
                        Request.Method.PUT,
                        delUrl,
                        response -> {
                            // response 에는 "2세트 기록삭제" 같은 문자열이 들어옴
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                            loadRecords();
                        },
                        error -> {
                            Log.e("DELETE_REQ", "삭제 실패", error);
                            Toast.makeText(this, "삭제 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // 필요 시 헤더 추가
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Accept", "text/plain");
                        return headers;
                    }
                };

                Volley.newRequestQueue(this).add(delReq);
            } catch (Exception e) {
                Log.e("DELETE_REQ", "URL 인코딩 실패", e);
            }
        });        cb.setOnCheckedChangeListener((b, isChecked) -> {
            try {
                Log.d("EXERCISE_SAVE", "체크 상태 변경됨 → isChecked: " + isChecked);

                kg.clearFocus();
                reps.clearFocus();

                String kgStr = kg.getText().toString().trim();
                String repsStr = reps.getText().toString().trim();

                if (kgStr.isEmpty() || repsStr.isEmpty()) {
                    Toast.makeText(this, "무게와 반복 수를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                int setNum = Integer.parseInt(tvNum.getText().toString());
                int repCount = Integer.parseInt(repsStr);
                BigDecimal weight = new BigDecimal(kgStr);

                String encodedExerciseName = URLEncoder.encode(exerciseName1, "UTF-8");
                String recUrl = "http://10.0.2.2:8080/api/exercise/add/" + userId + "/" + date + "/" + encodedExerciseName + "/record";

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("setCount", setNum);
                jsonBody.put("count", repCount);
                jsonBody.put("weight", weight);
                jsonBody.put("date", date);
                jsonBody.put("done", isChecked);

                Log.d("EXERCISE_SAVE", "보낼 JSON: " + jsonBody.toString());

                // JsonObjectRequest 대신
                StringRequest recReq = new StringRequest(
                        Request.Method.POST,
                        recUrl,
                        response -> {
                            // response 는 "1세트 저장완료!" 같은 문자열
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                            loadRecords();
                        },
                        error -> {
                            Log.e("EXERCISE_SAVE", "서버 저장 실패", error);
                            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                        }
                ) {
                    @Override
                    public byte[] getBody() {
                        return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };
                Volley.newRequestQueue(this).add(recReq);

            } catch (Exception ex) {
                Log.e("EXERCISE_SAVE", "예외 발생!", ex);
                Toast.makeText(this, "입력값 오류", Toast.LENGTH_SHORT).show();
            }
        });
        setContainer.addView(view);
    }
}
