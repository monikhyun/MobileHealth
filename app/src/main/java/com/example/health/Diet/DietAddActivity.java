package com.example.health.Diet;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.health.Friend.FriendListActivity;
import com.example.health.R;
import com.example.health.Exercise.ExerciseListActivity;
import com.example.health.Auth.MainActivity;
import com.example.health.Request.Diet.DietInsertRequest;
import com.example.health.Request.Diet.DietUpdateRequest;
import com.example.health.Stats.StatusActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DietAddActivity extends AppCompatActivity {
    private EditText editName, editCal, editCarb, editProtein, editFat;
    private Button btnSubmit, btnCancel, btnBreakfast, btnLunch, btnDinner;
    private Spinner topDropdownSpinner;
    private boolean isFirst = true;
    ImageView iconWorkout,icon_meal, icon_freinds,icon_stats,icon_home;

    private String mealtime = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietadd);

        Intent intent = getIntent();
        boolean isEdit = "edit".equals(intent.getStringExtra("mode"));
        Long dietId = intent.getLongExtra("dietId", -1);
        Log.d("dietid",""+dietId);
        editName = findViewById(R.id.editMealName);
        editCal = findViewById(R.id.editCalorie);
        editCarb = findViewById(R.id.editCarb);
        editProtein = findViewById(R.id.editProtein);
        editFat = findViewById(R.id.editFat);
        btnSubmit = findViewById(R.id.buttonSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        btnBreakfast = findViewById(R.id.btnBreakfast);
        btnLunch = findViewById(R.id.btnLunch);
        btnDinner = findViewById(R.id.btnDinner);
        icon_home = findViewById(R.id.icon_home);
        icon_freinds = findViewById(R.id.icon_friends);
        iconWorkout = findViewById(R.id.icon_workout);
        icon_meal = findViewById(R.id.icon_meal);
        icon_stats = findViewById(R.id.icon_stats);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userID = prefs.getString("USER_ID", null);  // 없으면 null 반환

        topDropdownSpinner = findViewById(R.id.topDropdownSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.menu_items,
                R.layout.spinner_item_bold
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_bold);
        topDropdownSpinner.setAdapter(adapter);

        topDropdownSpinner.setSelection(2);
        topDropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst){
                    isFirst = false;
                    return;
                }
                switch (position) {
                    case 0: // 홈
                        startActivity(new Intent(DietAddActivity.this, MainActivity.class));
                        break;
                    case 1: // 운동
                        startActivity(new Intent(DietAddActivity.this, ExerciseListActivity.class));
                        break;
                    case 2: // 식단
                        break;
                    case 3: // 친구
                        startActivity(new Intent(DietAddActivity.this, FriendListActivity.class));
                        break;
                    case 4: // 통계
                        startActivity(new Intent(DietAddActivity.this, StatusActivity.class));
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (userID != null) {
            // 정상적으로 꺼내진 경우
            Log.d("USER_ID", userID);
        } else {
            // 저장된 값이 없을 경우
            Log.d("USER_ID", "No userId found");
        }
        btnBreakfast.setOnClickListener(v -> setMealtime("아침", btnBreakfast));
        btnLunch.setOnClickListener(v -> setMealtime("점심", btnLunch));
        btnDinner.setOnClickListener(v -> setMealtime("저녁", btnDinner));

        // 편집 모드
        if (isEdit) {
            editName.setText(intent.getStringExtra("name"));
            editCal.setText(String.valueOf(intent.getDoubleExtra("calories", 0)));
            editCarb.setText(String.valueOf(intent.getIntExtra("carb", 0)));
            editProtein.setText(String.valueOf(intent.getIntExtra("protein", 0)));
            editFat.setText(String.valueOf(intent.getIntExtra("fat", 0)));
            String tm = intent.getStringExtra("mealtime");
            if ("BREAKFAST".equals(tm)) setMealtime("아침", btnBreakfast);
            else if ("LUNCH".equals(tm)) setMealtime("점심", btnLunch);
            else if ("DINNER".equals(tm)) setMealtime("저녁", btnDinner);
            btnSubmit.setText("수정");
        }

        btnSubmit.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String mealTimeEnum = "";
            switch (mealtime) {
                case "아침": mealTimeEnum = "BREAKFAST"; break;
                case "점심": mealTimeEnum = "LUNCH"; break;
                case "저녁": mealTimeEnum = "DINNER"; break;
            }
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id",dietId);
                jsonBody.put("userId", userID);
                jsonBody.put("name", editName.getText().toString());
                jsonBody.put("mealtime", mealTimeEnum);
                jsonBody.put("calories", new BigDecimal(editCal.getText().toString()));
                jsonBody.put("carb", Integer.valueOf(editCarb.getText().toString()));
                jsonBody.put("protein", Integer.valueOf(editProtein.getText().toString()));
                jsonBody.put("fat", Integer.valueOf(editFat.getText().toString()));
                jsonBody.put("date", today);
            } catch (JSONException e) {
                Toast.makeText(this, "입력값 오류!", Toast.LENGTH_SHORT).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(this);
            // 수정 모드
            if (isEdit) {
                Log.d("mealID",""+dietId);
                String url = "http://10.0.2.2:8080/api/diet/record/" + userID + "/" + dietId + "/update";
                DietUpdateRequest updateReq = new DietUpdateRequest(
                        userID, dietId, jsonBody,
                        resp -> { Toast.makeText(this, resp, Toast.LENGTH_SHORT).show(); setResult(RESULT_OK); finish(); },
                        err -> { err.printStackTrace(); Toast.makeText(this, "수정 실패", Toast.LENGTH_LONG).show(); }
                );
                queue.add(updateReq);
                // 처음 저장
            } else {
                DietInsertRequest insertReq = new DietInsertRequest(
                        userID, jsonBody,
                        resp -> { Toast.makeText(this, resp, Toast.LENGTH_SHORT).show(); setResult(RESULT_OK); finish(); },
                        err -> { err.printStackTrace(); Toast.makeText(this, "등록 실패", Toast.LENGTH_LONG).show(); }
                );
                queue.add(insertReq);
            }
        });

        btnCancel.setOnClickListener(v -> finish());


        icon_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietAddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        iconWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietAddActivity.this, ExerciseListActivity.class);
                startActivity(intent);
            }
        });
        icon_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietAddActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });

        icon_freinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietAddActivity.this, FriendListActivity.class);
                startActivity(intent);
            }
        });

        icon_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DietAddActivity.this, StatusActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setMealtime(String time, Button selectedButton) {
        mealtime = time;

        // 모든 버튼 배경 초기화 (개별 인스턴스 사용)
        btnBreakfast.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
        btnLunch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
        btnDinner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));

        // 선택된 버튼만 배경 강조
        selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB6C1")));
    }

    private String getKorean(String mealTime) {
        switch (mealTime) {
            case "BREAKFAST":
                return "아침";
            case "LUNCH":
                return "점심";
            case "DINNER":
                return "저녁";
            default:
                return mealTime;
        }
    }
}
