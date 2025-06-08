package com.example.health.Home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.health.DTO.InbodyResponseDto;
import com.example.health.R;

public class InbodyListAdapter extends RecyclerView.Adapter<InbodyListAdapter.InbodyViewHolder> {

    private final List<InbodyResponseDto> dataList;

    public InbodyListAdapter(List<InbodyResponseDto> dataList) {
        this.dataList = dataList;
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
        String weight = item.getWeight().stripTrailingZeros().toPlainString();
        String smm = item.getSMM().stripTrailingZeros().toPlainString();
        String fat = item.getFat_percent().stripTrailingZeros().toPlainString();

        holder.date.setText(item.getDate().toString());

        holder.weight.setText(holder.itemView.getContext().getString(R.string.weight_format, weight));
        holder.smm.setText(holder.itemView.getContext().getString(R.string.smm_format, smm));
        holder.fat.setText(holder.itemView.getContext().getString(R.string.fat_format, fat));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class InbodyViewHolder extends RecyclerView.ViewHolder {
        TextView date, weight, smm, fat;

        public InbodyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            weight = itemView.findViewById(R.id.weight);
            smm = itemView.findViewById(R.id.smm);
            fat = itemView.findViewById(R.id.fat);
        }
    }
}