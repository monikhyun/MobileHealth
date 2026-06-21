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

// 친구 목록을 RecyclerView로 표시하기 위한 어댑터 클래스
// 각 친구 항목은 프로필 이미지, 이름, 등급 아이콘으로 구성되며
// 서버로부터 받은 FriendItem 리스트를 기반으로 표시된다.
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    // 친구 항목 리스트 데이터
    private List<FriendItem> friendList;

    // 어댑터 생성자 - 외부에서 리스트 주입
    public FriendListAdapter(List<FriendItem> friendList) {
        this.friendList = friendList;
    }

    // ViewHolder를 생성하고 뷰를 연결함
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃(item_friend)을 inflate 하여 뷰 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    // 각 포지션의 데이터를 ViewHolder에 바인딩
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        // 해당 위치의 친구 객체 가져오기
        FriendItem friend = friendList.get(position);

        // 친구 이름 텍스트 설정
        holder.name.setText(friend.getUsername());

        // 디버깅용 로그 출력: 등급 확인
        Log.d("FriendListAdapter", "Grade: " + friend.getGrade());

        // 등급(Grade)에 따른 아이콘 설정
        switch (friend.getGrade().toUpperCase(Locale.ROOT)) {
            case "SEED":
                // 씨앗 등급
                holder.rankIcon.setImageResource(R.drawable.ic_rank_seed);
                break;
            case "SPROUT":
                // 새싹 등급
                holder.rankIcon.setImageResource(R.drawable.ic_rank_leaf);
                break;
            case "STEMS":
                // 꽃 등급
                holder.rankIcon.setImageResource(R.drawable.ic_rank_flower);
                break;
            case "TREE":
                // 나무 등급
                holder.rankIcon.setImageResource(R.drawable.ic_rank_tree);
                break;
            default:
                // 알 수 없는 등급은 기본값으로 설정
                holder.rankIcon.setImageResource(R.drawable.ic_rank_seed);
        }

        // 프로필 이미지 설정 (임시 아이콘으로 고정)
        holder.profileImage.setImageResource(R.drawable.ic_cat_profile);
    }

    // 전체 친구 수 반환
    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    // ViewHolder: 각 친구 항목의 뷰 구성요소를 저장하고 재사용
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;  // 친구의 프로필 이미지
        TextView name;           // 친구 이름
        ImageView rankIcon;      // 친구 등급 아이콘

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile);
            name = itemView.findViewById(R.id.text_friend_name);
            rankIcon = itemView.findViewById(R.id.image_rank);
        }
    }

    // 새로운 친구 리스트로 데이터를 갱신하고 RecyclerView 갱신 요청
    public void updateData(List<FriendItem> newList) {
        this.friendList = newList;
        notifyDataSetChanged(); // 데이터 변경 시 화면 갱신
    }
}