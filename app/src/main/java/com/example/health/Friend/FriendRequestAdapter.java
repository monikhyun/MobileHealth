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

// 친구 요청 목록을 RecyclerView로 표시하기 위한 어댑터 클래스
// 사용자 프로필 이미지, 닉네임, 등급 아이콘, 요청 수락 버튼으로 구성됨
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    // 버튼 클릭 시 서버에 친구 요청 수락을 전달할 인터페이스
    public interface OnConfirmClickListener {
        void onConfirmClick(String username, int position);
    }

    // 친구 요청 항목 리스트
    private List<FriendRequestItem> requestList;

    // 요청 수락 클릭 이벤트 리스너
    private OnConfirmClickListener listener;

    // 어댑터 생성자: 요청 목록과 클릭 리스너 초기화
    public FriendRequestAdapter(List<FriendRequestItem> requestList, OnConfirmClickListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    // ViewHolder 클래스: 하나의 친구 요청 항목 레이아웃 요소를 저장
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;     // 프로필 이미지 뷰
        TextView name;              // 사용자 이름 텍스트 뷰
        ImageView rankIcon;         // 등급 아이콘 이미지 뷰
        MaterialButton confirmButton; // 친구 수락 버튼

        public ViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.image_profile);
            name = view.findViewById(R.id.text_friend_name);
            rankIcon = view.findViewById(R.id.image_rank);
            confirmButton = view.findViewById(R.id.confirm_button);
        }
    }

    // ViewHolder를 생성하는 메서드: 레이아웃 파일을 inflate하여 ViewHolder 반환
    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    // 각 항목 데이터를 뷰에 바인딩하는 메서드
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendRequestItem item = requestList.get(position);

        // 닉네임 텍스트 설정
        holder.name.setText(item.getUsername());

        // 등급에 따라 아이콘 설정
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

        // 프로필 이미지 처리
        // 등급 텍스트와 충돌 방지 및 null/빈값 필터링 포함
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()
                && !imageUrl.equals("TREE") && !imageUrl.equals("SEED")
                && !imageUrl.equals("SPROUT") && !imageUrl.equals("STEMS")) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_cat_profile)
                    .into(holder.profileImage);
        } else {
            // 이미지 URL이 없거나 등급 값일 경우 기본 이미지 사용
            holder.profileImage.setImageResource(R.drawable.ic_cat_profile);
        }

        // 친구 요청 수락 버튼 클릭 시 콜백 실행
        holder.confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmClick(item.getUserId(), holder.getAdapterPosition());
            }
        });
    }

    // 전체 요청 목록의 크기 반환
    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // 요청 목록 데이터를 새로 갱신하고 RecyclerView를 갱신하는 메서드
    public void updateData(List<FriendRequestItem> newList) {
        this.requestList = newList;
        notifyDataSetChanged(); // 화면 다시 그리기
    }
}