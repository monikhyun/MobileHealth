package com.example.health.Stats;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.health.Auth.MainActivity;
import com.example.health.Diet.DietActivity;
import com.example.health.Exercise.ExerciseListActivity;
import com.example.health.Friend.FriendListActivity;
import com.example.health.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.fitness.*;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.request.*;
import com.google.android.gms.fitness.result.*;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class StatusActivity extends AppCompatActivity {
    private String userId;
    boolean isFirst = true;
    ImageView iconWorkout, icon_meal, icon_freinds, icon_stats, icon_home;

    TextView runTime, kcalText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userID = prefs.getString("USER_ID", null);  // 없으면 null 반환
        userId = userID;

        icon_home = findViewById(R.id.icon_home);
        icon_freinds = findViewById(R.id.icon_friends);
        iconWorkout = findViewById(R.id.icon_workout);
        icon_meal = findViewById(R.id.icon_meal);
        icon_stats = findViewById(R.id.icon_stats);
        runTime = findViewById(R.id.runTime);
        kcalText = findViewById(R.id.KcalText);
        Spinner topDropdownSpinner = findViewById(R.id.topDropdownSpinner);

        ArrayAdapter<CharSequence> topAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.menu_items,
                R.layout.spinner_item_bold
        );
        topAdapter.setDropDownViewResource(R.layout.spinner_item_bold);
        topDropdownSpinner.setAdapter(topAdapter);
        topDropdownSpinner.setSelection(4);
        topDropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                switch (position) {
                    case 0: // 홈
                        startActivity(new Intent(StatusActivity.this, MainActivity.class));
                        break;
                    case 1: // 운동
                        startActivity(new Intent(StatusActivity.this, ExerciseListActivity.class));
                        ;
                        break;
                    case 2: // 식단
                        startActivity(new Intent(StatusActivity.this, DietActivity.class));
                        break;
                    case 3: // 친구
                        startActivity(new Intent(StatusActivity.this, FriendListActivity.class));
                        break;
                    case 4: // 통계
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner barChartFilterSpinner = findViewById(R.id.barChartFilterSpinner);
        Spinner InBodyChartSpinner = findViewById(R.id.inBodyOptionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.bar_chart_filter_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barChartFilterSpinner.setAdapter(adapter);

        barChartFilterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                fetchChartData(selected.toLowerCase());
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        InBodyChartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                fetchInBodyBarChart(getLabel(selected));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // runTime 표시
        setRunTimeText();
        // Kcal계산 메서드
        fetchKcal();

        iconWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, ExerciseListActivity.class);
                startActivity(intent);
            }
        });
        icon_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, DietActivity.class);
                startActivity(intent);
            }
        });

        icon_freinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, FriendListActivity.class);
                startActivity(intent);
            }
        });

        icon_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatusActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchChartData(String type) {
        // 스피너에서 선택된 '일/주/월' 값을 API 호출용 영어로 변환
        String mappedType;
        switch (type) {
            // 일별
            case "일":
                mappedType = "daily";
                break;
            // 주별
            case "주":
                mappedType = "weekly";
                break;
            // 월별
            case "월":
                mappedType = "monthly";
                break;
            default:
                Toast.makeText(this, "잘못된 타입", Toast.LENGTH_SHORT).show();
                return;
        }

        // 요청 URL 생성 (일별은 userId만, 주/월별은 today 포함)
        String url;
        String baseUrl = "http://10.0.2.2:8080/api/stats/";
        if (mappedType.equals("daily")) {
            url = baseUrl + mappedType + "/" + this.userId;
        } else {
            String today = LocalDate.now().toString();
            url = baseUrl + mappedType + "/" + this.userId + "/" + today;
        }

        // 서버에 GET 요청 보내기
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // 응답(JSON 배열) 파싱 및 entries, labels 리스트 채우기
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();
                    List<Integer> colors = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            float value = (float) obj.getDouble("total");
                            String rawDate = obj.getString("startDate");
                            LocalDate parsedDate = LocalDate.parse(rawDate);
                            String label;
                            if (mappedType.equals("weekly")) {
                                // 주차를 월 내에서 표시
                                int weekOfMonth = parsedDate.get(java.time.temporal.WeekFields.of(java.util.Locale.KOREA).weekOfMonth());
                                label = weekOfMonth + "주차";
                            } else if (mappedType.equals("monthly")) {
                                label = parsedDate.getMonthValue() + "월";
                            } else {
                                // 요일(월/화/수...)로 라벨 표시
                                java.time.DayOfWeek dayOfWeek = parsedDate.getDayOfWeek();
                                java.time.format.TextStyle style = java.time.format.TextStyle.SHORT;
                                java.util.Locale locale = java.util.Locale.KOREAN;
                                label = dayOfWeek.getDisplayName(style, locale);
                            }

                            entries.add(new BarEntry(i, value));
                            labels.add(label);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // 막대 색상 3가지
                    for (int i = 0; i < entries.size(); i++) {
                        if (i % 3 == 0) colors.add(Color.parseColor("#FF7C8C"));
                        else if (i % 3 == 1) colors.add(Color.parseColor("#FFD700"));
                        else colors.add(Color.parseColor("#87CEFA"));
                    }
                    // 데이터 순서 및 레이블 설정 (월별은 역순 처리)
                    if (mappedType.equals("monthly")) {
                        Collections.reverse(entries);
                        Collections.reverse(labels);
                        Collections.reverse(colors);
                    }

                    // BarDataSet 생성 및 값 포매터
                    BarDataSet dataSet = new BarDataSet(entries, "");
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getBarLabel(BarEntry barEntry) {
                            return String.format("%.1fk", barEntry.getY() / 1000f);
                        }
                    });
                    dataSet.setColors(colors);
                    BarData data = new BarData(dataSet);
                    data.setDrawValues(true);
                    data.setValueTextSize(12f);  // or desired size
                    data.setBarWidth(0.6f);

                    // BarChart 객체 참조
                    BarChart barChart = findViewById(R.id.barChart);
                    barChart.setData(data);  // 차트에 BarData 설정
                    barChart.getXAxis().setAxisMinimum(-0.5f);// X축 범위 설정 (첫/끝 막대가 잘리치지 않도록)
                    barChart.getXAxis().setAxisMaximum(entries.size() - 0.5f);// 막대가 X축 눈금에 맞춰지도록 설정
                    barChart.setFitBars(true); // 차트 범례 숨기기
                    barChart.getLegend().setEnabled(false);
                    barChart.getDescription().setEnabled(false); // 차트 설명 텍스트 숨기기
                    barChart.getAxisLeft().setEnabled(false); // 왼쪽 Y축 숨기기
                    // 오른쪽 Y축 값 포맷터 설정 (k 단위)
                    barChart.getAxisRight().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format("%.1fk", value / 1000f);
                        }
                    });

                    // X축 라벨 및 스타일 설정
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setDrawGridLines(false);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                    // 차트 갱신
                    barChart.invalidate();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchInBodyBarChart(String type) {
        // 인바디 API URL 구성 및 View 초기화
        if (type.equals("fat_percent")) {
            type = "fat";
        }
        String url = "http://10.0.2.2:8080/api/stats/inbody/" + type + "/" + userId;
        HorizontalBarChart inbodyBarChart = findViewById(R.id.inBodyBarChart);
        LineChart inbodyLineChart = findViewById(R.id.inBodyLineChart);

        // BarChart 기본 속성 설정
        inbodyBarChart.getDescription().setEnabled(false);
        inbodyBarChart.getLegend().setEnabled(false);
        inbodyBarChart.getAxisRight().setEnabled(false);
        inbodyBarChart.getAxisLeft().setDrawGridLines(false);
        // 그래프 내용 영역을 오른쪽으로 약간 이동
        inbodyBarChart.setExtraLeftOffset(40f);

        if (type.equals("all")) {
            // '전체' 모드: BarChart 보이고 LineChart 숨기기
            inbodyBarChart.setVisibility(View.VISIBLE);
            inbodyLineChart.setVisibility(View.GONE);
            // 전체 데이터 단일 JSON 응답 요청
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONObject obj = response;
                            obj.remove("date");
                            // 원본 값, 한글 레이블, 색상, 키 목록 준비
                            List<Float> rawValues = new ArrayList<>();
                            List<String> labels = new ArrayList<>();
                            List<Integer> colors = new ArrayList<>();
                            // 키 목록 저장
                            List<String> keyList = new ArrayList<>();
                            JSONArray keys = obj.names();
                            if (keys != null) {
                                for (int i = 0; i < keys.length(); i++) {
                                    String key = keys.getString(i);
                                    float value = (float) obj.optDouble(key, 0.0);
                                    rawValues.add(value);
                                    labels.add(getKoreanLabel(key));
                                    colors.add(i % 3 == 0
                                            ? Color.parseColor("#FF7C8C")
                                            : i % 3 == 1
                                            ? Color.parseColor("#FFD700")
                                            : Color.parseColor("#87CEFA"));
                                    keyList.add(key);
                                }
                            }

                            // kg 단위 항목(weight, SMM, LBM) 중 최댓값 계산
                            float maxKg = rawValues.stream()
                                    .filter(v -> {
                                        String k = keyList.get(rawValues.indexOf(v));
                                        return k.equals("weight") || k.equals("SMM") || k.equals("LBM");
                                    })
                                    .max(Float::compare)
                                    .orElse(1f);

                            // 정규화 및 BarEntry 생성 (BMI/체지방률은 2배 스케일)
                            List<BarEntry> entries = new ArrayList<>();
                            for (int i = 0; i < rawValues.size(); i++) {
                                float raw = rawValues.get(i);
                                String key = keyList.get(i);
                                float normalized;
                                if (key.equals("BMI") || key.equals("fat")) {
                                    // BMI와 체지방률은 데이터 값 그대로, 길이는 2배 확장
                                    normalized = (raw / 100f) * 2f;
                                } else {
                                    normalized = maxKg == 0f ? 0f : raw / maxKg;
                                }
                                entries.add(new BarEntry(i, normalized));
                            }
                            // BarDataSet 생성 및 값 라벨 포맷터 설정
                            BarDataSet dataSet = new BarDataSet(entries, "");
                            dataSet.setColors(colors);
                            dataSet.setDrawValues(true);// 막대 위 값 표시 활성화
                            dataSet.setValueTextSize(12f);
                            dataSet.setValueTextColor(Color.BLACK);
                            // 실제 값으로 라벨 표시 (BMI, 체지방률은 %)
                            dataSet.setValueFormatter(new ValueFormatter() {
                                @Override
                                public String getBarLabel(BarEntry barEntry) {
                                    int idx = (int) barEntry.getX();
                                    float actual = rawValues.get(idx);
                                    String key = keyList.get(idx);
                                    if (key.equals("fat")) {
                                        // 체지방률: 소수 첫째 자리까지, 뒤에 % 기호
                                        return String.format(Locale.getDefault(), "%.1f%%", actual);
                                    } else if (key.equals("BMI")) {
                                        // BMI: 소수 첫째 자리까지, 뒤에 kg/m² 단위
                                        return String.format(Locale.getDefault(), "%.1fkg/m\u00B2", actual);
                                    } else {
                                        // 그 외: 소수 첫째 자리까지
                                        return String.format(Locale.getDefault(), "%.1f", actual);
                                    }
                                }
                            });

                            BarData data = new BarData(dataSet);
                            data.setDrawValues(true);
                            data.setValueTextSize(12f);
                            data.setValueTextColor(Color.BLACK);
                            data.setBarWidth(0.6f);

                            // X축 객체 가져오기 (가로 막대 차트에서 카테고리 레이블 축)
                            XAxis xAxis = inbodyBarChart.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // X축 레이블 포맷터 설정 (한글 카테고리명)
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE); // X축 위치를 차트 내부 하단으로 설정
                            xAxis.setGranularity(1f);// X축 눈금 간격을 1로 고정 (막대 하나당 하나의 눈금)
                            xAxis.setGranularityEnabled(true); // X축 간격 설정 활성화
                            xAxis.setDrawGridLines(false); // X축 그리드 라인 숨기기
                            // 카테고리 레이블과 바 사이 간격 조정
                            xAxis.setXOffset(-5f);

                            // Y축 그리드/레이블 숨기기
                            YAxis yAxis = inbodyBarChart.getAxisLeft();
                            yAxis.setDrawGridLines(false);
                            yAxis.setAxisMinimum(0f); // 0부터 값 시작
                            // 상단에 그려지는 값 눈금 레이블 숨기기
                            yAxis.setDrawLabels(false);

                            //  데이터 적용 및 차트 갱신
                            inbodyBarChart.setData(data);
                            // 막대 옆에 값 표시
                            inbodyBarChart.setDrawValueAboveBar(true);
                            inbodyBarChart.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(this, "인바디 종합 데이터 로드 실패", Toast.LENGTH_SHORT).show());
            Volley.newRequestQueue(this).add(request);
        } else {
            // 개별 타입 모드: LineChart 설정 및 데이터 처리
            inbodyBarChart.setVisibility(View.GONE);
            inbodyLineChart.setVisibility(View.VISIBLE);
            String jsonKey;
            if (type.equals("all")) {
                jsonKey = null;
            } else if (type.equals("weight")) {
                jsonKey = "weight";
            } else if (type.equals("SMM")) {
                jsonKey = "smm";
            } else if (type.equals("LBM")) {
                jsonKey = "lbm";
            } else if (type.equals("BMI")) {
                jsonKey = "bmi";
            } else if (type.equals("fat")) {
                jsonKey = "fat_percent";
            } else {
                jsonKey = type.toLowerCase();
            }
            // 개별 항목 조회: JSONArray 엔드 포인트, 최근 4개 데이터 표시
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        List<BarEntry> entries = new ArrayList<>();
                        List<String> labels = new ArrayList<>();
                        List<Integer> colors = new ArrayList<>();
                        int limit = Math.min(4, response.length());
                        try {
                            JSONArray reversed = new JSONArray();
                            for (int i = response.length() - 1; i >= 0; i--) {
                                reversed.put(response.getJSONObject(i));
                            }
                            for (int i = 0; i < limit; i++) {
                                JSONObject obj = reversed.getJSONObject(i);
                                float value = (float) obj.optDouble(jsonKey, 0.0);
                                String date = obj.getString("date");
                                entries.add(new BarEntry(i, value));
                                labels.add(formatDate(date));
                                colors.add(i % 3 == 0 ? Color.parseColor("#FF7C8C")
                                        : i % 3 == 1 ? Color.parseColor("#FFD700")
                                        : Color.parseColor("#87CEFA"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String labelName = getKoreanLabel(jsonKey);
                        // LineChart 설정
                        List<com.github.mikephil.charting.data.Entry> lineEntries = new ArrayList<>();
                        for (int i = 0; i < limit; i++) {
                            lineEntries.add(new com.github.mikephil.charting.data.Entry(i, entries.get(i).getY()));
                        }
                        com.github.mikephil.charting.data.LineDataSet lineDataSet = new com.github.mikephil.charting.data.LineDataSet(lineEntries, labelName);
                        lineDataSet.setDrawValues(true);// 꺾은선 차트 점 위 값 표시 활성화
                        lineDataSet.setValueTextSize(12f);
                        lineDataSet.setColor(Color.parseColor("#FF7C8C"));
                        lineDataSet.setLineWidth(2f);// 선 두께 설정
                        lineDataSet.setCircleColor(Color.parseColor("#FF7C8C"));// 점 색상 설정
                        lineDataSet.setCircleRadius(6f);// 점의 크기를 키우고, 내부 흰 점(hole)을 제거
                        lineDataSet.setDrawCircleHole(false);
                        com.github.mikephil.charting.data.LineData lineData = new com.github.mikephil.charting.data.LineData(lineDataSet);
                        inbodyLineChart.getDescription().setEnabled(false); // LineChart 설명 텍스트 숨기기
                        inbodyLineChart.getAxisRight().setEnabled(false);// 오른쪽 Y축 숨기기
                        XAxis lx = inbodyLineChart.getXAxis();
                        lx.setValueFormatter(new IndexAxisValueFormatter(labels));
                        lx.setPosition(XAxis.XAxisPosition.BOTTOM);
                        lx.setGranularity(1f);
                        inbodyLineChart.getAxisLeft().setDrawGridLines(false);
                        inbodyLineChart.setData(lineData);  // 꺾은선 차트 데이터 적용 및 갱신
                        inbodyLineChart.invalidate();

                    },
                    error -> Toast.makeText(this, "인바디 데이터 로드 실패", Toast.LENGTH_SHORT).show());
            Volley.newRequestQueue(this).add(request);
        }
    }

    private String formatDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd"));
    }

    // 인바디 스피너 값 매핑
    private String getLabel(String type) {
        switch (type) {
            case "전체":
                return "all";
            case "체중":
                return "weight";
            case "골격근량":
                return "SMM";
            case "체지방량":
                return "LBM";
            case "BMI":
                return "BMI";
            case "체지방률":
                return "fat";
            default:
                return "all";
        }
    }
    // 바 차트 데이터 한글 라벨 변환
    private String getKoreanLabel(String key) {
        switch (key) {
            case "weight":
                return "체중";
            case "SMM":
                return "골격근량";
            case "LBM":
                return "제지방량";
            case "BMI":
                return "BMI";
            case "fat":
                return "체지방률";
            default:
                return key;
        }
    }
    private void fetchKcal(){
        String url = "http://10.0.2.2:8080/api/stats/daily/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject dayObj = response.getJSONObject(i);
                            String date = dayObj.getString("startDate");

                            if (today.equals(date)) {
                                // 운동 볼륨을 칼로리로 변환
                                int volume = dayObj.getInt("total");
                                int calories = volume / 100;

                                kcalText.setText(calories + " kcal /일");
                                break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        kcalText.setText("불러오기 오류");
                    }
                },
                error -> {
                    error.printStackTrace();
                    kcalText.setText("네트워크 오류");
                }
        );

        queue.add(request);
    }
    private void setRunTimeText(){
        String url = "http://10.0.2.2:8080/api/home/activity/" + userId;
        // rumTime 가져오기
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int minutes = response.getInt("totalExerciseMinutes");
                        if (minutes < 60) {
                            runTime.setText(minutes + "초");
                        } else {
                            runTime.setText(minutes / 60 + "분");
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
    }
}

