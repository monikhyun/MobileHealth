<<<<<<<< HEAD:app/src/main/java/com/example/health/Exercise/AddedExerciseAdapter.java
// src/main/java/com/example/health/Exercise/AddedExerciseAdapter.java
package com.example.health.Exercise;
========
// AddedExerciseAdapter.java
package com.example.resister.Request.Exercise;
>>>>>>>> feat/diet:app/src/main/java/com/example/resister/Request/Exercise/AddedExerciseAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.health.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AddedExerciseAdapter
        extends RecyclerView.Adapter<AddedExerciseAdapter.ViewHolder> {

    // 화면에 표시할 데이터
    private final List<ExerciseItem> items = new ArrayList<>();

    // 클릭 콜백: 운동 아이템 전체를 눌렀을 때
    private final OnItemClickListener itemClickListener;

    // 삭제 콜백: 각 행의 삭제 버튼을 눌렀을 때
    private final OnDeleteClickListener deleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(ExerciseItem item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ExerciseItem item);
    }

    public AddedExerciseAdapter(
            List<ExerciseItem> initialData,
            OnItemClickListener itemClickListener,
            OnDeleteClickListener deleteClickListener
    ) {
        items.addAll(initialData);
        this.itemClickListener = itemClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    public void updateData(List<ExerciseItem> newData) {
        items.clear();
        items.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddedExerciseAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseItem item = items.get(position);
        holder.textPart.setText(item.bodyPart);
        holder.textExerciseName.setText(item.exerciseName);

        // ① 아이템 전체 클릭 시 (운동 상세 보기)
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(item));

        // ② 삭제 버튼 클릭 시
        holder.btn_remove_set.setOnClickListener(v -> deleteClickListener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPart;
        TextView textExerciseName;
        ImageButton btn_remove_set;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPart = itemView.findViewById(R.id.text_part);
            textExerciseName = itemView.findViewById(R.id.text_exercise_name);
            btn_remove_set = itemView.findViewById(R.id.btn_remove_set);
        }
    }
}