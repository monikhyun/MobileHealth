// com/example/health/Home/InbodyListAdapter.java
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

public class InbodyListAdapter extends RecyclerView.Adapter<InbodyListAdapter.InbodyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(InbodyResponseDto item);
    }

    private final List<InbodyResponseDto> dataList;
    private final OnItemClickListener listener;

    public InbodyListAdapter(List<InbodyResponseDto> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InbodyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inbody_list, parent, false);
        return new InbodyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InbodyViewHolder holder, int position) {
        InbodyResponseDto item = dataList.get(position);

        holder.date .setText(item.getDate().toString());
        holder.weight.setText(holder.itemView.getContext()
                .getString(R.string.weight_format, item.getWeight().stripTrailingZeros().toPlainString()));
        holder.smm   .setText(holder.itemView.getContext()
                .getString(R.string.smm_format,    item.getSMM().stripTrailingZeros().toPlainString()));
        holder.fat   .setText(holder.itemView.getContext()
                .getString(R.string.fat_format,    item.getFat_percent().stripTrailingZeros().toPlainString()));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class InbodyViewHolder extends RecyclerView.ViewHolder {
        TextView date, weight, smm, fat;
        InbodyViewHolder(@NonNull View itemView) {
            super(itemView);
            date   = itemView.findViewById(R.id.date);
            weight = itemView.findViewById(R.id.weight);
            smm    = itemView.findViewById(R.id.smm);
            fat    = itemView.findViewById(R.id.fat);
        }
    }
}