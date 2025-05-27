package com.example.resister;

import android.content.Intent;
import android.view.View;
import com.android.volley.Response;
import org.json.JSONArray;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.graphics.Rect;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.resister.Request.ExerciseAddRequest;
import com.example.resister.Request.ExerciseListRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.Set;

public class ExerciseAddListActivity extends AppCompatActivity {
    // 상단 바
    LinearLayout topBar;
    TextView textDate;
    ImageView btnProfile;

    // ▶ 검색바
    LinearLayout searchBar;
    Spinner spinnerBodypart;
    EditText etSearch;
    ImageView ivSearch;

    // 운동 목록
    RecyclerView recyclerExercise;

    // 하단 버튼 영역
    ImageButton btnAddSet;
    Button btnStart;
    TextView textTimer;

    // 하단 네비게이션
    LinearLayout navHome, navWorkout, navMeal, navFriends, navStats;
    ImageView iconHome, iconWorkout, iconMeal, iconFriends, iconStats;
    TextView textHome, textWorkout, textMeal, textFriends, textStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_add_list);

        // 상단 바
        topBar     = findViewById(R.id.topBar);
        textDate   = findViewById(R.id.text_date);
        btnProfile = findViewById(R.id.btn_profile);


        // 검색바
        searchBar = findViewById(R.id.searchBar);
        spinnerBodypart = findViewById(R.id.spinner_bodypart);
        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);

        // 운동 목록 RecyclerView
        recyclerExercise = findViewById(R.id.recycler_exercise);

        // RecyclerView 설정: 수직 레이아웃 매니저 + 아이템 간격 데코레이션
        recyclerExercise.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        int spacing = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerExercise.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull android.view.View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                outRect.top = spacing;
                outRect.bottom = spacing;
            }
        });
        // ▶ 어댑터 생성 및 연결
        ExerciseAdapter adapter = new ExerciseAdapter(this,
                new ArrayList<>(),
                item -> {
                    // SharedPreferences에서 userId 가져오기
                    String userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .getString("USER_ID", null);
                    // 현재 날짜를 "yyyy-MM-dd" 형식으로
                    String display = textDate.getText().toString();
                    DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault());
                    LocalDate localDate;
                    try {
                        localDate = LocalDate.parse(display, inputFmt);
                    } catch (DateTimeParseException e) {
                        throw new RuntimeException("날짜 파싱 실패: " + display, e);
                    }

                    // (4) API에 넘길 "yyyy-MM-dd" 포맷 문자열
                    String date = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE); // ex) "2025-05-06"


                    String jwt = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            .getString("JWT_TOKEN", "");
                    ExerciseAddRequest addReq = new ExerciseAddRequest(
                            jwt,
                            userId,
                            date,
                            item.exerciseName,
                            response -> {
                                Toast.makeText(this, "운동 추가 완료", Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                Toast.makeText(this, "운동 추가 실패", Toast.LENGTH_SHORT).show();
                            }
                    );

                    Volley.newRequestQueue(this).add(addReq);
                    Intent intent = new Intent(ExerciseAddListActivity.this, ExerciseListActivity.class);
                    intent.putExtra("EXERCISE_NAME", item.exerciseName);
                    intent.putExtra("DATE", date);
                    startActivity(intent);
                }
        );
        recyclerExercise.setAdapter(adapter);

        String jwtToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("JWT_TOKEN", null);
        if (jwtToken == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId   = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("USER_ID", null);

        // ▶ 검색 버튼 클릭 시 부위 및 검색어로 필터링
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 스피너에서 선택된 부위 읽기 ("전체"은 null 처리)
                String selected = spinnerBodypart.getSelectedItem().toString();
                String bodyPartParam = ("전체".equals(selected) || selected.isEmpty())
                    ? null : selected;
                // 검색어 읽기 (빈 문자열은 null 처리)
                String query = etSearch.getText().toString().trim();
                String nameParam = query.isEmpty() ? null : query;

                // 검색 API 호출
                ExerciseListRequest searchReq = ExerciseListRequest.search(
                    jwtToken,
                    bodyPartParam,
                    nameParam,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            adapter.updateDataJSONArray(response);
                        }
                    },
                    error -> Toast.makeText(ExerciseAddListActivity.this,
                            "검색 실패", Toast.LENGTH_SHORT).show()
                );
                Volley.newRequestQueue(ExerciseAddListActivity.this).add(searchReq);
            }
        });

        // 1) 찜한 운동 먼저 가져오기
        ExerciseListRequest favReq = ExerciseListRequest.getFavorites(
                jwtToken, userId,
                response -> {
                    // JSONArray → Set<String> favoriteNames
                    Set<String> favNames = new HashSet<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            favNames.add(obj.optString("exerciseName"));
                        }
                    }

                    // 2) 전체 운동 목록 가져오기
                    ExerciseListRequest allReq = ExerciseListRequest.getAll(
                            jwtToken,
                            arr -> {
                                // 각 항목에 isFavorite 플래그를 추가
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject o = arr.optJSONObject(i);
                                    if (o != null) {
                                        try {
                                            boolean fav = favNames.contains(o.optString("exerciseName"));
                                            o.put("isFavorite", fav);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                // 갱신
                                adapter.updateDataJSONArray(arr);
                            },
                            err -> Toast.makeText(this, "전체 운동 조회 실패", Toast.LENGTH_SHORT).show()
                    );
                    Volley.newRequestQueue(this).add(allReq);
                },
                err -> {
                    // 찜 목록 조회 실패해도, 전체 리스트만 불러오기
                    ExerciseListRequest allReq = ExerciseListRequest.getAll(
                            jwtToken,
                            adapter::updateDataJSONArray,
                            error -> Toast.makeText(this, "운동 목록 조회 실패", Toast.LENGTH_SHORT).show()
                    );
                    Volley.newRequestQueue(this).add(allReq);
                }
        );
        Volley.newRequestQueue(this).add(favReq);



        // ▶ 네비게이션 바
        navHome    = findViewById(R.id.nav_home);
        navWorkout = findViewById(R.id.nav_workout);
        navMeal    = findViewById(R.id.nav_meal);
        navFriends = findViewById(R.id.nav_friends);
        navStats   = findViewById(R.id.nav_stats);

        iconHome    = findViewById(R.id.icon_home);
        iconWorkout = findViewById(R.id.icon_workout);
        iconMeal    = findViewById(R.id.icon_meal);
        iconFriends = findViewById(R.id.icon_friends);
        iconStats   = findViewById(R.id.icon_stats);

        textHome    = findViewById(R.id.text_home);
        textWorkout = findViewById(R.id.text_workout);
        textMeal    = findViewById(R.id.text_meal);
        textFriends = findViewById(R.id.text_friends);
        textStats   = findViewById(R.id.text_stats);
    }
}
