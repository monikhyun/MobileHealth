// FriendListActivity.java
package com.example.health.Friend;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.health.Auth.MainActivity;
import com.example.health.Diet.DietActivity;
import com.example.health.Exercise.ExerciseAddListActivity;
import com.example.health.Exercise.ExerciseListActivity;
import com.example.health.R;
import com.example.health.Request.Friend.FriendAcceptRequest;
import com.example.health.Request.Friend.FriendAddCancelRequest;
import com.example.health.Request.Friend.FriendListRequest;
import com.example.health.Request.Friend.FriendRequestsRequest;
import com.example.health.Request.Friend.FriendSearchRequest;
import com.example.health.Stats.StatusActivity;
import com.example.health.databinding.ActivityFriendListBinding;
import com.example.health.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 친구 목록을 관리하고 보여주는 메인 화면 액티비티 클래스
// 친구 목록 조회, 친구 요청 수락, 친구 검색 및 추가 요청 등의 기능을 제공
// RecyclerView를 통해 친구 목록과 요청 목록을 동적으로 표시하고, 사용자 입력 기반 UI 이벤트를 처리함
public class FriendListActivity extends AppCompatActivity {
    // ViewBinding 객체 (레이아웃의 뷰들과 연결)
    private ActivityFriendListBinding binding;

    // 친구 목록을 표시하기 위한 어댑터
    private FriendListAdapter adapter;

    // 친구 목록 데이터 리스트
    private List<FriendItem> friendList;

    // 친구 요청 목록에 사용될 어댑터
    private FriendRequestAdapter friendRequestAdapter;

    // 이미 친구 요청을 보낸 사용자 ID를 저장하는 Set
    private final Set<String> requestedUserIds = new HashSet<>();

    // 친구 요청 목록 데이터 리스트
    private List<FriendRequestItem> requestList = new ArrayList<>();

    // 로그인된 사용자의 JWT 토큰
    private String jwtToken;

    // 로그인된 사용자 ID
    private String userId;

