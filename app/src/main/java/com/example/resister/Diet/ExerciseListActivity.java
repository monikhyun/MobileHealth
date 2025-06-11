package com.example.resister.Diet;

import com.example.resister.Request.Exercise.AddedExerciseAdapter;
import com.example.resister.Exercise.ExerciseAddListActivity;
import com.example.resister.Exercise.ExerciseDetailActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.resister.Request.Exercise.ExerciseItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ExerciseListActivity extends AppCompatActivity {
    private RecyclerView recyclerExercise;
    private ImageButton btnAddSet;
    private AddedExerciseAdapter adapter;
    TextView textDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        textDate = findViewById(R.id.text_date);
        // RecyclerView 설정
        recyclerExercise = findViewById(R.id.recycler_exercise);
        recyclerExercise.setLayoutManager(new LinearLayoutManager(this));

        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        adapter = new AddedExerciseAdapter(new ArrayList<>(), item -> {
            // 1) 화면에 표시된 텍스트에서 raw 문자열 꺼내기
            String raw = textDate.getText().toString().replace("▼", "").trim();
            // 2) 파싱
            DateTimeFormatter krFmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault());
            LocalDate parsed = LocalDate.parse(raw, krFmt);
            // 3) ISO 형식 만들기
            String isoDate = parsed.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // 4) intent 에 두 값 모두 담아서 넘기기
            Intent intent = new Intent(ExerciseListActivity.this,
                    ExerciseDetailActivity.class);
            intent.putExtra("EXERCISE_NAME", item.exerciseName);
            intent.putExtra("DATE", isoDate);
            startActivity(intent);
        });
        recyclerExercise.setAdapter(adapter);

        // "+" 버튼: AddList 화면으로 이동
        btnAddSet = findViewById(R.id.btn_add_set);
        btnAddSet.setOnClickListener(v ->
            startActivity(new Intent(this, ExerciseAddListActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddedExercises();
    }

    private void loadAddedExercises() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("USER_ID", null);
        if (userId == null) return;

        String raw = textDate.getText().toString();


        raw = raw.replace("▼", "").trim();


        DateTimeFormatter koreanFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.getDefault());
        LocalDate parsedDate = LocalDate.parse(raw, koreanFormatter);


        String isoDate = parsedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);  // "2025-05-06"
        String url = "http://10.0.2.2:8080/api/exercise/add/todo/" + userId + "/" + isoDate;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    List<ExerciseItem> items = new ArrayList<>();
                    Set<String> seenNames = new HashSet<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String part = obj.optString("bodyPart", "");
                            String name = obj.optString("exercise_name", "");
                            boolean done = obj.optBoolean("done", false);

                            if (seenNames.add(name)) {
                                items.add(new ExerciseItem(part, name, done));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.updateData(items);
                },
                error -> Toast.makeText(this, "추가된 운동 불러오기 실패", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }
}
