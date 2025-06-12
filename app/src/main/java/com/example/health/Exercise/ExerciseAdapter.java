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

// 운동 항목을 리스트로 표시하기 위한 RecyclerView 어댑터 클래스
// JSON 배열로부터 운동 정보를 받아와 ExerciseDto 리스트로 구성하며,
// 각 아이템을 클릭했을 때 해당 데이터를 콜백으로 전달함
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    // 어댑터에서 사용될 컨텍스트 (주로 View 생성 시 사용)
    private final Context context;

    // 화면에 표시할 운동 항목 데이터 리스트
    private final List<ExerciseDto> items = new ArrayList<>();

    // 아이템 클릭 이벤트를 처리하기 위한 콜백 인터페이스
    private final OnItemClickListener listener;

    // 아이템 클릭 이벤트 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(ExerciseDto item);
    }

    // 어댑터 생성자
    // 초기 데이터와 클릭 리스너를 받아 내부 변수 초기화
    public ExerciseAdapter(Context context, List<ExerciseDto> initialData, OnItemClickListener listener) {
        this.context = context;
        this.items.addAll(initialData); // 전달받은 초기 운동 항목 데이터를 리스트에 추가
        this.listener = listener;       // 클릭 리스너 설정
    }

    // JSON 배열을 기반으로 데이터 리스트 갱신하는 메서드
    // 서버로부터 받은 JSONArray를 순회하면서 ExerciseDto 객체 리스트로 변환
    public void updateDataJSONArray(JSONArray array) {
        items.clear(); // 기존 데이터 초기화
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.optJSONObject(i); // 각 요소를 JSONObject로 변환
            if (obj == null) continue; // null이면 건너뜀
            String part = obj.optString("bodyPart", "");         // 운동 부위 추출
            String name = obj.optString("exercise_name", "");    // 운동 이름 추출
            items.add(new ExerciseDto(part, name)); // 새 ExerciseDto 객체를 리스트에 추가
        }
        notifyDataSetChanged(); // RecyclerView 갱신 요청
    }

    // ViewHolder를 생성하여 연결하는 메서드
    // item_add_exercise.xml 레이아웃을 inflate하여 뷰를 생성함
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_add_exercise, parent, false); // 각 아이템의 레이아웃을 inflate
        return new ViewHolder(view, listener); // ViewHolder 객체 생성 및 반환
    }

    // ViewHolder에 데이터를 바인딩하는 메서드
    // 각 포지션에 맞는 운동 데이터를 해당 ViewHolder에 연결
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseDto dto = items.get(position); // 현재 위치의 데이터 가져오기
        holder.textPart.setText(dto.bodyPart); // 운동 부위 텍스트 설정
        holder.textName.setText(dto.exerciseName); // 운동 이름 텍스트 설정
    }

    // RecyclerView에 표시할 전체 아이템 수 반환
    @Override
    public int getItemCount() {
        return items.size();
    }

    // 각 리스트 아이템의 뷰와 데이터를 연결해주는 ViewHolder 클래스
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPart;        // 운동 부위를 표시할 TextView
        TextView textName;        // 운동 이름을 표시할 TextView

        // ViewHolder 생성자
        // XML 뷰를 실제 자바 객체로 연결하고 클릭 이벤트 처리 설정
        ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textPart = itemView.findViewById(R.id.text_part); // 운동 부위 텍스트 연결
            textName = itemView.findViewById(R.id.text_exercise_name); // 운동 이름 텍스트 연결

            // 전체 아이템 클릭 시 콜백 실행
            // 현재 TextView의 텍스트 값을 기반으로 새 ExerciseDto 객체를 생성하여 전달
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