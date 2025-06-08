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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.health.Auth.MainActivity;
import com.example.health.DTO.InbodyResponseDto;
import com.example.health.Home.InbodyListAdapter;
import com.example.health.R;
import com.example.health.Request.Home.InBodyListRequest;
import com.example.health.Request.Home.MyGradeRequest;
import com.example.health.Request.Home.MyPageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyPageActivity extends AppCompatActivity {

    // Top bar
    private ImageView btnProfile, btnAlarm, btnSetting;
    // Profile section
    private ImageView imageProfile, gradeIcon;
    private TextView profileName, profileHeight, gender, profileWeight, age;
    private Button editBtn;
    // Grade section
    private TextView goalText;
    private ImageView myGrade, nextGrade;
    private LinearLayout barFull, barFill;
    // InBody list
    private RecyclerView recyclerInbody;
    private TextView inbodyList;
    // FAB
    private ImageButton btnAddSet;
    // Navigation bar icons
    private ImageView iconHome, iconWorkout, iconMeal, iconFriends, iconStats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mypage);

        // --- 1) 뷰 바인딩 ---
        btnProfile    = findViewById(R.id.btn_profile);
        btnAlarm      = findViewById(R.id.btn_alarm);
        btnSetting    = findViewById(R.id.btn_setting);

        imageProfile  = findViewById(R.id.image_profile);
        gradeIcon     = findViewById(R.id.grade);
        profileName   = findViewById(R.id.profile_name);
        profileHeight = findViewById(R.id.profile_height);
        gender        = findViewById(R.id.gender);
        profileWeight = findViewById(R.id.profile_weight);
        age           = findViewById(R.id.age);
        editBtn       = findViewById(R.id.edit_btn);

        goalText      = findViewById(R.id.goal_text);
        myGrade       = findViewById(R.id.my_grade);
        nextGrade     = findViewById(R.id.next_grade);
        barFull       = findViewById(R.id.bar_full);
        barFill       = findViewById(R.id.bar_fill);

        inbodyList    = findViewById(R.id.inbody_list);
        recyclerInbody= findViewById(R.id.recycler_inbody);

        btnAddSet     = findViewById(R.id.btn_add_set);

        iconHome      = findViewById(R.id.icon_home);
        iconWorkout   = findViewById(R.id.icon_workout);
        iconMeal      = findViewById(R.id.icon_meal);
        iconFriends   = findViewById(R.id.icon_friends);
        iconStats     = findViewById(R.id.icon_stats);

        Spinner spinner = findViewById(R.id.topDropdownSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.menu_items,
                R.layout.spinner_item_bold
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_bold);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // --- 2) SharedPreferences에서 JWT, USER_ID 가져오기 ---
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String jwtToken = prefs.getString("JWT_TOKEN", null);
        String userId   = prefs.getString("USER_ID",    null);
        if (jwtToken == null || userId == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3) 프로필 정보 API 호출 ---
        MyPageRequest profileReq = new MyPageRequest(
                jwtToken, userId,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String name       = obj.optString("name");
                        String genderStr  = obj.optString("gender");
                        double heightVal  = obj.optDouble("height", 0);
                        double weightVal  = obj.optDouble("weight", 0);
                        String gradeStr   = obj.optString("grade");
                        String imageUrl   = obj.optString("image");
                        int    ageVal     = obj.optInt("age", 0);

                        profileName.setText(name);
                        profileHeight.setText(heightVal + "cm");
                        profileWeight.setText(weightVal + "kg");
                        age.setText(ageVal + "세");
                        gender.setText("MALE".equals(genderStr) ? "남성" : "여성");

                        if (imageUrl == null || imageUrl.isEmpty()) {
                            imageProfile.setImageResource(R.drawable.ic_cat_profile);
                        } else {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_cat_profile)
                                    .error(R.drawable.ic_cat_profile)
                                    .into(imageProfile);
                        }

                        // 작은 프로필 등급 아이콘
                        switch (gradeStr) {
                            case "SEED":   gradeIcon.setImageResource(R.drawable.ic_rank_seed);   break;
                            case "SPROUT": gradeIcon.setImageResource(R.drawable.ic_rank_leaf);   break;
                            case "STEMS":  gradeIcon.setImageResource(R.drawable.ic_rank_flower); break;
                            case "TREE":   gradeIcon.setImageResource(R.drawable.ic_rank_tree);   break;
                            default:       gradeIcon.setImageResource(R.drawable.ic_rank_seed);   break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "프로필 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "프로필 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(profileReq);

        // --- 4) 등급 정보 API 호출 ---
        MyGradeRequest gradeReq = new MyGradeRequest(
                jwtToken, userId,
                response -> {
                    try {
                        JSONObject obj        = new JSONObject(response);
                        String gradeStr       = obj.getString("memberGrade");
                        String nextGradeStr   = obj.optString("nextGrade", null);
                        long   remaining      = obj.getLong("count");

                        // my_grade
                        switch (gradeStr) {
                            case "SEED":   myGrade.setImageResource(R.drawable.ic_rank_seed);   break;
                            case "SPROUT": myGrade.setImageResource(R.drawable.ic_rank_leaf);   break;
                            case "STEMS":  myGrade.setImageResource(R.drawable.ic_rank_flower); break;
                            case "TREE":   myGrade.setImageResource(R.drawable.ic_rank_tree);   break;
                        }
                        // next_grade
                        if (nextGradeStr == null || "TREE".equals(gradeStr)) {
                            nextGrade.setVisibility(View.GONE);
                        } else {
                            nextGrade.setVisibility(View.VISIBLE);
                            switch (nextGradeStr) {
                                case "SEED":   nextGrade.setImageResource(R.drawable.ic_rank_seed);   break;
                                case "SPROUT": nextGrade.setImageResource(R.drawable.ic_rank_leaf);   break;
                                case "STEMS":  nextGrade.setImageResource(R.drawable.ic_rank_flower); break;
                                case "TREE":   nextGrade.setImageResource(R.drawable.ic_rank_tree);   break;
                            }
                        }
                        // goal_text: 숫자 강조
                        if ("TREE".equals(gradeStr)) {
                            goalText.setText("헬린이 탈출!");
                        } else {
                            String text = remaining + "번만 더 운동가자!";
                            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                            String numStr = String.valueOf(remaining);
                            int start = text.indexOf(numStr);
                            int end   = start + numStr.length();
                            ssb.setSpan(new RelativeSizeSpan(1.3f),
                                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#388E3C")),
                                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            goalText.setText(ssb);
                        }
                        // 게이지 바
                        long required;
                        switch (gradeStr) {
                            case "SEED":   required = 20;  break;
                            case "SPROUT": required = 40;  break;
                            case "STEMS":  required = 150; break;
                            case "TREE":   required = 150; break;
                            default:       required = 0;   break;
                        }
                        long done       = required - remaining;
                        float fraction  = required == 0 ? 1f : (float) done / required;
                        barFull.getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        barFull.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        int fullW = barFull.getWidth();
                                        int fillW = (int)(fraction * fullW);
                                        ViewGroup.LayoutParams lp = barFill.getLayoutParams();
                                        lp.width = fillW;
                                        barFill.setLayoutParams(lp);
                                    }
                                }
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "등급 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "등급 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(gradeReq);

        // --- 5) 인바디 목록 API 호출 & RecyclerView 세팅 ---
        InBodyListRequest inbodyReq = new InBodyListRequest(
                jwtToken, userId,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        List<InbodyResponseDto> list = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            BigDecimal weight    = new BigDecimal(o.optString("weight", "0"));
                            LocalDate  date       = LocalDate.parse(o.optString("date"));
                            BigDecimal smm       = new BigDecimal(o.optString("smm", "0"));
                            BigDecimal fat       = new BigDecimal(o.optString("fat_percent", "0"));
                            list.add(new InbodyResponseDto(weight, date, smm, null, null, fat));
                        }
                        recyclerInbody.setLayoutManager(new LinearLayoutManager(this));
                        recyclerInbody.setAdapter(new InbodyListAdapter(list));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "인바디 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "인바디 목록 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(inbodyReq);

        editBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(MyPageActivity.this, EditPageActivity.class);
            startActivity(intent);
        });

        // --- 6) 하단 네비게이션 리스너 ---
        iconHome   .setOnClickListener(v -> finish());
        iconWorkout.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.health.Exercise.ExerciseListActivity.class)));
        iconMeal   .setOnClickListener(v ->
                startActivity(new Intent(this, com.example.health.Diet.DietActivity.class)));
        iconFriends.setOnClickListener(v ->
                startActivity(new Intent(this, com.example.health.Friend.FriendListActivity.class)));
        iconStats  .setOnClickListener(v ->
                startActivity(new Intent(this, com.example.health.Stats.StatusActivity.class)));
    }
}