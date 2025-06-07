package com.example.health.Friend;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.health.R;

import java.util.List;
import java.util.Locale;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private List<FriendItem> friendList;

    public FriendListAdapter(List<FriendItem> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendItem friend = friendList.get(position);
        holder.name.setText(friend.getUsername());
        Log.d("FriendListAdapter", "Grade: " + friend.getGrade());
        // 계급 처리
        switch (friend.getGrade().toUpperCase(Locale.ROOT)) {
            case "SEED":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_seed);
                break;
            case "SPROUT":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_leaf);
                break;
            case "STEMS":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_flower);
                break;
            case "TREE":
                holder.rankIcon.setImageResource(R.drawable.ic_rank_tree);
                break;
            default:
                holder.rankIcon.setImageResource(R.drawable.ic_rank_seed);
        }

        holder.profileImage.setImageResource(R.drawable.ic_cat_profile);
    }

    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;
        ImageView rankIcon;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile);
            name = itemView.findViewById(R.id.text_friend_name);
            rankIcon = itemView.findViewById(R.id.image_rank);
        }
    }

    public void updateData(List<FriendItem> newList) {
        this.friendList = newList;
        notifyDataSetChanged();
    }
}