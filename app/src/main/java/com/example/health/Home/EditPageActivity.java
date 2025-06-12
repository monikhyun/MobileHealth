// 패키지 선언: 현재 클래스가 속한 패키지 위치를 지정
package com.example.health.Home;

// 안드로이드 및 뷰 관련 라이브러리 import
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// 외부 라이브러리 및 내부 클래스 import
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.health.Auth.MainActivity;
import com.example.health.Friend.*;
import com.example.health.R;
import com.example.health.Request.Friend.*;
import com.example.health.Request.Home.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * EditPageActivity
 * ----------------
 * 이 액티비티는 유저가 자신의 키, 몸무게, 나이, 성별, 프로필 사진을 수정할 수 있는 화면을 제공하며,
 * 친구 요청을 확인하고 수락할 수 있는 기능도 포함한다.
 */
public class EditPageActivity extends AppCompatActivity {

    // 상단바 버튼: 프로필 이동, 알림(친구 요청), 설정 버튼
    private ImageView btnProfile, btnAlarm, btnSetting;

    // 프로필 이미지 및 수정 버튼
    private ImageView imageProfile;
    private Button editBtn;

    // 유저 입력값 (키, 몸무게, 나이) 입력 필드
    private EditText heightEt, weightEt, ageEt;

    // 성별 선택용 라디오 그룹 및 버튼
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale;

    // 친구 목록 및 친구 요청 목록을 보여줄 어댑터
    private FriendListAdapter adapter;
    private FriendRequestAdapter friendRequestAdapter;

    // 취소 및 제출 버튼
    private Button btnCancel, buttonSubmit;

    // 친구 목록과 친구 요청 목록 데이터 저장용 리스트
    private List<FriendItem> friendList;
    private ArrayList<FriendRequestItem> requestList = new ArrayList<>();

    // JWT 인증 토큰 및 유저 ID
    private String jwtToken;
    private String userId;

    // 이미지 선택 요청 코드 상수값
    private static final int REQ_PICK_IMAGE = 1001;

    // 선택한 프로필 이미지 바이트 배열 및 파일 이름
    private byte[] selectedImageBytes;
    private String selectedImageName;

    // 하단바 탭 버튼 (이 액티비티에선 사용 X)
    private LinearLayout navHome, navWorkout, navMeal, navFriends, navStats;

