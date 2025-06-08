package com.example.health.Home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.health.Auth.MainActivity;
import com.example.health.Friend.FriendItem;
import com.example.health.Friend.FriendListAdapter;
import com.example.health.Friend.FriendRequestAdapter;
import com.example.health.Friend.FriendRequestItem;
import com.example.health.R;
import com.example.health.Request.Friend.FriendAcceptRequest;
import com.example.health.Request.Friend.FriendListRequest;
import com.example.health.Request.Friend.FriendRequestsRequest;
import com.example.health.Request.Home.EditProfileRequest;
import com.example.health.Request.Home.MyPageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EditPageActivity extends AppCompatActivity {
    // top bar
    private ImageView btnProfile, btnAlarm, btnSetting;

    // profile section
    private ImageView imageProfile;
    private Button editBtn;

    // inputs
    private EditText heightEt, weightEt, ageEt;
    private RadioGroup radioGroupGender;

    private FriendListAdapter adapter;
    private RadioButton radioMale, radioFemale;

    private FriendRequestAdapter friendRequestAdapter;
    // actions
    private Button btnCancel, buttonSubmit;

    private List<FriendItem> friendList;
    private ArrayList<FriendRequestItem> requestList = new ArrayList<>();
    private String jwtToken;
    private String userId;
    private static final int REQ_PICK_IMAGE = 1001;

    private byte[] selectedImageBytes;
    private String selectedImageName;

    // bottom nav
    private LinearLayout navHome, navWorkout, navMeal, navFriends, navStats;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) 레이아웃 설정
        setContentView(R.layout.activity_profile_edit);

        // 2) findViewById 로 뷰 바인딩
        btnProfile      = findViewById(R.id.btn_profile);
        btnAlarm        = findViewById(R.id.btn_alarm);
        btnSetting      = findViewById(R.id.btn_setting);

        imageProfile    = findViewById(R.id.image_profile);
        editBtn         = findViewById(R.id.edit_btn);

        heightEt        = findViewById(R.id.height);
        weightEt        = findViewById(R.id.weight);
        ageEt           = findViewById(R.id.age);

        radioGroupGender= findViewById(R.id.radioGroupGender);
        radioMale       = findViewById(R.id.radioMale);
        radioFemale     = findViewById(R.id.radioFemale);

        btnCancel       = findViewById(R.id.btnCancel);
        buttonSubmit    = findViewById(R.id.buttonSubmit);



        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        jwtToken = prefs.getString("JWT_TOKEN", null);
        userId = prefs.getString("USER_ID", null);

        friendList = new ArrayList<>();
        adapter = new FriendListAdapter(friendList);
        // 3) 리스너 설정
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(EditPageActivity.this, MyPageActivity.class);
            startActivity(intent);
        });
        btnAlarm.setOnClickListener(v -> {
            showFriendRequestDialog();
        });
        btnSetting.setOnClickListener(v -> {
            // TODO: 설정 화면 이동
        });

        MyPageRequest preloadReq = new MyPageRequest(
                jwtToken, userId,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        // 기존에 저장된 값들
                        String genderStr = obj.optString("gender");
                        double heightVal = obj.optDouble("height", 0);
                        double weightVal = obj.optDouble("weight", 0);
                        int    ageVal    = obj.optInt("age", 0);
                        String imageUrl  = obj.optString("image");

                        // EditText에 미리 채워주기
                        heightEt.setText(String.valueOf((int)heightVal));
                        weightEt.setText(String.valueOf((int)weightVal));
                        ageEt.setText(String.valueOf(ageVal));

                        // 성별 라디오 버튼 셋팅 + tint 적용
                        int checkedId = "MALE".equals(genderStr) ? R.id.radioMale : R.id.radioFemale;
                        radioGroupGender.check(checkedId);
                        // 배경 tint 적용
                        ColorStateList defaultTint = ColorStateList.valueOf(Color.parseColor("#A0A0A0"));
                        radioMale.setBackgroundTintList(defaultTint);
                        radioFemale.setBackgroundTintList(defaultTint);
                        ColorStateList pinkTint = ColorStateList.valueOf(Color.parseColor("#FFB6C1"));
                        if (checkedId == R.id.radioMale) {
                            radioMale.setBackgroundTintList(pinkTint);
                        } else {
                            radioFemale.setBackgroundTintList(pinkTint);
                        }

                        // 프로필 이미지
                        if (imageUrl == null || imageUrl.isEmpty()) {
                            imageProfile.setImageResource(R.drawable.ic_cat_profile);
                        } else {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_cat_profile)
                                    .error(R.drawable.ic_cat_profile)
                                    .into(imageProfile);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "프로필 불러오기 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "프로필 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(preloadReq);


        editBtn.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_PICK_IMAGE);
        });
        radioGroupGender.setOnCheckedChangeListener((group, checkedId) -> {
            // 기본으로 두 버튼 모두 투명하게 초기화
            ColorStateList defaultTint = ColorStateList.valueOf(Color.parseColor("#A0A0A0"));
            radioMale.setBackgroundTintList(defaultTint);
            radioFemale.setBackgroundTintList(defaultTint);

            // 선택된 버튼만 핑크로 강조
            ColorStateList pinkTint = ColorStateList.valueOf(Color.parseColor("#FFB6C1"));
            if (checkedId == R.id.radioMale) {
                radioMale.setBackgroundTintList(pinkTint);
            } else if (checkedId == R.id.radioFemale) {
                radioFemale.setBackgroundTintList(pinkTint);
            }
        });

        btnCancel.setOnClickListener(v -> finish());
        buttonSubmit.setOnClickListener(v -> {
            if (jwtToken == null || userId == null) {
                View toastView = LayoutInflater.from(this)
                        .inflate(R.layout.toast_friend_request, null);
                TextView tv = toastView.findViewById(R.id.text_toast_message);
                tv.setText("로그인 정보가 없습니다.");
                Toast t = new Toast(this);
                t.setView(toastView);
                t.setDuration(Toast.LENGTH_SHORT);
                t.show();
                return;
            }
            String height = heightEt.getText().toString().trim();
            String weight = weightEt.getText().toString().trim();
            String age    = ageEt.getText().toString().trim();
            int gid = radioGroupGender.getCheckedRadioButtonId();
            String gender = (gid == R.id.radioMale) ? "MALE" : "FEMALE";

            EditProfileRequest req = new EditProfileRequest(
                    jwtToken, userId,
                    selectedImageBytes, selectedImageName,
                    gender, height, weight, age,
                    response -> {
                        // 커스텀 토스트
                        View toastView = LayoutInflater.from(this)
                                .inflate(R.layout.toast_friend_request, null);
                        TextView tv = toastView.findViewById(R.id.text_toast_message);
                        tv.setText("프로필 수정을 완료했습니다.");
                        Toast t = new Toast(this);
                        t.setView(toastView);
                        t.setDuration(Toast.LENGTH_SHORT);
                        t.show();

                        // MyPageActivity 로 이동
                        startActivity(new Intent(this, MyPageActivity.class));
                        finish();
                    },
                    error -> {
                        View toastView = LayoutInflater.from(this)
                                .inflate(R.layout.toast_friend_request, null);
                        TextView tv = toastView.findViewById(R.id.text_toast_message);
                        tv.setText("프로필 수정 실패");
                        Toast t = new Toast(this);
                        t.setView(toastView);
                        t.setDuration(Toast.LENGTH_SHORT);
                        t.show();
                    }
            );
            Volley.newRequestQueue(this).add(req);
        });

    }

    private void showFriendRequestDialog() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                    View toastView = LayoutInflater.from(this)
                            .inflate(R.layout.toast_friend_request, null);
                    TextView tv = toastView.findViewById(R.id.text_toast_message);
                    tv.setText("친구 요청 수락 실패");
                    Toast t = new Toast(this);
                    t.setView(toastView);
                    t.setDuration(Toast.LENGTH_SHORT);
                    t.show();
                }
        );
        Volley.newRequestQueue(this).add(request);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;
            // 화면에 미리 보기
            Glide.with(this).load(uri).into(imageProfile);
            // 바이트로 읽기
            try (InputStream is = getContentResolver().openInputStream(uri);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) > 0) bos.write(buf, 0, len);
                selectedImageBytes = bos.toByteArray();
                // 파일명 추출
                String path = uri.getLastPathSegment();
                selectedImageName = (path != null ? path : "profile.jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}