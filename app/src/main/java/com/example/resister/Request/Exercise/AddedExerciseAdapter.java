// AddedExerciseAdapter.java
package com.example.resister.Request.Exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import com.example.resister.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AddedExerciseAdapter
        extends RecyclerView.Adapter<AddedExerciseAdapter.ViewHolder> {



    private final List<ExerciseItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(ExerciseItem item);
    }

    public AddedExerciseAdapter(List<ExerciseItem> initialData, OnItemClickListener listener) {
        items.addAll(initialData);
        this.listener = listener;
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
        holder.checkboxDone.setChecked(item.done);

        // 뷰 전체에 클릭 리스너 연결
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPart;
        TextView textExerciseName;
        CheckBox checkboxDone;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPart = itemView.findViewById(R.id.text_part);
            textExerciseName = itemView.findViewById(R.id.text_exercise_name);
            checkboxDone = itemView.findViewById(R.id.checkbox_done);
        }
    }
}