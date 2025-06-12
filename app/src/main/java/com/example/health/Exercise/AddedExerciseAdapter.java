package com.example.health.Exercise;

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

/**
 * 운동 추가 리스트를 보여주는 RecyclerView 어댑터 클래스
 * 사용자가 선택한 운동 종목을 리스트로 보여주며, 클릭 및 삭제 기능을 제공함
 */
public class AddedExerciseAdapter
        extends RecyclerView.Adapter<AddedExerciseAdapter.ViewHolder> {

    /**
     * 화면에 표시할 운동 아이템 데이터 리스트
     */
    private final List<ExerciseItem> items = new ArrayList<>();

    /**
     * 운동 아이템 클릭 콜백 인터페이스 (예: 상세 보기 등)
     */
    private final OnItemClickListener itemClickListener;

    /**
     * 운동 아이템 삭제 버튼 클릭 콜백 인터페이스
     */
    private final OnDeleteClickListener deleteClickListener;

    /**
     * 아이템 클릭 인터페이스 정의
     */
    public interface OnItemClickListener {
        void onItemClick(ExerciseItem item);
    }

    /**
     * 삭제 버튼 클릭 인터페이스 정의
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(ExerciseItem item);
    }

    /**
     * 어댑터 생성자 - 초기 데이터와 콜백 리스너를 받아 초기화
     * @param initialData 초기 운동 아이템 데이터 리스트
     * @param itemClickListener 운동 아이템 클릭 리스너
     * @param deleteClickListener 삭제 버튼 클릭 리스너
     */
    public AddedExerciseAdapter(
            List<ExerciseItem> initialData,
            OnItemClickListener itemClickListener,
            OnDeleteClickListener deleteClickListener
    ) {
        items.addAll(initialData); // 초기 운동 데이터 추가
        this.itemClickListener = itemClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    /**
     * 데이터 갱신 메서드 - 기존 목록을 새 데이터로 교체하고 화면 갱신
     * @param newData 새 운동 아이템 데이터 리스트
     */
    public void updateData(List<ExerciseItem> newData) {
        items.clear();
        items.addAll(newData);
        notifyDataSetChanged(); // RecyclerView에 데이터 변경 알림
    }

    /**
     * ViewHolder를 생성하여 레이아웃을 연결하는 메서드
     * @param parent 부모 ViewGroup
     * @param viewType 뷰 타입
     * @return ViewHolder 객체
     */
    @NonNull
    @Override
    public AddedExerciseAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        // item_exercise.xml 레이아웃을 inflate하여 뷰 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    /**
     * ViewHolder에 실제 데이터를 바인딩하는 메서드
     * @param holder ViewHolder 객체
     * @param position 현재 위치
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseItem item = items.get(position); // 현재 위치의 운동 아이템
        // 운동 부위와 운동 이름 텍스트 설정
        holder.textPart.setText(item.bodyPart);
        holder.textExerciseName.setText(item.exerciseName);

        // ① 아이템 전체 클릭 시 (예: 운동 상세 정보 보기)
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(item));

        // ② 삭제 버튼 클릭 시 해당 아이템 삭제 처리 콜백 호출
        holder.btn_remove_set.setOnClickListener(v -> deleteClickListener.onDeleteClick(item));
    }

    /**
     * 전체 아이템 개수 반환
     * @return 아이템 개수
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * RecyclerView의 각 아이템을 위한 ViewHolder 정의
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /** 운동 부위를 보여주는 텍스트뷰 */
        TextView textPart;
        /** 운동 이름을 보여주는 텍스트뷰 */
        TextView textExerciseName;
        /** 삭제 버튼 */
        ImageButton btn_remove_set;

        /**
         * ViewHolder 생성자 - XML 레이아웃 내 뷰 연결
         * @param itemView 아이템 뷰
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPart = itemView.findViewById(R.id.text_part);
            textExerciseName = itemView.findViewById(R.id.text_exercise_name);
            btn_remove_set = itemView.findViewById(R.id.btn_remove_set);
        }
    }
}