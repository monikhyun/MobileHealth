package com.example.resister;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Pie;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {

    private String userId;

    // Fetch diet data from server and update chart and meal list
    private void fetchDietData(String userId, String today) {
        String url = "http://10.0.2.2:8080/api/diet/" + userId + "?date=" + today;

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    // Prepare for summing nutrients and clearing previous views
                    double totalCarb = 0;
                    double totalProtein = 0;
                    double totalFat = 0;
                    double totalCal = 0;

                    List<DataEntry> data = new ArrayList<>();
                    LinearLayout mealContainer = findViewById(R.id.mealContainer);
                    mealContainer.removeAllViews();

                    // Parse each meal entry
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject meal = response.getJSONObject(i);
                        String name = meal.getString("name");
                        int carbMeal = meal.getInt("carb");
                        int proteinMeal = meal.getInt("protein");
                        int fatMeal = meal.getInt("fat");
                        String mealtime = meal.getString("mealtime");

                        // Accumulate totals
                        totalCarb += carbMeal;
                        totalProtein += proteinMeal;
                        totalFat += fatMeal;

                        // Inflate and populate meal item view
                        View mealView = getLayoutInflater()
                            .inflate(R.layout.diet_items, mealContainer, false);
                        ((TextView) mealView.findViewById(R.id.mealTimeText)).setText(mealtime);
                        ((TextView) mealView.findViewById(R.id.nameText)).setText(name);
                        ((TextView) mealView.findViewById(R.id.carbText))
                            .setText(String.valueOf(carbMeal));
                        ((TextView) mealView.findViewById(R.id.proteinText))
                            .setText(String.valueOf(proteinMeal));
                        ((TextView) mealView.findViewById(R.id.fatText))
                            .setText(String.valueOf(fatMeal));
                        mealContainer.addView(mealView);
                        // Add calories for this meal
                        totalCal += meal.getDouble("calories");
                    }

                    // Build chart data from totals
                    data.add(new ValueDataEntry("탄수화물", totalCarb));
                    data.add(new ValueDataEntry("단백질", totalProtein));
                    data.add(new ValueDataEntry("지방", totalFat));

                    Pie pie = AnyChart.pie();
                    pie.data(data);
                    pie.title("영양소 비율");
                    AnyChartView anyChartView = findViewById(R.id.meal_chart_view);
                    anyChartView.setChart(pie);

                    // Update TextViews with totals
                    ((TextView) findViewById(R.id.valueCarb)).setText(String.valueOf(totalCarb));
                    ((TextView) findViewById(R.id.valueProtein)).setText(String.valueOf(totalProtein));
                    ((TextView) findViewById(R.id.valueFat)).setText(String.valueOf(totalFat));
                    ((TextView) findViewById(R.id.valueCal)).setText(String.valueOf(totalCal));

                } catch (JSONException e) {
                    Log.e("DietFetchError", e.toString());
                }
            },
            error -> Log.e("DietFetchError", error.toString())
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // DietActivity: 오늘 날짜의 식단 정보를 불러오고 차트 및 리스트로 표시
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        // 로그인된 사용자 ID와 오늘 날짜 가져오기
        this.userId = getIntent().getStringExtra("userId");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // + 버튼 클릭 시 DietAddActivity로 이동
        findViewById(R.id.buttonAddMeal).setOnClickListener(v -> {
            Intent intent = new Intent(DietActivity.this, DietAddActivity.class);
            intent.putExtra("userId", this.userId);
            startActivityForResult(intent, 100);
        });

        fetchDietData(this.userId, today);
    }
    // DietAddActivity에서 돌아왔을 때 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            fetchDietData(this.userId, today);
        }
    }
}
