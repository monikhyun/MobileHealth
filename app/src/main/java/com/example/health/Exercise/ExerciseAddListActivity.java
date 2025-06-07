// src/main/java/com/example/health/Exercise/ExerciseAddListActivity.java
package com.example.health.Exercise;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.graphics.Rect;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.toolbox.Volley;
import com.example.health.Auth.MainActivity;
import com.example.health.Diet.DietActivity;
import com.example.health.Friend.FriendListActivity;
import com.example.health.Stats.StatusActivity;
import com.example.health.R;
import com.example.health.Request.Exercise.ExerciseAddRequest;
import com.example.health.Request.Exercise.ExerciseListRequest;
import com.example.health.Stats.StatusActivity;
import com.example.health.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ExerciseAddListActivity extends AppCompatActivity {
    // 상단 바
    private LinearLayout topBar;
    private TextView textDate;
    private ImageView btnProfile;

    // 검색바
    private LinearLayout searchBar;
    private Spinner spinnerBodypart;
    private EditText etSearch;
    private ImageView ivSearch;

    // 운동 목록 RecyclerView
    private RecyclerView recyclerExercise;

    // 하단 버튼 영역
    private ImageButton btnAddSet;
    private Button btnStart;
    private TextView textTimer;

    private ImageView btn_back;

    // 하단 네비게이션
    private LinearLayout navHome, navWorkout, navMeal, navFriends, navStats;
    private ImageView iconHome, iconWorkout, iconMeal, iconFriends, iconStats;
    private TextView textHome, textWorkout, textMeal, textFriends, textStats;

    // 선택된 날짜의 ISO 포맷 (yyyy-MM-dd)
    private String selectedDateIso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_exercise_add_list);

        // --- 뷰 바인딩 ---
        topBar         = findViewById(R.id.topBar);
        textDate       = findViewById(R.id.text_date);
        btnProfile     = findViewById(R.id.btn_profile);

        searchBar      = findViewById(R.id.searchBar);
        spinnerBodypart= findViewById(R.id.spinner_bodypart);
        etSearch       = findViewById(R.id.et_search);
        ivSearch       = findViewById(R.id.iv_search);

        recyclerExercise = findViewById(R.id.recycler_exercise);

        btn_back = findViewById(R.id.btn_back);
        btnAddSet      = findViewById(R.id.btn_add_set);
        btnStart       = findViewById(R.id.btn_start);
        textTimer      = findViewById(R.id.text_timer);

        navHome        = findViewById(R.id.nav_home);
        navWorkout     = findViewById(R.id.nav_workout);
        navMeal        = findViewById(R.id.nav_meal);
        navFriends     = findViewById(R.id.nav_friends);
        navStats       = findViewById(R.id.nav_stats);

        iconHome       = findViewById(R.id.icon_home);
        iconWorkout    = findViewById(R.id.icon_workout);
        iconMeal       = findViewById(R.id.icon_meal);
        iconFriends    = findViewById(R.id.icon_friends);
        iconStats      = findViewById(R.id.icon_stats);

        textHome       = findViewById(R.id.text_home);
        textWorkout    = findViewById(R.id.text_workout);
        textMeal       = findViewById(R.id.text_meal);
        textFriends    = findViewById(R.id.text_friends);
        textStats      = findViewById(R.id.text_stats);


        // RecyclerView: 세로 목록 + 아이템 간격
        recyclerExercise.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        int spacing = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerExercise.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect,
                                       @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                outRect.top = spacing;
                outRect.bottom = spacing;
            }
        });

        // 어댑터 생성 (null-safe 버전)
        ExerciseAdapter adapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            adapter = new ExerciseAdapter(
                    this,
                    new ArrayList<>(),
                    item -> {
                        // “운동 추가” 클릭 시, 서버에 POST 요청 보내기
                        String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .getString("USER_ID", null);
                        String jwt = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .getString("JWT_TOKEN", "");

                        // 현재 textDate 에 표시된 “yyyy년 M월 d일”을 파싱 → ISO 문자열 생성
                        String display = textDate.getText().toString()
                                .replace("▼", "").trim();
                        DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern(
                                "yyyy년 M월 d일", Locale.getDefault());
                        LocalDate localDate;
                        try {
                            localDate = LocalDate.parse(display, inputFmt);
                        } catch (DateTimeParseException e) {
                            Toast.makeText(this,
                                    "날짜 파싱 오류: " + display, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String isoDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

                        ExerciseAddRequest addReq = new ExerciseAddRequest(
                                jwt,
                                userId,
                                isoDate,
                                item.exerciseName,
                                response -> {
                                    Toast.makeText(this,
                                            "운동 추가 완료", Toast.LENGTH_SHORT).show();
                                    // 선택된 날짜와 운동이 넘어간 뒤, 목록 화면으로 돌아감
                                },
                                error -> {
                                    Toast.makeText(this,
                                            "운동 추가 실패", Toast.LENGTH_SHORT).show();
                                }
                        );
                        Volley.newRequestQueue(this).add(addReq);

                        Intent intent = new Intent(
                                ExerciseAddListActivity.this,
                                ExerciseListActivity.class);
                        intent.putExtra("EXERCISE_NAME", item.exerciseName);
                        intent.putExtra("DATE", isoDate);
                        startActivity(intent);
                    }
            );
        } else {
            adapter = null;
        }
        recyclerExercise.setAdapter(adapter);

        // “검색” 버튼 클릭 → ExerciseListRequest로 부위/이름 검색
        ExerciseAdapter finalAdapter = adapter;
        ivSearch.setOnClickListener(v -> {
            String jwtToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("JWT_TOKEN", null);
            String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("USER_ID", null);
            if (jwtToken == null || userId == null) {
                Toast.makeText(this,
                        "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 스피너/검색어 파라미터 준비
            String selected = spinnerBodypart.getSelectedItem().toString();
            String bodyPartParam = ("전체".equals(selected) || selected.isEmpty())
                    ? null : selected;
            String query = etSearch.getText().toString().trim();
            String nameParam = query.isEmpty() ? null : query;

            ExerciseListRequest searchReq = ExerciseListRequest.search(
                    jwtToken,
                    bodyPartParam,
                    nameParam,
                    response -> finalAdapter.updateDataJSONArray(response),
                    error -> Toast.makeText(this,
                            "검색 실패", Toast.LENGTH_SHORT).show()
            );
            Volley.newRequestQueue(this).add(searchReq);
        });

        // 찜 목록 먼저 불러오고, 전체 운동 리스트로 이어 붙이기
        String jwtToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("JWT_TOKEN", null);
        String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("USER_ID", null);
        if (jwtToken != null && userId != null) {
            ExerciseListRequest favReq = ExerciseListRequest.getFavorites(
                    jwtToken, userId,
                    response -> {
                        Set<String> favNames = new HashSet<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.optJSONObject(i);
                            if (obj != null) {
                                favNames.add(obj.optString("exerciseName"));
                            }
                        }
                        ExerciseListRequest allReq = ExerciseListRequest.getAll(
                                jwtToken,
                                arr -> {
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject o = arr.optJSONObject(i);
                                        if (o != null) {
                                            try {
                                                boolean fav = favNames.contains(
                                                        o.optString("exerciseName"));
                                                o.put("isFavorite", fav);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    adapter.updateDataJSONArray(arr);
                                },
                                err -> Toast.makeText(this,
                                        "전체 운동 조회 실패", Toast.LENGTH_SHORT).show()
                        );
                        Volley.newRequestQueue(this).add(allReq);
                    },
                    err -> {
                        // 찜 조회 실패해도 전체 리스트만 가져오기
                        ExerciseListRequest allReq = ExerciseListRequest.getAll(
                                jwtToken,
                                arr -> adapter.updateDataJSONArray(arr),
                                error -> Toast.makeText(this,
                                        "운동 목록 조회 실패", Toast.LENGTH_SHORT).show()
                        );
                        Volley.newRequestQueue(this).add(allReq);
                    }
            );
            Volley.newRequestQueue(this).add(favReq);
        }

        // ─── 날짜 선택 기능 추가 ───
        // 1) 처음 화면 열릴 때 “오늘”을 기본으로 설정
        LocalDate today = LocalDate.now();
        updateDateText(today);
        selectedDateIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 2) textDate 클릭하면 DatePickerDialog 띄우기
        textDate.setOnClickListener(v -> {
            // 현재 표시된 텍스트(“2025년 5월 6일 ▼”)에서 “▼” 제거 → 파싱
            String rawDisplay = textDate.getText().toString().replace("▼", "").trim();
            DateTimeFormatter parseFmt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                parseFmt = DateTimeFormatter.ofPattern(
                        "yyyy년 M월 d일", Locale.getDefault());
            }
            LocalDate current = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                current = LocalDate.parse(rawDisplay, parseFmt);
            }
            int year  = (current != null) ? current.getYear() : today.getYear();
            int month = (current != null) ? current.getMonthValue() - 1
                    : today.getMonthValue() - 1;
            int day   = (current != null) ? current.getDayOfMonth()
                    : today.getDayOfMonth();

            DatePickerDialog dpd = new DatePickerDialog(
                    ExerciseAddListActivity.this,
                    (DatePicker view, int selYear, int selMonth, int selDay) -> {
                        LocalDate picked = LocalDate.of(selYear, selMonth + 1, selDay);
                        updateDateText(picked);
                        selectedDateIso = picked.format(
                                DateTimeFormatter.ISO_LOCAL_DATE);
                        // (선택된 날짜로 목록을 다시 받아와야 한다면 → load 추가)
                        // 예시: loadSearchResults();
                    },
                    year, month, day
            );
            dpd.show();
        });
        btn_back.setOnClickListener(v -> finish());
    }

    /**
     * 상단 텍스트(textDate)를 “YYYY년 M월 d일 ▼” 형식으로 갱신
     */
    private void updateDateText(LocalDate date) {
        String display = String.format(Locale.getDefault(),
                "%d년 %d월 %d일 ▼",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        textDate.setText(display);
    }
}