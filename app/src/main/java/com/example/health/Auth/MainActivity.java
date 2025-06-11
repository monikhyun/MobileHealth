package com.example.health.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.health.Diet.DietActivity;
import com.example.health.Exercise.ExerciseListActivity;
import com.example.health.Friend.FriendListActivity;
import com.example.health.Home.MyPageActivity;
import com.example.health.Stats.StatusActivity;

import com.example.health.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageView iconWorkout,icon_meal, icon_freinds,icon_stats,icon_home;
    TextView totalV,runTime,stepCountText,distanceText;

    LineChart lineChart;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1001;
    private FitnessOptions fitnessOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        icon_home = findViewById(R.id.icon_home);
        icon_freinds = findViewById(R.id.icon_friends);
        iconWorkout = findViewById(R.id.icon_workout);
        icon_meal = findViewById(R.id.icon_meal);
        icon_stats = findViewById(R.id.icon_stats);
        totalV= findViewById(R.id.totalV);
        runTime = findViewById(R.id.runTime);
        lineChart = findViewById(R.id.lineChart);
        stepCountText = findViewById(R.id.stepCountText);
        distanceText = findViewById(R.id.distanceText);

        // Google Fit 권한 설정
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        // 권한 확인 및 요청
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
            );
        } else {
            readFitData(account);
        }
        profile = findViewById(R.id.btn_profile);

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
        // 1) SharedPreferences 열기
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // 2) USER_ID 읽어오기 (없으면 null)
        String userID = prefs.getString("USER_ID", null);
        Spinner topDropdownSpinner = findViewById(R.id.topDropdownSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.menu_items,
                R.layout.spinner_item_bold
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_bold);
        topDropdownSpinner.setAdapter(adapter);
        topDropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 홈
                        // 현재 액티비티가 홈이면 이동하지 않음
                        break;
                    case 1: // 운동
                        startActivity(new Intent(MainActivity.this, ExerciseListActivity.class));
                        break;
                    case 2: // 식단
                        startActivity(new Intent(MainActivity.this, DietActivity.class));
                        break;
                    case 3: // 친구
                        startActivity(new Intent(MainActivity.this, FriendListActivity.class));
                        break;
                    case 4: // 통계
                        startActivity(new Intent(MainActivity.this, StatusActivity.class));
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 운동 시간 받아와서 runTime에 표시
        if (userID != null) {
            String url = "http://10.0.2.2:8080/api/home/activity/" + userID;

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            int minutes = response.getInt("totalExerciseMinutes");
                            if(minutes<60){
                                runTime.setText(minutes+"초");
                            }else {
                                runTime.setText(minutes/60 + "분");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runTime.setText("0분");
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        runTime.setText("0분");
                    });

            queue.add(request);
            // 운동 총량 받아와서 totalV에 표시
            String totalUrl = "http://10.0.2.2:8080/api/stats/daily/" + userID;
            JsonArrayRequest totalRequest = new JsonArrayRequest(Request.Method.GET, totalUrl, null,
                    response2 -> {
                        try {
                            BigDecimal sum = BigDecimal.ZERO;
                            for (int i = 0; i < response2.length(); i++) {
                                JSONObject obj = response2.getJSONObject(i);
                                BigDecimal daily = new BigDecimal(obj.getString("total"));
                                sum = sum.add(daily);
                            }
                            double value = sum.doubleValue();
                            String display = value >= 1000 ? String.format("%.1fk", value / 1000) : String.valueOf((int) value);
                            totalV.setText(display);
                        } catch (Exception e) {
                            e.printStackTrace();
                            totalV.setText("N/A");
                        }
                    },
                    error2 -> {
                        error2.printStackTrace();
                        totalV.setText("ERR");
                    }
            );
            queue.add(totalRequest);
        }

        loadChartData(userID);


        iconWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExerciseListActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.nav_stats).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatusActivity.class);
            startActivity(intent);
        });
        icon_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });

        icon_freinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendListActivity.class);
                startActivity(intent);
            }
        });

        icon_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });


    }
    private void loadChartData(String userId) {
        String url = "http://10.0.2.2:8080/api/exercise/part/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    Map<String, Integer> thisWeekMap = new HashMap<>();
                    Map<String, Integer> lastWeekMap = new HashMap<>();
                    String thisWeekStart = "";

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String bodyPart = obj.getString("bodyPart");
                        int volume = obj.getInt("totalVolume");
                        String startDate = obj.getString("startDate");

                        if (thisWeekStart.isEmpty()) thisWeekStart = startDate;

                        if (startDate.equals(thisWeekStart)) {
                            thisWeekMap.put(bodyPart, volume);
                        } else {
                            lastWeekMap.put(bodyPart, volume);
                        }
                    }

                    List<String> fixedLabels = Arrays.asList("하체", "팔", "등", "어깨", "가슴");
                    List<Entry> thisWeekEntries = new ArrayList<>();
                    List<Entry> lastWeekEntries = new ArrayList<>();

                    for (int i = 0; i < fixedLabels.size(); i++) {
                        String part = fixedLabels.get(i);
                        thisWeekEntries.add(new Entry(i, thisWeekMap.getOrDefault(part, 0)));
                        lastWeekEntries.add(new Entry(i, lastWeekMap.getOrDefault(part, 0)));
                    }

                    LineDataSet set1 = new LineDataSet(thisWeekEntries, "이번 주");
                    LineDataSet set2 = new LineDataSet(lastWeekEntries, "저번 주");
                    set1.setColor(Color.BLUE);
                    set1.setCircleRadius(4f);
                    set1.setValueTextSize(12f);
                    set1.setCircleRadius(4f);// 점의 크기를 키우고, 내부 흰 점(hole)을 제거
                    set1.setDrawCircleHole(false);
                    set1.setCircleColor(Color.BLUE);

                    set2.setColor(Color.RED);
                    set2.setCircleColor(Color.RED);
                    set2.setCircleRadius(4f);
                    set2.setValueTextSize(12f);
                    set2.setCircleRadius(4f);// 점의 크기를 키우고, 내부 흰 점(hole)을 제거
                    set2.setDrawCircleHole(false);
                    LineData data = new LineData(set1, set2);
                    lineChart.setData(data);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(fixedLabels));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setAxisMinimum(-0.5f);
                    xAxis.setAxisMaximum(fixedLabels.size() - 0.5f);
                    xAxis.setDrawGridLines(false);
                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format("%.1fk", value / 1000f);
                        }
                    });
                    leftAxis.setDrawGridLines(false);

                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format("%.1fk", value / 1000f);
                        }
                    });
                    rightAxis.setDrawGridLines(false);

                    set1.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getPointLabel(Entry entry) {
                            return String.format("%.1fk", entry.getY() / 1000f);
                        }
                    });
                    set2.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getPointLabel(Entry entry) {
                            return String.format("%.1fk", entry.getY() / 1000f);
                        }
                    });

                    lineChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> error.printStackTrace()
        );

        queue.add(request);
    }
    private void readFitData(GoogleSignInAccount account) {
        // 걸음 수
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {
                    int totalSteps = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    stepCountText.setText(String.format("%,d", totalSteps));
                });

        // 이동 거리
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(dataSet -> {
                    float distance = dataSet.isEmpty() ? 0f :
                            dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat();
                    float distanceKm = distance / 1000f;
                    distanceText.setText(String.format("%.2f KM", distanceKm));
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
            if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                readFitData(account);
            }
        }
    }
}