    /**
     * 액티비티가 생성될 때 호출됨.
     * 뷰 초기화, 버튼 리스너 등록, 사용자 정보 불러오기, 친구 목록 조회 수행
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewBinding 초기화 및 레이아웃 설정
        binding = ActivityFriendListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 친구 목록 및 어댑터 초기화
        friendList = new ArrayList<>();
        adapter = new FriendListAdapter(friendList);
        binding.recyclerFriend.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFriend.setAdapter(adapter);

        // 친구 요청 알람 버튼 리스너 등록
        binding.btnAlarm.setOnClickListener(v -> showFriendRequestDialog());

        // 상단 날짜 표시
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        binding.textDate.setText(today.format(formatter) + " ▼");

        // 하단 네비게이션 아이콘 클릭 리스너 등록
        binding.iconHome.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, MainActivity.class);
            startActivity(intent);
        });
        binding.iconWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, ExerciseListActivity.class);
            startActivity(intent);
        });
        binding.iconMeal.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, DietActivity.class);
            startActivity(intent);
        });
        binding.iconStats.setOnClickListener(v -> {
            Intent intent = new Intent(FriendListActivity.this, StatusActivity.class);
            startActivity(intent);
        });

        // SharedPreferences에서 JWT 토큰과 사용자 ID 불러오기
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        jwtToken = prefs.getString("JWT_TOKEN", null);
        userId = prefs.getString("USER_ID", null);

        // 이미 요청한 친구 목록 불러오기
        loadRequestedUserIds();

        // 사용자 정보가 있으면 친구 목록 요청, 없으면 에러 토스트 표시
        if (jwtToken != null && userId != null) {
            fetchFriendList(userId, "Bearer " + jwtToken);
        } else {
            View toastView = LayoutInflater.from(this)
                    .inflate(R.layout.toast_friend_request, null);
            TextView tv = toastView.findViewById(R.id.text_toast_message);
            tv.setText("사용자 정보를 불러올 수 없습니다.");
            Toast t = new Toast(this);
            t.setView(toastView);
            t.setDuration(Toast.LENGTH_SHORT);
            t.show();
        }

        // 친구 추가 버튼 리스너 등록
        binding.btnAddFriend.setOnClickListener(v -> showFriendSearchDialog());
    }

    /**
     * 서버에서 친구 목록을 요청하여 RecyclerView에 업데이트함
     * @param userId 현재 사용자 ID
     * @param jwtToken 인증 토큰 (Bearer 포함)
     */
    private void fetchFriendList(String userId, String jwtToken) {
        // 서버에 친구 목록 요청 및 응답 파싱
        FriendListRequest request = new FriendListRequest(
                jwtToken,
                userId,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<FriendItem> items = new ArrayList<>();
                        // 응답에서 친구 데이터 파싱
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            items.add(new FriendItem(
                                    obj.optString("userid", ""),
                                    obj.optString("username", ""),
                                    obj.optString("grade", ""),
                                    obj.optString("imageUrl", null)
                            ));
                        }
                        // 어댑터에 데이터 업데이트
                        adapter.updateData(items);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        View toastView = LayoutInflater.from(this)
                                .inflate(R.layout.toast_friend_request, null);
                        TextView tv = toastView.findViewById(R.id.text_toast_message);
                        tv.setText("데이터 파싱 오류");
                        Toast t = new Toast(this);
                        t.setView(toastView);
                        t.setDuration(Toast.LENGTH_SHORT);
                        t.show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    View toastView = LayoutInflater.from(this)
                            .inflate(R.layout.toast_friend_request, null);
                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                    tv.setText("친구 목록 불러오기 실패");
                    Toast t = new Toast(this);
                    t.setView(toastView);
                    t.setDuration(Toast.LENGTH_SHORT);
                    t.show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * 친구 요청 목록을 표시하는 커스텀 다이얼로그를 생성하고 서버에서 요청 목록을 불러옴
     */
    private void showFriendRequestDialog() {
        // 다이얼로그 UI 생성 및 배경 투명 처리
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_friend_list);

        // 요청 목록 RecyclerView 및 어댑터 설정
        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestAdapter = new FriendRequestAdapter(requestList, (username, position) -> acceptFriendRequest(username, position));
        recyclerView.setAdapter(friendRequestAdapter);

        // 서버에서 친구 요청 목록 불러오기
        if (jwtToken != null && userId != null) {
            FriendRequestsRequest request = new FriendRequestsRequest(
                    jwtToken,
                    userId,
                    response -> {
                        try {
                            requestList.clear();
                            // 응답에서 친구 요청 데이터 파싱
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                requestList.add(new FriendRequestItem(
                                        obj.getString("userid"),
                                        obj.getString("username"),
                                        obj.optString("grade", ""),
                                        obj.optString("imageUrl", null)
                                ));
                            }
                            // 어댑터에 데이터 업데이트
                            friendRequestAdapter.updateData(new ArrayList<>(requestList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            TextView tv = toastView.findViewById(R.id.text_toast_message);
                            tv.setText("요청 목록 파싱 오류");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        View toastView = LayoutInflater.from(this)
                                .inflate(R.layout.toast_friend_request, null);
                        TextView tv = toastView.findViewById(R.id.text_toast_message);
                        tv.setText("친구 요청 목록 불러오기 실패");
                        Toast t = new Toast(this);
                        t.setView(toastView);
                        t.setDuration(Toast.LENGTH_SHORT);
                        t.show();
                    }
            );
            Volley.newRequestQueue(this).add(request);
        }
        dialog.show();
    }

    /**
     * 친구 추가 버튼을 눌렀을 때 호출됨.
     * 사용자 검색 다이얼로그를 띄우고 서버에서 검색 결과를 가져와 리스트에 표시
     */
    private void showFriendSearchDialog() {
        // 다이얼로그 UI 생성 및 배경 투명 처리
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_friend);

        // 검색 입력창, 버튼, RecyclerView 바인딩
        EditText editSearch = dialog.findViewById(R.id.edit_search);
        ImageView btnSearch = dialog.findViewById(R.id.btn_search);
        RecyclerView recyclerSearch = dialog.findViewById(R.id.recycler_search);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));

        // 검색 결과 리스트 및 어댑터 설정
        List<FriendItem> searchResults = new ArrayList<>();
        FriendSearchAdapter searchAdapter = new FriendSearchAdapter(
                this, searchResults, jwtToken, userId, requestedUserIds,
                new FriendSearchAdapter.OnFriendRequestListener() {
                    @Override
                    public void onFriendRequested(String userid) {
                        saveRequestedUserId(userid);
                    }

                    @Override
                    public void onFriendCanceled(String userid) {
                        removeRequestedUserId(userid);
                    }
                });
        recyclerSearch.setAdapter(searchAdapter);

