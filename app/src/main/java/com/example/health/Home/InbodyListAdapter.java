// 패키지 및 관련 클래스 import
package com.example.health.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health.DTO.InbodyResponseDto;
import com.example.health.R;

import java.util.List;

/**
 * InbodyListAdapter
 * ------------------
 * RecyclerView에 인바디 기록 리스트를 출력하기 위한 어댑터 클래스.
 * 각 아이템은 날짜, 체중, SMM, 체지방률 정보를 포함한다.
 */
public class InbodyListAdapter extends RecyclerView.Adapter<InbodyListAdapter.InbodyViewHolder> {

    /**
     * 아이템 클릭 시 콜백 처리를 위한 인터페이스 정의
     */
    public interface OnItemClickListener {
        void onItemClick(InbodyResponseDto item);  // 클릭된 인바디 항목 객체를 전달
    }

    // 실제 표시할 데이터 리스트
    private final List<InbodyResponseDto> dataList;

    // 클릭 이벤트를 전달받을 리스너
    private final OnItemClickListener listener;

    /**
     * 생성자
     * @param dataList 인바디 데이터 리스트
     * @param listener 아이템 클릭 리스너
     */
    public InbodyListAdapter(List<InbodyResponseDto> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    /**
     * 새로운 뷰홀더(ViewHolder) 객체 생성 시 호출됨
     * @param parent  뷰가 붙을 부모 뷰그룹
     * @param viewType 뷰 타입 (여기선 단일 타입만 사용)
     * @return InbodyViewHolder 객체
     */
    @NonNull
    @Override
    public InbodyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 인바디 아이템 레이아웃을 inflate (item_inbody_list.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inbody_list, parent, false);
        return new InbodyViewHolder(view);
    }

    /**
     * 뷰홀더에 데이터를 바인딩하는 메소드 (화면에 아이템 표시될 때마다 호출됨)
     * @param holder   현재 바인딩할 뷰홀더
     * @param position 데이터 리스트 내 위치
     */
    @Override
    public void onBindViewHolder(@NonNull InbodyViewHolder holder, int position) {
        // 해당 위치의 인바디 데이터 가져오기
        InbodyResponseDto item = dataList.get(position);

        // 날짜 문자열 설정
        holder.date.setText(item.getDate().toString());

        // 체중, SMM, 체지방률: 소수점 0 제거 후 문자열로 변환하여 표시
        holder.weight.setText(holder.itemView.getContext()
                .getString(R.string.weight_format, item.getWeight().stripTrailingZeros().toPlainString()));
        holder.smm.setText(holder.itemView.getContext()
                .getString(R.string.smm_format, item.getSMM().stripTrailingZeros().toPlainString()));
        holder.fat.setText(holder.itemView.getContext()
                .getString(R.string.fat_format, item.getFat_percent().stripTrailingZeros().toPlainString()));

        // 해당 아이템 클릭 시 리스너 호출
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    /**
     * 데이터 리스트의 크기를 반환 (RecyclerView가 아이템 개수를 알기 위해 호출)
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 뷰홀더 클래스 (아이템 레이아웃의 뷰들을 캐싱)
     */
    static class InbodyViewHolder extends RecyclerView.ViewHolder {
        // 날짜, 체중, SMM, 체지방률 텍스트뷰 참조
        TextView date, weight, smm, fat;

        InbodyViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_inbody_list.xml에 정의된 각 뷰 바인딩
            date = itemView.findViewById(R.id.date);
            weight = itemView.findViewById(R.id.weight);
            smm = itemView.findViewById(R.id.smm);
            fat = itemView.findViewById(R.id.fat);
        }
    }
}