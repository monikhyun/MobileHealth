// ExerciseAdapter.java
package com.example.health.Exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health.DTO.ExerciseDto;
import com.example.health.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
  private final Context context;
  private final List<ExerciseDto> items = new ArrayList<>();
  private final OnItemClickListener listener;

  public interface OnItemClickListener {
    void onItemClick(ExerciseDto item);
  }

  public ExerciseAdapter(Context context, List<ExerciseDto> initialData, OnItemClickListener listener) {
    this.context = context;
    this.items.addAll(initialData);
    this.listener = listener;
  }

  public void updateDataJSONArray(JSONArray array) {
      items.clear();
      for (int i = 0; i < array.length(); i++) {
          JSONObject obj = array.optJSONObject(i);
          if (obj == null) continue;
          String part = obj.optString("bodyPart", "");
          String name = obj.optString("exercise_name", "");
          items.add(new ExerciseDto(part, name));
      }
      notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_add_exercise, parent, false);
    return new ViewHolder(view, listener);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ExerciseDto dto = items.get(position);
    holder.textPart.setText(dto.bodyPart);
    holder.textName.setText(dto.exerciseName);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView textPart;
    TextView textName;
    ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
      super(itemView);
      textPart = itemView.findViewById(R.id.text_part);
      textName = itemView.findViewById(R.id.text_exercise_name);
      itemView.setOnClickListener(v ->
        listener.onItemClick(
          new ExerciseDto(
            textPart.getText().toString(),
            textName.getText().toString()
          )
        )
      );
    }
  }
}