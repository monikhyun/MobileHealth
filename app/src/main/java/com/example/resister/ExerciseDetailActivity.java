package com.example.resister;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseDetailActivity extends AppCompatActivity {
    private LinearLayout setContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        setContainer = findViewById(R.id.set_container);

        // 기본 세트 1개 추가
        addSetView();

        // 추가 버튼 하단에 추가
        View addButtonView = LayoutInflater.from(this).inflate(R.layout.item_add_button, setContainer, false);
        setContainer.addView(addButtonView);

        addButtonView.findViewById(R.id.btn_add_set).setOnClickListener(v -> {
            setContainer.removeView(addButtonView);
            addSetView();
            setContainer.addView(addButtonView);
        });
    }

    private void addSetView() {
        View setView = LayoutInflater.from(this).inflate(R.layout.item_exercise_set, setContainer, false);

        TextView setNumberText = setView.findViewById(R.id.text_set_number);
        EditText inputKg = setView.findViewById(R.id.input_kg);
        EditText inputReps = setView.findViewById(R.id.input_reps);
        CheckBox completeCheck = setView.findViewById(R.id.check_complete);

        View removeButton = setView.findViewById(R.id.btn_remove_set);
        if (removeButton != null) {
            removeButton.setOnClickListener(v -> {
                setContainer.removeView(setView);
                renumberSets();
            });
        }

        if (completeCheck != null) {
            completeCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    String kg = inputKg.getText().toString();
                    String reps = inputReps.getText().toString();
                    String setNumber = setNumberText.getText().toString();

                    android.util.Log.d("ExerciseDetail", "Saving set: " + setNumber + ", KG: " + kg + ", Reps: " + reps);
                }
            });
        }

        int index = setContainer.getChildCount();
        if (index > 0 && setContainer.getChildAt(index - 1).findViewById(R.id.btn_add_set) != null) {
            index--; // insert before add button
        }
        setContainer.addView(setView, index);

        renumberSets();
    }

    private void renumberSets() {
        int number = 1;
        for (int i = 0; i < setContainer.getChildCount(); i++) {
            View child = setContainer.getChildAt(i);
            if (child.findViewById(R.id.btn_add_set) != null) continue;
            TextView numberView = child.findViewById(R.id.text_set_number);
            if (numberView != null) {
                numberView.setText(String.valueOf(number++));
            }
        }
    }
}