        // 검색 버튼 클릭 시 서버에서 친구 검색 요청
        btnSearch.setOnClickListener(v -> {
            String keyword = editSearch.getText().toString().trim();
            if (keyword.isEmpty()) return;

            // 서버에 친구 검색 요청 및 결과 파싱
            FriendSearchRequest request = new FriendSearchRequest(jwtToken, userId, keyword,
                    response -> {
                        try {
                            JSONArray array = new JSONArray(response);
                            List<FriendItem> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                FriendItem item = new FriendItem(
                                        obj.optString("userid", ""),
                                        obj.optString("username", ""),
                                        obj.optString("grade", ""),
                                        obj.optString("imageUrl", null)
                                );
                                // 이미 요청한 친구는 요청 상태로 표시
                                item.setRequested(requestedUserIds.contains(item.getUserid()));
                                list.add(item);
                            }
                            // 어댑터에 검색 결과 업데이트
                            searchAdapter.updateData(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            View toastView = LayoutInflater.from(this)
                                    .inflate(R.layout.toast_friend_request, null);
                            TextView tv = toastView.findViewById(R.id.text_toast_message);
                            tv.setText("파싱 오류");
                            Toast t = new Toast(this);
                            t.setView(toastView);
                            t.setDuration(Toast.LENGTH_SHORT);
                            t.show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        View toastView = LayoutInflater.from(this)
                                .inflate(R.layout.toast_friend_request, null);
                        TextView tv = toastView.findViewById(R.id.text_toast_message);
                        tv.setText("검색 실패");
                        Toast t = new Toast(this);
                        t.setView(toastView);
                        t.setDuration(Toast.LENGTH_SHORT);
                        t.show();
                    }
            );
            Volley.newRequestQueue(this).add(request);
        });

        dialog.show();
    }

    /**
     * 특정 친구 요청을 수락하고, 친구 목록을 새로 불러옴
     * @param username 요청을 수락할 사용자명
     * @param position 리스트에서의 위치 (삭제용)
     */
    private void acceptFriendRequest(String username, int position) {
        // 서버에 친구 요청 수락 요청
        FriendAcceptRequest request = new FriendAcceptRequest(
                jwtToken,
                userId,
                username,
                response -> {
                    // 수락 성공 토스트 표시
                    View toastView = LayoutInflater.from(this).inflate(R.layout.toast_friend_request, null);
                    TextView toastText = toastView.findViewById(R.id.text_toast_message);
                    toastText.setText("친구 요청을 수락하였습니다.");

                    Toast toast = new Toast(this);
                    toast.setView(toastView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();

                    // 요청 목록에서 해당 항목 제거 및 UI 갱신
                    requestList.remove(position);
                    friendRequestAdapter.updateData(new ArrayList<>(requestList));

                    // 친구 목록 새로 불러오기
                    fetchFriendList(userId, "Bearer " + jwtToken);
                },
                error -> {
                    error.printStackTrace();
                    View toastView = LayoutInflater.from(this)
                            .inflate(R.layout.toast_friend_request, null);
                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                    tv.setText("요청 수락 실패");
                    Toast t = new Toast(this);
                    t.setView(toastView);
                    t.setDuration(Toast.LENGTH_SHORT);
                    t.show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * 친구 요청을 보낸 사용자 ID를 SharedPreferences와 메모리에 저장
     */
    private void saveRequestedUserId(String id) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
        Set<String> updated = new HashSet<>(saved);
        updated.add(id);
        prefs.edit().putStringSet("REQUESTED_USER_IDS", updated).apply();
        requestedUserIds.add(id);
    }

    /**
     * 친구 요청을 취소한 사용자 ID를 SharedPreferences와 메모리에서 제거
     */
    private void removeRequestedUserId(String id) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
        Set<String> updated = new HashSet<>(saved);
        updated.remove(id);
        prefs.edit().putStringSet("REQUESTED_USER_IDS", updated).apply();
        requestedUserIds.remove(id);
    }

    /**
     * SharedPreferences에 저장된 요청된 사용자 ID 목록을 메모리로 불러옴
     */
    private void loadRequestedUserIds() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
        requestedUserIds.clear();
        requestedUserIds.addAll(saved);
    }
}