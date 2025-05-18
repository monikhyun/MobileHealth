package com.example.resister;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import com.example.resister.Request.DietRequest;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // DietActivity: 오늘 날짜의 식단 정보를 불러오고 차트 및 리스트로 표시
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        // + 버튼 클릭 시 DietAddActivity로 이동
        findViewById(R.id.buttonAddMeal).setOnClickListener(v -> {
            Intent intent = new Intent(DietActivity.this, DietAddActivity.class);
            startActivityForResult(intent, 100);
        });

        // 로그인된 사용자 ID와 오늘 날짜 가져오기
        String id = getIntent().getStringExtra("userID");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Response.Listener<String> rListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // 서버 응답으로부터 탄단지 값 추출하여 파이차트 구성
                    JSONObject jResponse = new JSONObject(response);

                    double carb = jResponse.getDouble("carb");
                    double protein = jResponse.getDouble("protein");
                    double fat = jResponse.getDouble("fat");

                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("탄수화물", carb));
                    data.add(new ValueDataEntry("단백질", protein));
                    data.add(new ValueDataEntry("지방", fat));

                    Pie pie = AnyChart.pie();
                    pie.data(data);
                    pie.title("영양소 비율");
                    AnyChartView anyChartView = findViewById(R.id.meal_chart_view);
                    anyChartView.setChart(pie);

                    // mealContainer 내부 기존 뷰 제거 후 새로 받은 식단 정보로 뷰 동적 생성
                    LinearLayout mealContainer = findViewById(R.id.mealContainer);
                    mealContainer.removeAllViews();

                    JSONArray meals = jResponse.getJSONArray("meals");
                    for (int i = 0; i < meals.length(); i++) {
                        // 각 식단 객체에서 정보 추출 후 diet_items.xml 레이아웃을 inflate 하여 표시
                        JSONObject meal = meals.getJSONObject(i);
                        String name = meal.getString("name");
                        double carbMeal = meal.getDouble("carb");
                        double proteinMeal = meal.getDouble("protein");
                        double fatMeal = meal.getDouble("fat");
                        String mealtime = meal.getString("mealtime");

                        View mealView = getLayoutInflater().inflate(R.layout.diet_items, mealContainer, false);
                        ((TextView) mealView.findViewById(R.id.mealTimeText)).setText(mealtime);
                        ((TextView) mealView.findViewById(R.id.nameText)).setText(name);
                        ((TextView) mealView.findViewById(R.id.carbText)).setText(String.valueOf(carbMeal));
                        ((TextView) mealView.findViewById(R.id.proteinText)).setText(String.valueOf(proteinMeal));
                        ((TextView) mealView.findViewById(R.id.fatText)).setText(String.valueOf(fatMeal));
                        mealContainer.addView(mealView);
                    }

                } catch (JSONException e) {
                    Log.d("diet", e.toString());
                }
            }
        };

        DietRequest request = new DietRequest(id, today, rListener);
        RequestQueue queue = Volley.newRequestQueue(DietActivity.this);
        queue.add(request);
    }
    // DietAddActivity에서 돌아왔을 때 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            double carb = data.getDoubleExtra("carb", 0);
            double protein = data.getDoubleExtra("protein", 0);
            double fat = data.getDoubleExtra("fat", 0);

            // 새로 추가된 식단의 탄단지 정보로 차트 갱신
            List<DataEntry> newData = new ArrayList<>();
            newData.add(new ValueDataEntry("탄수화물", carb));
            newData.add(new ValueDataEntry("단백질", protein));
            newData.add(new ValueDataEntry("지방", fat));

            Pie pie = AnyChart.pie();
            pie.data(newData);
            pie.title("입력 기반 영양소 비율");
            AnyChartView anyChartView = findViewById(R.id.meal_chart_view);
            anyChartView.setChart(pie);
        }
    }
}
