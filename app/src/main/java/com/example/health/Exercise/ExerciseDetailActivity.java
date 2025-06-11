package com.example.health.Exercise;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.health.Auth.MainActivity;
import com.example.health.DTO.ExerciseRecordDto;
import com.example.health.Diet.DietActivity;
import com.example.health.Stats.StatusActivity;
import com.example.health.Friend.FriendListActivity;
import com.example.health.R;
import com.example.health.Request.Exercise.ExerciseDataRequest;
import com.example.health.Request.Exercise.ExerciseRecordDeleteRequest;
import com.example.health.Request.Exercise.ExerciseRecordLoadRequest;
import com.example.health.Request.Exercise.ExerciseRecordRequest;
import com.example.health.Stats.StatusActivity;
import com.example.health.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ExerciseDetailActivity extends AppCompatActivity {
    private LinearLayout setContainer;
    private String exerciseName;
    private String date;           // ISO 포맷 (yyyy-MM-dd)
    private String userId;
    private String selectedDateIso;
    private RequestQueue requestQueue;

    private ImageView iconWorkout, btn_back;
    private TextView textDate;     // 상단에 표시할 날짜 텍스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_exercise_detail);

        // --- 뷰 바인딩 ---
        setContainer = findViewById(R.id.set_container);
        iconWorkout = findViewById(R.id.icon_workout);
        btn_back    = findViewById(R.id.btn_back);
        textDate    = findViewById(R.id.text_date);

        requestQueue = Volley.newRequestQueue(this);



        // 로그인 체크
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);
        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Intent 로부터 EXERCISE_NAME, DATE (ISO: yyyy-MM-dd) 받아오기
        exerciseName = getIntent().getStringExtra("EXERCISE_NAME");
        date         = getIntent().getStringExtra("DATE");
        selectedDateIso = date;

        // 상단 날짜 텍스트를 “yyyy년 M월 d일” 형식으로 설정
        try {
            LocalDate parsed = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            updateDateText(parsed);
        } catch (Exception e) {
            // 파싱 실패 시, 오늘 날짜로 기본 설정
            LocalDate today = LocalDate.now();
            updateDateText(today);
            selectedDateIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        // (1) 운동 메타데이터 가져오기
        loadExerciseMetadata();

        // (2) 기본 세트 뷰 1개 추가
        addSetView();

        // (3) “세트 추가” 버튼 붙이기
        View addButtonView = LayoutInflater.from(this)
                .inflate(R.layout.item_add_button, setContainer, false);
        setContainer.addView(addButtonView);
        addButtonView.findViewById(R.id.btn_add_set)
                .setOnClickListener(v -> {
                    setContainer.removeView(addButtonView);
                    addSetView();
                    setContainer.addView(addButtonView);
                });

        // (4) 서버에 저장된 세트 불러오기
        loadRecords();

        // 뒤로가기 버튼
        iconWorkout.setOnClickListener(v -> finish());
        btn_back.setOnClickListener(v -> finish());
    }

    /**
     * 상단 텍스트(textDate)를 “YYYY년 M월 d일” 형태로 갱신
     */
    private void updateDateText(LocalDate date) {
        String display = String.format(Locale.getDefault(),
                "%d년 %d월 %d일",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        textDate.setText(display);
    }

    /**
     * (1) 운동 메타데이터(이름/설명/이미지) 로드 → ExerciseDataRequest 사용
     */
    private void loadExerciseMetadata() {
        try {
            ExerciseDataRequest req = new ExerciseDataRequest(
                    exerciseName,
                    resp -> {
                        // resp: JSONObject { "exerciseName": ..., "description": ..., "exerciseImagePath": ... }
                        runOnUiThread(() -> {
                            TextView tvName = findViewById(R.id.text_exercise_name);
                            tvName.setText(resp.optString("exerciseName", "이름 없음"));

                            TextView tvTip = findViewById(R.id.text_tip_content);
                            tvTip.setText(resp.optString("description", "설명 없음"));

                            ImageView iv = findViewById(R.id.image_exercise);
                            String imagePath = resp.optString("exerciseImagePath", null);
                            if (imagePath != null) {
                                int resId = getResources().getIdentifier(
                                        imagePath, "drawable", getPackageName());
                                if (resId != 0) {
                                    iv.setImageResource(resId);
                                } else {
                                    iv.setImageResource(R.drawable.logo);
                                }
                            } else {
                                iv.setImageResource(R.drawable.logo);
                            }
                        });
                    },
                    error -> {
                        Log.e("EXERCISE_META", "메타데이터 로드 실패", error);
                        Toast.makeText(this, "메타데이터 로드 실패", Toast.LENGTH_SHORT).show();
                    }
            );
            requestQueue.add(req);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "메타데이터 요청 생성 중 오류", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * (2) 화면에 세트 입력 필드 하나 추가 → 완료 체크 시 ExerciseRecordRequest 로 POST
     */
    private void addSetView() {
        View setView = LayoutInflater.from(this)
                .inflate(R.layout.item_exercise_set, setContainer, false);

        TextView setNumberText = setView.findViewById(R.id.text_set_number);
        EditText inputKg       = setView.findViewById(R.id.input_kg);
        EditText inputReps     = setView.findViewById(R.id.input_reps);
        CheckBox completeCheck = setView.findViewById(R.id.check_complete);
        View removeButton      = setView.findViewById(R.id.btn_remove_set);

        // “X” 버튼(세트 행 삭제) 리스너
        if (removeButton != null) {
            removeButton.setOnClickListener(v -> {
                setContainer.removeView(setView);
                renumberSets();
            });
        }

        // 완료 체크박스 변경 시 → ExerciseRecordRequest POST
        completeCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                String kgStr   = inputKg.getText().toString().trim();
                String repsStr = inputReps.getText().toString().trim();
                if (kgStr.isEmpty() || repsStr.isEmpty()) {
                    Toast.makeText(this, "무게와 반복 수를 입력해주세요", Toast.LENGTH_SHORT).show();
                    completeCheck.setChecked(false);
                    return;
                }
                int setNum   = Integer.parseInt(setNumberText.getText().toString());
                int repCount = Integer.parseInt(repsStr);
                double weight = new BigDecimal(kgStr).doubleValue();

                ExerciseRecordRequest recordReq = new ExerciseRecordRequest(
                        userId,
                        selectedDateIso,    // 상단에 표시된 날짜(ISO 포맷)
                        exerciseName,
                        setNum,
                        repCount,
                        weight,
                        isChecked,
                        response -> {
                            // "1세트 저장완료!" 등 서버 응답
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                            loadRecords();
                        },
                        error -> {
                            Log.e("EXERCISE_SAVE", "저장 실패", error);
                            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
                            completeCheck.setChecked(false);
                        }
                );
                requestQueue.add(recordReq);
            } catch (Exception ex) {
                Log.e("EXERCISE_SAVE", "예외 발생", ex);
                Toast.makeText(this, "입력값 오류", Toast.LENGTH_SHORT).show();
                completeCheck.setChecked(false);
            }
        });

        // “세트 추가” 버튼 바로 앞에 삽입
        int index = setContainer.getChildCount();
        if (index > 0 &&
                setContainer.getChildAt(index - 1).findViewById(R.id.btn_add_set) != null) {
            index--;
        }
        setContainer.addView(setView, index);
        renumberSets();
    }

    /**
     * (3) 서버에 저장된 세트 전체 목록 불러오기 → ExerciseRecordLoadRequest 사용
     */
    private void loadRecords() {
        // static 뷰(메타 설명, “세트 추가” 버튼 등) 뒤쪽 뷰 모두 지우기
        int staticCount = setContainer.indexOfChild(
                findViewById(R.id.text_tip_content)) + 1;
        int total = setContainer.getChildCount();
        if (total > staticCount) {
            setContainer.removeViews(staticCount, total - staticCount);
        }

        ExerciseRecordLoadRequest loadReq = new ExerciseRecordLoadRequest(
                userId,
                selectedDateIso,   // 현재 선택된 날짜 (ISO)
                exerciseName,
                arr -> {
                    // 응답 JSONArray ( [{setCount:1, count:10, weight:60.0, done:true}, ... ] )
                    if (arr.length() == 0) {
                        addSetView();
                    } else {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.optJSONObject(i);
                            double rawWeight = o.optDouble("weight", 0.0);
                            BigDecimal weightBd = BigDecimal.valueOf(rawWeight)
                                    .setScale(2, RoundingMode.HALF_UP);
                            int setCount = o.optInt("setCount");
                            int count    = o.optInt("count");
                            boolean done = o.optBoolean("done");

                            ExerciseRecordDto dto = new ExerciseRecordDto(
                                    setCount,
                                    count,
                                    weightBd,
                                    done
                            );
                            addRecordView(dto);
                        }
                    }
                    // “세트 추가” 버튼 다시 붙여주기
                    View addBtn = LayoutInflater.from(this)
                            .inflate(R.layout.item_add_button, setContainer, false);
                    setContainer.addView(addBtn);
                    addBtn.findViewById(R.id.btn_add_set).setOnClickListener(v -> {
                        setContainer.removeView(addBtn);
                        addSetView();
                        setContainer.addView(addBtn);
                    });
                },
                error -> {
                    Log.e("LOAD_RECORDS", "기록 조회 실패", error);
                    Toast.makeText(this, "기록 조회 실패", Toast.LENGTH_SHORT).show();
                    addSetView();
                }
        );
        requestQueue.add(loadReq);
    }

    /**
     * (4) 서버에서 받아온 dto 값으로 “세트 뷰” 붙이기
     *     → ExerciseRecordDeleteRequest 사용 (삭제)
     */
    private void addRecordView(ExerciseRecordDto dto) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.item_exercise_set, setContainer, false);

        TextView tvNum = view.findViewById(R.id.text_set_number);
        EditText kg    = view.findViewById(R.id.input_kg);
        EditText reps  = view.findViewById(R.id.input_reps);
        CheckBox cb    = view.findViewById(R.id.check_complete);
        ImageButton btnDel = view.findViewById(R.id.btn_remove_set);

        tvNum.setText(String.valueOf(dto.getSetCount()));
        kg.setText(dto.getWeight().toString());
        reps.setText(String.valueOf(dto.getCount()));
        cb.setChecked(dto.getDone());

        // “삭제” 버튼 → ExerciseRecordDeleteRequest
        btnDel.setOnClickListener(v -> {
            ExerciseRecordDeleteRequest delReq = new ExerciseRecordDeleteRequest(
                    userId,
                    selectedDateIso,   // 현재 날짜 (ISO)
                    exerciseName,
                    dto.getSetCount(),
                    response -> {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                        loadRecords();
                    },
                    error -> {
                        Log.e("DELETE_REQ", "삭제 실패", error);
                        Toast.makeText(this,
                                "삭제 실패: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
            );
            requestQueue.add(delReq);
        });

        // 이미 완료된 세트도 수정할 수 있도록 → POST 로직 재사용
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                String kgStr   = kg.getText().toString().trim();
                String repsStr = reps.getText().toString().trim();
                if (kgStr.isEmpty() || repsStr.isEmpty()) {
                    Toast.makeText(this,
                            "무게와 반복 수를 입력해주세요", Toast.LENGTH_SHORT).show();
                    cb.setChecked(!isChecked);
                    return;
                }
                int setNum   = Integer.parseInt(tvNum.getText().toString());
                int repCount = Integer.parseInt(repsStr);
                double weight = new BigDecimal(kgStr).doubleValue();

                ExerciseRecordRequest recordReq = new ExerciseRecordRequest(
                        userId,
                        selectedDateIso,   // 현재 날짜 (ISO)
                        exerciseName,
                        setNum,
                        repCount,
                        weight,
                        isChecked,
                        response -> {
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                            loadRecords();
                        },
                        error -> {
                            Log.e("EXERCISE_SAVE", "저장 실패", error);
                            Toast.makeText(this,
                                    "저장 실패", Toast.LENGTH_SHORT).show();
                            cb.setChecked(!isChecked);
                        }
                );
                requestQueue.add(recordReq);
            } catch (Exception ex) {
                Log.e("EXERCISE_SAVE", "예외 발생", ex);
                Toast.makeText(this, "입력값 오류", Toast.LENGTH_SHORT).show();
                cb.setChecked(!isChecked);
            }
        });

        setContainer.addView(view);
    }

    /** 세트 번호를 순서대로 매기는 헬퍼 */
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
}