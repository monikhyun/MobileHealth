package com.example.health.Friend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.health.R;
import com.example.health.Request.Friend.FriendAddRequest;
import com.example.health.Request.Friend.FriendAddCancelRequest;

import org.json.JSONException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder> {

    private final List<FriendItem> friendList;
    private final Context context;
    private final String jwtToken;
    private final String userId;
    private final Set<String> requestedUserIds;

    private final OnFriendRequestListener requestListener;

    public FriendSearchAdapter(Context context, List<FriendItem> friendList, String jwtToken, String userId,
                               Set<String> requestedUserIds, OnFriendRequestListener requestListener) {
        this.context = context;
        this.friendList = friendList;
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.requestedUserIds = requestedUserIds;
        this.requestListener = requestListener;
    }

    @NonNull
    @Override
    public FriendSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSearchAdapter.ViewHolder holder, int position) {
        FriendItem item = friendList.get(position);
        holder.name.setText(item.getUsername());

        // 프로필 이미지
        holder.profile.setImageResource(R.drawable.ic_cat_profile);

        // 계급
        switch (item.getGrade()) {
            case "SEED": holder.rank.setImageResource(R.drawable.ic_rank_seed); break;
            case "SPROUT": holder.rank.setImageResource(R.drawable.ic_rank_leaf); break;
            case "STEMS": holder.rank.setImageResource(R.drawable.ic_rank_flower); break;
            case "TREE": holder.rank.setImageResource(R.drawable.ic_rank_tree); break;
            default: holder.rank.setImageResource(R.drawable.ic_rank_seed); break;
        }

        // 버튼 상태 설정
        if (item.isRequested()) {
            holder.btnAdd.setImageResource(R.drawable.ic_remove_white);
            holder.btnAdd.setBackgroundResource(R.drawable.round_button_red);
        } else {
            holder.btnAdd.setImageResource(R.drawable.ic_add_white);
            holder.btnAdd.setBackgroundResource(R.drawable.round_button);
        }

        // 버튼 클릭 이벤트
        holder.btnAdd.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (item.isRequested()) {
                // 친구 요청 취소
                FriendAddCancelRequest cancelRequest = new FriendAddCancelRequest(jwtToken, userId, item.getUserid(),
                        response -> {
                            item.setRequested(false);
                            notifyItemChanged(pos);

                            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            Set<String> requestedIds = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
                            Set<String> updatedIds = new HashSet<>(requestedIds);
                            updatedIds.remove(item.getUserid());
                            prefs.edit().putStringSet("REQUESTED_USER_IDS", updatedIds).apply();

                            requestedUserIds.remove(item.getUserid());
                            requestListener.onFriendCanceled(item.getUserid());

                            showCustomToast("친구 요청이 취소되었습니다.");
                        },
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(context, "요청 취소 실패", Toast.LENGTH_SHORT).show();
                        }
                );
                Volley.newRequestQueue(context).add(cancelRequest);
            } else {
                // 친구 요청
                FriendAddRequest request = new FriendAddRequest(jwtToken, userId, item.getUserid(),
                        response -> {
                            item.setRequested(true);
                            notifyItemChanged(pos);

                            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            Set<String> requestedIds = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
                            Set<String> updatedIds = new HashSet<>(requestedIds);
                            updatedIds.add(item.getUserid());
                            prefs.edit().putStringSet("REQUESTED_USER_IDS", updatedIds).apply();

                            requestedUserIds.add(item.getUserid());
                            requestListener.onFriendRequested(item.getUserid());

                            showCustomToast("친구 요청을 보냈습니다.");
                        },
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(context, "친구 요청 실패", Toast.LENGTH_SHORT).show();
                        }
                );
                Volley.newRequestQueue(context).add(request);
            }
        });
    }

    private void showCustomToast(String message) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_friend_request, null);
        TextView toastText = toastView.findViewById(R.id.text_toast_message);
        toastText.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void updateData(List<FriendItem> newList) {
        friendList.clear();
        for (FriendItem item : newList) {
            item.setRequested(requestedUserIds.contains(item.getUserid()));
            friendList.add(item);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile, rank;
        TextView name;
        ImageButton btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.image_profile);
            rank = itemView.findViewById(R.id.image_rank);
            name = itemView.findViewById(R.id.text_friend_name);
            btnAdd = itemView.findViewById(R.id.image_button);
        }
    }

    public interface OnFriendRequestListener {
        void onFriendRequested(String userid);
        void onFriendCanceled(String userid);
    }
}