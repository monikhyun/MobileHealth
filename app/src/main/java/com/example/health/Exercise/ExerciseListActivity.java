// src/main/java/com/example/health/Exercise/ExerciseListActivity.java
package com.example.health.Exercise;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.health.Auth.MainActivity;
import com.example.health.Diet.DietActivity;
import com.example.health.Friend.FriendListActivity;
import com.example.health.R;
import com.example.health.Request.Exercise.AddedExerciseDeleteRequest;
import com.example.health.Request.Exercise.AddedExercisesRequest;
import com.example.health.Request.Exercise.ExerciseTimeLogRecordRequest;
import com.example.health.Request.Exercise.ExerciseTimeLogRequest;
import com.example.health.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ExerciseListActivity extends AppCompatActivity {
    private RecyclerView recyclerExercise;
    private AddedExerciseAdapter adapter;
    private ImageButton btnAddSet;
    private ImageView icon_meal;
    private TextView textDate;
    private Button btnStart;
    private TextView textTimer;

    private RequestQueue requestQueue;
    private String selectedDateIso; // YYYY-MM-DD

    // 타이머 관련
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private boolean isTimerRunning = false;
    private int elapsedSeconds = 0;

    // 1초마다 호출될 Runnable
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds++;
            updateTimerText();
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_exercise_list);

        // Volley RequestQueue 초기화
        requestQueue = Volley.newRequestQueue(this);

        // View 바인딩
        textDate = findViewById(R.id.text_date);
        recyclerExercise = findViewById(R.id.recycler_exercise);
        btnAddSet = findViewById(R.id.btn_add_set);
        icon_meal = findViewById(R.id.icon_meal);
        btnStart = findViewById(R.id.btn_start);
        textTimer = findViewById(R.id.text_timer);


        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.iconHome.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseListActivity.this, MainActivity.class);
            startActivity(intent);
        });


        binding.iconMeal.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseListActivity.this, DietActivity.class);
            startActivity(intent);
        });

        binding.iconFriends.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseListActivity.this, FriendListActivity.class);
            startActivity(intent);
        });

        binding.iconStats.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseListActivity.this, StatusActivity.class);
            startActivity(intent);
        });

        // RecyclerView 설정
        recyclerExercise.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 생성 (컬백: 아이템 전체 클릭 / 삭제 버튼 클릭)
        adapter = new AddedExerciseAdapter(
                new ArrayList<>(),
                // ① 운동 항목 클릭 시 → 디테일 페이지
                item -> {
                    Intent intent = new Intent(ExerciseListActivity.this, ExerciseDetailActivity.class);
                    intent.putExtra("EXERCISE_NAME", item.exerciseName);
                    intent.putExtra("DATE", selectedDateIso);
                    startActivity(intent);
                },
                // ② “삭제” 버튼 클릭 시 → 서버 삭제 요청 후 목록 갱신
                item -> {
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    String jwtToken = prefs.getString("JWT_TOKEN", null);
                    String userId = prefs.getString("USER_ID", null);
                    if (jwtToken == null || userId == null) {
                        Toast.makeText(ExerciseListActivity.this,
                                "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        AddedExerciseDeleteRequest deleteReq = new AddedExerciseDeleteRequest(
                                jwtToken,
                                userId,
                                selectedDateIso,
                                item.exerciseName,
                                response -> {
                                    Toast.makeText(ExerciseListActivity.this,
                                            "운동이 제거되었습니다.", Toast.LENGTH_SHORT).show();
                                    loadAddedExercises();
                                },
                                error -> {
                                    Toast.makeText(ExerciseListActivity.this,
                                            "삭제 실패: " + error.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                        );
                        requestQueue.add(deleteReq);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(ExerciseListActivity.this,
                                "URL 생성 오류", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        recyclerExercise.setAdapter(adapter);

        // “세트 추가” 버튼 클릭 → ExerciseAddListActivity로 이동
        btnAddSet.setOnClickListener(v ->
                startActivity(new Intent(this, ExerciseAddListActivity.class))
        );
        // “식단” 아이콘 클릭 → DietActivity로 이동
        icon_meal.setOnClickListener(v ->
                startActivity(new Intent(this, DietActivity.class))
        );

        // ─── 날짜 초기화 & DatePickerDialog ─────────────────────────
        LocalDate today = LocalDate.now();
        updateDateText(today);
        selectedDateIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        loadSavedExerciseTime();

        textDate.setOnClickListener(v -> {
            String rawDisplay = textDate.getText().toString().replace("▼", "").trim();
            DateTimeFormatter parseFmt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                parseFmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault());
            }
            LocalDate current = today;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    current = LocalDate.parse(rawDisplay, parseFmt);
                } catch (Exception ignored) {
                    current = LocalDate.now();
                }
            }
            int year = current.getYear();
            int month = current.getMonthValue() - 1;
            int day = current.getDayOfMonth();

            DatePickerDialog dpd = new DatePickerDialog(
                    ExerciseListActivity.this,
                    (DatePicker view, int selYear, int selMonth, int selDay) -> {
                        LocalDate picked = LocalDate.of(selYear, selMonth + 1, selDay);
                        updateDateText(picked);
                        selectedDateIso = picked.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        loadAddedExercises();
                    },
                    year, month, day
            );
            dpd.show();
        });
        // ────────────────────────────────────────────────────────────

        // ─── 운동 시작 버튼 (타이머 시작/중지 + 배경색 토글) ───
        btnStart.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // ▶ 시작
                isTimerRunning = true;
                btnStart.setText("운동 종료");
                btnStart.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#FC5B5B"))
                );
                timerHandler.postDelayed(timerRunnable, 1000);

            } else {
                // ▶ 종료
                isTimerRunning = false;
                btnStart.setText("운동 시작");
                btnStart.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#00D26A"))
                );
                timerHandler.removeCallbacks(timerRunnable);

                // ▶ 종료 시점에 서버에 저장
                saveExerciseTimeToServer();
            }
        });
        // ────────────────────────────────────────────────────────────
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddedExercises();
        loadSavedExerciseTime();
    }

    /** 날짜 텍스트를 “YYYY년 M월 d일 ▼” 형태로 보여주는 헬퍼 */
    private void updateDateText(LocalDate date) {
        String display = String.format(Locale.getDefault(),
                "%d년 %d월 %d일 ▼",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        textDate.setText(display);
    }

    private void updateTimerText() {
        int min = elapsedSeconds / 60;
        int sec = elapsedSeconds % 60;
        textTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
    }

    private void loadSavedExerciseTime() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwt = prefs.getString("JWT_TOKEN", null);
        String userId = prefs.getString("USER_ID", null);
        if (jwt == null || userId == null) return;

        try {
            ExerciseTimeLogRequest req = new ExerciseTimeLogRequest(
                    jwt, userId, selectedDateIso,
                    resp -> {
                        // DailyLogDto 에서 time 필드(초) 꺼내기
                        int serverSeconds = resp.optInt("totalExerciseMinutes", 0);
                        elapsedSeconds = serverSeconds;
                        updateTimerText();
                    },
                    err -> {
                        Toast.makeText(this, "운동 시간 로드 실패", Toast.LENGTH_SHORT).show();
                    }
            );
            requestQueue.add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveExerciseTimeToServer() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwt = prefs.getString("JWT_TOKEN", null);
        String userId = prefs.getString("USER_ID", null);
        if (jwt == null || userId == null) return;

        try {
            ExerciseTimeLogRecordRequest req = new ExerciseTimeLogRecordRequest(
                    jwt, userId, selectedDateIso, elapsedSeconds,
                    response -> {
                        Toast.makeText(this,
                                "운동 시간 저장 완료", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(this,
                                "운동 시간 저장 실패", Toast.LENGTH_SHORT).show();
                    }
            );
            requestQueue.add(req);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * GET /api/exercise/add/todo/{userId}/{date} → 어댑터에 반영
     */
    private void loadAddedExercises() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);
        String jwtToken = prefs.getString("JWT_TOKEN", null);

        if (userId == null || jwtToken == null) {
            Toast.makeText(this,
                    "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        AddedExercisesRequest request = new AddedExercisesRequest(
                jwtToken,
                userId,
                selectedDateIso,
                response -> {
                    List<ExerciseItem> items = new ArrayList<>();
                    Set<String> seenNames = new HashSet<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String part = obj.optString("bodyPart", "");
                            String name = obj.optString("exercise_name", "");
                            boolean done = obj.optBoolean("done", false);
                            if (seenNames.add(name)) {
                                items.add(new ExerciseItem(part, name, done));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.updateData(items);
                },
                error -> {
                    Toast.makeText(this,
                            "추가된 운동 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(request);
    }
}