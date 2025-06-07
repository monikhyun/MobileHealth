package com.example.health.Diet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Pie;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.example.health.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {

    private String userId;



    // Fetch diet data from server and update chart and meal list
    private void fetchDietData(String today) {
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
                    BigDecimal totalkcal =BigDecimal.ZERO;

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
                        BigDecimal kcalMeal = BigDecimal.valueOf(meal.getDouble("calories"));

                        // Accumulate totals
                        totalCarb += carbMeal;
                        totalProtein += proteinMeal;
                        totalFat += fatMeal;
                        totalkcal = totalkcal.add(kcalMeal);

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
                    Log.d("Nutrients", "Carb: " + totalCarb + ", Protein: " + totalProtein + ", Fat: " + totalFat);
                    // Set total values to summary TextViews
                    ((TextView) findViewById(R.id.valueCarb)).setText(String.valueOf((int) totalCarb));
                    ((TextView) findViewById(R.id.valueProtein)).setText(String.valueOf((int) totalProtein));
                    ((TextView) findViewById(R.id.valueFat)).setText(String.valueOf((int) totalFat));
                    ((TextView) findViewById(R.id.valueCal)).setText(totalkcal.toPlainString());


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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);


        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);  // 클래스 필드에 저장

        if (userId != null) {

            Log.d("USER_ID", userId);
        } else {

            Log.d("USER_ID", "No userId found");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // + 버튼 클릭 시 DietAddActivity로 이동
        findViewById(R.id.buttonAddMeal).setOnClickListener(v -> {
            Intent intent = new Intent(DietActivity.this, DietAddActivity.class);
            startActivityForResult(intent, 100);
        });

        fetchDietData(today);
    }
    // DietAddActivity에서 돌아왔을 때 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DietActivity", "onActivityResult 호출됨: " + requestCode + ", result: " + resultCode); // 추가

        if (userId != null) {
            // 정상적으로 꺼내진 경우
            Log.d("USER_ID", userId);
        } else {
            // 저장된 값이 없을 경우
            Log.d("USER_ID", "No userId found");
            return;
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Log.d("today", today);
            fetchDietData(today);
        }
    }
}
