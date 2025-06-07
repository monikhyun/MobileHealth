package com.example.health.Friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.health.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    public interface OnConfirmClickListener {
        void onConfirmClick(String username, int position);
    }

    private List<FriendRequestItem> requestList;
    private OnConfirmClickListener listener;

    public FriendRequestAdapter(List<FriendRequestItem> requestList, OnConfirmClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;
        ImageView rankIcon;
        MaterialButton confirmButton;

        public ViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.image_profile);
            name = view.findViewById(R.id.text_friend_name);
            rankIcon = view.findViewById(R.id.image_rank);
            confirmButton = view.findViewById(R.id.confirm_button);
        }
    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendRequestItem item = requestList.get(position);
        holder.name.setText(item.getUsername());

        switch (item.getGrade()) {
            case "TREE":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_tree);
                break;
            case "STEMS":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_flower);
                break;
            case "SPROUT":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_leaf);
                break;
            default:
                holder.rankIcon.setImageResource(R.drawable.ic_rank_seed);
        }

        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty() && !imageUrl.equals("TREE") && !imageUrl.equals("SEED")
                && !imageUrl.equals("SPROUT") && !imageUrl.equals("STEMS")) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_cat_profile)
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_cat_profile);
        }

        holder.confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmClick(item.getUserId(), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateData(List<FriendRequestItem> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }
}