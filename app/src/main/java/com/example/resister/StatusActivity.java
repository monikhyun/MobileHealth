package com.example.resister;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatusActivity extends AppCompatActivity {
    private String userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        userId = getIntent().getStringExtra("userId");
        BarChart barChart = findViewById(R.id.barChart);
        Spinner barChartFilterSpinner = findViewById(R.id.barChartFilterSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.bar_chart_filter_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barChartFilterSpinner.setAdapter(adapter);

        barChartFilterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                fetchChartData(selected);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void fetchChartData(String type) {
        String url = "http://10.0.2.2:8080/api/exercise/summary?userId=" + this.userId + "&type=" + type;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            float value = (float) obj.getDouble("value");
                            String label = obj.getString("label");
                            entries.add(new BarEntry(i, value));
                            labels.add(label);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "운동량");
                    dataSet.setColor(Color.parseColor("#FF7C8C"));
                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.9f);

                    BarChart barChart = findViewById(R.id.barChart);
                    barChart.setData(data);
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setGranularityEnabled(true);
                    barChart.setFitBars(true);
                    barChart.invalidate();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
