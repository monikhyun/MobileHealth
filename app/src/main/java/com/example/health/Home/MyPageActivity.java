// MyPageActivity.java
// 사용자의 마이페이지를 구성하는 주요 액티비티로, 프로필 정보, 등급 정보, 인바디 기록을 불러오고 보여주며 편집/추가할 수 있는 기능 포함

package com.example.health.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.health.Auth.MainActivity;
import com.example.health.DTO.InbodyResponseDto;
import com.example.health.R;
import com.example.health.Request.Home.*;

import org.json.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class MyPageActivity extends AppCompatActivity {

    // 상단바 버튼들
    private ImageView btnProfile, btnAlarm, btnSetting;
    // 프로필 정보
    private ImageView imageProfile, gradeIcon;
    private TextView profileName, profileHeight, gender, profileWeight, age;
    private Button editBtn;
    // 등급 관련
    private TextView goalText;
    private ImageView myGrade, nextGrade;
    private LinearLayout barFull, barFill;
    // 인바디 기록 목록
    private RecyclerView recyclerInbody;
    private TextView inbodyList;
    // 인바디 추가 버튼 (FAB)
    private Button btnAddSet;
    // 하단 네비게이션 바 아이콘들
    private ImageView iconHome, iconWorkout, iconMeal, iconFriends, iconStats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mypage);

        // --- 1) 뷰 바인딩 (레이아웃의 뷰들과 연결) ---
        // (생략: findViewById 코드들)

        // 드롭다운 스피너 설정
        Spinner spinner = findViewById(R.id.topDropdownSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.menu_items, R.layout.spinner_item_bold);
        adapter.setDropDownViewResource(R.layout.spinner_item_bold);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                // 선택된 항목 처리 (필요 시 추가)
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // --- 2) SharedPreferences에서 JWT, USER_ID 가져오기 ---
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = prefs.getString("JWT_TOKEN", null);
        String userId   = prefs.getString("USER_ID", null);
        if (jwtToken == null || userId == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3) 프로필 정보 로드 ---
        MyPageRequest profileReq = new MyPageRequest(jwtToken, userId,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        // 프로필 정보 세팅
                        // 성별, 이미지, 등급 아이콘 설정 포함
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "프로필 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
                });
        Volley.newRequestQueue(this).add(profileReq);

        // --- 4) 등급 정보 불러오기 및 게이지 바 계산 ---
        MyGradeRequest gradeReq = new MyGradeRequest(jwtToken, userId,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        // 현재 등급, 다음 등급, 남은 일수 설정 및 텍스트 강조 처리
                        // 게이지 바 길이 계산 후 반영
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "등급 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "등급 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
                });
        Volley.newRequestQueue(this).add(gradeReq);

        // --- 5) 인바디 기록 리스트 API 요청 & RecyclerView 설정 ---
        InBodyListRequest inbodyReq = new InBodyListRequest(jwtToken, userId,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        List<InbodyResponseDto> list = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            list.add(new InbodyResponseDto(/* weight, date, smm, fat 등 설정 */));
                        }
                        recyclerInbody.setLayoutManager(new LinearLayoutManager(this));
                        recyclerInbody.setAdapter(new InbodyListAdapter(list, item -> {
                            // 인바디 항목 클릭 시, 수정 화면으로 이동 + 값 전달
                            InBodyLoadRequest loadReq = new InBodyLoadRequest(jwtToken, userId, item.getDate().toString(),
                                    resp -> {
                                        try {
                                            Intent intent = new Intent(this, InBodyEditActivity.class);
                                            // intent.putExtra(...) 로 데이터 전달
                                            startActivity(intent);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            Toast.makeText(this, "불러온 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    error -> Toast.makeText(this, "기록 불러오기 실패", Toast.LENGTH_SHORT).show());
                            Volley.newRequestQueue(this).add(loadReq);
                        }));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "인바디 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "인바디 목록 불러오기 실패", Toast.LENGTH_SHORT).show();
                });
        Volley.newRequestQueue(this).add(inbodyReq);

        // --- 6) 프로필 수정 버튼 클릭 시 ---
        editBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, EditPageActivity.class));
        });

        // --- 7) 하단 네비게이션바 리스너 설정 ---
        iconHome   .setOnClickListener(v -> finish());
        iconWorkout.setOnClickListener(v -> startActivity(new Intent(this, com.example.health.Exercise.ExerciseListActivity.class)));
        iconMeal   .setOnClickListener(v -> startActivity(new Intent(this, com.example.health.Diet.DietActivity.class)));
        iconFriends.setOnClickListener(v -> startActivity(new Intent(this, com.example.health.Friend.FriendListActivity.class)));
        iconStats  .setOnClickListener(v -> startActivity(new Intent(this, com.example.health.Stats.StatusActivity.class)));

        // --- 8) 인바디 기록 추가 버튼 클릭 시 ---
        btnAddSet.setOnClickListener(v -> {
            startActivity(new Intent(this, InBodyRecordActivity.class));
        });
    }
}