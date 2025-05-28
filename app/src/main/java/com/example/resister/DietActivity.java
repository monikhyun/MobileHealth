package com.example.resister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {

    private String userId;
    private ActivityResultLauncher<Intent> dietAddLauncher;
    private boolean shouldRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);

        dietAddLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        shouldRefresh = true;
                    }
                });

        findViewById(R.id.buttonAddMeal).setOnClickListener(v -> {
            Intent intent = new Intent(DietActivity.this, DietAddActivity.class);
            dietAddLauncher.launch(intent);
        });

        fetchDietData(getToday());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRefresh) {
            fetchDietData(getToday());
            shouldRefresh = false;
        }
    }

    private String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void fetchDietData(String today) {
        String url = "http://10.0.2.2:8080/api/diet/" + userId + "?date=" + today;

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> new Thread(() -> {
                    double totalCarb = 0, totalProtein = 0, totalFat = 0;
                    BigDecimal totalKcal = BigDecimal.ZERO;
                    List<JSONObject> diets = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject diet = response.getJSONObject(i);
                            diets.add(diet);

                            totalCarb += diet.getInt("carb");
                            totalProtein += diet.getInt("protein");
                            totalFat += diet.getInt("fat");
                            totalKcal = totalKcal.add(BigDecimal.valueOf(diet.getDouble("calories")));
                        }
                    } catch (JSONException e) {
                        Log.e("DietFetchError", "JSON parsing failed", e);
                    }

                    double finalCarb = totalCarb;
                    double finalProtein = totalProtein;
                    double finalFat = totalFat;
                    BigDecimal finalKcal = totalKcal;

                    runOnUiThread(() -> {
                        LinearLayout mealContainer = findViewById(R.id.mealContainer);
                        mealContainer.removeAllViews();

                        for (JSONObject diet : diets) {
                            try {
                                String name = diet.getString("name");
                                int carbMeal = diet.getInt("carb");
                                int proteinMeal = diet.getInt("protein");
                                int fatMeal = diet.getInt("fat");
                                String mealtime = diet.getString("mealtime");
                                BigDecimal kcal = BigDecimal.valueOf(diet.getDouble("calories"));

                                View mealView = getLayoutInflater()
                                        .inflate(R.layout.diet_items, mealContainer, false);
                                ((TextView) mealView.findViewById(R.id.mealTimeText)).setText(mealtime);
                                ((TextView) mealView.findViewById(R.id.nameText)).setText(name);
                                ((TextView) mealView.findViewById(R.id.carbText)).setText(String.valueOf(carbMeal));
                                ((TextView) mealView.findViewById(R.id.proteinText)).setText(String.valueOf(proteinMeal));
                                ((TextView) mealView.findViewById(R.id.fatText)).setText(String.valueOf(fatMeal));
                                ((TextView) mealView.findViewById(R.id.calText)).setText(kcal.toPlainString());

                                mealView.setTag(diet);
                                mealView.setOnClickListener(v -> {
                                    Intent intent = new Intent(DietActivity.this, DietAddActivity.class);
                                    intent.putExtra("mode", "edit");
                                    try {
                                        Log.d("DietDebug", "diet JSON: " + diet.toString());
                                        intent.putExtra("dietId", diet.getLong("id"));
                                        intent.putExtra("name", diet.getString("name"));
                                        intent.putExtra("carb", diet.getInt("carb"));
                                        intent.putExtra("protein", diet.getInt("protein"));
                                        intent.putExtra("fat", diet.getInt("fat"));
                                        intent.putExtra("calories", diet.getDouble("calories"));
                                        intent.putExtra("mealtime", diet.getString("mealtime"));
                                    } catch (JSONException e) {
                                        Log.e("DietActivity", "Failed to pass diet info", e);
                                        return;
                                    }
                                    dietAddLauncher.launch(intent);
                                });

                                mealContainer.addView(mealView);
                            } catch (JSONException e) {
                                Log.e("DietActivity", "View binding failed", e);
                            }
                        }

                        // MPAndroidChart 파이차트 갱신
                        PieChart pieChart = findViewById(R.id.meal_chart_view);
                        List<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) finalCarb, "탄수화물"));
                        entries.add(new PieEntry((float) finalProtein, "단백질"));
                        entries.add(new PieEntry((float) finalFat, "지방"));

                        PieDataSet dataSet = new PieDataSet(entries, "");
                        dataSet.setColors(
                                Color.parseColor("#FFA7A7"), // 탄수화물
                                Color.parseColor("#B2EBF4"), // 단백질
                                Color.parseColor("#FAED7D")  // 지방
                        );
                        dataSet.setValueTextSize(13f);
                        dataSet.setValueTextColor(Color.WHITE);
                        dataSet.setSliceSpace(2f);

                        PieData pieData = new PieData(dataSet);
                        pieData.setValueFormatter(new PercentFormatter(pieChart));

                        pieChart.setUsePercentValues(true);
                        pieChart.setHoleRadius(45f);
                        pieChart.setTransparentCircleRadius(50f);
                        pieChart.setEntryLabelColor(Color.BLACK);
                        pieChart.setEntryLabelTextSize(12f);
                        pieChart.setData(pieData);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.getLegend().setEnabled(false);
                        pieChart.setCenterText("영양소 비율");
                        pieChart.setCenterTextSize(16f);
                        pieChart.invalidate(); // 다시 그리기

                        // 합계 텍스트 업데이트
                        ((TextView) findViewById(R.id.valueCarb)).setText(String.valueOf((int) finalCarb));
                        ((TextView) findViewById(R.id.valueProtein)).setText(String.valueOf((int) finalProtein));
                        ((TextView) findViewById(R.id.valueFat)).setText(String.valueOf((int) finalFat));
                        ((TextView) findViewById(R.id.valueCal)).setText(finalKcal.toPlainString());
                    });
                }).start(),
                error -> Log.e("DietFetchError", error.toString())
        );

        request.setShouldCache(false);
        queue.add(request);
    }
}