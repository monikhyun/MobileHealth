package com.example.health.Diet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.health.Auth.MainActivity;
import com.example.health.Exercise.ExerciseListActivity;
import com.example.health.Friend.FriendListActivity;
import com.example.health.R;
import com.example.health.Request.Diet.DietInsertRequest;
import com.example.health.Request.Diet.DietUpdateRequest;
import com.example.health.Stats.StatusActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DietAddActivity extends AppCompatActivity {
    private EditText editName, editCal, editCarb, editProtein, editFat;
    private Button btnSubmit, btnCancel, btnBreakfast, btnLunch, btnDinner;
    private Spinner topDropdownSpinner;
    private boolean isFirst = true;
    private String mealtime = "";
    private String userID;
    private Long dietId = -1L;
    private boolean isEdit = false;

    ImageView iconWorkout, icon_meal, icon_freinds, icon_stats, icon_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietadd);

        initViews();
        initNavigation();
        handleIntent();
        setupDropdownMenu();
        setupButtons();
    }

    private void initViews() {
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

        topDropdownSpinner = findViewById(R.id.topDropdownSpinner);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userID = prefs.getString("USER_ID", null);
        Log.d("USER_ID", userID != null ? userID : "No userId found");
    }

    private void initNavigation() {
        icon_home.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        iconWorkout.setOnClickListener(v -> startActivity(new Intent(this, ExerciseListActivity.class)));
        icon_meal.setOnClickListener(v -> startActivity(new Intent(this, DietActivity.class)));
        icon_freinds.setOnClickListener(v -> startActivity(new Intent(this, FriendListActivity.class)));
        icon_stats.setOnClickListener(v -> startActivity(new Intent(this, StatusActivity.class)));
    }

    private void setupDropdownMenu() {
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
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                switch (position) {
                    case 0:
                        startActivity(new Intent(DietAddActivity.this, MainActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(DietAddActivity.this, ExerciseListActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(DietAddActivity.this, FriendListActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(DietAddActivity.this, StatusActivity.class));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        isEdit = "edit".equals(intent.getStringExtra("mode"));
        dietId = intent.getLongExtra("dietId", -1);

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
    }

    private void setupButtons() {
        btnBreakfast.setOnClickListener(v -> setMealtime("아침", btnBreakfast));
        btnLunch.setOnClickListener(v -> setMealtime("점심", btnLunch));
        btnDinner.setOnClickListener(v -> setMealtime("저녁", btnDinner));

        btnSubmit.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String mealTimeEnum = "";
            if ("아침".equals(mealtime)) {
                mealTimeEnum = "BREAKFAST";
            } else if ("점심".equals(mealtime)) {
                mealTimeEnum = "LUNCH";
            } else if ("저녁".equals(mealtime)) {
                mealTimeEnum = "DINNER";
            }

            JSONObject jsonBody = createDietJson(userID, dietId, mealTimeEnum, today);
            if (jsonBody == null) {
                Toast.makeText(this, "입력값 오류!", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            if (isEdit) {
                String url = "http://10.0.2.2:8080/api/diet/record/" + userID + "/" + dietId + "/update";
                queue.add(new DietUpdateRequest(userID, dietId, jsonBody,
                        resp -> {
                            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        },
                        err -> {
                            err.printStackTrace();
                            Toast.makeText(this, "수정 실패", Toast.LENGTH_LONG).show();
                        }
                ));
            } else {
                queue.add(new DietInsertRequest(userID, jsonBody,
                        resp -> {
                            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        },
                        err -> {
                            err.printStackTrace();
                            Toast.makeText(this, "등록 실패", Toast.LENGTH_LONG).show();
                        }
                ));
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private JSONObject createDietJson(String userID, Long dietId, String mealTimeEnum, String today) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", dietId);
            jsonBody.put("userId", userID);
            jsonBody.put("name", editName.getText().toString());
            jsonBody.put("mealtime", mealTimeEnum);
            jsonBody.put("calories", new BigDecimal(editCal.getText().toString()));
            jsonBody.put("carb", Integer.parseInt(editCarb.getText().toString()));
            jsonBody.put("protein", Integer.parseInt(editProtein.getText().toString()));
            jsonBody.put("fat", Integer.parseInt(editFat.getText().toString()));
            jsonBody.put("date", today);
            return jsonBody;
        } catch (Exception e) {
            return null;
        }
    }

    private void setMealtime(String time, Button selectedButton) {
        mealtime = time;

        btnBreakfast.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
        btnLunch.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));
        btnDinner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A0A0A0")));

        selectedButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB6C1")));
    }
}