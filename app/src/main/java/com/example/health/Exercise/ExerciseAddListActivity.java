// src/main/java/com/example/health/Exercise/ExerciseAddListActivity.java
package com.example.health.Exercise;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.graphics.Rect;
import android.view.LayoutInflater;
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

        // ───────────── 액션바 숨기기: 전체화면 UI를 위해 ─────────────
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_exercise_add_list); // 레이아웃 XML 연결

        // ──────────────── ① 뷰 바인딩: 화면의 주요 UI 요소를 변수에 연결 ────────────────
        // 상단 바(날짜, 프로필), 검색 영역, 운동 목록, 하단 버튼, 네비게이션 바 등
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


        // ──────────────── ② 운동 목록 RecyclerView 설정 ────────────────
        // (1) 세로 방향 목록으로 설정
        recyclerExercise.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // (2) 각 항목 위아래에 8dp 간격 여백 추가
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

        // ──────────────── ③ 운동 목록 어댑터 생성 및 아이템 클릭 처리 ────────────────
        ExerciseAdapter adapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 어댑터 생성 (초기 데이터는 빈 리스트, 클릭 리스너 구현)
            adapter = new ExerciseAdapter(
                    this,
                    new ArrayList<>(),
                    item -> {
                        // [운동 아이템 클릭 시 로직]
                        // 1. 사용자 토큰/ID 불러오기
                        String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .getString("USER_ID", null);
                        String jwt = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .getString("JWT_TOKEN", "");

                        // 2. 상단 날짜 텍스트("yyyy년 M월 d일")를 파싱하여 ISO 형식으로 변환
                        String display = textDate.getText().toString()
                                .replace("▼", "").trim();
                        DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern(
                                "yyyy년 M월 d일", Locale.getDefault());
                        LocalDate localDate;
                        try {
                            localDate = LocalDate.parse(display, inputFmt);
                        } catch (DateTimeParseException e) {
                            // 날짜 파싱 오류: 토스트로 사용자 안내
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            TextView tv = toastView.findViewById(R.id.text_toast_message);
                            tv.setText("날짜 파싱 오류 : " + display);
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                            return;
                        }
                        String isoDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

                        // 3. 운동 추가 요청 객체 생성(서버 POST) 및 전송
                        ExerciseAddRequest addReq = new ExerciseAddRequest(
                                jwt,
                                userId,
                                isoDate,
                                item.exerciseName,
                                response -> {
                                    // 성공: 운동 추가 완료 토스트 표시
                                    View toastView = LayoutInflater.from(this)
                                            .inflate(R.layout.toast_friend_request, null);
                                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                                    tv.setText("운동 추가 완료");
                                    Toast t = new Toast(this);
                                    t.setView(toastView);
                                    t.setDuration(Toast.LENGTH_SHORT);
                                    t.show();
                                    // 선택된 날짜와 운동이 넘어간 뒤, 목록 화면으로 돌아감
                                },
                                error -> {
                                    // 실패: 에러 토스트 표시
                                    View toastView = LayoutInflater.from(this)
                                            .inflate(R.layout.toast_friend_request, null);
                                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                                    tv.setText("운동 추가 실패");
                                    Toast t = new Toast(this);
                                    t.setView(toastView);
                                    t.setDuration(Toast.LENGTH_SHORT);
                                    t.show();
                                }
                        );
                        Volley.newRequestQueue(this).add(addReq);

                        // 4. 운동 상세 리스트 화면(ExerciseListActivity)으로 이동
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

        // ──────────────── ④ 운동 검색 기능: 검색 버튼 클릭 시 서버에 검색 요청 ────────────────
        ExerciseAdapter finalAdapter = adapter;
        ivSearch.setOnClickListener(v -> {
            // (1) 토큰/ID 유효성 확인
            String jwtToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("JWT_TOKEN", null);
            String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .getString("USER_ID", null);
            if (jwtToken == null || userId == null) {
                // 미로그인: 안내 토스트
                View toastView = LayoutInflater.from(this)
                        .inflate(R.layout.toast_friend_request, null);
                TextView tv = toastView.findViewById(R.id.text_toast_message);
                tv.setText("로그인이 필요합니다.");
                Toast t = new Toast(this);
                t.setView(toastView);
                t.setDuration(Toast.LENGTH_SHORT);
                t.show();
                return;
            }
            // (2) 검색 파라미터 준비: 부위(스피너), 이름(검색창)
            String selected = spinnerBodypart.getSelectedItem().toString();
            String bodyPartParam = ("전체".equals(selected) || selected.isEmpty())
                    ? null : selected;
            String query = etSearch.getText().toString().trim();
            String nameParam = query.isEmpty() ? null : query;

            // (3) 검색 요청 객체 생성 및 전송
            ExerciseListRequest searchReq = ExerciseListRequest.search(
                    jwtToken,
                    bodyPartParam,
                    nameParam,
                    response -> finalAdapter.updateDataJSONArray(response),
                    error -> {
                        // 실패 시: 토스트 안내
                        View toastView = LayoutInflater.from(this)
                            .inflate(R.layout.toast_friend_request, null);
                        TextView tv = toastView.findViewById(R.id.text_toast_message);
                        tv.setText("검색 실패");
                        Toast t = new Toast(this);
                        t.setView(toastView);
                        t.setDuration(Toast.LENGTH_SHORT);
                        t.show();
                    }
            );
            Volley.newRequestQueue(this).add(searchReq);
        });

        // ──────────────── ⑤ 운동 즐겨찾기(찜) 및 전체 목록 불러오기 ────────────────
        // 1. 즐겨찾기(찜) 목록을 먼저 요청
        String jwtToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("JWT_TOKEN", null);
        String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("USER_ID", null);
        if (jwtToken != null && userId != null) {
            ExerciseListRequest favReq = ExerciseListRequest.getFavorites(
                    jwtToken, userId,
                    response -> {
                        // 2. 즐겨찾기 운동 이름 set에 저장
                        Set<String> favNames = new HashSet<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.optJSONObject(i);
                            if (obj != null) {
                                favNames.add(obj.optString("exerciseName"));
                            }
                        }
                        // 3. 전체 운동 목록 요청, 즐겨찾기 여부 표시 필드 추가
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
                                err -> {
                                    // 전체 운동 조회 실패: 토스트 안내
                                    View toastView = LayoutInflater.from(this)
                                        .inflate(R.layout.toast_friend_request, null);
                                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                                    tv.setText("전체 운동 조회 실패");
                                    Toast t = new Toast(this);
                                    t.setView(toastView);
                                    t.setDuration(Toast.LENGTH_SHORT);
                                    t.show();
                                }
                        );
                        Volley.newRequestQueue(this).add(allReq);
                    },
                    err -> {
                        // 즐겨찾기 조회 실패 시에도 전체 운동 목록만 불러옴
                        ExerciseListRequest allReq = ExerciseListRequest.getAll(
                                jwtToken,
                                arr -> adapter.updateDataJSONArray(arr),
                                error -> {
                                    // 운동 목록 조회 실패: 토스트 안내
                                    View toastView = LayoutInflater.from(this)
                                        .inflate(R.layout.toast_friend_request, null);
                                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                                    tv.setText("운동 목록 조회 실패");
                                    Toast t = new Toast(this);
                                    t.setView(toastView);
                                    t.setDuration(Toast.LENGTH_SHORT);
                                    t.show();
                                }
                        );
                        Volley.newRequestQueue(this).add(allReq);
                    }
            );
            Volley.newRequestQueue(this).add(favReq);
        }

        // ──────────────── ⑥ 날짜 선택 기능 ────────────────
        // 1) 첫 진입 시 오늘 날짜를 기본으로 설정
        LocalDate today = LocalDate.now();
        updateDateText(today);
        selectedDateIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 2) 날짜 텍스트 클릭 시 DatePickerDialog로 날짜 선택
        textDate.setOnClickListener(v -> {
            // (1) 현재 표시 텍스트에서 "▼" 제거 후 파싱
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

            // (2) 날짜 선택 다이얼로그 표시
            DatePickerDialog dpd = new DatePickerDialog(
                    ExerciseAddListActivity.this,
                    (DatePicker view, int selYear, int selMonth, int selDay) -> {
                        // (3) 선택한 날짜로 텍스트 및 내부 상태 갱신
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

        // ──────────────── ⑦ 뒤로가기 버튼 클릭 처리 ────────────────
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