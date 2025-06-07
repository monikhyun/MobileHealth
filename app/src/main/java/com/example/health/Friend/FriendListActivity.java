// FriendListActivity.java
package com.example.health.Friend;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class FriendListActivity extends AppCompatActivity {
    private ActivityFriendListBinding binding;
    private FriendListAdapter adapter;
    private List<FriendItem> friendList;
    private FriendRequestAdapter friendRequestAdapter;
    private final Set<String> requestedUserIds = new HashSet<>();
    private List<FriendRequestItem> requestList = new ArrayList<>();
    private String jwtToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        friendList = new ArrayList<>();
        adapter = new FriendListAdapter(friendList);
        binding.recyclerFriend.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFriend.setAdapter(adapter);

        binding.btnAlarm.setOnClickListener(v -> showFriendRequestDialog());

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        binding.textDate.setText(today.format(formatter) + " ▼");

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        jwtToken = prefs.getString("JWT_TOKEN", null);
        userId = prefs.getString("USER_ID", null);

        loadRequestedUserIds();

        if (jwtToken != null && userId != null) {
            fetchFriendList(userId, "Bearer " + jwtToken);
        } else {
            Toast.makeText(this, "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        binding.btnAddFriend.setOnClickListener(v -> showFriendSearchDialog());
    }

    private void fetchFriendList(String userId, String jwtToken) {
        FriendListRequest request = new FriendListRequest(
                jwtToken,
                userId,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<FriendItem> items = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            items.add(new FriendItem(
                                    obj.optString("userid", ""),
                                    obj.optString("username", ""),
                                    obj.optString("grade", ""),
                                    obj.optString("imageUrl", null)
                            ));
                        }
                        adapter.updateData(items);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "친구 목록 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void showFriendRequestDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_friend_list);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestAdapter = new FriendRequestAdapter(requestList, (username, position) -> acceptFriendRequest(username, position));
        recyclerView.setAdapter(friendRequestAdapter);

        if (jwtToken != null && userId != null) {
            FriendRequestsRequest request = new FriendRequestsRequest(
                    jwtToken,
                    userId,
                    response -> {
                        try {
                            requestList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                requestList.add(new FriendRequestItem(
                                        obj.getString("userid"),
                                        obj.getString("username"),
                                        obj.optString("grade", ""),
                                        obj.optString("imageUrl", null)
                                ));
                            }
                            friendRequestAdapter.updateData(new ArrayList<>(requestList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "요청 목록 파싱 오류", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "친구 요청 목록 불러오기 실패", Toast.LENGTH_SHORT).show();
                    }
            );
            Volley.newRequestQueue(this).add(request);
        }
        dialog.show();
    }

    private void showFriendSearchDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_friend);

        EditText editSearch = dialog.findViewById(R.id.edit_search);
        ImageView btnSearch = dialog.findViewById(R.id.btn_search);
        RecyclerView recyclerSearch = dialog.findViewById(R.id.recycler_search);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));

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

        btnSearch.setOnClickListener(v -> {
            String keyword = editSearch.getText().toString().trim();
            if (keyword.isEmpty()) return;

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
                                item.setRequested(requestedUserIds.contains(item.getUserid()));
                                list.add(item);
                            }
                            searchAdapter.updateData(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "파싱 오류", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "검색 실패", Toast.LENGTH_SHORT).show();
                    }
            );
            Volley.newRequestQueue(this).add(request);
        });

        dialog.show();
    }

    private void acceptFriendRequest(String username, int position) {
        FriendAcceptRequest request = new FriendAcceptRequest(
                jwtToken,
                userId,
                username,
                response -> {
                    View toastView = LayoutInflater.from(this).inflate(R.layout.toast_friend_request, null);
                    TextView toastText = toastView.findViewById(R.id.text_toast_message);
                    toastText.setText("친구 요청을 수락하였습니다.");

                    Toast toast = new Toast(this);
                    toast.setView(toastView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();

                    requestList.remove(position);
                    friendRequestAdapter.updateData(new ArrayList<>(requestList));

                    fetchFriendList(userId, "Bearer " + jwtToken);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "요청 수락 실패", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void saveRequestedUserId(String id) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
        Set<String> updated = new HashSet<>(saved);
        updated.add(id);
        prefs.edit().putStringSet("REQUESTED_USER_IDS", updated).apply();
        requestedUserIds.add(id);
    }

    private void removeRequestedUserId(String id) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
        Set<String> updated = new HashSet<>(saved);
        updated.remove(id);
        prefs.edit().putStringSet("REQUESTED_USER_IDS", updated).apply();
        requestedUserIds.remove(id);
    }

    private void loadRequestedUserIds() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet("REQUESTED_USER_IDS", new HashSet<>());
        requestedUserIds.clear();
        requestedUserIds.addAll(saved);
    }
}