    /**
     * 액티비티 시작 시 실행되는 메소드
     * 뷰 초기화, 데이터 불러오기, 이벤트 등록 등을 수행
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit); // 레이아웃 설정

        // 1. 뷰 바인딩
        btnProfile = findViewById(R.id.btn_profile);
        btnAlarm = findViewById(R.id.btn_alarm);
        btnSetting = findViewById(R.id.btn_setting);

        imageProfile = findViewById(R.id.image_profile);
        editBtn = findViewById(R.id.edit_btn);

        heightEt = findViewById(R.id.height);
        weightEt = findViewById(R.id.weight);
        ageEt = findViewById(R.id.age);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        btnCancel = findViewById(R.id.btnCancel);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // 키/몸무게 입력시 소수점 포함 숫자만 입력되도록 설정
        int decimalFlags = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        heightEt.setInputType(decimalFlags);
        weightEt.setInputType(decimalFlags);

        // 2. SharedPreferences에서 JWT 토큰 및 사용자 ID 불러오기
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        jwtToken = prefs.getString("JWT_TOKEN", null);
        userId = prefs.getString("USER_ID", null);

        // 3. 친구 목록 초기화 및 어댑터 설정
        friendList = new ArrayList<>();
        adapter = new FriendListAdapter(friendList);

        // 4. 각 버튼 클릭 시 동작 정의
        btnProfile.setOnClickListener(v -> {
            // 프로필 페이지 이동
            startActivity(new Intent(this, MyPageActivity.class));
        });

        btnAlarm.setOnClickListener(v -> {
            // 친구 요청 다이얼로그 표시
            showFriendRequestDialog();
        });

        btnSetting.setOnClickListener(v -> {
            // 현재 설정 버튼 기능 없음
        });

        // 5. 사용자 데이터 서버에서 불러오기
        MyPageRequest preloadReq = new MyPageRequest(
                jwtToken, userId,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String genderStr = obj.optString("gender");
                        double heightVal = obj.optDouble("height", 0);
                        double weightVal = obj.optDouble("weight", 0);
                        int ageVal = obj.optInt("age", 0);
                        String imageUrl = obj.optString("image");

                        // 불러온 데이터로 입력 필드 설정
                        heightEt.setText(String.valueOf((int)heightVal));
                        weightEt.setText(String.valueOf((int)weightVal));
                        ageEt.setText(String.valueOf(ageVal));

                        // 성별 설정 및 색상 적용
                        int checkedId = "MALE".equals(genderStr) ? R.id.radioMale : R.id.radioFemale;
                        radioGroupGender.check(checkedId);
                        ColorStateList defaultTint = ColorStateList.valueOf(Color.parseColor("#A0A0A0"));
                        radioMale.setBackgroundTintList(defaultTint);
                        radioFemale.setBackgroundTintList(defaultTint);

                        ColorStateList pinkTint = ColorStateList.valueOf(Color.parseColor("#FFB6C1"));
                        if (checkedId == R.id.radioMale) {
                            radioMale.setBackgroundTintList(pinkTint);
                        } else {
                            radioFemale.setBackgroundTintList(pinkTint);
                        }

                        // 프로필 이미지 로드
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

        // 6. 이미지 변경 버튼 클릭 → 갤러리 오픈
        editBtn.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, REQ_PICK_IMAGE);
        });

        // 7. 성별 라디오 버튼 선택 변경 시 색상 변경
        radioGroupGender.setOnCheckedChangeListener((group, checkedId) -> {
            ColorStateList defaultTint = ColorStateList.valueOf(Color.parseColor("#A0A0A0"));
            radioMale.setBackgroundTintList(defaultTint);
            radioFemale.setBackgroundTintList(defaultTint);
            ColorStateList pinkTint = ColorStateList.valueOf(Color.parseColor("#FFB6C1"));
            if (checkedId == R.id.radioMale) {
                radioMale.setBackgroundTintList(pinkTint);
            } else {
                radioFemale.setBackgroundTintList(pinkTint);
            }
        });

        // 8. 취소 버튼 클릭 → 액티비티 종료
        btnCancel.setOnClickListener(v -> finish());

        // 9. 제출 버튼 클릭 → 서버에 프로필 수정 요청 전송
        buttonSubmit.setOnClickListener(v -> {
            if (jwtToken == null || userId == null) {
                showToast("로그인 정보가 없습니다.");
                return;
            }

            String height = heightEt.getText().toString().trim();
            String weight = weightEt.getText().toString().trim();
            String age = ageEt.getText().toString().trim();
            int gid = radioGroupGender.getCheckedRadioButtonId();
            String gender = (gid == R.id.radioMale) ? "MALE" : "FEMALE";

            EditProfileRequest req = new EditProfileRequest(
                    jwtToken, userId,
                    selectedImageBytes, selectedImageName,
                    gender, height, weight, age,
                    response -> {
                        showToast("프로필 수정을 완료했습니다.");
                        startActivity(new Intent(this, MyPageActivity.class));
                        finish();
                    },
                    error -> {
                        showToast("프로필 수정 실패");
                    }
            );
            Volley.newRequestQueue(this).add(req);
        });
    }

    /**
     * 친구 요청 다이얼로그 생성 및 표시
     */
    private void showFriendRequestDialog() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_friend_list);

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestAdapter = new FriendRequestAdapter(requestList, (username, pos) -> acceptFriendRequest(username, pos));
        recyclerView.setAdapter(friendRequestAdapter);

        // 서버에 친구 요청 목록 요청
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
                            showToast("요청 목록 파싱 오류");
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        showToast("친구 요청 목록 불러오기 실패");
                    }
            );
            Volley.newRequestQueue(this).add(request);
        }
        dialog.show();
    }

    /**
     * 친구 요청 수락 처리
     */
    private void acceptFriendRequest(String username, int position) {
        FriendAcceptRequest request = new FriendAcceptRequest(
                jwtToken,
                userId,
                username,
                response -> {
                    showToast("친구 요청을 수락하였습니다.");
                    requestList.remove(position);
                    friendRequestAdapter.updateData(new ArrayList<>(requestList));
                    fetchFriendList(userId, "Bearer " + jwtToken);
                },
                error -> {
                    error.printStackTrace();
                    showToast("친구 요청 수락 실패");
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * 친구 목록 새로 불러오기
     */
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
                        showToast("데이터 파싱 오류");
                    }
                },
                error -> {
                    error.printStackTrace();
                    showToast("친구 목록 불러오기 실패");
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * 이미지 선택 후 결과 처리
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) return;

            Glide.with(this).load(uri).into(imageProfile);

            try (InputStream is = getContentResolver().openInputStream(uri);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) > 0) bos.write(buf, 0, len);

                selectedImageBytes = bos.toByteArray();
                String path = uri.getLastPathSegment();
                selectedImageName = (path != null ? path : "profile.jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 커스텀 토스트 메시지를 표시하는 유틸 메소드
     */
    private void showToast(String message) {
        View toastView = LayoutInflater.from(this).inflate(R.layout.toast_friend_request, null);
        TextView tv = toastView.findViewById(R.id.text_toast_message);
        tv.setText(message);
        Toast t = new Toast(this);
        t.setView(toastView);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
    }
}