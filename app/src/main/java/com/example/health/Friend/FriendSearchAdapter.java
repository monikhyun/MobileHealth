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

// 친구 검색 결과를 RecyclerView로 보여주는 어댑터 클래스
// 사용자에게 친구 요청 또는 요청 취소 기능을 제공하며
// 요청 상태는 SharedPreferences와 in-memory Set을 함께 사용해 관리
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
        // RecyclerView 항목을 위한 레이아웃을 inflate하여 ViewHolder 생성
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_add, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSearchAdapter.ViewHolder holder, int position) {
        FriendItem item = friendList.get(position);
        holder.name.setText(item.getUsername());

        // 고정 프로필 이미지 설정 (향후 서버 연동 시 Glide 적용 가능)
        holder.profile.setImageResource(R.drawable.ic_cat_profile);

        // 등급에 따라 아이콘 설정
        switch (item.getGrade()) {
            case "SEED": holder.rank.setImageResource(R.drawable.ic_rank_seed); break;
            case "SPROUT": holder.rank.setImageResource(R.drawable.ic_rank_leaf); break;
            case "STEMS": holder.rank.setImageResource(R.drawable.ic_rank_flower); break;
            case "TREE": holder.rank.setImageResource(R.drawable.ic_rank_tree); break;
            default: holder.rank.setImageResource(R.drawable.ic_rank_seed); break;
        }

        // 버튼 UI 상태: 요청 여부에 따라 모양 다르게 설정
        if (item.isRequested()) {
            holder.btnAdd.setImageResource(R.drawable.ic_remove_white);
            holder.btnAdd.setBackgroundResource(R.drawable.round_button_red);
        } else {
            holder.btnAdd.setImageResource(R.drawable.ic_add_white);
            holder.btnAdd.setBackgroundResource(R.drawable.round_button);
        }

        // 친구 추가 또는 취소 버튼 클릭 이벤트 처리
        holder.btnAdd.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (item.isRequested()) {
                // [요청 취소 로직]
                // Volley 요청 전송하여 서버에 친구 요청 취소 API 호출
                // 요청 성공 시 SharedPreferences와 리스트 상태 갱신
                FriendAddCancelRequest cancelRequest = new FriendAddCancelRequest(jwtToken, userId, item.getUserid(),
                        response -> {
                            item.setRequested(false);
                            notifyItemChanged(pos);

                            // SharedPreferences에서 요청 취소된 사용자 ID 제거
                            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            Set<String> requestedIds = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
                            Set<String> updatedIds = new HashSet<>(requestedIds);
                            updatedIds.remove(item.getUserid());
                            prefs.edit().putStringSet("REQUESTED_USER_IDS", updatedIds).apply();

                            // 메모리 내 요청 ID Set에서도 제거
                            requestedUserIds.remove(item.getUserid());
                            requestListener.onFriendCanceled(item.getUserid());

                            // 커스텀 토스트로 취소 메시지 출력
                            showCustomToast("친구 요청이 취소되었습니다.");
                        },
                        error -> {
                            error.printStackTrace();
                            Toast.makeText(context, "요청 취소 실패", Toast.LENGTH_SHORT).show();
                        }
                );
                Volley.newRequestQueue(context).add(cancelRequest);
            } else {
                // [요청 전송 로직]
                // Volley 요청 전송하여 친구 요청 API 호출
                // 요청 성공 시 SharedPreferences에 ID 저장 및 상태 갱신
                FriendAddRequest request = new FriendAddRequest(jwtToken, userId, item.getUserid(),
                        response -> {
                            item.setRequested(true);
                            notifyItemChanged(pos);

                            // SharedPreferences에 요청된 사용자 ID 추가
                            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            Set<String> requestedIds = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
                            Set<String> updatedIds = new HashSet<>(requestedIds);
                            updatedIds.add(item.getUserid());
                            prefs.edit().putStringSet("REQUESTED_USER_IDS", updatedIds).apply();

                            // 메모리 내 요청 ID Set에도 추가
                            requestedUserIds.add(item.getUserid());
                            requestListener.onFriendRequested(item.getUserid());

                            // 커스텀 토스트로 요청 성공 메시지 출력
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

    // 커스텀 토스트 메시지를 출력하는 메서드
    // 지정된 레이아웃을 inflate 하여 사용자에게 메시지를 시각적으로 전달
    private void showCustomToast(String message) {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_friend_request, null);
        TextView toastText = toastView.findViewById(R.id.text_toast_message);
        toastText.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    // RecyclerView 항목 수 반환
    @Override
    public int getItemCount() {
        return friendList.size();
    }

    // 외부에서 데이터 갱신 시 호출
    // 기존 목록을 초기화하고 요청 상태 반영 후 갱신
    public void updateData(List<FriendItem> newList) {
        friendList.clear();
        for (FriendItem item : newList) {
            item.setRequested(requestedUserIds.contains(item.getUserid()));
            friendList.add(item);
        }
        notifyDataSetChanged();
    }

    // 각 리스트 항목을 구성하는 ViewHolder 클래스
    // 친구 이름, 등급 아이콘, 프로필 이미지, 요청 버튼 구성
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